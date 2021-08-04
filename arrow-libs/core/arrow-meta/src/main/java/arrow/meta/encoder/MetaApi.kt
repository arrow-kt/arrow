package arrow.meta.encoder

import arrow.meta.ast.Annotation
import arrow.meta.ast.Code
import arrow.meta.ast.Func
import arrow.meta.ast.Modifier
import arrow.meta.ast.Parameter
import arrow.meta.ast.Tree
import arrow.meta.ast.Type
import arrow.meta.ast.TypeName
import javax.lang.model.element.TypeElement
import kotlin.reflect.KClass

public data class TypeClassInstance(
  val instance: Type,
  val dataType: Type,
  val typeClass: Type,
  val instanceTypeElement: TypeElement,
  val dataTypeTypeElement: TypeElement,
  val typeClassTypeElement: TypeElement,
  val projectedCompanion: TypeName
)

public val KotlinReservedKeywords: Set<String> = setOf(
  "package",
  "as",
  "typealias",
  "class",
  "this",
  "super",
  "val",
  "var",
  "fun",
  "for",
  "null",
  "true",
  "false",
  "is",
  "in",
  "throw",
  "return",
  "break",
  "continue",
  "object",
  "if",
  "try",
  "else",
  "while",
  "do",
  "when",
  "interface",
  "typeof"
)

/**
 * Arrow-Meta provides a hydrated AST representing Kotlin Code.
 * The current implementation relies on TypeElement, KotlinMetadata library and Kotlin Poet
 * to get all the info it needs so you can access the values without manually fiddling with
 * proto buffers or java reflection.
 *
 * The current implementation includes support for [TypeClassInstance] and all the subtrees under the
 * [Tree] hierarchy as well as the notion of emulated higher kinded types via `arrow.Kind`.
 * Among other things one can defunctionalize a Kinded value representation into it's concrete counter part:
 * `Kind<ForOption, A> -> Option<A>`
 */
public interface MetaApi {

  /**
   * Remove undesired constrains such as `java.lang.Object` which appears
   * in upper bound position in certain type shapes
   */
  public fun TypeName.WildcardType.removeConstrains(): TypeName.WildcardType
  public fun TypeName.ParameterizedType.removeConstrains(): TypeName.ParameterizedType
  public fun TypeName.Classy.removeConstrains(): TypeName.Classy
  public fun TypeName.FunctionLiteral.removeConstrains(): TypeName.FunctionLiteral
  public fun TypeName.removeConstrains(): TypeName
  public fun TypeName.TypeVariable.removeConstrains(): TypeName.TypeVariable

  /**
   * Obtains type information for a class or interface given a TypeName
   */
  public val TypeName.type: Type?

  /**
   * Determine if this [TypeName.ParameterizedType] is in kinded position.
   * ex: `arrow.Kind<ForOption, A>` => true
   * ex: `Option<A>` => false
   *
   * The current definition of kinded for Arrow Meta is that a parameterized kinded type is a type that:
   * 1. Extends from arrow.Kind.
   * 2. The type has two type arguments.
   * 3. The first type argument is a type variable.
   */
  public val TypeName.ParameterizedType.kinded: Boolean

  /**
   * Performs a type application transforming a type in kinded position into it's concrete counterpart:
   * ex: `Kind<ForOption, A> -> Option<A>`
   */
  public val TypeName.downKind: TypeName
  public val TypeName.TypeVariable.downKind: TypeName
  public val TypeName.FunctionLiteral.downKind: TypeName
  public val TypeName.ParameterizedType.downKind: TypeName
  public val TypeName.WildcardType.downKind: TypeName
  public val TypeName.Classy.downKind: TypeName

  /**
   * Recursively gathers all the nested [TypeName.TypeVariable] inside a [TypeName]
   */
  public val TypeName.nestedTypeVariables: List<TypeName>
  public val TypeName.TypeVariable.nestedTypeVariables: List<TypeName>
  public val TypeName.WildcardType.nestedTypeVariables: List<TypeName>
  public val TypeName.FunctionLiteral.nestedTypeVariables: List<TypeName>
  public val TypeName.ParameterizedType.nestedTypeVariables: List<TypeName>
  public val TypeName.Classy.nestedTypeVariables: List<TypeName>

  /**
   * Normalizes potentially rogue types coming from Java introspection into their Kotlin counterpart.
   * ex: `java.lang.Integer -> Kotlin.Int`
   * It's implementation is partial and does not cover all corner cases.
   */
  public fun TypeName.asKotlin(): TypeName
  public fun TypeName.TypeVariable.asKotlin(): TypeName.TypeVariable
  public fun TypeName.ParameterizedType.asKotlin(): TypeName.ParameterizedType
  public fun TypeName.FunctionLiteral.asKotlin(): TypeName.FunctionLiteral
  public fun TypeName.WildcardType.asKotlin(): TypeName.WildcardType
  public fun TypeName.Classy.asKotlin(): TypeName.Classy

