package arrow.validation.refinedTypes.string

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

interface NonEmptyString<F> : Refinement<F, String> {
  override fun String.refinement(): Boolean = this.isNotEmpty()

  fun String.nonEmptyString(): Kind<F, String> = refine(this)

  fun <A> String.nonEmptyString(f: (String) -> A): Kind<F, A> = refine(this, f)

  override fun invalidValueMsg(a: String): String = "\"$a\" must not be empty"
}

@extension
interface ValidatedNonEmptyString : NonEmptyString<ValidatedPartialOf<Nel<RefinedPredicateException>>> {
  override fun applicativeError(): ApplicativeError<ValidatedPartialOf<Nel<RefinedPredicateException>>,
    Nel<RefinedPredicateException>> =
    Validated.applicativeError(Nel.semigroup())
}

@extension
interface EitherNonEmptyString : NonEmptyString<EitherPartialOf<Nel<RefinedPredicateException>>> {
  override fun applicativeError(): ApplicativeError<EitherPartialOf<Nel<RefinedPredicateException>>,
    Nel<RefinedPredicateException>> =
    Either.applicativeError()
}
