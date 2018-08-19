package arrow.meta.encoder.instances

import arrow.common.utils.*
import arrow.derive.normalizeType
import arrow.meta.ast.*
import arrow.meta.ast.Annotation
import arrow.meta.ast.Modifier
import arrow.meta.ast.TypeName
import arrow.meta.encoder.EncodingError
import arrow.meta.encoder.EncodingResult
import arrow.meta.encoder.MetaEncoder
import com.squareup.kotlinpoet.*
import me.eugeniomarletti.kotlin.metadata.*
import me.eugeniomarletti.kotlin.metadata.jvm.getJvmConstructorSignature
import me.eugeniomarletti.kotlin.metadata.jvm.getJvmFieldSignature
import me.eugeniomarletti.kotlin.metadata.jvm.getJvmMethodSignature
import me.eugeniomarletti.kotlin.metadata.jvm.jvmPropertySignature
import me.eugeniomarletti.kotlin.metadata.shadow.metadata.deserialization.TypeTable
import me.eugeniomarletti.kotlin.metadata.shadow.metadata.deserialization.hasReceiver
import java.lang.IllegalArgumentException
import javax.lang.model.element.*
import javax.lang.model.type.NoType
import javax.lang.model.util.ElementFilter
import me.eugeniomarletti.kotlin.metadata.shadow.metadata.ProtoBuf as proto

class TypeEncoder(private val encoderUtils: EncoderUtils) : MetaEncoder<Type>, ProcessorUtils by encoderUtils.processorUtils {

  override fun encode(element: Element): EncodingResult<Type> {
    val encodingResult: EncodingResult<Type> =
      when (element.kind) {
        ElementKind.INTERFACE -> EncodingResult.just(Type(Type.Kind.Interface))
        ElementKind.CLASS -> {
          val typeElement = element as TypeElement
          val classBuilder = Type(Type.Kind.Class)
          val declaredConstructorSignatures = element.meta.constructorList.map { it.getJvmConstructorSignature(element.meta.nameResolver, element.meta.classProto.typeTable) }
          val constructors = ElementFilter.constructorsIn(elementUtils.getAllMembers(element)).filter {
            declaredConstructorSignatures.contains(it.jvmMethodSignature)
          }.mapNotNull { it.asConstructor(element) }
          EncodingResult.just(classBuilder.copy(
            primaryConstructor = constructors.find { it.first }?.second,
            superclass = if (typeElement.superclass is NoType) null else typeElement.superclass.asTypeName().toMeta()
          ))
        }
        else -> EncodingResult.raiseError(EncodingError.UnsupportedElementType("Unsupported ${this}, as (${element.kind}) to Type", element))
      }
    return encodingResult.map {
      val typeElement = element as TypeElement
      it.copy(
        annotations = typeElement.annotations(),
        modifiers = typeElement.modifiers(),
        typeVariables = typeElement.typeVariables(),
        superInterfaces = typeElement.superInterfaces(),
        allFunctions = typeElement.allFunctions(),
        declaredFunctions = typeElement.declaredFunctions(),
        properties = typeElement.properties(),
        types = (typeElement.meta.sealedSubClassNames() +
          typeElement.meta.nestedClassNames()).mapNotNull { it.asType() }
      )
    }
  }

  fun TypeElement.asMetaType(): Type? =
    encode(this).fold({ null }, { it })

  private fun proto.Type.asTypeName(meta: ClassOrPackageDataWrapper.Class, outputTypeAlias: Boolean): TypeName {
    val fullName = extractFullName(meta, outputTypeAlias).normalizeType()
    val pck = fullName.substringBefore("<").substringBeforeLast(".")
    val simpleName = fullName.substringBefore("<").substringAfterLast(".")
    return if (argumentList.isEmpty()) TypeName.Class(
      simpleName = simpleName,
      fqName = fullName,
      pckg = PackageName(pck),
      nullable = nullable
    )
    else TypeName.ParameterizedType(
      name = fullName,
      rawType = TypeName.Class(simpleName, "$pck.$simpleName", PackageName(pck)),
      typeArguments = argumentList.map { TypeName.TypeVariable(it.type.extractFullName(meta).normalizeType()) },
      enclosingType = TypeName.Class(
        simpleName = meta.simpleName,
        fqName = meta.fullName.normalizeType(),
        pckg = PackageName(meta.`package`),
        nullable = false
      )
    )
  }

