package arrow.meta.processor

import arrow.common.utils.*
import arrow.core.*
import arrow.derive.normalizeType
import arrow.meta.ast.*
import arrow.meta.ast.Annotation
import arrow.meta.ast.Modifier
import arrow.meta.ast.TypeName
import arrow.meta.encoder.MetaEncoder
import javax.lang.model.element.Modifier as ElModifier
import com.squareup.kotlinpoet.*
import me.eugeniomarletti.kotlin.metadata.*
import me.eugeniomarletti.kotlin.metadata.jvm.getJvmConstructorSignature
import me.eugeniomarletti.kotlin.metadata.jvm.getJvmFieldSignature
import me.eugeniomarletti.kotlin.metadata.jvm.getJvmMethodSignature
import me.eugeniomarletti.kotlin.metadata.jvm.jvmPropertySignature
import me.eugeniomarletti.kotlin.metadata.shadow.metadata.ProtoBuf
import me.eugeniomarletti.kotlin.metadata.shadow.metadata.deserialization.NameResolver
import me.eugeniomarletti.kotlin.metadata.shadow.metadata.deserialization.TypeTable
import me.eugeniomarletti.kotlin.metadata.shadow.metadata.deserialization.hasReceiver
import java.lang.IllegalArgumentException
import javax.lang.model.element.*
import javax.lang.model.util.ElementFilter

interface MetaProcessorUtils : ProcessorUtils {

  val TypeElement.meta: ClassOrPackageDataWrapper.Class

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

  fun TypeElement.declaredFunctions(): List<Func> {
    val declaredFunctionSignatures = meta.functionList.map { it.getJvmMethodSignature(meta.nameResolver, meta.classProto.typeTable) }
    return allFunctions().filter {
      declaredFunctionSignatures.contains(it.jvmMethodSignature)
    }
  }

  fun TypeElement.allFunctions(): List<Func> {
    val superTypes = supertypes(meta, TypeTable(meta.classProto.typeTable), emptyList())
      .filterIsInstance(ClassOrPackageDataWrapper.Class::class.java)
    val declaredFunctions = meta.functionList.map { meta to it }
    val inheritedFunctions = superTypes.flatMap { s -> s.functionList.map { s to it } }
    val allFunctions = declaredFunctions + inheritedFunctions
    val allMembers = ElementFilter.methodsIn(elementUtils.getAllMembers(this))
    val declaredType = typeUtils.getDeclaredType(this)
    return allMembers.filterNot {
      it.modifiers.containsAll(listOf(javax.lang.model.element.Modifier.PRIVATE, javax.lang.model.element.Modifier.FINAL))
    }.filterNot {
      it.modifiers.containsAll(listOf(javax.lang.model.element.Modifier.PUBLIC, javax.lang.model.element.Modifier.FINAL))
    }.map { member ->
      val templateFunction: Pair<ClassOrPackageDataWrapper.Class, ProtoBuf.Function>? = allFunctions.find { (proto, function) ->
        function.getJvmMethodSignature(proto.nameResolver, proto.classProto.typeTable) == member.jvmMethodSignature
      }
      val function = FunSpec.overriding(member, declaredType, typeUtils).build().toMeta(member, templateFunction)
      val result =
        if (templateFunction != null && templateFunction.second.hasReceiverType()) {
          val receiverTypeName = function.parameters[0].type
          val functionWithReceiver = function.copy(receiverType = receiverTypeName)
          val arguments = functionWithReceiver.parameters.drop(1)
          functionWithReceiver.copy(parameters = arguments)
        } else function
      result
    }
  }

  fun TypeName.TypeVariable.removeConstrains(): TypeName.TypeVariable =
    copy(
      bounds = bounds.mapNotNull { if (it is TypeName.Classy && it.fqName == "java.lang.Object") null else it.removeConstrains() },
      variance = null
    )

  fun TypeName.WildcardType.removeConstrains(): TypeName.WildcardType =
    copy(
      upperBounds = upperBounds.map { it.removeConstrains() },
      lowerBounds = lowerBounds.map { it.removeConstrains() }
    )

