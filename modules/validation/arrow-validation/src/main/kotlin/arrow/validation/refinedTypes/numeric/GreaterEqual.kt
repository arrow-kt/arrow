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

internal fun <A : Number> isGreaterEqualThan(ORD: Order<A>, a: A, min: A): Boolean =
  ORD.run { a.gte(min) }

/**
 * `GreaterEqual` defines a subset of Numbers which are greater or equal than [min]
 */
interface GreaterEqual<F, A : Number> : Refinement<F, A> {

  fun ORD(): Order<A>
  fun min(): A

  override fun A.refinement(): Boolean = isGreaterEqualThan(ORD(), this, min())

  override fun invalidValueMsg(a: A): String = "$a must be greater or equal than ${min()}"

  /**
   * Commented method or class
   *
   * {: data-executable='true'}
   *
   * ```kotlin:ank
   * import arrow.core.extensions.order
   * import arrow.validation.refinedTypes.numeric.validated.greaterEqual.greaterEqual
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   val min = 400
   *
   *   val x = 100
   *   val y = 300
   *   val z = 200
   *
   *   val xyResult = (x + y).greaterEqual(Int.order(), min)
   *   val xzResult = (x + z).greaterEqual(Int.order(), min)
   *  //sampleEnd
   *
   *  println(xyResult.isValid)
   *  println(xzResult.isValid)
   *  }
   *  ```
   */
  fun A.greaterEqual(): Kind<F, A> = refine(this)

  fun <B> A.greaterEqual(f: (A) -> B): Kind<F, B> = refine(this, f)
}

@extension
interface ValidatedGreaterEqual<A : Number> : GreaterEqual<ValidatedPartialOf<Nel<RefinedPredicateException>>, A> {
  override fun ORD(): Order<A>
  override fun min(): A

  override fun applicativeError(): ApplicativeError<ValidatedPartialOf<Nel<RefinedPredicateException>>, Nel<RefinedPredicateException>> =
    Validated.applicativeError(Nel.semigroup())
}

@extension
interface EitherGreaterEqual<A : Number> : GreaterEqual<EitherPartialOf<Nel<RefinedPredicateException>>, A> {
  override fun ORD(): Order<A>
  override fun min(): A

  override fun applicativeError(): ApplicativeError<EitherPartialOf<Nel<RefinedPredicateException>>, Nel<RefinedPredicateException>> =
    Either.applicativeError()
}