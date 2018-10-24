package arrow.meta.encoder.jvm

import arrow.common.utils.ClassOrPackageDataWrapper
import arrow.common.utils.ProcessorUtils
import arrow.meta.Either
import arrow.meta.ast.*
import arrow.meta.ast.Annotation
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.asTypeName
import com.squareup.kotlinpoet.asTypeVariableName
import me.eugeniomarletti.kotlin.metadata.*
import me.eugeniomarletti.kotlin.metadata.jvm.getJvmConstructorSignature
import me.eugeniomarletti.kotlin.metadata.jvm.getJvmFieldSignature
import me.eugeniomarletti.kotlin.metadata.jvm.getJvmMethodSignature
import me.eugeniomarletti.kotlin.metadata.jvm.jvmPropertySignature
import me.eugeniomarletti.kotlin.metadata.shadow.metadata.deserialization.TypeTable
import me.eugeniomarletti.kotlin.metadata.shadow.metadata.deserialization.hasReceiver
import javax.lang.model.element.*
import javax.lang.model.element.Modifier
import javax.lang.model.type.NoType
import javax.lang.model.util.ElementFilter
import javax.lang.model.util.Elements

interface TypeElementEncoder : KotlinMetatadataEncoder, KotlinPoetEncoder, ProcessorUtils {

  fun processorUtils(): ProcessorUtils

  val typeElementToMeta: (classElement: TypeElement) -> ClassOrPackageDataWrapper

  val TypeElement.meta: ClassOrPackageDataWrapper.Class
    get() = typeElementToMeta(this) as ClassOrPackageDataWrapper.Class

  fun Element.packageName(): Either<EncodingError, PackageName> =
    when (this) {
      is PackageElement -> Either.Right(PackageName(qualifiedName.toString()))
      else -> Either.Left(
        EncodingError.UnsupportedElementType("Unsupported ${this}, as ($kind) to PackageName", this)
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

  fun TypeElement.allFunctions(declaredElement: TypeElement): List<Func> =
    processorUtils().run {
      metaApi().run {
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
          try {
            val function = FunSpec.overriding(
              member,
              declaredType,
              processorUtils().typeUtils
            ).build().toMeta(member)
            val result =
              if (templateFunction != null && templateFunction.second.modality != null) {
                val fMod = function.copy(
                  modifiers = function.modifiers + listOfNotNull(templateFunction.second.modality?.toMeta())
                )
                if (templateFunction.second.hasReceiverType()) {
                  val receiverTypeName = fMod.parameters[0].type.asKotlin()
                  val functionWithReceiver = fMod.copy(receiverType = receiverTypeName)
                  val arguments = functionWithReceiver.parameters.drop(1)
                  functionWithReceiver.copy(parameters = arguments)
                } else fMod
              } else function
            result
          } catch (e: IllegalArgumentException) {
            //logW("Can't override: ${declaredElement.simpleName} :: $member")
            //some public final functions can't be seen as overridden
            templateFunction?.second?.toMeta(templateFunction.first, member)
          }
        }.toList()
        members
      }
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
    try {
      elements.getTypeElement(name)
    } catch (e: Exception) {
      null
    }

  fun TypeElement.asMetaType(): Type? =
    type().fold({ null }, { it })
}