  fun TypeName.ParameterizedType.removeConstrains(): TypeName.ParameterizedType =
    copy(
      enclosingType = enclosingType?.removeConstrains(),
      rawType = rawType.removeConstrains(),
      typeArguments = typeArguments.map { it.removeConstrains() }
    )

  fun TypeName.Classy.removeConstrains(): TypeName.Classy = this

  fun TypeName.removeConstrains(): TypeName =
    when (this) {
      is TypeName.TypeVariable -> removeConstrains()
      is TypeName.WildcardType -> removeConstrains()
      is TypeName.ParameterizedType -> removeConstrains()
      is TypeName.Classy -> removeConstrains()
    }

  fun Parameter.removeConstrains(): Parameter =
    copy(
      modifiers = emptyList(),
      type = type.removeConstrains()
    )

  fun Parameter.downKind(): Parameter =
    copy(type = type.downKind())

  fun Parameter.defaultDummyArgValue(): Parameter =
    copy(defaultValue = when {
      type.simpleName == "Unit" -> Code("Unit")
      else -> null
    })

  fun Func.downKindParameters(): Func =
    copy(parameters = parameters.map { it.downKind() })

  fun Func.defaultDummyArgValues(): Func =
    copy(parameters = parameters.map { it.defaultDummyArgValue() })

  fun Func.addExtraDummyArg(): Func {
    val dummyArg: Parameter = Parameter("arg${parameters.size + 1}", TypeName.Unit).defaultDummyArgValue()
    return copy(parameters = parameters + listOf(dummyArg))
  }

  fun Func.removeConstrains(): Func =
    copy(
      modifiers = emptyList(),
      annotations = emptyList(),
      receiverType = receiverType?.removeConstrains(),
      returnType = returnType?.removeConstrains(),
      parameters = parameters.map { it.removeConstrains() },
      typeVariables = typeVariables.map { it.removeConstrains() }
    )

  fun TypeElement.superInterfaces(): List<TypeName> =
    meta.classProto.supertypeList.map { type ->
      val superTypeName = type.extractFullName(meta, true).normalizeType()
      val pckg = superTypeName.substringBefore("<").substringBeforeLast(".")
      val simpleName = superTypeName.substringBefore("<").substringAfterLast(".")
      if (type.argumentList.isNotEmpty())
        TypeName.ParameterizedType(
          name = superTypeName.removeVariance(),
          enclosingType = asTypeName(),
          nullable = false,
          typeArguments = type.argumentList.map {
            TypeName.TypeVariable(name = it.type.extractFullName(meta).normalizeType())
          },
          rawType = TypeName.Classy(simpleName, "$pckg.$simpleName", PackageName(pckg), nullable = false)
        )
      else TypeName.Classy(simpleName, superTypeName, PackageName(pckg), nullable = false)
    }

  fun TypeElement.typeVariables(): List<TypeName.TypeVariable> =
    typeParameters.map { it.asTypeVariableName().toMeta() }

