package arrow.meta.encoder.jvm

import arrow.common.utils.ClassOrPackageDataWrapper
import arrow.common.utils.ProcessorUtils
import arrow.meta.Either
import arrow.meta.ast.Annotation
import arrow.meta.ast.Code
import arrow.meta.ast.Func
import arrow.meta.ast.PackageName
import arrow.meta.ast.Parameter
import arrow.meta.ast.Property
import arrow.meta.ast.Tree
import arrow.meta.ast.Type
import arrow.meta.ast.TypeName
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.asTypeName
import com.squareup.kotlinpoet.asTypeVariableName
import me.eugeniomarletti.kotlin.metadata.getterModality
import me.eugeniomarletti.kotlin.metadata.getterVisibility
import me.eugeniomarletti.kotlin.metadata.isDelegated
import me.eugeniomarletti.kotlin.metadata.isPrimary
import me.eugeniomarletti.kotlin.metadata.isSuspend
import me.eugeniomarletti.kotlin.metadata.isVar
import me.eugeniomarletti.kotlin.metadata.jvm.getJvmConstructorSignature
import me.eugeniomarletti.kotlin.metadata.jvm.getJvmFieldSignature
import me.eugeniomarletti.kotlin.metadata.jvm.getJvmMethodSignature
import me.eugeniomarletti.kotlin.metadata.jvm.jvmPropertySignature
import me.eugeniomarletti.kotlin.metadata.modality
import me.eugeniomarletti.kotlin.metadata.shadow.metadata.ProtoBuf
import me.eugeniomarletti.kotlin.metadata.shadow.metadata.deserialization.TypeTable
import me.eugeniomarletti.kotlin.metadata.shadow.metadata.deserialization.hasReceiver
import me.eugeniomarletti.kotlin.metadata.shadow.metadata.jvm.JvmProtoBuf
import me.eugeniomarletti.kotlin.metadata.visibility
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.Modifier
import javax.lang.model.element.PackageElement
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.lang.model.type.NoType
import javax.lang.model.util.ElementFilter
import javax.lang.model.util.Elements
import kotlin.coroutines.Continuation

@Suppress("LargeClass")
interface TypeElementEncoder : KotlinMetatadataEncoder, KotlinPoetEncoder, ProcessorUtils {

  fun processorUtils(): ProcessorUtils

  fun Element.kDoc(): String?

  val typeElementToMeta: (classElement: TypeElement) -> ClassOrPackageDataWrapper

  val TypeElement.meta: ClassOrPackageDataWrapper.Class
    get() = typeElementToMeta(this) as ClassOrPackageDataWrapper.Class

  fun Element.packageName(): Either<EncodingError, PackageName> =
    when (this) {
      is PackageElement -> Either.Right(PackageName(qualifiedName.toString()))
      else -> Either.Left(
        EncodingError.UnsupportedElementType("Unsupported $this, as ($kind) to PackageName", this)
      )
    }

  fun Element.tree(): Either<EncodingError, Tree> =
    when (kind) {
      ElementKind.PACKAGE -> packageName()
      ElementKind.CLASS -> type()
      ElementKind.INTERFACE -> type()
      else -> Either.Left(EncodingError.UnsupportedElementType("Not supported: $kind", this))
    }

