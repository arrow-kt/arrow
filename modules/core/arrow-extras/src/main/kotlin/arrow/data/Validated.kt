package arrow.data

import arrow.Kind
import arrow.core.*
import arrow.higherkind
import arrow.typeclasses.Applicative
import arrow.typeclasses.Semigroup

typealias ValidatedNel<E, A> = Validated<Nel<E>, A>
typealias Valid<A> = Validated.Valid<A>
typealias Invalid<E> = Validated.Invalid<E>

/**
 * Port of https://github.com/typelevel/cats/blob/master/core/src/main/scala/cats/data/Validated.scala
 */
@higherkind
sealed class Validated<out E, out A> : ValidatedOf<E, A> {

  companion object {

    fun <E, A> invalidNel(e: E): ValidatedNel<E, A> = Invalid(NonEmptyList(e, listOf()))

    fun <E, A> validNel(a: A): ValidatedNel<E, A> = Valid(a)

    /**
     * Converts a `Try<A>` to a `Validated<Throwable, A>`.
     */
    fun <A> fromTry(t: Try<A>): Validated<Throwable, A> = t.fold({ Invalid(it) }, { Valid(it) })

    /**
     * Converts an `Either<A, B>` to an `Validated<A, B>`.
     */
    fun <E, A> fromEither(e: Either<E, A>): Validated<E, A> = e.fold({ Invalid(it) }, { Valid(it) })

    /**
     * Converts an `Option<B>` to an `Validated<A, B>`, where the provided `ifNone` values is returned on
     * the invalid of the `Validated` when the specified `Option` is `None`.
     */
    fun <E, A> fromOption(o: Option<A>, ifNone: () -> E): Validated<E, A> =
      o.fold(
        { Invalid(ifNone()) },
        { Valid(it) }
      )

  }

  data class Valid<out A>(val a: A) : Validated<Nothing, A>()

  data class Invalid<out E>(val e: E) : Validated<E, Nothing>()

  inline fun <B> fold(fe: (E) -> B, fa: (A) -> B): B =
    when (this) {
      is Valid -> fa(a)
      is Invalid -> (fe(e))
    }

  val isValid =
    fold({ false }, { true })
  val isInvalid =
    fold({ true }, { false })

  /**
   * Is this Valid and matching the given predicate
   */
  fun exist(predicate: (A) -> Boolean): Boolean = fold({ false }, { predicate(it) })

  /**
   * Converts the value to an Either<E, A>
   */
  fun toEither(): Either<E, A> = fold({ Left(it) }, { Right(it) })

  /**
   * Returns Valid values wrapped in Some, and None for Invalid values
   */
  fun toOption(): Option<A> = fold({ None }, { Some(it) })

  /**
   * Convert this value to a single element List if it is Valid,
   * otherwise return an empty List
   */
  fun toList(): List<A> = fold({ listOf() }, { listOf(it) })

  /** Lift the Invalid value into a NonEmptyList. */
  fun toValidatedNel(): ValidatedNel<E, A> =
    fold(
      { invalidNel(it) },
      { Valid(it) }
    )

  /**
   * Convert to an Either, apply a function, convert back. This is handy
   * when you want to use the Monadic properties of the Either type.
   */
  fun <EE, B> withEither(f: (Either<E, A>) -> Either<EE, B>): Validated<EE, B> = fromEither(f(toEither()))

  /**
   * Validated is a [functor.Bifunctor], this method applies one of the
   * given functions.
   */
  fun <EE, AA> bimap(fe: (E) -> EE, fa: (A) -> AA): Validated<EE, AA> = fold({ Invalid(fe(it)) }, { Valid(fa(it)) })

  /**
   * Apply a function to a Valid value, returning a new Valid value
   */
  fun <B> map(f: (A) -> B): Validated<E, B> = bimap(::identity) { f(it) }

  /**
   * Apply a function to an Invalid value, returning a new Invalid value.
   * Or, if the original valid was Valid, return it.
   */
  fun <EE> leftMap(f: (E) -> EE): Validated<EE, A> = bimap({ f(it) }, ::identity)

  /**
   * apply the given function to the value with the given B when
   * valid, otherwise return the given B
   */
  fun <B> foldLeft(b: B, f: (B, A) -> B): B = fold({ b }, { f(b, it) })

