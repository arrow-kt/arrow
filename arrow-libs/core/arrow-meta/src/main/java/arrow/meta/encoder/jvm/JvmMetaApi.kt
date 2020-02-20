package arrow.meta.encoder.jvm

import arrow.common.utils.ProcessorUtils
import arrow.meta.ast.Annotation
import arrow.meta.ast.Code
import arrow.meta.ast.Func
import arrow.meta.ast.Modifier
import arrow.meta.ast.PackageName
import arrow.meta.ast.Parameter
import arrow.meta.ast.Type
import arrow.meta.ast.TypeName
import arrow.meta.decoder.TypeDecoder
import arrow.meta.encoder.MetaApi
import arrow.meta.encoder.TypeClassInstance
import javax.lang.model.element.TypeElement
import kotlin.reflect.KClass

/**
 * A JVM implementation of the Meta Api meant to be mixed in with kapt annotation processors
 */
interface JvmMetaApi : MetaApi, TypeElementEncoder, ProcessorUtils, TypeDecoder {

  override fun metaApi(): MetaApi = this

  val typeNameDownKind: (typeName: arrow.meta.ast.TypeName) -> arrow.meta.ast.TypeName

  /**
   * @see [MetaApi.removeConstrains]
   */
  override fun TypeName.WildcardType.removeConstrains(): TypeName.WildcardType =
    copy(
      upperBounds = upperBounds.map { it.removeConstrains() },
      lowerBounds = lowerBounds.map { it.removeConstrains() }
    )

  /**
   * @see [MetaApi.removeConstrains]
   */
  override fun TypeName.ParameterizedType.removeConstrains(): TypeName.ParameterizedType =
    copy(
      enclosingType = enclosingType?.removeConstrains(),
      rawType = rawType.removeConstrains(),
      typeArguments = typeArguments.map { it.removeConstrains() }
    )

  /**
   * @see [MetaApi.removeConstrains]
   */
  override fun TypeName.Classy.removeConstrains(): TypeName.Classy = this

  /**
   * @see [MetaApi.removeConstrains]
   */
  override fun TypeName.removeConstrains(): TypeName =
    when (this) {
      is TypeName.TypeVariable -> removeConstrains()
      is TypeName.WildcardType -> removeConstrains()
      is TypeName.ParameterizedType -> removeConstrains()
      is TypeName.Classy -> removeConstrains()
      is TypeName.FunctionLiteral -> removeConstrains()
    }

  /**
   * @see [MetaApi.removeConstrains]
   */
  override fun Parameter.removeConstrains(): Parameter =
    copy(
      modifiers = emptyList(),
      type = type.removeConstrains()
    )

  /**
   * @see [MetaApi.addExtraDummyArg]
   */
  override fun Func.addExtraDummyArg(): Func {
    val dummyArg: Parameter = Parameter("arg${parameters.size + 1}", TypeName.Unit).defaultDummyArgValue()
    return copy(parameters = parameters + listOf(dummyArg))
  }

  /**
   * @see [MetaApi.prependExtraDummyArg]
   */
  override fun Func.prependExtraDummyArg(): Func {
    val dummyArg: Parameter = Parameter("arg${parameters.size + 1}", TypeName.Unit).defaultDummyArgValue()
    return copy(parameters = listOf(dummyArg) + parameters)
  }

  /**
   * @see [MetaApi.removeDummyArgs]
   */
  override fun Func.removeDummyArgs(): Func =
    copy(parameters = parameters.filterNot { it.type.simpleName == "Unit" })

  /**
   * @see [MetaApi.countDummyArgs]
   */
  override fun Func.countDummyArgs(): Int =
    parameters.count { it.type.simpleName == "Unit" }

  /**
   * @see [MetaApi.removeConstrains]
   */
  override fun Func.removeConstrains(keepModifiers: Set<Modifier>): Func =
    copy(
      modifiers = modifiers.filterNot { it !in keepModifiers },
      annotations = emptyList(),
      receiverType = receiverType?.removeConstrains(),
      returnType = returnType?.removeConstrains(),
      parameters = parameters.map { it.removeConstrains() },
      typeVariables = typeVariables.map { it.removeConstrains() }
    )