  fun Element.type(): Either<EncodingError, Type> =
    metaApi().run {
      val encodingResult: Either<EncodingError, Type> =
        elementUtils.getPackageOf(this@type).let { pckg ->
          val pckgName = pckg.qualifiedName.toString().asKotlin()
          when (kind) {
            ElementKind.INTERFACE -> Either.Right(Type(PackageName(pckgName), asType().asTypeName().toMeta(), Type.Shape.Interface))
            ElementKind.CLASS -> {
              val typeElement = this@type as TypeElement
              val classBuilder = Type(PackageName(pckgName), asType().asTypeName().toMeta(), Type.Shape.Class)
              val declaredConstructorSignatures = meta.constructorList.map { it.getJvmConstructorSignature(meta.nameResolver, meta.classProto.typeTable) }
              val constructors = ElementFilter.constructorsIn(elementUtils.getAllMembers(this@type)).filter {
                declaredConstructorSignatures.contains(it.jvmMethodSignature)
              }.mapNotNull { it.asConstructor(this@type) }
              Either.Right(classBuilder.copy(
                primaryConstructor = constructors.find { it.first }?.second,
                superclass = if (typeElement.superclass is NoType) null else typeElement.superclass.asTypeName().toMeta()
              ))
            }
            else -> Either.Left(EncodingError.UnsupportedElementType("Unsupported ${this@TypeElementEncoder}, as ($kind) to Type", this@type))
          }
        }
      encodingResult.map {
        val typeElement = this@type as TypeElement
        it.copy(
          kdoc = typeElement.kDoc()?.let(::Code),
          annotations = typeElement.annotations(),
          modifiers = typeElement.modifiers(),
          typeVariables = typeElement.typeVariables(),
          superInterfaces = typeElement.superInterfaces(),
          allFunctions = typeElement.allFunctions(typeElement),
          declaredFunctions = typeElement.declaredFunctions(typeElement),
          properties = typeElement.properties(),
          types = typeElement.sealedSubClassNames().mapNotNull {
            if (it is TypeName.Classy && it.simpleName == "Companion") null else it.type
          }
        )
      }
    }

  fun TypeElement.properties(): List<Property> =
    meta.propertyList.map { property ->
      val jvmPropertySignature: String = property.jvmPropertySignature.toString()
      val jvmFieldSignature = property.getJvmFieldSignature(meta.nameResolver, meta.classProto.typeTable)
      val prop = Property(
        name = meta.nameResolver.getString(property.name),
        jvmPropertySignature = jvmPropertySignature,
        jvmFieldSignature = jvmFieldSignature?.toString(),
        type = property.returnType.asTypeName(meta),
        mutable = property.isVar,
        modifiers = listOfNotNull(
          property.visibility?.asModifier(),
          property.modality?.asModifier()
        ),
        receiverType = property.receiverType.asTypeName(meta),
        delegated = property.isDelegated,
        getter = Func(
          name = "get",
          returnType = property.returnType.asTypeName(meta),
          jvmMethodSignature = jvmPropertySignature,
          receiverType = property.receiverType.asTypeName(meta),
          modifiers = listOfNotNull(
            property.getterVisibility?.asModifier(),
            property.getterModality?.asModifier()
          )
        ),
        setter = Func(
          name = "set",
          returnType = TypeName.Unit,
          jvmMethodSignature = jvmPropertySignature,
          receiverType = property.receiverType.asTypeName(meta),
          modifiers = listOfNotNull(
            property.getterVisibility?.asModifier(),
            property.getterModality?.asModifier()
          )
        )
      )
      if (property.hasReceiver()) {
        prop.copy(receiverType = property.receiverType.asTypeName(meta))
      } else prop
    }

  fun TypeElement.declaredFunctions(declaredElement: TypeElement): List<Func> {
    val declaredFunctionSignatures = meta.functionList.map { it.getJvmMethodSignature(meta.nameResolver, meta.classProto.typeTable) }
    return allFunctions(declaredElement).filter {
      declaredFunctionSignatures.contains(it.jvmMethodSignature)
    }
  }

  private fun Func.maybeAsSuspendedContinuation(protoFun: ProtoBuf.Function): Func =
    if (protoFun.isSuspend) {
      copy(
        modifiers = modifiers + arrow.meta.ast.Modifier.Suspend,
        parameters = parameters.filterNot { it.type.rawName == "kotlin.coroutines.Continuation" },
        returnType = parameters.lastOrNull().let {
          val t = it?.type
          if (t is TypeName.ParameterizedType &&
            t.rawName == "kotlin.coroutines.Continuation" &&
            t.typeArguments.isNotEmpty()) t.typeArguments[0]
          else null
        } ?: returnType
      )
    } else this

