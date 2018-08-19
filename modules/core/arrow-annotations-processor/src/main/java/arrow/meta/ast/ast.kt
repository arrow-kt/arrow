package arrow.meta.ast

data class Code(val value : String)

sealed class Tree

data class PackageName(
  val value: String) : Tree() {
  companion object
}

data class TypeAlias(
  val name: String,
  val value: TypeName) : Tree()

data class Import(
  val qualifiedName: String,
  val alias: String? = null): Tree()

data class File(
  val fileName: String,
  val packageName: PackageName? = null,
  val imports: List<Import> = emptyList(),
  val types: List<Type> = emptyList(),
  val functions : List<Fun> = emptyList()) {
  companion object
}

sealed class TypeName : Tree() {
  data class TypeVariable(
    val name: String,
    val bounds: List<TypeName> = emptyList(),
    val variance: Modifier? = null,
    val reified: Boolean = false,
    val nullable: Boolean = false,
    val annotations: List<Annotation> = emptyList()) : TypeName()

  data class WildcardType(
    val name: String,
    val upperBounds: List<TypeName>,
    val lowerBounds: List<TypeName>,
    val nullable: Boolean,
    val annotations: List<Annotation>) : TypeName()

  data class ParameterizedType(
    val name: String,
    val enclosingType: TypeName? = null,
    val rawType: TypeName,
    val typeArguments: List<TypeName> = emptyList(),
    val nullable: Boolean = false,
    val annotations: List<Annotation> = emptyList()) : TypeName()

  data class Class(
    val simpleName: String,
    val fqName: String,
    val pckg: PackageName,
    val nullable: Boolean = false,
    val annotations: List<Annotation> = emptyList()) : TypeName()

  companion object {
    val Unit: TypeName = TypeName.Class(simpleName = "Unit", pckg = PackageName("kotlin"), fqName = "kotlin.Unit")
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
  object Delegate : UseSiteTarget() }

data class Parameter(
  val name: String,
  val type: TypeName,
  val defaultValue: Code? = null,
  val annotations: List<Annotation> = emptyList(),
  val modifiers: List<Modifier> = emptyList()): Tree()

data class Annotation(
  val type: TypeName,
  val members: List<Code>,
  val useSiteTarget: UseSiteTarget?): Tree()

data class Property(
  val name: String,
  val type: TypeName,
  val mutable: Boolean,
  val kdoc: Code? = null,
  val initializer: Code? = null,
  val delegated: Boolean = false,
  val getter: Fun,
  val setter: Fun?,
  val receiverType: TypeName? = null,
  val jvmPropertySignature: String,
  val jvmFieldSignature: String?,
  val annotations: List<Annotation> = emptyList(),
  val modifiers: List<Modifier> = emptyList()): Tree()

data class Fun(
  val name: String,
  val kdoc: Code? = null,
  val receiverType: TypeName? = null,
  val returnType: TypeName?,
  val body: Code? = null,
  val annotations: List<Annotation> = emptyList(),
  val modifiers: List<Modifier> = emptyList(),
  val typeVariables: List<TypeName.TypeVariable> = emptyList(),
  val parameters: List<Parameter> = emptyList(),
  val jvmMethodSignature: String): Tree()

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
  object Companion : Modifier()
  object Inline : Modifier()
  object NoInline : Modifier()
  object CrossInline : Modifier()
  object Reified : Modifier()
  object Infix : Modifier()
  object Operator : Modifier()
  object Data : Modifier()
  object In : Modifier()
  object Out : Modifier()
  object VarArg: Modifier()
}

data class Type(
  val name: TypeName,
  val kind: Type.Kind,
  val kdoc: Code? = null,
  val modifiers: List<Modifier> = emptyList(),
  val primaryConstructor: Fun? = null,
  val superclass: TypeName? = null,
  val initializer: Code? = null,
  val superInterfaces: List<TypeName> = emptyList(),
  val enumConstants: Map<String, Type> = emptyMap(),
  val annotations: List<Annotation> = emptyList(),
  val typeVariables: List<TypeName.TypeVariable> = emptyList(),
  val superclassConstructorParameters: List<Code> = emptyList(),
  val properties: List<Property> = emptyList(),
  val declaredFunctions: List<Fun> = emptyList(),
  val allFunctions: List<Fun> = emptyList(),
  val types: List<Type> = emptyList()): Tree() {

  sealed class Kind {
    object Class : Kind()
    object Interface: Kind()
    object Object: Kind()
  }

}