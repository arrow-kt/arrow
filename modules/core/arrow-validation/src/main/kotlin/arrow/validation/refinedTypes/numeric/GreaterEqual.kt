package arrow.validation.refinedTypes.numeric

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
import arrow.validation.refinedTypes.bool.Not

interface GreaterEqual<F, A : Number> : Not<F, A> {

  fun ORD(): Order<A>
  fun min(): A

  override fun A.refinement(): Boolean = ORD().run { gte(min()) }

  override fun invalidValueMsg(a: A): String = "$a must be greater or equal than ${min()}"

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