  /**
   * @see [MetaApi.removeConstrains]
   */
  override fun TypeName.TypeVariable.removeConstrains(): TypeName.TypeVariable =
    copy(
      bounds = bounds.mapNotNull { if (it is TypeName.Classy && it.fqName == "java.lang.Object") null else it },
      variance = null
    )

  /**
   * @see [MetaApi.getDownKind]
   */
  override fun Parameter.downKind(): Parameter =
    copy(type = type.downKind)

  /**
   * @see [MetaApi.defaultDummyArgValue]
   */
  override fun Parameter.defaultDummyArgValue(): Parameter =
    copy(defaultValue = when {
      type.simpleName == "Unit" -> Code("Unit")
      else -> null
    })

  /**
   * @see [MetaApi.downKindParameters]
   */
  override fun Func.downKindParameters(): Func =
    copy(parameters = parameters.map { it.downKind() })

  /**
   * @see [MetaApi.downKindReceiver]
   */
  override fun Func.downKindReceiver(): Func =
    copy(receiverType = receiverType?.downKind)

  /**
   * @see [MetaApi.downKindReturnType]
   */
  override fun Func.downKindReturnType(): Func =
    copy(returnType = returnType?.downKind)

  /**
   * Applies replacement on a type recursively changing it's wrapper type for it's wrapped type
   * and [MetaApi.getDownKind] as needed
   * ex: Kind<ForSetK, A> -> Set<A>
   */
  fun TypeName.TypeVariable.wrap(wrapped: Pair<TypeName, TypeName.ParameterizedType>): TypeName.TypeVariable =
    wrapped.let { (wrapper, wrapping) ->
      if (rawName == wrapper.rawName) copy(
        name = name.replace(wrapper.rawName, wrapping.rawName),
        bounds = bounds.map { it.wrap(wrapped) }
      )
      else this
    }

  /**
   * Applies replacement on a type recursively changing it's wrapper type for it's wrapped type
   * and [MetaApi.getDownKind] as needed
   * ex: Kind<ForSetK, A> -> Set<A>
   */
  fun TypeName.WildcardType.wrap(wrapped: Pair<TypeName, TypeName.ParameterizedType>): TypeName.WildcardType =
    wrapped.let { (wrapper, wrapping) ->
      if (rawName == wrapper.rawName) copy(
        name = name.replace(wrapper.rawName, wrapping.rawName),
        lowerBounds = lowerBounds.map { it.wrap(wrapped) },
        upperBounds = upperBounds.map { it.wrap(wrapped) }
      )
      else this
    }

  /**
   * Applies replacement on a type recursively changing it's wrapper type for it's wrapped type
   * and [MetaApi.getDownKind] as needed
   * ex: Kind<ForSetK, A> -> Set<A>
   */
  fun TypeName.ParameterizedType.wrap(wrapped: Pair<TypeName, TypeName.ParameterizedType>): TypeName.ParameterizedType =
    wrapped.let { (wrapper, wrapping) ->
      if (rawName == wrapper.rawName) copy(
        name = name.replace(wrapper.rawName, wrapping.rawName),
        typeArguments = typeArguments.map { it.wrap(wrapped) },
        enclosingType = enclosingType?.wrap(wrapped),
        rawType = rawType.wrap(wrapped)
      )
      else this
    }

  /**
   * Applies replacement on a type recursively changing it's wrapper type for it's wrapped type
   * and [MetaApi.getDownKind] as needed
   * ex: Kind<ForSetK, A> -> Set<A>
   */
  fun TypeName.Classy.wrap(wrapped: Pair<TypeName, TypeName.ParameterizedType>): TypeName.Classy =
    wrapped.let { (wrapper, wrapping) ->
      if (rawName == wrapper.rawName) wrapping.rawType
      else this
    }

  /**
   * Applies replacement on a type recursively changing it's wrapper type for it's wrapped type
   * and [MetaApi.getDownKind] as needed
   * ex: Kind<ForSetK, A> -> Set<A>
   */
  fun TypeName.FunctionLiteral.wrap(wrapped: Pair<TypeName, TypeName.ParameterizedType>): TypeName.FunctionLiteral =
    copy(
      receiverType = receiverType?.wrap(wrapped),
      parameters = parameters.map { it.wrap(wrapped) },
      returnType = returnType.wrap(wrapped)
    )