  private fun Func.fixSuspendedParameters(): Func =
    copy(
      parameters = parameters.map { p ->
        val t = p.type
        val result = if (t is TypeName.ParameterizedType) t.asSuspendedContinuation()
        else t
        p.copy(type = result)
      }
    )

  fun TypeName.ParameterizedType.isContinuation(): Boolean =
    (rawType.fqName == Function1::class.qualifiedName || rawType.fqName == Function2::class.qualifiedName && typeArguments.size >= 2) && continuationType() != null

  fun TypeName.ParameterizedType.continuationType(): TypeName.ParameterizedType? {
    val wildcard = typeArguments.filterIsInstance<TypeName.WildcardType>().find {
      it.lowerBounds.isNotEmpty() && {
        val lowerBoundsContinuation = it.lowerBounds[0]
        lowerBoundsContinuation is TypeName.ParameterizedType &&
          lowerBoundsContinuation.isContinuationType()
      }()
    }
    return wildcard?.lowerBounds?.get(0) as? TypeName.ParameterizedType
  }

  fun TypeName.ParameterizedType.isMonadContinuation(): Boolean =
    rawType.fqName == Function2::class.qualifiedName && typeArguments.size == 3 && {
      val maybeContinuation = typeArguments[1]
      when {
        maybeContinuation is TypeName.WildcardType &&
          maybeContinuation.lowerBounds.isNotEmpty() -> {
          val lowerBoundsContinuation = maybeContinuation.lowerBounds[0]
          lowerBoundsContinuation is TypeName.ParameterizedType &&
            lowerBoundsContinuation.isContinuationType()
        }
        else -> false
      }
    }() && (name.contains("Monad.*Continuation".toRegex()) || name.contains("Concurrent.*Continuation".toRegex()))

  fun TypeName.ParameterizedType.isContinuationType() =
    rawType.fqName == Continuation::class.qualifiedName

  private fun TypeName.ParameterizedType.asSuspendedContinuation(): TypeName =
    when {
      isMonadContinuation() -> TypeName.FunctionLiteral(
        modifiers = listOf(arrow.meta.ast.Modifier.Suspend),
        receiverType = typeArguments[0],
        parameters = emptyList(),
        returnType = (typeArguments[1] as? TypeName.WildcardType)?.let {
          it.lowerBounds[0] as? TypeName.ParameterizedType
        }?.let {
          it.typeArguments[0]
        } ?: TypeName.Unit
      )
      isContinuation() -> {
        val continuation = continuationType()
        val params = typeArguments.filterIsInstance<TypeName.WildcardType>().takeWhile { it.lowerBounds[0] != continuation }
        TypeName.FunctionLiteral(
          receiverType = null,
          modifiers = listOf(arrow.meta.ast.Modifier.Suspend),
          parameters = params,
          returnType = continuation?.typeArguments?.get(0) ?: TypeName.Unit
        )
      }
      else -> this
    }

  fun Func.fixLiterals(meta: ClassOrPackageDataWrapper.Class, protoFun: ProtoBuf.Function): Func =
    copy(
      receiverType = receiverType?.fixLiterals(meta, protoFun.receiverType),
      parameters = if (parameters.size <= protoFun.valueParameterList.size)
        parameters.mapIndexed { n, p -> p.fixLiterals(meta, protoFun.valueParameterList[n]) }
      else parameters,
      returnType = returnType?.fixLiterals(meta, protoFun.returnType)
    )

  private fun Parameter.fixLiterals(meta: ClassOrPackageDataWrapper.Class, protoParam: ProtoBuf.ValueParameter): Parameter =
    copy(type = type.fixLiterals(meta, protoParam.type))

  private fun TypeName.fixLiterals(
    meta: ClassOrPackageDataWrapper.Class,
    protoType: ProtoBuf.Type
  ): TypeName =
    when (this) {
      is TypeName.TypeVariable -> this
      is TypeName.WildcardType -> this
      is TypeName.FunctionLiteral -> this
      is TypeName.ParameterizedType -> fixLiterals(meta, protoType)
      is TypeName.Classy -> this
    }