  fun TypeElement.properties(): List<Property> =
    meta.propertyList.map { property ->
      val jvmPropertySignature: String = property.jvmPropertySignature.toString()
      val jvmFieldSignature = property.getJvmFieldSignature(meta.nameResolver, meta.classProto.typeTable)
      val prop = Property(
        name = meta.nameResolver.getString(property.name),
        jvmPropertySignature = jvmPropertySignature,
        jvmFieldSignature = jvmFieldSignature?.toString(),
        type = property.returnType.asTypeName(meta, false),
        mutable = property.isVar,
        modifiers = listOfNotNull(
          property.visibility?.asModifier(),
          property.modality?.asModifier()
        ),
        receiverType = property.receiverType.asTypeName(meta, false),
        delegated = property.isDelegated,
        getter = Fun(
          name = "get",
          returnType = property.returnType.asTypeName(meta, false),
          jvmMethodSignature = jvmPropertySignature,
          receiverType = property.receiverType.asTypeName(meta, false),
          modifiers = listOfNotNull(
            property.getterVisibility?.asModifier(),
            property.getterModality?.asModifier()
          )
        ),
        setter = Fun(
          name = "set",
          returnType = TypeName.Unit,
          jvmMethodSignature = jvmPropertySignature,
          receiverType = property.receiverType.asTypeName(meta, false),
          modifiers = listOfNotNull(
            property.getterVisibility?.asModifier(),
            property.getterModality?.asModifier()
          )
        )
      )
      if (property.hasReceiver()) {
        prop.copy(receiverType = property.receiverType.asTypeName(meta, true))
      } else prop
    }

  fun TypeElement.declaredFunctions(): List<Fun> {
    val declaredFunctionSignatures = meta.functionList.map { it.getJvmMethodSignature(meta.nameResolver, meta.classProto.typeTable) }
    return allFunctions().filter {
      declaredFunctionSignatures.contains(it.jvmMethodSignature)
    }
  }

  fun TypeElement.allFunctions(): List<Fun> {
    val superTypes = supertypes(meta, TypeTable(meta.classProto.typeTable), emptyList())
      .filterIsInstance(ClassOrPackageDataWrapper.Class::class.java)
    val declaredFunctions = meta.functionList.map { meta to it }
    val inheritedFunctions = superTypes.flatMap { s -> s.functionList.map { s to it } }
    val allFunctions = declaredFunctions + inheritedFunctions
    val allMembers = ElementFilter.methodsIn(elementUtils.getAllMembers(this))
    val declaredType = typeUtils.getDeclaredType(this)
    return allMembers.filterNot {
      it.modifiers.containsAll(listOf(javax.lang.model.element.Modifier.PUBLIC, javax.lang.model.element.Modifier.FINAL))
    }.map { member ->
      val templateFunction = allFunctions.find { (proto, function) ->
        function.getJvmMethodSignature(proto.nameResolver, proto.classProto.typeTable) == member.jvmMethodSignature
      }
      val function = FunSpec.overriding(member, declaredType, typeUtils).build().toMeta(member)
      val result =
        if (templateFunction != null) {
          val returningFun =
            function.copy(returnType = member.returnType.asTypeName().toMeta())
          if (templateFunction.second.hasReceiverType()) {
            val receiverTypeName = returningFun.parameters[0].type
            val functionWithReceiver = returningFun.copy(receiverType = receiverTypeName)
            val arguments = functionWithReceiver.parameters.drop(1)
            functionWithReceiver.copy(parameters = arguments)
          } else returningFun
        } else function
      result
    }
  }

  fun TypeElement.superInterfaces(): List<TypeName> =
    meta.classProto.supertypeList.map { type ->
      val superTypeName = type.extractFullName(meta, true).normalizeType()
      val pckg = superTypeName.substringBefore("<").substringBeforeLast(".")
      val simpleName = superTypeName.substringBefore("<").substringAfterLast(".")
      if (type.argumentList.isNotEmpty())
        TypeName.ParameterizedType(
          name = superTypeName,
          enclosingType = asTypeName(),
          nullable = false,
          typeArguments = type.argumentList.map {
            TypeName.TypeVariable(name = it.type.extractFullName(meta).normalizeType())
          },
          rawType = TypeName.Class(simpleName, "$pckg.$simpleName", PackageName(pckg), nullable = false)
        )
      else TypeName.Class(simpleName, superTypeName, PackageName(pckg), nullable = false)
    }

  fun TypeElement.typeVariables(): List<TypeName.TypeVariable> =
    typeParameters.map { it.asTypeVariableName().toMeta() }

  fun TypeElement.modifiers(): List<Modifier> =
    listOfNotNull(meta.classProto.modality?.asModifier(), meta.classProto.visibility?.asModifier())