  /**
   * Applies replacement on a type recursively changing it's wrapper type for it's wrapped type
   * and [MetaApi.getDownKind] as needed
   * ex: Kind<ForSetK, A> -> Set<A>
   */
  fun TypeName.wrap(wrapped: Pair<TypeName, TypeName.ParameterizedType>): TypeName =
    when (this) {
      is TypeName.TypeVariable -> wrap(wrapped)
      is TypeName.WildcardType -> wrap(wrapped)
      is TypeName.ParameterizedType -> wrap(wrapped)
      is TypeName.Classy -> wrap(wrapped)
      is TypeName.FunctionLiteral -> wrap(wrapped)
    }

  /**
   * Applies replacement on all types of this function recursively changing wrapper types for their wrapped type
   * over all three receiver, parameters and return type.
   * and [MetaApi.getDownKind] as needed
   */
  fun Func.wrap(wrappedType: Pair<TypeName, TypeName.ParameterizedType>? = null): Func =
    wrappedType?.let { wrapped ->
      val receiverType = receiverType?.downKind?.wrap(wrapped)
      val parameters = parameters.map {
        when (it.type) {
          is TypeName.FunctionLiteral -> it.copy(type = it.type.wrap(wrapped))
          else -> it.copy(type = it.type.downKind.wrap(wrapped))
        }
      }
      val returnType = returnType?.downKind?.wrap(wrapped)
      copy(
        receiverType = receiverType,
        parameters = parameters,
        returnType = returnType
      )
    } ?: this

  /**
   * @see [MetaApi.defaultDummyArgValues]
   */
  override fun Func.defaultDummyArgValues(): Func =
    copy(parameters = parameters.map { it.defaultDummyArgValue() })

  private fun String.resolveKotlinPrimitive(): String =
    if (this.startsWith("kotlin.")) replace("kotlin.", "java.lang.")
    else this

  /**
   * @see [MetaApi.getAsType]
   */
  override val TypeName.type: Type?
    get() = when (this) {
      is TypeName.TypeVariable -> getTypeElement(name.resolveKotlinPrimitive(), elementUtils)?.asMetaType()
      is TypeName.WildcardType -> null
      is TypeName.ParameterizedType -> rawType.type
      is TypeName.Classy -> getTypeElement(fqName, elementUtils)?.asMetaType()
      is TypeName.FunctionLiteral -> null
    }

  /**
   * @see [MetaApi.TODO]
   */
  override val Code.Companion.TODO: Code
    get() = Code("return TODO()")

  /**
   * @see [MetaApi.getDownKind]
   */
  @Suppress("StringLiteralDuplication")
  override val TypeName.TypeVariable.downKind: TypeName
    get() = name.downKind().let { (pckg, unPrefixedName, extraArgs) ->
      if (pckg.isBlank()) this
      else {
        if (extraArgs.isEmpty()) copy(name = "$pckg.$unPrefixedName")
        else TypeName.ParameterizedType(
          name = "$pckg.$unPrefixedName",
          typeArguments = extraArgs.map { TypeName.TypeVariable(it) },
          rawType = TypeName.Classy(unPrefixedName, "$pckg.$unPrefixedName", PackageName(pckg))
        )
      }
    }

  /**
   * @see [MetaApi.getKinded]
   */
  override val TypeName.ParameterizedType.kinded: Boolean
    get() = typeArguments.isNotEmpty() &&
      !typeArguments[0].simpleName.startsWith("Conested") &&
      rawType.fqName == "arrow.Kind" &&
      typeArguments.size == 2 &&
      (typeArguments[0] !is TypeName.TypeVariable)

  /**
   * @see [MetaApi.getNestedTypeVariables]
   */
  override val TypeName.TypeVariable.nestedTypeVariables: List<TypeName>
    get() = listOf(this)

  /**
   * @see [MetaApi.getNestedTypeVariables]
   */
  override val TypeName.WildcardType.nestedTypeVariables: List<TypeName>
    get() = upperBounds.flatMap { it.nestedTypeVariables }

  /**
   * @see [MetaApi.getNestedTypeVariables]
   */
  override val TypeName.ParameterizedType.nestedTypeVariables: List<TypeName>
    get() = typeArguments.flatMap { it.nestedTypeVariables }

