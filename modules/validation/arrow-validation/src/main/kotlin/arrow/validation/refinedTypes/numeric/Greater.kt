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

internal fun <A : Number> isGreaterThan(ORD: Order<A>, a: A, min: A): Boolean =
  ORD.run { a.gt(min) }

/**
 * `Greater` defines a subset of Numbers wich are greater than [min].
 */
interface Greater<F, A : Number> : Refinement<F, A> {

  fun ORD(): Order<A>
  fun min(): A

  override fun A.refinement(): Boolean = isGreaterThan(ORD(), this, min())

  /**
   * Commented method or class
   *
   * {: data-executable='true'}
   *
   * ```kotlin:ank
   * import arrow.core.extensions.order
   * import arrow.validation.refinedTypes.numeric.validated.greater.greater
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   val min = 350
   *
   *   val x = 100
   *   val y = 300
   *   val z = 200
   *
   *   val xyResult = (x + y).greater(Int.order(), min)
   *   val xzResult = (x + z).greater(Int.order(), min)
   *  //sampleEnd
   *
   *  println(xyResult.isValid)
   *  println(xzResult.isValid)
   *  }
   *  ```
   */
  fun A.greater(): Kind<F, A> = refine(this)

  fun <B> A.greater(f: (A) -> B): Kind<F, B> = refine(this, f)

  override fun invalidValueMsg(a: A): String = "$a must be greater than ${min()}"
}

@extension
interface ValidatedGreater<A : Number> : Greater<ValidatedPartialOf<Nel<RefinedPredicateException>>, A> {
  override fun ORD(): Order<A>
  override fun min(): A

  override fun applicativeError(): ApplicativeError<ValidatedPartialOf<Nel<RefinedPredicateException>>,
    Nel<RefinedPredicateException>> =
    Validated.applicativeError(Nel.semigroup())
}

@extension
interface EitherGreater<A : Number> : Greater<EitherPartialOf<Nel<RefinedPredicateException>>, A> {
  override fun ORD(): Order<A>
  override fun min(): A

  override fun applicativeError(): ApplicativeError<EitherPartialOf<Nel<RefinedPredicateException>>,
    Nel<RefinedPredicateException>> =
    Either.applicativeError()
}