  /**
   * Attempts to lookup the platform counter type of a kotlin platform type like
   * ex: in the JVM kotlin.Int -> java.lang.Integer
   */
  public fun TypeName.Classy.asPlatform(): TypeName.Classy

  /**
   * Returns a suitable companion for this type considering it's kinded or conested position.
   * This is frequently use to project extensions or other static instances as needed via codegen
   */
  public val TypeName.projectedCompanion: TypeName

  /**
   * Resets all type arguments to [Any?]
   */
  public fun TypeName.widenTypeArgs(): TypeName

  /**
   * Returns a type name given a `KClass`
   */
  public fun <A : Any> TypeName.Companion.typeNameOf(clazz: KClass<A>): TypeName

  /**
   * Appends (...argN: Unit = Unit) at the end of the parameter lists of this function.
   * This is frequently done to work around JVM overload clashes specially when extending kinded values
   * which show the same JVM signature after erasure
   */
  public fun Func.addExtraDummyArg(): Func

  /**
   * Prepends (...argN: Unit = Unit) at the beginning of the parameter lists of this function.
   * This is frequently done to work around JVM overload clashes specially when extending kinded values
   * which show the same JVM signature after erasure
   */
  public fun Func.prependExtraDummyArg(): Func

  /**
   * Removes all dummy args from this function.
   * @see [addExtraDummyArg]
   */
  public fun Func.removeDummyArgs(): Func

  /**
   * Number of dummy arguments contained in this function
   */
  public fun Func.countDummyArgs(): Int

  /**
   * Removes all modifiers and annotations from this function and normalizes type variables upper bound
   * constrains to not explicitly include implicit types such as `java.lang.Object`.
   * Preserves all modifiers [keepModifiers]
   */
  public fun Func.removeConstrains(keepModifiers: Set<Modifier> = emptySet()): Func

  /**
   * Performs a type application transforming all parameter types in this function
   * in kinded position into it's concrete counterpart:
   * ex: `(fa: Kind<ForOption, A>) -> (fa: Option<A>)`
   */
  public fun Func.downKindParameters(): Func

  /**
   * Performs a type application transforming the receiver type in this function
   * in kinded position into it's concrete counterpart:
   * ex: `Kind<ForOption, A>.someFun(): A` -> `Option.someFun(): A`
   */
  public fun Func.downKindReceiver(): Func

  /**
   * Performs a type application transforming the return type in this function
   * in kinded position into it's concrete counterpart:
   * ex: `someFun(): Kind<ForOption, A>` -> `someFun(): Option<A>`
   */
  public fun Func.downKindReturnType(): Func

  /**
   * Applies default values to dummy args
   */
  public fun Func.defaultDummyArgValues(): Func

  /**
   * Whether this function contains a modifier such as Modifier.Final, etc.
   */
  public fun Func.containsModifier(modifier: Modifier): Boolean

  /**
   * @see [removeConstrains]
   */
  public fun Parameter.removeConstrains(): Parameter

  /**
   * Performs a type application transforming the type of this parameter
   * in kinded position into it's concrete counterpart:
   * ex: `arg: Kind<ForOption, A>` -> `arg: Option<A>`
   */
  public fun Parameter.downKind(): Parameter

  /**
   * Returns a new parameter with `Unit` as default value if this of type unit
   */
  public fun Parameter.defaultDummyArgValue(): Parameter

  /**
   * A block of code for a right hand side T O D O return
   */
  public val Code.Companion.TODO: Code

  /**
   * The list of functions a type class instance needs to implement to resolve
   * it's hierarchical dependencies to other type classes
   * ex: override fun MF(): arrow.typeclasses.Monad<F> in the KleisliMonadInstance
   * ```
   * fun <F, D> Companion.monad(MF: Monad<F>): KleisliMonad<F, D> =
   *   object : arrow.instances.KleisliMonad<F, D> { override fun MF(): arrow.typeclasses.Monad<F> = MF }`
   * ```
   */
  public val TypeClassInstance.requiredAbstractFunctions: List<Func>

  /**
   * The list of parameters a type class instance needs to be able to implement
   * the [requiredAbstractFunctions]
   * ex: override fun MF: Monad<F> in KleisliMonadInstance
   * ```
   * fun <F, D> Companion.monad(MF: Monad<F>): KleisliMonad<F, D> =
   *   object : arrow.instances.KleisliMonad<F, D> { override fun MF(): arrow.typeclasses.Monad<F> = MF }`
   * ```
   */
  public val TypeClassInstance.requiredParameters: List<Parameter>

  /**
   * A instance of @JvmName in the Arrow Meta AST
   */
  public fun JvmName(name: String): Annotation

  /**
   * A instance of @PublishedApi in the Arrow Meta AST
   */
  public fun PublishedApi(): Annotation

  /**
   * A instance of @SuppressAnnotation in the Arrow Meta AST
   */
  public fun SuppressAnnotation(vararg names: String): Annotation

  /**
   * Renders this generated method as deprecated presenting the alternative
   * forward or final Api in Arrow 1.0.
   */
  public fun DeprecatedAnnotation(msg: String, replaceWithExpression: String, imports: List<String>): Annotation
}
