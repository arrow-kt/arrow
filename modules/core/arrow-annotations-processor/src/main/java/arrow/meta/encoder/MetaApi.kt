package arrow.meta.encoder

import arrow.meta.ast.*
import arrow.meta.ast.Annotation
import javax.lang.model.element.TypeElement
import kotlin.reflect.KClass

data class TypeClassInstance(
  val instance: Type,
  val dataType: Type,
  val typeClass: Type,
  val instanceTypeElement: TypeElement,
  val dataTypeTypeElement: TypeElement,
  val typeClassTypeElement: TypeElement,
  val projectedCompanion: TypeName
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
 * `Kind<ForIO, A> -> IO<A>`
 */
interface MetaApi {

  /**
   * Remove undesired constrains such as `java.lang.Object` which appears
   * in upper bound position in certain type shapes
   */
  fun TypeName.WildcardType.removeConstrains(): TypeName.WildcardType
  fun TypeName.ParameterizedType.removeConstrains(): TypeName.ParameterizedType
  fun TypeName.Classy.removeConstrains(): TypeName.Classy
  fun TypeName.removeConstrains(): TypeName
  fun TypeName.TypeVariable.removeConstrains(): TypeName.TypeVariable

  /**
   * Obtains type information for a class or interface given a TypeName
   */
  tailrec fun TypeName.asType(): Type?

  /**
   * Determine if this [TypeName.ParameterizedType] is in kinded position.
   * ex: `arrow.Kind<ForIO, A>` => true
   * ex: `IO<A>` => false
   *
   * The current definition of kinded for Arrow Meta is that a parameterized kinded type is a type that:
   * 1. Extends from arrow.Kind.
   * 2. The type has two type arguments.
   * 3. The first type argument is a type variable.
   */
  fun TypeName.ParameterizedType.isKinded(): Boolean

  /**
   * Performs a type application transforming a type in kinded position into it's concrete counterpart:
   * ex: `Kind<ForIO, A> -> IO<A>`
   */
  fun TypeName.downKind(): TypeName
  fun TypeName.TypeVariable.downKind(): TypeName.TypeVariable
  fun TypeName.ParameterizedType.downKind(): TypeName.ParameterizedType
  fun TypeName.WildcardType.downKind(): TypeName.WildcardType
  fun TypeName.Classy.downKind(): TypeName.Classy

  /**
   * Recursively gathers all the nested [TypeName.TypeVariable] inside a [TypeName]
   */
  fun TypeName.nestedTypeVariables(): List<TypeName>
  fun TypeName.TypeVariable.nestedTypeVariables(): List<TypeName>
  fun TypeName.WildcardType.nestedTypeVariables(): List<TypeName>
  fun TypeName.ParameterizedType.nestedTypeVariables(): List<TypeName>
  fun TypeName.Classy.nestedTypeVariables(): List<TypeName>


  /**
   * Normalizes potentially rogue types coming from Java introspection into their Kotlin counterpart.
   * ex: `java.lang.Integer -> Kotlin.Int`
   * It's implementation is partial and does not cover all corner cases.
   */
  fun TypeName.asKotlin(): TypeName
  fun TypeName.TypeVariable.asKotlin(): TypeName.TypeVariable
  fun TypeName.ParameterizedType.asKotlin(): TypeName.ParameterizedType
  fun TypeName.WildcardType.asKotlin(): TypeName.WildcardType
  fun TypeName.Classy.asKotlin(): TypeName.Classy

  /**
   * Returns a suitable companion for this type considering it's kinded or conested position.
   * This is frequently use to project extensions or other static instances as needed via codegen
   */
  fun TypeName.projectedCompanion(): TypeName

  /**
   * Returns a type name given a `KClass`
   */
  fun <A : Any> TypeName.Companion.typeNameOf(clazz: KClass<A>): TypeName

  /**
   * Appends (...argN: Unit = Unit) at the end of the parameter lists of this function.
   * This is frequently done to work around JVM overload clashes specially when extending kinded values
   * which show the same JVM signature after erasure
   */
  fun Func.addExtraDummyArg(): Func

  /**
   * Removes all dummy args from this function.
   * @see [addExtraDummyArg]
   */
  fun Func.removeDummyArgs(): Func

  /**
   * Number of dummy arguments contained in this function
   */
  fun Func.countDummyArgs(): Int

  /**
   * Removes all modifiers and annotations from this function and normalizes type variables upper bound
   * constrains to not explicitly include implicit types such as `java.lang.Object`
   */
  fun Func.removeConstrains(): Func

  /**
   * Performs a type application transforming all parameter types in this function
   * in kinded position into it's concrete counterpart:
   * ex: `(fa: Kind<ForIO, A>) -> (fa: IO<A>)`
   */
  fun Func.downKindParameters(): Func

  /**
   * Performs a type application transforming the receiver type in this function
   * in kinded position into it's concrete counterpart:
   * ex: `Kind<ForIO, A>.someFun(): A` -> `IO.someFun(): A`
   */
  fun Func.downKindReceiver(): Func

  /**
   * Performs a type application transforming the return type in this function
   * in kinded position into it's concrete counterpart:
   * ex: `someFun(): Kind<ForIO, A>` -> `someFun(): IO<A>`
   */
  fun Func.downKindReturnType(): Func

  /**
   * Applies default values to dummy args
   */
  fun Func.defaultDummyArgValues(): Func

  /**
   * Whether this function contains a modifier such as Modifier.Final, etc.
   */
  fun Func.containsModifier(modifier: Modifier): Boolean

  /**
   * @see [removeConstrains]
   */
  fun Parameter.removeConstrains(): Parameter

  /**
   * Performs a type application transforming the type of this parameter
   * in kinded position into it's concrete counterpart:
   * ex: `arg: Kind<ForIO, A>` -> `arg: IO<A>`
   */
  fun Parameter.downKind(): Parameter

  /**
   * Returns a new parameter with `Unit` as default value if this of type unit
   */
  fun Parameter.defaultDummyArgValue(): Parameter

  /**
   * A block of code for a right hand side T O D O return
   */
  val Code.Companion.TODO: Code

  /**
   * The list of functions a type class instance needs to implement to resolve
   * it's hierarchical dependencies to other type classes
   * ex: override fun MF(): arrow.typeclasses.Monad<F> in the KleisliMonadInstance
   * ```
   * fun <F, D> Companion.monad(MF: Monad<F>): KleisliMonadInstance<F, D> =
   *   object : arrow.instances.KleisliMonadInstance<F, D> { override fun MF(): arrow.typeclasses.Monad<F> = MF }`
   * ```
   */
  val TypeClassInstance.requiredAbstractFunctions: List<Func>

  /**
   * The list of parameters a type class instance needs to be able to implement
   * the [requiredAbstractFunctions]
   * ex: override fun MF: Monad<F> in KleisliMonadInstance
   * ```
   * fun <F, D> Companion.monad(MF: Monad<F>): KleisliMonadInstance<F, D> =
   *   object : arrow.instances.KleisliMonadInstance<F, D> { override fun MF(): arrow.typeclasses.Monad<F> = MF }`
   * ```
   */
  val TypeClassInstance.requiredParameters: List<Parameter>

  /**
   * A instance of @JvmName in the Arrow Meta AST
   */
  fun JvmName(name: String): Annotation

  /**
   * A instance of @SuppressAnnotation in the Arrow Meta AST
   */
  fun SuppressAnnotation(vararg names: String): Annotation

}