package arrow.validation.refinedTypes.generic

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
import arrow.validation.RefinedPredicateException
import arrow.validation.Refinement

interface NonEmpty<F, A> : Refinement<F, A> {
  fun empty(): A

  override fun A.refinement(): Boolean = this != empty()

  fun A.nonEmpty(): Kind<F, A> = refine(this)

  fun <B> A.nonEmpty(f: (A) -> B): Kind<F, B> = refine(this, f)

  override fun invalidValueMsg(a: A): String = "$a must not be empty"
}

@extension
interface ValidatedNonEmpty<A> : NonEmpty<ValidatedPartialOf<Nel<RefinedPredicateException>>, A> {
  override fun empty(): A

  override fun applicativeError(): ApplicativeError<ValidatedPartialOf<Nel<RefinedPredicateException>>,
    Nel<RefinedPredicateException>> =
    Validated.applicativeError(Nel.semigroup())
}

@extension
interface EitherNonEmpty<A> : NonEmpty<EitherPartialOf<Nel<RefinedPredicateException>>, A> {
  override fun empty(): A

  override fun applicativeError(): ApplicativeError<EitherPartialOf<Nel<RefinedPredicateException>>,
    Nel<RefinedPredicateException>> =
    Either.applicativeError()
}
