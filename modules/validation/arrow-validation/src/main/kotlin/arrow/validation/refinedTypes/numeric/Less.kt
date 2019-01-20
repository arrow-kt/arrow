package arrow.validation.refinedTypes.numeric

import arrow.Kind
import arrow.core.Either
import arrow.core.EitherPartialOf
import arrow.data.Nel
import arrow.data.Validated
import arrow.data.ValidatedPartialOf
import arrow.extension
import arrow.core.extensions.either.applicativeError.applicativeError
import arrow.data.extensions.nonemptylist.semigroup.semigroup
import arrow.data.extensions.validated.applicativeError.applicativeError
import arrow.typeclasses.ApplicativeError
import arrow.typeclasses.Order
import arrow.validation.RefinedPredicateException
import arrow.validation.Refinement

internal fun <A: Number> isLessThan(ORD: Order<A>, a: A, max: A): Boolean =
  ORD.run { a.lt(max) }

/**
 * `Less` defines a subset of Numbers which are less than [max]
 */
interface Less<F, A : Number> : Refinement<F, A> {

  fun ORD(): Order<A>
  fun max(): A

  override fun A.refinement(): Boolean = isLessThan(ORD(), this, max())

  /**
   * Commented method or class
   *
   * {: data-executable='true'}
   *
   * ```kotlin:ank
   * import arrow.core.extensions.order
   * import arrow.validation.refinedTypes.numeric.validated.less.less
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   val max = 350
   *
   *   val x = 100
   *   val y = 300
   *   val z = 200
   *
   *   val xyResult = (x + y).less(Int.order(), max)
   *   val xzResult = (x + z).less(Int.order(), max)
   *  //sampleEnd
   *
   *  println(xyResult.isValid)
   *  println(xzResult.isValid)
   *  }
   *  ```
   */
  fun A.less(): Kind<F, A> = refine(this)

  fun <B> A.less(f: (A) -> B): Kind<F, B> = refine(this, f)

  override fun invalidValueMsg(a: A): String = "$a must be less ${max()}"
}

@extension
interface ValidatedLess<A : Number> :
  Less<ValidatedPartialOf<Nel<RefinedPredicateException>>, A> {
  override fun ORD(): Order<A>
  override fun max(): A

  override fun applicativeError(): ApplicativeError<ValidatedPartialOf<Nel<RefinedPredicateException>>,
    Nel<RefinedPredicateException>> =
    Validated.applicativeError(Nel.semigroup())
}

@extension
interface EitherLess<A : Number> :
  Less<EitherPartialOf<Nel<RefinedPredicateException>>, A> {
  override fun ORD(): Order<A>
  override fun max(): A

  override fun applicativeError(): ApplicativeError<EitherPartialOf<Nel<RefinedPredicateException>>,
    Nel<RefinedPredicateException>> =
    Either.applicativeError()
}