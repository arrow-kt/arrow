/**
 * The arrow AST represents already reified immutable values of the trees
 * a compiler may see over types and their members
 */
package arrow.meta.ast

@Suppress("UtilityClassWithPublicConstructor")
sealed class Tree {
  companion object
}

data class Code(val value: String) {
  override fun toString(): String = value

  companion object {
    val empty = Code("")
  }
}

data class PackageName(
  val value: String
) : Tree() {
  companion object
}

data class TypeAlias(
  val name: String,
  val value: TypeName
) : Tree() {
  companion object
}

data class Import(
  val qualifiedName: String,
  val alias: String? = null
) : Tree() {
  companion object
}

sealed class TypeName : Tree() {

  abstract val simpleName: String
  abstract val rawName: String

  data class TypeVariable(
    val name: String,
    val bounds: List<TypeName> = emptyList(),
    val variance: Modifier? = null,
    val reified: Boolean = false,
    val nullable: Boolean = false,
    val annotations: List<Annotation> = emptyList()
  ) : TypeName() {

    override val simpleName: String
      get() = name

    override val rawName: String
      get() = name.substringBefore("<")

    companion object
  }

  data class WildcardType(
    val name: String,
    val upperBounds: List<TypeName>,
    val lowerBounds: List<TypeName>,
    val nullable: Boolean,
    val annotations: List<Annotation>
  ) : TypeName() {

    override val simpleName: String
      get() = name

    override val rawName: String
      get() = name.substringBefore("<")

    companion object
  }

  data class FunctionLiteral(
    val modifiers: List<Modifier> = emptyList(),
    val receiverType: TypeName?,
    val parameters: List<TypeName>,
    val returnType: TypeName
  ) : TypeName() {
    override val simpleName: String
      get() = if (receiverType != null)
        "(${receiverType.simpleName}).(${parameters.joinToString(", ") {it.simpleName }}) -> ${returnType.simpleName}"
    else
        "(${parameters.joinToString(", ") {it.simpleName }}) -> ${returnType.simpleName}"

    override val rawName: String
      get() {
        val arity = parameters.size + if (receiverType != null) 1 else 0
        return "kotlin.Function$arity"
      }
  }

  data class ParameterizedType(
    val name: String,
    val enclosingType: TypeName? = null,
    val rawType: Classy,
    val typeArguments: List<TypeName> = emptyList(),
    val nullable: Boolean = false,
    val annotations: List<Annotation> = emptyList()
  ) : TypeName() {

    override val rawName: String
      get() = name.substringBefore("<")

    override val simpleName: String
      get() = rawType.simpleName

    companion object
  }

  data class Classy(
    override val simpleName: String,
    val fqName: String,
    val pckg: PackageName,
    val nullable: Boolean = false,
    val annotations: List<Annotation> = emptyList()
  ) : TypeName() {

    override val rawName: String
      get() = fqName

    fun companion(): Classy =
      copy(
        simpleName = "Companion",
        fqName = "$fqName.Companion",
        pckg = PackageName("${pckg.value}.$simpleName")
      )

    companion object {
      fun from(pck: String, simpleName: String): Classy =
        Classy(simpleName, "$pck.$simpleName", PackageName(pck))
    }
  }

  companion object {
    val Unit: TypeName = TypeName.Classy(simpleName = "Unit", pckg = PackageName("kotlin"), fqName = "kotlin.Unit")
    val AnyNullable: TypeName = TypeName.TypeVariable("Any?")
  }
}

sealed class UseSiteTarget {
  object File : UseSiteTarget()
  object Property : UseSiteTarget()
  object Field : UseSiteTarget()
  object Get : UseSiteTarget()
  object Set : UseSiteTarget()
  object Receiver : UseSiteTarget()
  object Param : UseSiteTarget()
  object SetParam : UseSiteTarget()
  object Delegate : UseSiteTarget()
  companion object
}

data class Parameter(
  val name: String,
  val type: TypeName,
  val defaultValue: Code? = null,
  val annotations: List<Annotation> = emptyList(),
  val modifiers: List<Modifier> = emptyList()
) : Tree() {
  companion object
}

data class Annotation(
  val type: TypeName,
  val members: List<Code>,
  val useSiteTarget: UseSiteTarget?
) : Tree() {
  companion object
}

data class Property(
  val name: String,
  val type: TypeName,
  val mutable: Boolean = false,
  val kdoc: Code? = null,
  val initializer: Code? = null,
  val delegated: Boolean = false,
  val getter: Func? = null,
  val setter: Func? = null,
  val receiverType: TypeName? = null,
  val jvmPropertySignature: String? = null,
  val jvmFieldSignature: String? = null,
  val annotations: List<Annotation> = emptyList(),
  val modifiers: List<Modifier> = emptyList()
) : Tree() {
  companion object
}

data class Func(
  val name: String,
  val kdoc: Code? = null,
  val receiverType: TypeName? = null,
  val returnType: TypeName?,
  val body: Code? = null,
  val annotations: List<Annotation> = emptyList(),
  val modifiers: List<Modifier> = emptyList(),
  val typeVariables: List<TypeName.TypeVariable> = emptyList(),
  val parameters: List<Parameter> = emptyList(),
  val jvmMethodSignature: String = ""
) : Tree() {
  companion object
}

sealed class Modifier {
  object Public : Modifier()
  object Protected : Modifier()
  object Private : Modifier()
  object Internal : Modifier()
  object Expect : Modifier()
  object Actual : Modifier()
  object Final : Modifier()
  object Open : Modifier()
  object Abstract : Modifier()
  object Sealed : Modifier()
  object Const : Modifier()
  object External : Modifier()
  object Override : Modifier()
  object LateInit : Modifier()
  object Tailrec : Modifier()
  object Suspend : Modifier()
  object Inner : Modifier()
  object Enum : Modifier()
  object Annotation : Modifier()
  object CompanionObject : Modifier()
  object Inline : Modifier()
  object NoInline : Modifier()
  object CrossInline : Modifier()
  object Reified : Modifier()
  object Infix : Modifier()
  object Operator : Modifier()
  object Data : Modifier()
  object InVariance : Modifier()
  object OutVariance : Modifier()
  object VarArg : Modifier()
  companion object
}

data class Type(
  val packageName: PackageName,
  val name: TypeName,
  val kind: Type.Shape,
  val kdoc: Code? = null,
  val modifiers: List<Modifier> = emptyList(),
  val primaryConstructor: Func? = null,
  val superclass: TypeName? = null,
  val initializer: Code? = null,
  val superInterfaces: List<TypeName> = emptyList(),
  val enumConstants: Map<String, Type> = emptyMap(),
  val annotations: List<Annotation> = emptyList(),
  val typeVariables: List<TypeName.TypeVariable> = emptyList(),
  val superclassConstructorParameters: List<Code> = emptyList(),
  val properties: List<Property> = emptyList(),
  val declaredFunctions: List<Func> = emptyList(),
  val allFunctions: List<Func> = emptyList(),
  val types: List<Type> = emptyList()
) : Tree() {

  sealed class Shape {
    object Class : Shape()
    object Interface : Shape()
    object Object : Shape()
    companion object
  }

  companion object
}