  /**
   * @see [MetaApi.getNestedTypeVariables]
   */
  override val TypeName.Classy.nestedTypeVariables: List<TypeName>
    get() = emptyList()

  /**
   * @see [MetaApi.getNestedTypeVariables]
   */
  override val TypeName.nestedTypeVariables: List<TypeName>
    get() = when (this) {
      is TypeName.TypeVariable -> nestedTypeVariables
      is TypeName.WildcardType -> nestedTypeVariables
      is TypeName.ParameterizedType -> nestedTypeVariables
      is TypeName.Classy -> nestedTypeVariables
      is TypeName.FunctionLiteral -> nestedTypeVariables
    }

  /**
   * @see [MetaApi.getDownKind]
   */
  override val TypeName.ParameterizedType.downKind: TypeName.ParameterizedType
    get() = if (kinded) {
      val witness = typeArguments[0].downKind
      val tail = when (witness) {
        is TypeName.ParameterizedType -> witness.typeArguments + typeArguments.drop(1)
        is TypeName.WildcardType -> {
          if (witness.name == "arrow.core.Const") {
            val head = typeArguments[0]
            val missingTypeArgs = typeArguments.drop(1)
            head.nestedTypeVariables + missingTypeArgs
          } else typeArguments.drop(1)
        }
        else -> typeArguments.drop(1)
      }.map { it.downKind }
      val fullName = when (witness) {
        is TypeName.TypeVariable -> witness.name
        is TypeName.WildcardType -> witness.name
        is TypeName.ParameterizedType -> witness.name
        is TypeName.Classy -> witness.fqName
        is TypeName.FunctionLiteral -> witness.simpleName
      }
      copy(name = fullName, rawType = fullName.asClassy(), typeArguments = tail)
    } else this

  /**
   * @see [MetaApi.containsModifier]
   */
  override fun Func.containsModifier(modifier: Modifier): Boolean =
    modifiers.contains(modifier)

  /**
   * @see [MetaApi.getDownKind]
   */
  override val TypeName.WildcardType.downKind: TypeName
    get() = if (upperBounds.isNotEmpty() &&
      (upperBounds.find {
        name.matches("arrow.Kind<(\\w?), (\\w?)>".toRegex()) ||
          name.matches("arrow.Kind<arrow.Kind<(\\w?), (\\w?)>, (\\w?)>".toRegex())
      } != null)) {
      this
    } else {
      name.downKind().let { (pckg, unPrefixedName, extraArgs) ->
        when {
          pckg.isBlank() -> this
          extraArgs.isEmpty() -> copy(
            name = "$pckg.$unPrefixedName",
            lowerBounds = lowerBounds.map { it.downKind },
            upperBounds = upperBounds.map { it.downKind }
          )
          else -> TypeName.ParameterizedType(
            name = "$pckg.$unPrefixedName",
            typeArguments = extraArgs.map { TypeName.TypeVariable(it) },
            rawType = TypeName.Classy(unPrefixedName, "$pckg.$unPrefixedName", PackageName(pckg))
          )
        }
      }
    }

  /**
   * @see [MetaApi.getDownKind]
   */
  override val TypeName.Classy.downKind: TypeName
    get() = fqName.downKind().let { (pckg, unPrefixedName, extraArgs) ->
      if (extraArgs.isEmpty()) copy(simpleName = unPrefixedName, fqName = "$pckg.$unPrefixedName")
      else TypeName.ParameterizedType(
        name = "$pckg.$unPrefixedName",
        typeArguments = extraArgs.map { TypeName.TypeVariable(it) },
        rawType = TypeName.Classy(unPrefixedName, "$pckg.$unPrefixedName", PackageName(pckg))
      )
    }

  fun typeNameDownKindImpl(typeName: TypeName): TypeName =
    when (typeName) {
      is TypeName.TypeVariable -> typeName.downKind.asKotlin()
      is TypeName.WildcardType -> typeName.downKind.asKotlin()
      is TypeName.ParameterizedType -> typeName.downKind.asKotlin()
      is TypeName.Classy -> typeName.downKind.asKotlin()
      is TypeName.FunctionLiteral -> typeName.downKind.asKotlin()
    }

