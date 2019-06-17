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

internal fun <A : Number> isLessEqualThan(ORD: Order<A>, a: A, max: A): Boolean =
  ORD.run { a.lte(max) }

/**
 * `LessEqual` defines a subset of Numbers which are less or equal than [max]
 */
interface LessEqual<F, A : Number> : Refinement<F, A> {
  fun ORD(): Order<A>
  fun max(): A

  override fun A.refinement(): Boolean = isLessEqualThan(ORD(), this, max())

  override fun invalidValueMsg(a: A): String = "$a must be less or equal than ${max()}"

  /**
   * Commented method or class
   *
   * {: data-executable='true'}
   *
   * ```kotlin:ank
   * import arrow.core.extensions.order
   * import arrow.validation.refinedTypes.numeric.validated.lessEqual.lessEqual
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   val max = 400
   *
   *   val x = 100
   *   val y = 300
   *   val z = 200
   *
   *   val xyResult = (x + y).lessEqual(Int.order(), max)
   *   val xzResult = (x + z).lessEqual(Int.order(), max)
   *  //sampleEnd
   *
   *  println(xyResult.isValid)
   *  println(xzResult.isValid)
   *  }
   *  ```
   */
  fun A.lessEqual(): Kind<F, A> = refine(this)

  fun <B> A.lessEqual(f: (A) -> B): Kind<F, B> = refine(this, f)
}

@extension
interface ValidatedLessEqual<A : Number> : LessEqual<ValidatedPartialOf<Nel<RefinedPredicateException>>, A> {
  override fun ORD(): Order<A>
  override fun max(): A

  override fun applicativeError(): ApplicativeError<ValidatedPartialOf<Nel<RefinedPredicateException>>,
    Nel<RefinedPredicateException>> =
    Validated.applicativeError(Nel.semigroup())
}

@extension
interface EitherLessEqual<A : Number> : LessEqual<EitherPartialOf<Nel<RefinedPredicateException>>, A> {
  override fun ORD(): Order<A>
  override fun max(): A

  override fun applicativeError(): ApplicativeError<EitherPartialOf<Nel<RefinedPredicateException>>,
    Nel<RefinedPredicateException>> =
    Either.applicativeError()
}
