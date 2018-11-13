package arrow.validation.refinedTypes

import arrow.Kind
import arrow.core.Either
import arrow.core.EitherPartialOf
import arrow.data.Nel
import arrow.data.Validated
import arrow.data.ValidatedPartialOf
import arrow.extension
import arrow.instances.either.applicativeError.applicativeError
import arrow.instances.nonemptylist.semigroup.semigroup
import arrow.instances.validated.applicativeError.applicativeError
import arrow.typeclasses.ApplicativeError
import arrow.typeclasses.Order
import arrow.validation.RefinedPredicateException
import arrow.validation.Refinement

interface Less<F, A : Number> : Refinement<F, A> {

  fun ORD(): Order<A>
  fun max(): A

  override fun A.refinement(): Boolean = ORD().run { lt(max()) }

  fun A.less(): Kind<F, A> = refine(this)

  fun <B> A.less(f: (A) -> B): Kind<F, B> = refine(this, f)

  companion object {
    fun errorMsg(x: Number, max: Number): String = "$x must be less than $max"
  }
}

@extension
interface ValidatedLess<A : Number> : Less<ValidatedPartialOf<Nel<RefinedPredicateException>>, A> {
  override fun ORD(): Order<A>
  override fun max(): A

  override fun applicativeError(): ApplicativeError<ValidatedPartialOf<Nel<RefinedPredicateException>>,
    Nel<RefinedPredicateException>> =
    Validated.applicativeError(Nel.semigroup())

  override fun invalidValueMsg(a: A): String = Less.errorMsg(a, max())
}

@extension
interface EitherLess<A : Number> : Less<EitherPartialOf<Nel<RefinedPredicateException>>, A> {
  override fun ORD(): Order<A>
  override fun max(): A

  override fun applicativeError(): ApplicativeError<EitherPartialOf<Nel<RefinedPredicateException>>,
    Nel<RefinedPredicateException>> =
    Either.applicativeError()

  override fun invalidValueMsg(a: A): String = Less.errorMsg(a, max())
}