  fun TypeElement.modifiers(): List<Modifier> =
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
    typeElement.meta.constructorList.find {
      it.getJvmConstructorSignature(typeElement.meta.nameResolver, typeElement.meta.classProto.typeTable) == this.jvmMethodSignature
    }?.let {
      it.isPrimary to
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

  private fun VariableElement.toMeta(): Parameter =
    Parameter(
      name = simpleName.toString(),
      type = asType().asTypeName().toMeta()
    )

  private fun ElModifier.toMeta(): Modifier? =
    when (this) {
      ElModifier.PUBLIC -> Modifier.Public
      ElModifier.PROTECTED -> Modifier.Protected
      ElModifier.PRIVATE -> Modifier.Private
      ElModifier.ABSTRACT -> Modifier.Abstract
      ElModifier.DEFAULT -> null
      ElModifier.STATIC -> null
      ElModifier.FINAL -> Modifier.Final
      ElModifier.TRANSIENT -> null
      ElModifier.VOLATILE -> null
      ElModifier.SYNCHRONIZED -> null
      ElModifier.NATIVE -> null
      ElModifier.STRICTFP -> null
    }

  fun TypeElement.sealedSubClassNames(): List<TypeName> =
    if (meta.classProto.sealedSubclassFqNameList.isNotEmpty())
      meta.classProto.sealedSubclassFqNameList.map {
        val fqName = meta.nameOf(it).normalizeType()
        ClassName.bestGuess(fqName).toMeta()
      }
    else emptyList()

  private fun ClassOrPackageDataWrapper.Class.nameOf(id: Int): String =
    nameResolver.getString(id)

  private fun ProtoBuf.Type.extractFullName(
    nameResolver: NameResolver,
    getTypeParameter: (index: Int) -> ProtoBuf.TypeParameter?,
    outputTypeAlias: Boolean = true,
    throwOnGeneric: Throwable? = null
  ): String {

    if (!hasClassName() && throwOnGeneric != null) throw throwOnGeneric

    val typeParam = getTypeParameter(typeParameter)

    val name = when {
      hasTypeParameter() && typeParam != null -> typeParam.name
      hasTypeParameterName() -> typeParameterName
      outputTypeAlias && hasAbbreviatedType() -> abbreviatedType.typeAliasName
      else -> className
    }.let { nameResolver.getString(it).escapedClassName }

    val argumentList = when {
      outputTypeAlias && hasAbbreviatedType() -> abbreviatedType.argumentList
      else -> argumentList
    }
    val arguments = argumentList
      .takeIf { it.isNotEmpty() }
      ?.joinToString(prefix = "<", postfix = ">") {
        when {
          it.hasType() -> it.type.extractFullName(nameResolver, getTypeParameter, outputTypeAlias, throwOnGeneric)
          throwOnGeneric != null -> throw throwOnGeneric
          else -> "*"
        }
      }
      ?: ""

    val nullability = if (nullable) "?" else ""

    return name + arguments + nullability
  }

  private fun ProtoBuf.Type.extractFullName(
    classData: ClassOrPackageDataWrapper,
    outputTypeAlias: Boolean = true
  ): String =
    extractFullName(
      nameResolver = classData.nameResolver,
      getTypeParameter = { classData.getTypeParameter(it) },
      outputTypeAlias = outputTypeAlias,
      throwOnGeneric = null
    )

  private fun ProtoBuf.Type.asTypeName(meta: ClassOrPackageDataWrapper.Class): TypeName {
    val fullName = extractFullName(meta).normalizeType()
    val pck = fullName.substringBefore("<").substringBeforeLast(".")
    val simpleName = fullName.substringBefore("<").substringAfterLast(".")
    return if (argumentList.isEmpty()) TypeName.Classy(
      simpleName = simpleName,
      fqName = fullName,
      pckg = PackageName(pck),
      nullable = nullable
    )
    else TypeName.ParameterizedType(
      name = fullName.removeVariance(),
      rawType = TypeName.Classy(simpleName, "$pck.$simpleName", PackageName(pck)),
      typeArguments = argumentList.map { TypeName.TypeVariable(it.type.extractFullName(meta).normalizeType()) },
      enclosingType = TypeName.Classy(
        simpleName = meta.simpleName,
        fqName = meta.fullName.normalizeType(),
        pckg = PackageName(meta.`package`),
        nullable = false
      )
    )
  }

  private fun String.removeVariance(): String =
    replace("out ", "").replace("in ", "")

  private fun ProtoBuf.Visibility.asModifier(): Modifier? =
    when (this) {
      ProtoBuf.Visibility.INTERNAL -> Modifier.Internal
      ProtoBuf.Visibility.PRIVATE -> Modifier.Private
      ProtoBuf.Visibility.PROTECTED -> Modifier.Protected
      ProtoBuf.Visibility.PUBLIC -> Modifier.Public
      ProtoBuf.Visibility.PRIVATE_TO_THIS -> Modifier.Private
      ProtoBuf.Visibility.LOCAL -> null
    }

  private fun ProtoBuf.Modality.asModifier(): Modifier =
    when (this) {
      ProtoBuf.Modality.FINAL -> Modifier.Final
      ProtoBuf.Modality.OPEN -> Modifier.Open
      ProtoBuf.Modality.ABSTRACT -> Modifier.Abstract
      ProtoBuf.Modality.SEALED -> Modifier.Sealed
    }

  private fun com.squareup.kotlinpoet.CodeBlock.toMeta(): Code =
    Code(this.toString())

  private fun com.squareup.kotlinpoet.AnnotationSpec.UseSiteTarget.toMeta(): UseSiteTarget =
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

  fun ProtoBuf.ValueParameter.defaultInitValue(meta: ClassOrPackageDataWrapper.Class): Code {
    val tpe = type.asTypeName(meta)
    return when (tpe) {
      is TypeName.TypeVariable -> Code("= TODO()")
      is TypeName.WildcardType -> Code("= TODO()")
      is TypeName.ParameterizedType -> Code("= TODO()")
      is TypeName.Classy -> {
        Code("= ${tpe.simpleName}")
      }
    }
  }

  private fun com.squareup.kotlinpoet.FunSpec.toMeta(
    element: ExecutableElement,
    templateFunction: Pair<ClassOrPackageDataWrapper.Class, ProtoBuf.Function>?): Func {
    val params = parameters
    return Func(
      name = element.simpleName.toString(),
      annotations = annotations.map { it.toMeta() },
      typeVariables = typeVariables.map { it.toMeta() },
      modifiers = modifiers.map { it.toMeta() },
      returnType = returnType?.toMeta()?.downKind(),
      receiverType = receiverType?.toMeta(),
      kdoc = kdoc.toMeta(),
      body = body.toMeta(),
      parameters = parameters.map { it.toMeta() },
      jvmMethodSignature = element.jvmMethodSignature
    )
  }

  private fun com.squareup.kotlinpoet.AnnotationSpec.toMeta(): Annotation =
    Annotation(type.toMeta(), members = members.map { it.toMeta() }, useSiteTarget = useSiteTarget?.toMeta())

  private fun com.squareup.kotlinpoet.WildcardTypeName.toMeta(): TypeName.WildcardType =
    TypeName.WildcardType(
      toString().replace("out ", "").replace("in ", ""),
      upperBounds.map { it.toMeta() },
      lowerBounds = lowerBounds.map { it.toMeta() },
      nullable = nullable,
      annotations = annotations.map { it.toMeta() }
    )

  private fun com.squareup.kotlinpoet.ClassName.toMeta(): TypeName.Classy =
    TypeName.Classy(
      simpleName = simpleName,
      fqName = canonicalName,
      annotations = annotations.map { it.toMeta() },
      nullable = nullable,
      pckg = PackageName(packageName)
    )

  private fun com.squareup.kotlinpoet.ParameterizedTypeName.toMeta(): TypeName =
    TypeName.ParameterizedType(
      name = toString().removeVariance(),
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
      KModifier.COMPANION -> Modifier.CompanionObject
      KModifier.INLINE -> Modifier.Inline
      KModifier.NOINLINE -> Modifier.NoInline
      KModifier.CROSSINLINE -> Modifier.CrossInline
      KModifier.REIFIED -> Modifier.Reified
      KModifier.INFIX -> Modifier.Infix
      KModifier.OPERATOR -> Modifier.Operator
      KModifier.DATA -> Modifier.Data
      KModifier.IN -> Modifier.InVariance
      KModifier.OUT -> Modifier.OutVariance
    }

  private fun com.squareup.kotlinpoet.TypeVariableName.toMeta(): TypeName.TypeVariable =
    TypeName.TypeVariable(
      name = name,
      bounds = bounds.map { it.toMeta().removeConstrains() },
      annotations = annotations.map { it.toMeta() },
      nullable = nullable,
      reified = reified,
      variance = variance?.toMeta()
    )

  fun com.squareup.kotlinpoet.TypeName.toMeta(): TypeName =
    when (this) {
      is WildcardTypeName -> toMeta()
      is ClassName -> toMeta()
      is ParameterizedTypeName -> toMeta()
      is TypeVariableName -> toMeta()
      else -> throw IllegalArgumentException("Unsupported type name: $this")
    }

  private fun getTypeElement(name: String): Option<TypeElement> =
    Try { elementUtils.getTypeElement(name) }.fold({ None }) { el -> if (el == null) None else el.some() }

  tailrec fun TypeName.asType(encoder: MetaEncoder<Type>): Type? =
    when (this) {
      is TypeName.TypeVariable -> getTypeElement(name).map { it.asMetaType(encoder) }.orNull()
      is TypeName.WildcardType -> null
      is TypeName.ParameterizedType -> rawType.asType(encoder)
      is TypeName.Classy -> getTypeElement(fqName).map { it.asMetaType(encoder) }.orNull()
    }

  fun TypeElement.asMetaType(encoder: MetaEncoder<Type>): Type? =
    encoder.encode(this).fold({ null }, { it })

  private fun String.downKind(): Tuple2<String, String> {
    val rawTypeName = substringBefore("<")
    val pckg = rawTypeName.substringBeforeLast(".")
    val simpleName = rawTypeName.substringAfterLast(".")
    val unAppliedName =
      if (simpleName.startsWith("For")) simpleName.drop("For".length)
      else simpleName
    return if (unAppliedName.endsWith("PartialOf")) pckg toT unAppliedName.substringBeforeLast("PartialOf")
    else pckg toT unAppliedName
  }

  val Code.Companion.TODO: Code
    get() = Code("return TODO()")

  fun TypeName.TypeVariable.downKind(): TypeName.TypeVariable =
    this

  fun TypeName.ParameterizedType.downKind(): TypeName.ParameterizedType =
    if (rawType.fqName == "arrow.Kind" && typeArguments.size >= 2) {
      val witness = typeArguments[0].downKind()
      val tail = typeArguments.drop(1)
      if (witness is TypeName.Classy)
        copy(name = witness.fqName, rawType = witness, typeArguments = tail.map { it.downKind() })
      else this
    } else this

  fun TypeName.WildcardType.downKind(): TypeName.WildcardType =
    name.downKind().let { (pckg, unPrefixedName) ->
      copy(name = "$pckg.$unPrefixedName")
    }

  fun TypeName.Classy.downKind(): TypeName.Classy =
    fqName.downKind().let { (pckg, unPrefixedName) ->
      copy(simpleName = unPrefixedName, fqName = "$pckg.$unPrefixedName")
    }

  fun TypeName.downKind(): TypeName =
    when (this) {
      is TypeName.TypeVariable -> downKind()
      is TypeName.WildcardType -> downKind()
      is TypeName.ParameterizedType -> downKind()
      is TypeName.Classy -> downKind()
    }

  data class TypeClassInstance(
    val instance: Type,
    val dataType: Type,
    val typeClass: Type,
    val instanceTypeElement: TypeElement,
    val dataTypeTypeElement: TypeElement,
    val typeClassTypeElement: TypeElement
  )

  fun TypeElement.typeClassInstance(typeEncoder: MetaEncoder<Type>): TypeClassInstance? {
    val superInterfaces = superInterfaces()
    val instance = asMetaType(typeEncoder)
    return if (instance != null && superInterfaces.isNotEmpty()) {
      val typeClassTypeName = superInterfaces[0]
      val typeClass = typeClassTypeName.asType(typeEncoder)
      if (typeClass != null && typeClassTypeName is TypeName.ParameterizedType && typeClassTypeName.typeArguments.isNotEmpty()) {
        val dataTypeName = typeClassTypeName.typeArguments[0]
        val dataTypeDownKinded = dataTypeName.downKind()
        val dataType = dataTypeDownKinded.asType(typeEncoder)
        if (dataType != null && dataTypeDownKinded is TypeName.TypeVariable) TypeClassInstance(
          instance = instance,
          dataType = dataType,
          typeClass = typeClass,
          instanceTypeElement = this,
          dataTypeTypeElement = elementUtils.getTypeElement(dataTypeDownKinded.name),
          typeClassTypeElement = elementUtils.getTypeElement(typeClassTypeName.rawType.fqName)
        )
        else null
      } else null
    } else null
  }

}