  private fun TypeName.ParameterizedType.fixLiterals(
    meta: ClassOrPackageDataWrapper.Class,
    protoType: ProtoBuf.Type
  ): TypeName {
    val jvmTypeAnnotations: List<String> = protoType.getExtension(JvmProtoBuf.typeAnnotation).map {
      meta.nameResolver.getString(it.id)
    }
    return if (
      "kotlin/ExtensionFunctionType" in jvmTypeAnnotations && typeArguments.size >= 2)
      if (isMonadContinuation() || isContinuation()) asSuspendedContinuation()
      else TypeName.FunctionLiteral(
        receiverType = typeArguments[0],
        parameters = typeArguments.drop(1).dropLast(1),
        returnType = typeArguments.lastOrNull() ?: TypeName.Unit
      )
    else
      this
  }

  fun TypeElement.allFunctions(declaredElement: TypeElement): List<Func> =
    processorUtils().run {
      val superTypes = supertypes(
        declaredElement.meta,
        TypeTable(declaredElement.meta.classProto.typeTable),
        processorUtils(),
        emptyList()
      ).filterIsInstance(ClassOrPackageDataWrapper.Class::class.java)
      val declaredFunctions = declaredElement.meta.functionList.map { meta to it }
      val inheritedFunctions = superTypes.flatMap { s -> s.functionList.map { s to it } }
      val allFunctions = declaredFunctions + inheritedFunctions
      val allMembers = ElementFilter.methodsIn(processorUtils().elementUtils.getAllMembers(declaredElement))
      val declaredType = processorUtils().typeUtils.getDeclaredType(
        declaredElement,
        *declaredElement.typeParameters.map { it.asType() }.toTypedArray()
      )
      val filteredMembers = allMembers.asSequence().filterNot {
        it.modifiers.containsAll(listOf(Modifier.PRIVATE, Modifier.FINAL))
      }.filterNot {
        it.modifiers.containsAll(listOf(Modifier.PUBLIC, Modifier.FINAL, Modifier.NATIVE))
      }
      val members = filteredMembers.mapNotNull { member ->
        val templateFunction = allFunctions.find { (proto, function) ->
          function.getJvmMethodSignature(proto.nameResolver, proto.classProto.typeTable) == member.jvmMethodSignature
        }
        @Suppress("SwallowedException")
        try {
          val function = FunSpec.overriding(
            member,
            declaredType,
            processorUtils().typeUtils
          ).build().toMeta(member).let {
            @Suppress("UnnecessaryLet")
            it.copy(kdoc = member.kDoc()?.let(::Code))
          }

          val result =
            if (templateFunction != null && templateFunction.second.modality != null) {
              val (metaFun, protoFun) = templateFunction
              val fMod = function.copy(
                modifiers = function.modifiers +
                  listOfNotNull(protoFun.modality?.toMeta()) +
                  modifiersFromFlags(protoFun.flags)
              ).maybeAsSuspendedContinuation(protoFun)
              if (protoFun.hasReceiverType()) {
                val receiverTypeName = metaApi().run {
                  fMod.parameters[0].type.asKotlin().fixLiterals(metaFun, protoFun.receiverType)
                }
                val functionWithReceiver = fMod.copy(receiverType = receiverTypeName)
                val arguments = functionWithReceiver.parameters.drop(1)
                functionWithReceiver.copy(parameters = arguments)
                  .fixLiterals(metaFun, protoFun)
              } else fMod.fixLiterals(metaFun, protoFun)
            } else function
          result.fixSuspendedParameters()
        } catch (e: IllegalArgumentException) {
          // some public final functions can't be seen as overridden
          templateFunction?.second?.toMeta(templateFunction.first, member)
        }
      }.toList()
      members
    }

