package arrow.meta.processor

import arrow.common.utils.*
import arrow.meta.ast.*
import arrow.meta.ast.Annotation
import arrow.meta.ast.Modifier
import arrow.meta.ast.TypeName
import arrow.meta.encoder.MetaEncoder
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
import shadow.core.None
import shadow.core.Some
import shadow.core.Try
import shadow.core.Tuple2
import javax.lang.model.element.*
import javax.lang.model.util.ElementFilter
import kotlin.reflect.KClass
import javax.lang.model.element.Modifier as ElModifier

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

  fun TypeElement.declaredFunctions(declaredElement: TypeElement): List<Func> {
    val declaredFunctionSignatures = meta.functionList.map { it.getJvmMethodSignature(meta.nameResolver, meta.classProto.typeTable) }
    return allFunctions(declaredElement).filter {
      declaredFunctionSignatures.contains(it.jvmMethodSignature)
    }
  }

  fun String.asKotlin(): String =
    removeBackticks()
      .replace("/", ".")
      .replace("kotlin.jvm.functions", "kotlin")
      .replace("java.util.Collection", "kotlin.collections.Collection")
      .replace("java.lang.Throwable", "kotlin.Throwable").let {
        if (it == "java.lang") it.replace("java.lang", "kotlin")
        else it
      }.let {
        if (it == "java.util") it.replace("java.util", "kotlin.collections")
        else it
      }.replace("Integer", "Int")

  fun TypeElement.allFunctions(declaredElement: TypeElement): List<Func> {
    val superTypes = supertypes(declaredElement.meta, TypeTable(declaredElement.meta.classProto.typeTable), emptyList())
      .filterIsInstance(ClassOrPackageDataWrapper.Class::class.java)
    val declaredFunctions = declaredElement.meta.functionList.map { meta to it }
    val inheritedFunctions = superTypes.flatMap { s -> s.functionList.map { s to it } }
    val allFunctions = declaredFunctions + inheritedFunctions
    val allMembers = ElementFilter.methodsIn(elementUtils.getAllMembers(declaredElement))
    val declaredType = typeUtils.getDeclaredType(declaredElement, *declaredElement.typeParameters.map { it.asType() }.toTypedArray())
    return allMembers.asSequence().filterNot {
      it.modifiers.containsAll(listOf(javax.lang.model.element.Modifier.PRIVATE, javax.lang.model.element.Modifier.FINAL))
    }.filterNot {
      it.modifiers.containsAll(listOf(javax.lang.model.element.Modifier.PUBLIC, javax.lang.model.element.Modifier.FINAL))
    }.map { member ->
      val templateFunction: Pair<ClassOrPackageDataWrapper.Class, ProtoBuf.Function>? = allFunctions.find { (proto, function) ->
        function.getJvmMethodSignature(proto.nameResolver, proto.classProto.typeTable) == member.jvmMethodSignature
      }
      val function = FunSpec.overriding(member, declaredType, typeUtils).build().toMeta(member)
      val result =
        if (templateFunction != null && templateFunction.second.modality != null) {
          val fMod = function.copy(modifiers = function.modifiers + listOfNotNull(templateFunction.second.modality?.toMeta()))
          if (templateFunction.second.hasReceiverType()) {
            val receiverTypeName = fMod.parameters[0].type.asKotlin()
            val functionWithReceiver = fMod.copy(receiverType = receiverTypeName)
            val arguments = functionWithReceiver.parameters.drop(1)
            functionWithReceiver.copy(parameters = arguments)
          } else fMod
        } else function
      result
    }.toList()
  }

  private fun ProtoBuf.Modality.toMeta(): Modifier =
    when (this) {
      ProtoBuf.Modality.FINAL -> Modifier.Final
      ProtoBuf.Modality.OPEN -> Modifier.Open
      ProtoBuf.Modality.ABSTRACT -> Modifier.Abstract
      ProtoBuf.Modality.SEALED -> Modifier.Sealed
    }

  fun TypeName.TypeVariable.removeConstrains(): TypeName.TypeVariable =
    copy(
      bounds = bounds.mapNotNull { if (it is TypeName.Classy && it.fqName == "java.lang.Object") null else it },
      variance = null
    )

  fun TypeName.TypeVariable.disambiguate(existing: List<TypeName.TypeVariable>, prefix: String = "_"): TypeName.TypeVariable =
    if (existing.asSequence().map { it.name }.contains(name)) copy(
      name = "$prefix$name"
    ) else this

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

  fun Func.downKindReceiver(): Func =
    copy(receiverType = receiverType?.downKind())

  fun Func.downKindReturnType(): Func =
    copy(returnType = returnType?.downKind())

  fun Func.defaultDummyArgValues(): Func =
    copy(parameters = parameters.map { it.defaultDummyArgValue() })

  fun Func.addExtraDummyArg(): Func {
    val dummyArg: Parameter = Parameter("arg${parameters.size + 1}", TypeName.Unit).defaultDummyArgValue()
    return copy(parameters = parameters + listOf(dummyArg))
  }

  fun Func.removeDummyArgs(): Func =
    copy(parameters = parameters.filterNot { it.type.simpleName == "Unit" })

  fun Func.countDummyArgs(): Int =
    parameters.count { it.type.simpleName == "Unit" }

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
      type = asType().asTypeName().toMeta().asKotlin(),
      modifiers = modifiers.mapNotNull { it.toMeta() }
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
        val fqName = meta.nameOf(it).asKotlin()
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
    val fullName = extractFullName(meta).asKotlin()
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
      rawType = TypeName.Classy(simpleName, "$pck.$simpleName".asKotlin(), PackageName(pck.asKotlin())),
      typeArguments = argumentList.map { TypeName.TypeVariable(it.type.extractFullName(meta).asKotlin()) },
      enclosingType = TypeName.Classy(
        simpleName = meta.simpleName.asKotlin(),
        fqName = meta.fullName.asKotlin(),
        pckg = PackageName(meta.`package`.asKotlin()),
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
      type = type.toMeta().asKotlin(),
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

  private fun com.squareup.kotlinpoet.FunSpec.toMeta(element: ExecutableElement): Func =
    Func(
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
      name = toString().removeVariance().asKotlin(),
      upperBounds = upperBounds.map { it.toMeta() },
      lowerBounds = lowerBounds.map { it.toMeta() },
      nullable = nullable,
      annotations = annotations.map { it.toMeta() }
    )

  private fun com.squareup.kotlinpoet.ClassName.toMeta(): TypeName.Classy =
    TypeName.Classy(
      simpleName = simpleName,
      fqName = canonicalName.asKotlin(),
      annotations = annotations.map { it.toMeta() },
      nullable = nullable,
      pckg = PackageName(packageName.asKotlin())
    )

  private fun com.squareup.kotlinpoet.ParameterizedTypeName.toMeta(): TypeName =
    TypeName.ParameterizedType(
      name = toString().removeVariance().asKotlin(),
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
      name = name.asKotlin(),
      bounds = bounds.map
      { it.toMeta().removeConstrains() },
      annotations = annotations.map
      { it.toMeta() },
      nullable = nullable,
      reified = reified,
      variance = variance?.toMeta()
    )

  fun trace(): Unit = Unit

  val typeNameToMeta: (typeName: com.squareup.kotlinpoet.TypeName) -> TypeName

  fun com.squareup.kotlinpoet.TypeName.toMeta(): TypeName =
    typeNameToMeta(this)

  fun typeNameToMetaImpl(typeName: com.squareup.kotlinpoet.TypeName): TypeName {
    trace()
    return when (typeName) {
      is ClassName -> typeName.toMeta()
      is ParameterizedTypeName -> typeName.toMeta()
      is TypeVariableName -> typeName.toMeta()
      is WildcardTypeName -> typeName.toMeta()
      else -> throw IllegalArgumentException("arrow-meta has no bindings for unsupported type name: $this")
    }
  }

  private fun getTypeElement(name: String): shadow.core.Option<TypeElement> =
    Try { elementUtils.getTypeElement(name) }.fold({ None }) { el -> if (el == null) None else Some(el) }

  tailrec fun TypeName.asType(encoder: MetaEncoder<Type>): Type? =
    when (this) {
      is TypeName.TypeVariable -> getTypeElement(name).map { it.asMetaType(encoder) }.orNull()
      is TypeName.WildcardType -> null
      is TypeName.ParameterizedType -> rawType.asType(encoder)
      is TypeName.Classy -> getTypeElement(fqName).map { it.asMetaType(encoder) }.orNull()
    }

  fun TypeElement.asMetaType(encoder: MetaEncoder<Type>): Type? =
    encoder.encode(this).fold({ null }, { it })

  private fun String.asClassy(): TypeName.Classy {
    val seed = toString().asKotlin()
    val rawTypeName = seed.substringBefore("<")
    val pckg = if (rawTypeName.contains(".")) rawTypeName.substringBeforeLast(".") else ""
    val simpleName = rawTypeName.substringAfterLast(".")
    return TypeName.Classy(simpleName, rawTypeName, PackageName(pckg))
  }

  private fun String.downKind(): Tuple2<String, String> =
    run {
      val classy = asClassy()
      val kindedClassy = when {
        classy.fqName == "arrow.Kind" -> {
          val result = substringAfter("arrow.Kind<").substringBefore(",").asClassy()
          if (result.fqName == "arrow.typeclasses.ForConst") result
          else classy
        }
        else -> classy
      }
      val unAppliedName =
        if (kindedClassy.simpleName.startsWith("For")) kindedClassy.simpleName.drop("For".length)
        else kindedClassy.simpleName
      when {
        unAppliedName.endsWith("PartialOf") -> Tuple2(kindedClassy.pckg.value, unAppliedName.substringBeforeLast("PartialOf"))
        unAppliedName.endsWith("Of") -> Tuple2(kindedClassy.pckg.value, unAppliedName.substringBeforeLast("Of"))
        else -> Tuple2(kindedClassy.pckg.value, unAppliedName)
      }
    }

  val Code.Companion.TODO: Code
    get() = Code("return TODO()")

  fun TypeName.TypeVariable.downKind(): TypeName.TypeVariable =
    name.downKind().let { (pckg, unPrefixedName) ->
      if (pckg.isBlank()) this else copy(name = "$pckg.$unPrefixedName")
    }

  fun TypeName.ParameterizedType.isKinded(): Boolean =
    typeArguments.isNotEmpty() &&
      !typeArguments[0].simpleName.startsWith("Conested") &&
      rawType.fqName == "arrow.Kind" &&
      typeArguments.size == 2 &&
      (typeArguments[0] !is TypeName.TypeVariable)

  fun TypeName.TypeVariable.nestedTypeVariables(): List<TypeName> =
    listOf(this)

  fun TypeName.WildcardType.nestedTypeVariables(): List<TypeName> =
    upperBounds.flatMap { it.nestedTypeVariables() }

  fun TypeName.ParameterizedType.nestedTypeVariables(): List<TypeName> =
    typeArguments.flatMap { it.nestedTypeVariables() }

  fun TypeName.Classy.nestedTypeVariables(): List<TypeName> =
    emptyList()

  fun TypeName.nestedTypeVariables(): List<TypeName> =
    when (this) {
      is TypeName.TypeVariable -> nestedTypeVariables()
      is TypeName.WildcardType -> nestedTypeVariables()
      is TypeName.ParameterizedType -> nestedTypeVariables()
      is TypeName.Classy -> nestedTypeVariables()
    }

  fun TypeName.ParameterizedType.downKind(): TypeName.ParameterizedType =
    if (isKinded()) {
      val witness = typeArguments[0].downKind()
      val tail = when (witness) {
        is TypeName.ParameterizedType -> typeArguments.drop(1) + witness.typeArguments
        is TypeName.WildcardType -> {
          if (witness.name == "arrow.typeclasses.Const") {
            val head = typeArguments[0]
            val missingTypeArgs = typeArguments.drop(1)
            head.nestedTypeVariables() + missingTypeArgs
          } else typeArguments.drop(1)
        }
        else -> typeArguments.drop(1)
      }.map { it.downKind() }
      val fullName = when (witness) {
        is TypeName.TypeVariable -> witness.name
        is TypeName.WildcardType -> witness.name
        is TypeName.ParameterizedType -> witness.name
        is TypeName.Classy -> witness.fqName
      }
      copy(name = fullName, rawType = fullName.asClassy(), typeArguments = tail)
    } else this

  fun Func.containsModifier(modifier: Modifier): Boolean =
    modifiers.contains(modifier)

  fun TypeName.WildcardType.downKind(): TypeName.WildcardType =
    name.downKind().let { (pckg, unPrefixedName) ->
      copy(
        name = "$pckg.$unPrefixedName",
        lowerBounds = lowerBounds.map { it.downKind() },
        upperBounds = upperBounds.map { it.downKind() }
      )
    }

  fun TypeName.Classy.downKind(): TypeName.Classy =
    fqName.downKind().let { (pckg, unPrefixedName) ->
      copy(simpleName = unPrefixedName, fqName = "$pckg.$unPrefixedName")
    }

  fun typeNameDownKindImpl(typeName: TypeName): TypeName =
    when (typeName) {
      is TypeName.TypeVariable -> typeName.downKind().asKotlin()
      is TypeName.WildcardType -> typeName.downKind().asKotlin()
      is TypeName.ParameterizedType -> typeName.downKind().asKotlin()
      is TypeName.Classy -> typeName.downKind().asKotlin()
    }

  val typeNameDownKind: (typeName: TypeName) -> TypeName

  fun TypeName.downKind(): TypeName =
    typeNameDownKind(this)

  fun TypeName.asKotlin(): TypeName =
    when (this) {
      is TypeName.TypeVariable -> asKotlin()
      is TypeName.WildcardType -> asKotlin()
      is TypeName.ParameterizedType -> asKotlin()
      is TypeName.Classy -> asKotlin()
    }

  fun TypeName.TypeVariable.asKotlin(): TypeName.TypeVariable =
    copy(name = name.asKotlin())

  fun TypeName.ParameterizedType.asKotlin(): TypeName.ParameterizedType =
    copy(name = name.asKotlin(), rawType = rawType.asKotlin(), typeArguments = typeArguments.map { it.asKotlin() })

  fun TypeName.WildcardType.asKotlin(): TypeName.WildcardType =
    copy(
      name = name.asKotlin(),
      upperBounds = upperBounds.map { it.asKotlin() },
      lowerBounds = lowerBounds.map { it.asKotlin() }
    )

  fun TypeName.Classy.asKotlin(): TypeName.Classy =
    copy(simpleName = simpleName.asKotlin(), fqName = fqName.asKotlin(), pckg = PackageName(pckg.value.asKotlin()))

  data class TypeClassInstance(
    val instance: Type,
    val dataType: Type,
    val typeClass: Type,
    val instanceTypeElement: TypeElement,
    val dataTypeTypeElement: TypeElement,
    val typeClassTypeElement: TypeElement,
    val projectedCompanion: TypeName
  )

  val TypeClassInstance.requiredAbstractFunctions: List<Func>
    get() = instance.declaredFunctions
      .filter { it.containsModifier(Modifier.Abstract) }
      .map {
        it.copy(
          modifiers = it.modifiers - Modifier.Abstract,
          body = Code("return ${it.name}")
        )
      }

  val TypeClassInstance.requiredParameters: List<Parameter>
    get() = requiredAbstractFunctions.mapNotNull {
      if (it.returnType != null) Parameter(it.name, it.returnType.asKotlin()) else null
    }

  fun <A : Any> TypeName.Companion.typeNameOf(clazz: KClass<A>): TypeName =
    TypeName.Classy(
      simpleName = clazz.java.simpleName,
      fqName = clazz.java.name,
      pckg = PackageName(clazz.java.`package`.name)
    )

  fun JvmName(name: String): Annotation =
    Annotation(
      type = TypeName.typeNameOf(JvmName::class),
      members = listOf(Code(""""$name"""")),
      useSiteTarget = null
    )

  fun SuppressAnnotation(vararg names: String): Annotation =
    Annotation(
      type = TypeName.typeNameOf(Suppress::class),
      members = names.map { Code(it) },
      useSiteTarget = null
    )

  fun TypeName.projectedCompanion(): TypeName {
    val dataTypeDownKinded = downKind()
    return when {
      this is TypeName.TypeVariable &&
        (dataTypeDownKinded.simpleName == "arrow.Kind" ||
          dataTypeDownKinded.simpleName == "arrow.typeclasses.Conested") -> {
        simpleName
          .substringAfterLast("arrow.Kind<")
          .substringAfterLast("arrow.typeclasses.Conested<")
          .substringBefore(",")
          .substringBefore("<")
          .downKind().let { (pckg, simpleName) ->
            TypeName.Classy.from(pckg, simpleName)
          }
      }
      dataTypeDownKinded is TypeName.Classy ->
        dataTypeDownKinded.copy(simpleName = simpleName.substringAfterLast("."))
      else -> dataTypeDownKinded
    }
  }

  fun TypeElement.typeClassInstance(typeEncoder: MetaEncoder<Type>): TypeClassInstance? {
    val superInterfaces = superInterfaces()
    val instance = asMetaType(typeEncoder)
    return if (instance != null && superInterfaces.isNotEmpty()) {
      val typeClassTypeName = superInterfaces[0]
      val typeClass = typeClassTypeName.asType(typeEncoder)
      if (typeClass != null && typeClassTypeName is TypeName.ParameterizedType && typeClassTypeName.typeArguments.isNotEmpty()) {
        val dataTypeName = typeClassTypeName.typeArguments[0]
        //profunctor and other cases are parametric to Kind2 values or Conested
        val projectedCompanion = dataTypeName.projectedCompanion()
        val dataTypeDownKinded = dataTypeName.downKind()
        val dataType = dataTypeDownKinded.asType(typeEncoder)
        if (dataType != null && dataTypeDownKinded is TypeName.TypeVariable) TypeClassInstance(
          instance = instance,
          dataType = dataType,
          typeClass = typeClass,
          instanceTypeElement = this@typeClassInstance,
          dataTypeTypeElement = elementUtils.getTypeElement(dataTypeDownKinded.name),
          typeClassTypeElement = elementUtils.getTypeElement(typeClassTypeName.rawType.fqName),
          projectedCompanion = projectedCompanion
        )
        else null
      } else null
    } else null
  }

}