  val TypeElement.meta: ClassOrPackageDataWrapper.Class
    get() = encoderUtils.typeElementToMeta(this) as ClassOrPackageDataWrapper.Class

  tailrec fun TypeName.asType(): Type? =
    when (this) {
      is TypeName.TypeVariable -> null
      is TypeName.WildcardType -> null
      is TypeName.ParameterizedType -> rawType.asType()
      is TypeName.Class -> elementUtils.getTypeElement(fqName).asMetaType()
    }

  fun ClassOrPackageDataWrapper.Class.sealedSubClassNames(): List<TypeName> =
    if (classProto.sealedSubclassFqNameList.isNotEmpty())
      classProto.sealedSubclassFqNameList.map {
        val fqName = nameOf(it).normalizeType()
        ClassName.bestGuess(fqName).toMeta()
      }
    else emptyList()

  fun ClassOrPackageDataWrapper.Class.nestedClassNames(): List<TypeName> =
    if (classProto.nestedClassNameList.isNotEmpty())
      classProto.nestedClassNameList.map {
        val fqName = nameOf(it).normalizeType()
        ClassName.bestGuess(fqName).toMeta()
      }
    else emptyList()

  fun ClassOrPackageDataWrapper.Class.nameOf(id: Int): String =
    nameResolver.getString(id)

  fun Element.getPackage(): PackageElement {
    var t = this
    while (t.kind != ElementKind.PACKAGE) {
      t = t.enclosingElement
    }
    return t as PackageElement
  }

  fun Element.isClassOrInterface() = kind.isClass || kind.isInterface

  fun com.squareup.kotlinpoet.CodeBlock.toMeta(): Code =
    Code(toString())

  fun com.squareup.kotlinpoet.AnnotationSpec.UseSiteTarget.toMeta(): UseSiteTarget =
    when (this) {
      AnnotationSpec.UseSiteTarget.FILE -> UseSiteTarget.File
      AnnotationSpec.UseSiteTarget.PROPERTY -> UseSiteTarget.Property
      AnnotationSpec.UseSiteTarget.FIELD -> UseSiteTarget.Field
      AnnotationSpec.UseSiteTarget.GET -> UseSiteTarget.Get
      AnnotationSpec.UseSiteTarget.SET -> UseSiteTarget.Set
      AnnotationSpec.UseSiteTarget.RECEIVER -> UseSiteTarget.Receiver
      AnnotationSpec.UseSiteTarget.PARAM -> UseSiteTarget.Param
      AnnotationSpec.UseSiteTarget.SETPARAM -> UseSiteTarget.SetParam
      AnnotationSpec.UseSiteTarget.DELEGATE -> UseSiteTarget.Delegate
    }

  private fun com.squareup.kotlinpoet.ParameterSpec.toMeta(): Parameter =
    Parameter(
      name = name,
      modifiers = modifiers.map { it.toMeta() },
      annotations = annotations.map { it.toMeta() },
      type = type.toMeta(),
      defaultValue = defaultValue?.toMeta()
    )

  private fun com.squareup.kotlinpoet.FunSpec.toMeta(element: ExecutableElement): Fun =
    Fun(
      name = element.simpleName.toString(),
      annotations = annotations.map { it.toMeta() },
      typeVariables = typeVariables.map { it.toMeta() },
      modifiers = modifiers.map { it.toMeta() },
      returnType = returnType?.toMeta(),
      receiverType = receiverType?.toMeta(),
      kdoc = kdoc.toMeta(),
      body = body.toMeta(),
      parameters = parameters.map { it.toMeta() },
      jvmMethodSignature = element.jvmMethodSignature
    )

  private fun com.squareup.kotlinpoet.AnnotationSpec.toMeta(): Annotation =
    Annotation(type.toMeta(), members = members.map { it.toMeta() }, useSiteTarget = useSiteTarget?.toMeta())


  private fun com.squareup.kotlinpoet.WildcardTypeName.toMeta(): TypeName.WildcardType =
    TypeName.WildcardType(
      toString(),
      upperBounds.map { it.toMeta() },
      lowerBounds = lowerBounds.map { it.toMeta() },
      nullable = nullable,
      annotations = annotations.map { it.toMeta() }
    )

  private fun com.squareup.kotlinpoet.ClassName.toMeta(): TypeName.Class =
    TypeName.Class(
      simpleName = simpleName,
      fqName = canonicalName,
      annotations = annotations.map { it.toMeta() },
      nullable = nullable,
      pckg = PackageName(packageName)
    )

