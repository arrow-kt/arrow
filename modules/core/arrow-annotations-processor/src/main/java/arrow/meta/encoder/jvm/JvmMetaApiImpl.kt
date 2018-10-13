package arrow.meta.encoder.jvm

import arrow.common.utils.ProcessorUtils
import arrow.meta.ast.*
import arrow.meta.ast.Annotation
import arrow.meta.decoder.TypeDecoder
import arrow.meta.encoder.MetaApi
import arrow.meta.encoder.TypeClassInstance
import javax.lang.model.element.TypeElement
import kotlin.reflect.KClass

interface JvmMetaApiImpl : MetaApi, TypeElementEncoder, ProcessorUtils, TypeDecoder {

  override fun metaApi(): MetaApi = this

  val typeNameDownKind: (typeName: arrow.meta.ast.TypeName) -> arrow.meta.ast.TypeName

  override fun TypeName.WildcardType.removeConstrains(): TypeName.WildcardType =
    copy(
      upperBounds = upperBounds.map { it.removeConstrains() },
      lowerBounds = lowerBounds.map { it.removeConstrains() }
    )

  override fun TypeName.ParameterizedType.removeConstrains(): TypeName.ParameterizedType =
    copy(
      enclosingType = enclosingType?.removeConstrains(),
      rawType = rawType.removeConstrains(),
      typeArguments = typeArguments.map { it.removeConstrains() }
    )

  override fun TypeName.Classy.removeConstrains(): TypeName.Classy = this

  override fun TypeName.removeConstrains(): TypeName =
    when (this) {
      is TypeName.TypeVariable -> removeConstrains()
      is TypeName.WildcardType -> removeConstrains()
      is TypeName.ParameterizedType -> removeConstrains()
      is TypeName.Classy -> removeConstrains()
    }

  override fun Parameter.removeConstrains(): Parameter =
    copy(
      modifiers = emptyList(),
      type = type.removeConstrains()
    )

  override fun Func.addExtraDummyArg(): Func {
    val dummyArg: Parameter = Parameter("arg${parameters.size + 1}", TypeName.Unit).defaultDummyArgValue()
    return copy(parameters = parameters + listOf(dummyArg))
  }

  override fun Func.removeDummyArgs(): Func =
    copy(parameters = parameters.filterNot { it.type.simpleName == "Unit" })

  override fun Func.countDummyArgs(): Int =
    parameters.count { it.type.simpleName == "Unit" }

  override fun Func.removeConstrains(): Func =
    copy(
      modifiers = emptyList(),
      annotations = emptyList(),
      receiverType = receiverType?.removeConstrains(),
      returnType = returnType?.removeConstrains(),
      parameters = parameters.map { it.removeConstrains() },
      typeVariables = typeVariables.map { it.removeConstrains() }
    )

  override fun TypeName.TypeVariable.removeConstrains(): TypeName.TypeVariable =
    copy(
      bounds = bounds.mapNotNull { if (it is TypeName.Classy && it.fqName == "java.lang.Object") null else it },
      variance = null
    )

  override fun TypeName.TypeVariable.disambiguate(existing: List<TypeName.TypeVariable>, prefix: String): TypeName.TypeVariable =
    if (existing.asSequence().map { it.name }.contains(name)) copy(
      name = "$prefix$name"
    ) else this

  override fun Parameter.downKind(): Parameter =
    copy(type = type.downKind())

  override fun Parameter.defaultDummyArgValue(): Parameter =
    copy(defaultValue = when {
      type.simpleName == "Unit" -> Code("Unit")
      else -> null
    })

  override fun Func.downKindParameters(): Func =
    copy(parameters = parameters.map { it.downKind() })

  override fun Func.downKindReceiver(): Func =
    copy(receiverType = receiverType?.downKind())

  override fun Func.downKindReturnType(): Func =
    copy(returnType = returnType?.downKind())

  override fun Func.defaultDummyArgValues(): Func =
    copy(parameters = parameters.map { it.defaultDummyArgValue() })

  fun String.resolveKotlinPrimitive(): String =
    if (this.startsWith("kotlin.")) replace("kotlin.", "java.lang.")
    else this

  override tailrec fun TypeName.asType(): Type? =
    when (this) {
      is TypeName.TypeVariable -> getTypeElement(name.resolveKotlinPrimitive(), elementUtils)?.asMetaType()
      is TypeName.WildcardType -> null
      is TypeName.ParameterizedType -> rawType.asType()
      is TypeName.Classy -> getTypeElement(fqName, elementUtils)?.asMetaType()
    }

  override val Code.Companion.TODO: Code
    get() = Code("return TODO()")

