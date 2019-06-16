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
import arrow.typeclasses.Order
import arrow.validation.RefinedPredicateException
import arrow.validation.Refinement

/**
 * `NonNegative` defines a subset of all Numbers which are non negative
 * (this is x >= 0)
 */
interface NonNegative<F, A : Number> : Refinement<F, A> {
  fun ORD(): Order<A>

  override fun A.refinement(): Boolean = !isNegative(ORD(), this)

  /**
   * Commented method or class
   *
   * {: data-executable='true'}
   *
   * ```kotlin:ank
   * import arrow.core.extensions.order
   * import arrow.validation.refinedTypes.numeric.validated.nonNegative.nonNegative
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   val result = 0.nonNegative(Int.order())
   *  //sampleEnd
   *
   *  println(result.isValid)
   *  }
   *  ```
   */
  fun A.nonNegative(): Kind<F, A> = refine(this)

  fun <B> A.nonNegative(f: (A) -> B): Kind<F, B> = refine(this, f)

  override fun invalidValueMsg(a: A): String = "$a must be greater or equal than 0"
}

@extension
interface ValidatedNonNegative<A : Number> : NonNegative<ValidatedPartialOf<Nel<RefinedPredicateException>>, A> {
  override fun ORD(): Order<A>

  override fun applicativeError(): ApplicativeError<ValidatedPartialOf<Nel<RefinedPredicateException>>,
    Nel<RefinedPredicateException>> =
    Validated.applicativeError(Nel.semigroup())
}

@extension
interface EitherNonNegative<A : Number> : NonNegative<EitherPartialOf<Nel<RefinedPredicateException>>, A> {
  override fun ORD(): Order<A>

  override fun applicativeError(): ApplicativeError<EitherPartialOf<Nel<RefinedPredicateException>>,
    Nel<RefinedPredicateException>> =
    Either.applicativeError()
}