  /**
   * @see [MetaApi.getDownKind]
   */
  override val TypeName.downKind: TypeName
    get() = typeNameDownKind(this)

  /**
   * @see [MetaApi.asKotlin]
   */
  override fun TypeName.asKotlin(): TypeName =
    when (this) {
      is TypeName.TypeVariable -> asKotlin()
      is TypeName.WildcardType -> asKotlin()
      is TypeName.ParameterizedType -> asKotlin()
      is TypeName.Classy -> asKotlin()
      is TypeName.FunctionLiteral -> asKotlin()
    }

  /**
   * @see [MetaApi.asKotlin]
   */
  override fun TypeName.TypeVariable.asKotlin(): TypeName.TypeVariable =
    copy(name = name.asKotlin(), bounds = bounds.map { it.asKotlin() })

  /**
   * @see [MetaApi.asKotlin]
   */
  override fun TypeName.ParameterizedType.asKotlin(): TypeName.ParameterizedType =
    copy(name = name.asKotlin(), rawType = rawType.asKotlin(), typeArguments = typeArguments.map { it.asKotlin() })

  /**
   * @see [MetaApi.asKotlin]
   */
  override fun TypeName.WildcardType.asKotlin(): TypeName.WildcardType =
    copy(
      name = name.asKotlin(),
      upperBounds = upperBounds.map { it.asKotlin() },
      lowerBounds = lowerBounds.map { it.asKotlin() }
    )

  /**
   * @see [MetaApi.asKotlin]
   */
  override fun TypeName.Classy.asKotlin(): TypeName.Classy =
    if (simpleName == "Iterable") copy(simpleName = simpleName.asKotlin(), fqName = fqName.asKotlin(), pckg = PackageName("kotlin.collections"))
    else copy(simpleName = simpleName.asKotlin(), fqName = fqName.asKotlin(), pckg = PackageName(pckg.value.asKotlin()))

  /**
   * @see [MetaApi.asPlatform]
   */
  override fun TypeName.Classy.asPlatform(): TypeName.Classy =
    fqName.asPlatform().let {
      copy(
        simpleName = simpleName.asPlatform(),
        fqName = it,
        pckg = PackageName(it.substringBeforeLast("."))
      )
    }

  /**
   * @see [MetaApi.requiredAbstractFunctions]
   */
  override val TypeClassInstance.requiredAbstractFunctions: List<Func>
    get() = instance.declaredFunctions
      .asSequence()
      .filter { it.containsModifier(Modifier.Abstract) }
      .map {
        it.copy(
          modifiers = it.modifiers - Modifier.Abstract,
          body = Code("return ${it.name}")
        )
      }
      .toList()

  /**
   * @see [MetaApi.requiredParameters]
   */
  override val TypeClassInstance.requiredParameters: List<Parameter>
    get() = requiredAbstractFunctions.mapNotNull {
      if (it.returnType != null) Parameter(it.name, it.returnType.asKotlin()) else null
    }

  /**
   * @see [MetaApi.typeNameOf]
   */
  override fun <A : Any> TypeName.Companion.typeNameOf(clazz: KClass<A>): TypeName =
    TypeName.Classy(
      simpleName = clazz.java.simpleName,
      fqName = clazz.java.name,
      pckg = PackageName(clazz.java.`package`.name)
    )

