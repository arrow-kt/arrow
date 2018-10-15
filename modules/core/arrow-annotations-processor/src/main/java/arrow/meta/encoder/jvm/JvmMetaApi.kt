package arrow.meta.encoder.jvm

import arrow.common.utils.ProcessorUtils
import arrow.meta.ast.*
import arrow.meta.ast.Annotation
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
  override fun Func.removeConstrains(): Func =
    copy(
      modifiers = emptyList(),
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
   * @see [MetaApi.downKind]
   */
  override fun Parameter.downKind(): Parameter =
    copy(type = type.downKind())

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
    copy(receiverType = receiverType?.downKind())

  /**
   * @see [MetaApi.downKindReturnType]
   */
  override fun Func.downKindReturnType(): Func =
    copy(returnType = returnType?.downKind())

  /**
   * Applies replacement on a type recursively changing it's wrapper type for it's wrapped type
   * and [MetaApi.downKind] as needed
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
   * and [MetaApi.downKind] as needed
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
   * and [MetaApi.downKind] as needed
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
   * and [MetaApi.downKind] as needed
   * ex: Kind<ForSetK, A> -> Set<A>
   */
  fun TypeName.Classy.wrap(wrapped: Pair<TypeName, TypeName.ParameterizedType>): TypeName.Classy =
    wrapped.let { (wrapper, wrapping) ->
      if (rawName == wrapper.rawName) wrapping.rawType
      else this
    }

  /**
   * Applies replacement on a type recursively changing it's wrapper type for it's wrapped type
   * and [MetaApi.downKind] as needed
   * ex: Kind<ForSetK, A> -> Set<A>
   */
  fun TypeName.wrap(wrapped: Pair<TypeName, TypeName.ParameterizedType>): TypeName =
    when (this) {
      is TypeName.TypeVariable -> wrap(wrapped)
      is TypeName.WildcardType -> wrap(wrapped)
      is TypeName.ParameterizedType -> wrap(wrapped)
      is TypeName.Classy -> wrap(wrapped)
    }

  /**
   * Applies replacement on all types of this function recursively changing wrapper types for their wrapped type
   * over all three receiver, parameters and return type.
   * and [MetaApi.downKind] as needed
   */
  fun Func.wrap(wrappedType: Pair<TypeName, TypeName.ParameterizedType>? = null): Func =
    wrappedType?.let { wrapped ->
      val receiverType = receiverType?.downKind()?.wrap(wrapped)
      val parameters = parameters.map { it.copy(type = it.type.downKind().wrap(wrapped)) }
      val returnType = returnType?.downKind()?.wrap(wrapped)
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
   * @see [MetaApi.asType]
   */
  override tailrec fun TypeName.asType(): Type? =
    when (this) {
      is TypeName.TypeVariable -> getTypeElement(name.resolveKotlinPrimitive(), elementUtils)?.asMetaType()
      is TypeName.WildcardType -> null
      is TypeName.ParameterizedType -> rawType.asType()
      is TypeName.Classy -> getTypeElement(fqName, elementUtils)?.asMetaType()
    }

  /**
   * @see [MetaApi.TODO]
   */
  override val Code.Companion.TODO: Code
    get() = Code("return TODO()")

  /**
   * @see [MetaApi.downKind]
   */
  override fun TypeName.TypeVariable.downKind(): TypeName.TypeVariable =
    name.downKind().let { (pckg, unPrefixedName) ->
      if (pckg.isBlank()) this else copy(name = "$pckg.$unPrefixedName")
    }

  /**
   * @see [MetaApi.isKinded]
   */
  override fun TypeName.ParameterizedType.isKinded(): Boolean =
    typeArguments.isNotEmpty() &&
      !typeArguments[0].simpleName.startsWith("Conested") &&
      rawType.fqName == "arrow.Kind" &&
      typeArguments.size == 2 &&
      (typeArguments[0] !is TypeName.TypeVariable)

  /**
   * @see [MetaApi.nestedTypeVariables]
   */
  override fun TypeName.TypeVariable.nestedTypeVariables(): List<TypeName> =
    listOf(this)

  /**
   * @see [MetaApi.nestedTypeVariables]
   */
  override fun TypeName.WildcardType.nestedTypeVariables(): List<TypeName> =
    upperBounds.flatMap { it.nestedTypeVariables() }

  /**
   * @see [MetaApi.nestedTypeVariables]
   */
  override fun TypeName.ParameterizedType.nestedTypeVariables(): List<TypeName> =
    typeArguments.flatMap { it.nestedTypeVariables() }

  /**
   * @see [MetaApi.nestedTypeVariables]
   */
  override fun TypeName.Classy.nestedTypeVariables(): List<TypeName> =
    emptyList()

  /**
   * @see [MetaApi.nestedTypeVariables]
   */
  override fun TypeName.nestedTypeVariables(): List<TypeName> =
    when (this) {
      is TypeName.TypeVariable -> nestedTypeVariables()
      is TypeName.WildcardType -> nestedTypeVariables()
      is TypeName.ParameterizedType -> nestedTypeVariables()
      is TypeName.Classy -> nestedTypeVariables()
    }

  /**
   * @see [MetaApi.downKind]
   */
  override fun TypeName.ParameterizedType.downKind(): TypeName.ParameterizedType =
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

  /**
   * @see [MetaApi.containsModifier]
   */
  override fun Func.containsModifier(modifier: Modifier): Boolean =
    modifiers.contains(modifier)

  /**
   * @see [MetaApi.downKind]
   */
  override fun TypeName.WildcardType.downKind(): TypeName.WildcardType =
    if (upperBounds.isNotEmpty() &&
      (upperBounds.find {
        name.matches("arrow.Kind<(\\w?), (\\w?)>".toRegex()) ||
          name.matches("arrow.Kind<arrow.Kind<(\\w?), (\\w?)>, (\\w?)>".toRegex())
      } != null)) {
      this
    } else {
      name.downKind().let { (pckg, unPrefixedName) ->
        copy(
          name = "$pckg.$unPrefixedName",
          lowerBounds = lowerBounds.map { it.downKind() },
          upperBounds = upperBounds.map { it.downKind() }
        )
      }
    }

  /**
   * @see [MetaApi.downKind]
   */
  override fun TypeName.Classy.downKind(): TypeName.Classy =
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

  /**
   * @see [MetaApi.downKind]
   */
  override fun TypeName.downKind(): TypeName =
    typeNameDownKind(this)

  /**
   * @see [MetaApi.asKotlin]
   */
  override fun TypeName.asKotlin(): TypeName =
    when (this) {
      is TypeName.TypeVariable -> asKotlin()
      is TypeName.WildcardType -> asKotlin()
      is TypeName.ParameterizedType -> asKotlin()
      is TypeName.Classy -> asKotlin()
    }

  /**
   * @see [MetaApi.asKotlin]
   */
  override fun TypeName.TypeVariable.asKotlin(): TypeName.TypeVariable =
    copy(name = name.asKotlin())

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
    copy(simpleName = simpleName.asKotlin(), fqName = fqName.asKotlin(), pckg = PackageName(pckg.value.asKotlin()))

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
   * @see [MetaApi.SuppressAnnotation]
   */
  override fun SuppressAnnotation(vararg names: String): Annotation =
    Annotation(
      type = TypeName.typeNameOf(Suppress::class),
      members = names.map { Code(it) },
      useSiteTarget = null
    )

  /**
   * @see [MetaApi.projectedCompanion]
   */
  override fun TypeName.projectedCompanion(): TypeName {
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

  /**
   * Returns a Pair matching a type as wrapper and the type it wraps
   * ex: SetK<A> to Set<A>
   */
  fun Type.kindWrapper(): Pair<TypeName, TypeName.ParameterizedType>? =
    // At this time extension generation for wrapper is only supported for wrappers with a single type argument
    // because of the complexity of down-kind wrappers of multiple type args such as cases like Kind<ForMapK,
    if (primaryConstructor?.parameters?.size == 1 && typeVariables.size == 1) {
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
        val typeClass = typeClassTypeName.asType()
        when {
          typeClass != null && typeClassTypeName is TypeName.ParameterizedType && typeClassTypeName.typeArguments.isNotEmpty() -> {
            val dataTypeName = typeClassTypeName.typeArguments[0]
            //profunctor and other cases are parametric to Kind2 values or Conested
            val projectedCompanion = dataTypeName.projectedCompanion()
            val dataTypeDownKinded = dataTypeName.downKind()
            val dataType = dataTypeDownKinded.asType()
            when {
              dataType != null && dataTypeDownKinded is TypeName.TypeVariable -> TypeClassInstance(
                instance = instance,
                dataType = dataType,
                typeClass = typeClass,
                instanceTypeElement = this@typeClassInstance,
                dataTypeTypeElement = elementUtils.getTypeElement(dataTypeDownKinded.name),
                typeClassTypeElement = elementUtils.getTypeElement(typeClassTypeName.rawType.fqName),
                projectedCompanion = projectedCompanion
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

}