  fun TypeElement.superInterfaces(): List<TypeName> =
    meta.classProto.supertypeList.map { type ->
      val superTypeName = type.extractFullName(meta, true).removeVariance().asKotlin()
      val pckg = superTypeName.substringBefore("<").substringBeforeLast(".")
      val simpleName = superTypeName.substringBefore("<").substringAfterLast(".")
      if (type.argumentList.isNotEmpty())
        TypeName.ParameterizedType(
          name = superTypeName.asKotlin(),
          enclosingType = asTypeName(),
          nullable = false,
          typeArguments = type.argumentList.map {
            TypeName.TypeVariable(name = it.type.extractFullName(meta).asKotlin())
          },
          rawType = TypeName.Classy(simpleName, "$pckg.$simpleName", PackageName(pckg), nullable = false)
        )
      else TypeName.Classy(simpleName, superTypeName, PackageName(pckg), nullable = false)
    }

  fun TypeElement.typeVariables(): List<TypeName.TypeVariable> =
    typeParameters.map { it.asTypeVariableName().toMeta() }

  fun TypeElement.modifiers(): List<arrow.meta.ast.Modifier> =
    listOfNotNull(meta.classProto.modality?.asModifier(), meta.classProto.visibility?.asModifier())

  fun TypeElement.asTypeName(): TypeName =
    asType().asTypeName().toMeta()

  fun TypeElement.annotations(): List<Annotation> =
    annotationMirrors.mapNotNull {
      when {
        it.annotationType.asTypeName().toString() != "kotlin.Metadata" -> AnnotationSpec.get(it).toMeta()
        else -> null
      }
    }

  fun ExecutableElement.asConstructor(typeElement: TypeElement): Pair<Boolean, Func>? =
    kotlinMetadataUtils().run {
      typeElement.meta.constructorList.find {
        it.getJvmConstructorSignature(typeElement.meta.nameResolver, typeElement.meta.classProto.typeTable) == this@asConstructor.jvmMethodSignature
      }?.let { constructor ->
        constructor.isPrimary to
          Func(
            name = "constructor",
            annotations = emptyList(),
            typeVariables = emptyList(),
            modifiers = modifiers.mapNotNull { it.toMeta() },
            returnType = returnType?.asTypeName()?.toMeta(),
            receiverType = receiverType?.asTypeName()?.toMeta(),
            kdoc = null,
            body = null,
            parameters = parameters.map { it.toMeta() },
            jvmMethodSignature = jvmMethodSignature
          )
      }
    }

  private fun VariableElement.toMeta(): Parameter =
    Parameter(
      name = simpleName.toString(),
      type = metaApi().run { asType().asTypeName().toMeta().asKotlin() },
      modifiers = modifiers.mapNotNull { it.toMeta() }
    )

  private fun Modifier.toMeta(): arrow.meta.ast.Modifier? =
    when (this) {
      Modifier.PUBLIC -> arrow.meta.ast.Modifier.Public
      Modifier.PROTECTED -> arrow.meta.ast.Modifier.Protected
      Modifier.PRIVATE -> arrow.meta.ast.Modifier.Private
      Modifier.ABSTRACT -> arrow.meta.ast.Modifier.Abstract
      Modifier.DEFAULT -> null
      Modifier.STATIC -> null
      Modifier.FINAL -> arrow.meta.ast.Modifier.Final
      Modifier.TRANSIENT -> null
      Modifier.VOLATILE -> null
      Modifier.SYNCHRONIZED -> null
      Modifier.NATIVE -> null
      Modifier.STRICTFP -> null
    }

  fun TypeElement.sealedSubClassNames(): List<TypeName> =
    if (meta.classProto.sealedSubclassFqNameList.isNotEmpty())
      meta.classProto.sealedSubclassFqNameList.map {
        val fqName = meta.nameOf(it).asKotlin()
        TypeName.Classy.from(fqName.substringBeforeLast("."), fqName.substringAfterLast("."))
      }
    else emptyList()

  fun getTypeElement(name: String, elements: Elements): TypeElement? =
    @Suppress("SwallowedException")
    try {
      elements.getTypeElement(name)
    } catch (e: Exception) {
      null
    }

  fun TypeElement.asMetaType(): Type? =
    type().fold({ null }, { it })
}