  /**
   * @see [MetaApi.JvmName]
   */
  override fun JvmName(name: String): Annotation =
    Annotation(
      type = TypeName.typeNameOf(JvmName::class),
      members = listOf(Code(""""$name"""")),
      useSiteTarget = null
    )

  /**
   * @see [MetaApi.PublishedApi]
   */
  override fun PublishedApi(): Annotation =
    Annotation(
      type = TypeName.typeNameOf(PublishedApi::class),
      members = listOf(Code.empty),
      useSiteTarget = null
    )

  /**
   * @see [MetaApi.SuppressAnnotation]
   */
  override fun SuppressAnnotation(vararg names: String): Annotation =
    Annotation(
      type = TypeName.typeNameOf(Suppress::class),
      members = names.map { Code(it) },
      useSiteTarget = null
    )

  /**
   * @see [MetaApi.getProjectedCompanion]
   */
  override val TypeName.projectedCompanion: TypeName
    get() {
      val dataTypeDownKinded = downKind
      return when {
        this is TypeName.TypeVariable &&
          (dataTypeDownKinded.simpleName.startsWith("arrow.Kind") ||
            dataTypeDownKinded.simpleName.startsWith("arrow.typeclasses.Conested")) -> {
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

  override fun TypeName.widenTypeArgs(): TypeName =
    when (val unconstrained = removeConstrains()) {
      is TypeName.TypeVariable ->
        if (unconstrained.bounds.isNotEmpty()) unconstrained.bounds[0]
        else TypeName.AnyNullable
      is TypeName.WildcardType -> unconstrained
      is TypeName.FunctionLiteral -> unconstrained
      is TypeName.ParameterizedType -> unconstrained.copy(
        typeArguments = unconstrained.typeArguments.map { it.widenTypeArgs() }
      )
      is TypeName.Classy -> unconstrained
    }.asKotlin()

  /**
   * Returns a Pair matching a type as wrapper and the type it wraps
   * ex: SetK<A> to Set<A>
   */
  val Type.kindWrapper: Pair<TypeName, TypeName.ParameterizedType>?
    get() = if (primaryConstructor?.parameters?.size == 1 && typeVariables.size == 1) {
      val wrappedType = primaryConstructor.parameters[0].type.asKotlin()
      when (wrappedType) {
        is TypeName.ParameterizedType -> {
          val superInterfacesNames =
            superInterfaces.asSequence()
              .filterIsInstance(TypeName.ParameterizedType::class.java)
              .map { it.name }.toList()
          if (superInterfacesNames.contains(wrappedType.name)) name to wrappedType
          else null
        }
        else -> null
      }
    } else null

  /**
   * Returns all the type information needed for type class introspection assuming
   * this type element is a valid type class instance:
   * An interface annotated with @extension with at least one type argument and extending another interface
   * with one type argument as the first element in its extends clause
   */
  fun TypeElement.typeClassInstance(): TypeClassInstance? {
    val superInterfaces = superInterfaces()
    val instance = asMetaType()
    return when {
      instance != null && superInterfaces.isNotEmpty() -> {
        val typeClassTypeName = superInterfaces[0]
        val typeClass = typeClassTypeName.type
        when {
          typeClass != null && typeClassTypeName is TypeName.ParameterizedType && typeClassTypeName.typeArguments.isNotEmpty() -> {
            val dataTypeTypeArg = typeClassTypeName.typeArguments[0]
            val dataTypeName =
              if (dataTypeTypeArg is TypeName.TypeVariable && dataTypeTypeArg.name.contains("PartialOf<"))
                TypeName.TypeVariable(dataTypeTypeArg.name.substringBefore("PartialOf<").substringAfter("<"))
              else dataTypeTypeArg
            // profunctor and other cases are parametric to Kind2 values or Conested
            val projectedCompanion = dataTypeName.projectedCompanion
            val dataTypeDownKinded = dataTypeName.downKind
            val dataType = dataTypeDownKinded.type
            when {
              dataType != null -> TypeClassInstance(
                instance = instance,
                dataType = dataType,
                typeClass = typeClass,
                instanceTypeElement = this@typeClassInstance,
                dataTypeTypeElement = elementUtils.getTypeElement(dataTypeDownKinded.rawName),
                typeClassTypeElement = elementUtils.getTypeElement(typeClassTypeName.rawName),
                projectedCompanion =
                if (projectedCompanion is TypeName.ParameterizedType) projectedCompanion.rawType
                else projectedCompanion
              )
              else -> null
            }
          }
          else -> null
        }
      }
      else -> null
    }
  }

  override fun TypeName.FunctionLiteral.removeConstrains(): TypeName.FunctionLiteral =
    this

  override val TypeName.FunctionLiteral.downKind: TypeName.FunctionLiteral
    get() = copy(
      receiverType = receiverType?.downKind,
      parameters = parameters.map { it.downKind },
      returnType = returnType.downKind
    )

  override val TypeName.FunctionLiteral.nestedTypeVariables: List<TypeName>
    get() = emptyList()

  override fun TypeName.FunctionLiteral.asKotlin(): TypeName.FunctionLiteral =
    this
}