  fun <B> foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
    when (this) {
      is Valid -> f(this.a, lb)
      is Invalid -> lb
    }

  fun swap(): Validated<A, E> = fold({ Valid(it) }, { Invalid(it) })
}

/**
 * Return the Valid value, or the default if Invalid
 */
fun <E, B> ValidatedOf<E, B>.getOrElse(default: () -> B): B =
    fix().fold({ default() }, ::identity)

/**
 * Return the Valid value, or null if Invalid
 */
fun <E, B> ValidatedOf<E, B>.orNull(): B? =
  getOrElse { null }

/**
 * Return the Valid value, or the result of f if Invalid
 */
fun <E, B> ValidatedOf<E, B>.valueOr(f: (E) -> B): B =
  fix().fold({ f(it) }, ::identity)

/**
 * If `this` is valid return `this`, otherwise if `that` is valid return `that`, otherwise combine the failures.
 * This is similar to [orElse] except that here failures are accumulated.
 */
fun <E, A> ValidatedOf<E, A>.findValid(SE: Semigroup<E>, that: () -> Validated<E, A>): Validated<E, A> =
  fix().fold(

    { e ->
      that().fold(
        { ee -> Invalid(SE.run { e.combine(ee) }) },
        { Valid(it) }
      )
    },
    { Valid(it) }
  )

/**
 * Return this if it is Valid, or else fall back to the given default.
 * The functionality is similar to that of [findValid] except for failure accumulation,
 * where here only the error on the right is preserved and the error on the left is ignored.
 */
fun <E, A> ValidatedOf<E, A>.orElse(default: () -> Validated<E, A>): Validated<E, A> =
  fix().fold(
    { default() },
    { Valid(it) }
  )

/**
 * From Apply:
 * if both the function and this value are Valid, apply the function
 */
fun <E, A, B> ValidatedOf<E, A>.ap(SE: Semigroup<E>, f: Validated<E, (A) -> B>): Validated<E, B> =
  fix().fold(
    { e -> f.fold({ Invalid(SE.run { it.combine(e) }) }, { Invalid(e) }) },
    { a -> f.fold(::Invalid) { Valid(it(a)) } }
  )

fun <E, A> ValidatedOf<E, A>.handleLeftWith(f: (E) -> ValidatedOf<E, A>): Validated<E, A> =
  fix().fold({ f(it).fix() }, ::Valid)

fun <G, E, A, B> ValidatedOf<E, A>.traverse(GA: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, Validated<E, B>> = GA.run {
  fix().fold({ e -> just(Invalid(e)) }, { a -> f(a).map(::Valid) })
}

fun <G, E, A> ValidatedOf<E, Kind<G, A>>.sequence(GA: Applicative<G>): Kind<G, Validated<E, A>> =
  fix().traverse(GA, ::identity)

fun <E, A> ValidatedOf<E, A>.combine(SE: Semigroup<E>,
                                            SA: Semigroup<A>,
                                            y: ValidatedOf<E, A>): Validated<E, A> =
  y.fix().let { that ->
    when {
      this is Valid && that is Valid -> Valid(SA.run { a.combine(that.a) })
      this is Invalid && that is Invalid -> Invalid(SE.run { e.combine(that.e) })
      this is Invalid -> this
      else -> that
    }
  }

fun <E, A> ValidatedOf<E, A>.combineK(SE: Semigroup<E>, y: ValidatedOf<E, A>): Validated<E, A> {
  val xev = fix()
  val yev = y.fix()
  return when (xev) {
    is Valid -> xev
    is Invalid -> when (yev) {
      is Invalid -> Invalid(SE.run { xev.e.combine(yev.e) })
      is Valid -> yev
    }
  }
}

/**
 * Converts the value to an Ior<E, A>
 */
fun <E, A> ValidatedOf<E, A>.toIor(): Ior<E, A> =
  fix().fold({ Ior.Left(it) }, { Ior.Right(it) })

fun <E, A> A.valid(): Validated<E, A> =
  Valid(this)

fun <E, A> E.invalid(): Validated<E, A> =
  Invalid(this)

fun <E, A> A.validNel(): ValidatedNel<E, A> =
  Validated.validNel(this)

fun <E, A> E.invalidNel(): ValidatedNel<E, A> =
  Validated.invalidNel(this)