  private fun com.squareup.kotlinpoet.ParameterizedTypeName.toMeta(): TypeName =
    TypeName.ParameterizedType(
      name = toString(),
      nullable = nullable,
      annotations = annotations.map { it.toMeta() },
      enclosingType = null,
      rawType = rawType.toMeta(),
      typeArguments = typeArguments.map { it.toMeta() }
    )

  private fun com.squareup.kotlinpoet.KModifier.toMeta(): Modifier =
    when (this) {
      KModifier.PUBLIC -> Modifier.Public
      KModifier.PROTECTED -> Modifier.Protected
      KModifier.PRIVATE -> Modifier.Private
      KModifier.INTERNAL -> Modifier.Internal
      KModifier.EXPECT -> Modifier.Expect
      KModifier.ACTUAL -> Modifier.Actual
      KModifier.FINAL -> Modifier.Final
      KModifier.OPEN -> Modifier.Open
      KModifier.ABSTRACT -> Modifier.Abstract
      KModifier.SEALED -> Modifier.Sealed
      KModifier.CONST -> Modifier.Const
      KModifier.EXTERNAL -> Modifier.External
      KModifier.OVERRIDE -> Modifier.Override
      KModifier.LATEINIT -> Modifier.LateInit
      KModifier.TAILREC -> Modifier.Tailrec
      KModifier.VARARG -> Modifier.VarArg
      KModifier.SUSPEND -> Modifier.Suspend
      KModifier.INNER -> Modifier.Inner
      KModifier.ENUM -> Modifier.Enum
      KModifier.ANNOTATION -> Modifier.Annotation
      KModifier.COMPANION -> Modifier.Companion
      KModifier.INLINE -> Modifier.Inline
      KModifier.NOINLINE -> Modifier.NoInline
      KModifier.CROSSINLINE -> Modifier.CrossInline
      KModifier.REIFIED -> Modifier.Reified
      KModifier.INFIX -> Modifier.Infix
      KModifier.OPERATOR -> Modifier.Operator
      KModifier.DATA -> Modifier.Data
      KModifier.IN -> Modifier.In
      KModifier.OUT -> Modifier.Out
    }


  private fun com.squareup.kotlinpoet.TypeVariableName.toMeta(): TypeName.TypeVariable =
    TypeName.TypeVariable(
      name = toString(),
      bounds = bounds.map { it.toMeta() },
      annotations = annotations.map { it.toMeta() },
      nullable = nullable,
      reified = reified,
      variance = variance?.toMeta()
    )

  private fun com.squareup.kotlinpoet.TypeName.toMeta(): TypeName =
    when (this) {
      is WildcardTypeName -> toMeta()
      is ClassName -> toMeta()
      is ParameterizedTypeName -> toMeta()
      is TypeVariableName -> toMeta()
      else -> throw IllegalArgumentException("Unsupported type name: $this")
    }

  fun TypeElement.asTypeName(): TypeName =
    asType().asTypeName().toMeta()

  fun TypeElement.annotations(): List<Annotation> =
    annotationMirrors.mapNotNull {
      when {
        it.annotationType.asTypeName().toString() != "kotlin.Metadata" -> AnnotationSpec.get(it).toMeta()
        else -> null
      }
    }

  private fun ExecutableElement.asConstructor(typeElement: TypeElement): Pair<Boolean, Fun>? =
    typeElement.meta.constructorList.find {
      it.getJvmConstructorSignature(typeElement.meta.nameResolver, typeElement.meta.classProto.typeTable) == this.jvmMethodSignature
    }?.let {
      val declaredType = typeUtils.getDeclaredType(typeElement)
      it.isPrimary to FunSpec
        .overriding(this, declaredType, typeUtils).build()
        .toMeta(this)
        .copy(name = "constructor")
    }

  private fun proto.Visibility.asModifier(): Modifier? =
    when (this) {
      proto.Visibility.INTERNAL -> Modifier.Internal
      proto.Visibility.PRIVATE -> Modifier.Private
      proto.Visibility.PROTECTED -> Modifier.Protected
      proto.Visibility.PUBLIC -> Modifier.Public
      proto.Visibility.PRIVATE_TO_THIS -> Modifier.Private
      proto.Visibility.LOCAL -> null
    }

  private fun proto.Modality.asModifier(): Modifier =
    when (this) {
      proto.Modality.FINAL -> Modifier.Final
      proto.Modality.OPEN -> Modifier.Open
      proto.Modality.ABSTRACT -> Modifier.Abstract
      proto.Modality.SEALED -> Modifier.Sealed
    }
}