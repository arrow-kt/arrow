package arrow.validation.refinedTypes.numeric

import arrow.Kind
import arrow.core.Either
import arrow.core.EitherPartialOf
import arrow.core.Nel
import arrow.core.Validated
import arrow.core.ValidatedPartialOf
import arrow.extension
import arrow.core.extensions.either.applicativeError.applicativeError
import arrow.core.extensions.nonemptylist.semigroup.semigroup
import arrow.core.extensions.validated.applicativeError.applicativeError
import arrow.typeclasses.ApplicativeError
import arrow.typeclasses.Eq
import arrow.validation.RefinedPredicateException
import arrow.validation.Refinement

internal fun <A : Number> isNonZero(EQ: Eq<A>, a: A): Boolean =
  EQ.run { a.neqv(zero()) }

/**
 * `NonZero` defines a subset of Numbers which are not 0
 */
interface NonZero<F, A : Number> : Refinement<F, A> {
  fun EQ(): Eq<A>

  override fun A.refinement(): Boolean = isNonZero(EQ(), this)

  /**
   * Commented method or class
   *
   * {: data-executable='true'}
   *
   * ```kotlin:ank
   * import arrow.core.extensions.eq
   * import arrow.validation.refinedTypes.numeric.validated.nonZero.nonZero
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   val result = 0.nonZero(Int.eq())
   *  //sampleEnd
   *
   *  println(result.isValid)
   *  }
   *  ```
   */
  fun A.nonZero(): Kind<F, A> = refine(this)

  fun <B> A.nonZero(f: A.() -> B): Kind<F, B> = refine(this, f)

  companion object {
    fun errorMsg(x: Number): String = "$x cannot be 0"
  }
}

@extension
interface ValidatedNonZero<A : Number> :
  NonZero<ValidatedPartialOf<Nel<RefinedPredicateException>>, A> {
  override fun EQ(): Eq<A>

  override fun applicativeError(): ApplicativeError<ValidatedPartialOf<Nel<RefinedPredicateException>>,
    Nel<RefinedPredicateException>> =
    Validated.applicativeError(Nel.semigroup())

  override fun invalidValueMsg(a: A): String = NonZero.errorMsg(a)
}

@extension
interface EitherNonZero<A : Number> :
  NonZero<EitherPartialOf<Nel<RefinedPredicateException>>, A> {
  override fun EQ(): Eq<A>

  override fun applicativeError(): ApplicativeError<EitherPartialOf<Nel<RefinedPredicateException>>,
    Nel<RefinedPredicateException>> =
    Either.applicativeError()

  override fun invalidValueMsg(a: A): String = NonZero.errorMsg(a)
}