  override fun TypeName.TypeVariable.downKind(): TypeName.TypeVariable =
    name.downKind().let { (pckg, unPrefixedName) ->
      if (pckg.isBlank()) this else copy(name = "$pckg.$unPrefixedName")
    }

  override fun TypeName.ParameterizedType.isKinded(): Boolean =
    typeArguments.isNotEmpty() &&
      !typeArguments[0].simpleName.startsWith("Conested") &&
      rawType.fqName == "arrow.Kind" &&
      typeArguments.size == 2 &&
      (typeArguments[0] !is TypeName.TypeVariable)

  override fun TypeName.TypeVariable.nestedTypeVariables(): List<TypeName> =
    listOf(this)

  override fun TypeName.WildcardType.nestedTypeVariables(): List<TypeName> =
    upperBounds.flatMap { it.nestedTypeVariables() }

  override fun TypeName.ParameterizedType.nestedTypeVariables(): List<TypeName> =
    typeArguments.flatMap { it.nestedTypeVariables() }

  override fun TypeName.Classy.nestedTypeVariables(): List<TypeName> =
    emptyList()

  override fun TypeName.nestedTypeVariables(): List<TypeName> =
    when (this) {
      is TypeName.TypeVariable -> nestedTypeVariables()
      is TypeName.WildcardType -> nestedTypeVariables()
      is TypeName.ParameterizedType -> nestedTypeVariables()
      is TypeName.Classy -> nestedTypeVariables()
    }

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

  override fun Func.containsModifier(modifier: Modifier): Boolean =
    modifiers.contains(modifier)

  override fun TypeName.WildcardType.downKind(): TypeName.WildcardType =
    name.downKind().let { (pckg, unPrefixedName) ->
      copy(
        name = "$pckg.$unPrefixedName",
        lowerBounds = lowerBounds.map { it.downKind() },
        upperBounds = upperBounds.map { it.downKind() }
      )
    }

  override fun TypeName.Classy.downKind(): TypeName.Classy =
    fqName.downKind().let { (pckg, unPrefixedName) ->
      copy(simpleName = unPrefixedName, fqName = "$pckg.$unPrefixedName")
    }

  override fun typeNameDownKindImpl(typeName: TypeName): TypeName =
    when (typeName) {
      is TypeName.TypeVariable -> typeName.downKind().asKotlin()
      is TypeName.WildcardType -> typeName.downKind().asKotlin()
      is TypeName.ParameterizedType -> typeName.downKind().asKotlin()
      is TypeName.Classy -> typeName.downKind().asKotlin()
    }

  override fun TypeName.downKind(): TypeName =
    typeNameDownKind(this)

  override fun TypeName.asKotlin(): TypeName =
    when (this) {
      is TypeName.TypeVariable -> asKotlin()
      is TypeName.WildcardType -> asKotlin()
      is TypeName.ParameterizedType -> asKotlin()
      is TypeName.Classy -> asKotlin()
    }

  override fun TypeName.TypeVariable.asKotlin(): TypeName.TypeVariable =
    copy(name = name.asKotlin())

  override fun TypeName.ParameterizedType.asKotlin(): TypeName.ParameterizedType =
    copy(name = name.asKotlin(), rawType = rawType.asKotlin(), typeArguments = typeArguments.map { it.asKotlin() })

  override fun TypeName.WildcardType.asKotlin(): TypeName.WildcardType =
    copy(
      name = name.asKotlin(),
      upperBounds = upperBounds.map { it.asKotlin() },
      lowerBounds = lowerBounds.map { it.asKotlin() }
    )

  override fun TypeName.Classy.asKotlin(): TypeName.Classy =
    copy(simpleName = simpleName.asKotlin(), fqName = fqName.asKotlin(), pckg = PackageName(pckg.value.asKotlin()))

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

  override val TypeClassInstance.requiredParameters: List<Parameter>
    get() = requiredAbstractFunctions.mapNotNull {
      if (it.returnType != null) Parameter(it.name, it.returnType.asKotlin()) else null
    }

  override fun <A : Any> TypeName.Companion.typeNameOf(clazz: KClass<A>): TypeName =
    TypeName.Classy(
      simpleName = clazz.java.simpleName,
      fqName = clazz.java.name,
      pckg = PackageName(clazz.java.`package`.name)
    )

  override fun JvmName(name: String): Annotation =
    Annotation(
      type = TypeName.typeNameOf(JvmName::class),
      members = listOf(Code(""""$name"""")),
      useSiteTarget = null
    )

  override fun SuppressAnnotation(vararg names: String): Annotation =
    Annotation(
      type = TypeName.typeNameOf(Suppress::class),
      members = names.map { Code(it) },
      useSiteTarget = null
    )

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

  override fun TypeElement.typeClassInstance(): TypeClassInstance? {
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