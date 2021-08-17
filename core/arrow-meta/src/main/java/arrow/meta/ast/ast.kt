/**
 * The arrow AST represents already reified immutable values of the trees
 * a compiler may see over types and their members
 */
package arrow.meta.ast

@Suppress("UtilityClassWithPublicConstructor")
public sealed class Tree {
  public companion object
}

public data class Code(val value: String) {
  override fun toString(): String = value

  public companion object {
    public val empty: Code = Code("")
  }
}

public data class PackageName(
  val value: String
) : Tree() {
  public companion object
}

public data class TypeAlias(
  val name: String,
  val value: TypeName
) : Tree() {
  public companion object
}

public data class Import(
  val qualifiedName: String,
  val alias: String? = null
) : Tree() {
  public companion object
}

public sealed class TypeName : Tree() {

  public abstract val simpleName: String
  public abstract val rawName: String

  public data class TypeVariable(
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

    public companion object
  }

  public data class WildcardType(
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

    public companion object
  }

  public data class FunctionLiteral(
    val modifiers: List<Modifier> = emptyList(),
    val receiverType: TypeName?,
    val parameters: List<TypeName>,
    val returnType: TypeName
  ) : TypeName() {
    override val simpleName: String
      get() = if (receiverType != null) {
        "(${receiverType.simpleName}).(${parameters.joinToString(", ") { it.simpleName }}) -> ${returnType.simpleName}"
      } else {
        "(${parameters.joinToString(", ") { it.simpleName }}) -> ${returnType.simpleName}"
      }

    override val rawName: String
      get() {
        val arity = parameters.size + if (receiverType != null) 1 else 0
        return "kotlin.Function$arity"
      }
  }

  public data class ParameterizedType(
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

    public companion object
  }

  public data class Classy(
    override val simpleName: String,
    val fqName: String,
    val pckg: PackageName,
    val nullable: Boolean = false,
    val annotations: List<Annotation> = emptyList()
  ) : TypeName() {

    override val rawName: String
      get() = fqName

    public fun companion(): Classy =
      copy(
        simpleName = "Companion",
        fqName = "$fqName.Companion",
        pckg = PackageName("${pckg.value}.$simpleName")
      )

    public companion object {
      public fun from(pck: String, simpleName: String): Classy =
        Classy(simpleName, "$pck.$simpleName", PackageName(pck))
    }
  }

  public companion object {
    public val Unit: TypeName =
      TypeName.Classy(simpleName = "Unit", pckg = PackageName("kotlin"), fqName = "kotlin.Unit")
    public val AnyNullable: TypeName = TypeName.TypeVariable("Any?")
  }
}

public sealed class UseSiteTarget {
  public object File : UseSiteTarget()
  public object Property : UseSiteTarget()
  public object Field : UseSiteTarget()
  public object Get : UseSiteTarget()
  public object Set : UseSiteTarget()
  public object Receiver : UseSiteTarget()
  public object Param : UseSiteTarget()
  public object SetParam : UseSiteTarget()
  public object Delegate : UseSiteTarget()
  public companion object
}

public data class Parameter(
  val name: String,
  val type: TypeName,
  val defaultValue: Code? = null,
  val annotations: List<Annotation> = emptyList(),
  val modifiers: List<Modifier> = emptyList()
) : Tree() {
  public companion object
}

public data class Annotation(
  val type: TypeName,
  val members: List<Code>,
  val useSiteTarget: UseSiteTarget?
) : Tree() {
  public companion object
}

public data class Property(
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
  public companion object
}

public data class Func(
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
  public companion object
}

public sealed class Modifier {
  public object Public : Modifier()
  public object Protected : Modifier()
  public object Private : Modifier()
  public object Internal : Modifier()
  public object Expect : Modifier()
  public object Actual : Modifier()
  public object Final : Modifier()
  public object Open : Modifier()
  public object Abstract : Modifier()
  public object Sealed : Modifier()
  public object Const : Modifier()
  public object External : Modifier()
  public object Override : Modifier()
  public object LateInit : Modifier()
  public object Tailrec : Modifier()
  public object Suspend : Modifier()
  public object Inner : Modifier()
  public object Enum : Modifier()
  public object Annotation : Modifier()
  public object CompanionObject : Modifier()
  public object Inline : Modifier()
  public object NoInline : Modifier()
  public object CrossInline : Modifier()
  public object Reified : Modifier()
  public object Infix : Modifier()
  public object Operator : Modifier()
  public object Data : Modifier()
  public object InVariance : Modifier()
  public object OutVariance : Modifier()
  public object VarArg : Modifier()
  public companion object
}

public data class Type(
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

  public sealed class Shape {
    public object Class : Shape()
    public object Interface : Shape()
    public object Object : Shape()
    public companion object
  }

  public companion object
}
