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
import arrow.validation.RefinedPredicateException
import arrow.validation.Refinement
import arrow.validation.refinedTypes.bool.Not

interface LessEqual<F, A : Number> : Not<F, A> {
  override fun REF(): Refinement<F, A>

  override fun applicativeError(): ApplicativeError<F, Nel<RefinedPredicateException>>

  override fun invalidValueMsg(a: A): String

  fun A.lessEqual(): Kind<F, A> = refine(this)

  fun <B> A.lessEqual(f: (A) -> B): Kind<F, B> = refine(this, f)

  companion object {
    fun errorMsg(x: Number) = "$x must be less or equal than defined max"
  }
}

@extension
interface ValidatedLessEqual<A : Number> : LessEqual<ValidatedPartialOf<Nel<RefinedPredicateException>>, A> {
  override fun REF(): Refinement<ValidatedPartialOf<Nel<RefinedPredicateException>>, A>

  override fun applicativeError(): ApplicativeError<ValidatedPartialOf<Nel<RefinedPredicateException>>,
    Nel<RefinedPredicateException>> =
    Validated.applicativeError(Nel.semigroup())

  override fun invalidValueMsg(a: A): String = LessEqual.errorMsg(a)
}

@extension
interface EitherLessEqual<A : Number> : LessEqual<EitherPartialOf<Nel<RefinedPredicateException>>, A> {
  override fun REF(): Refinement<EitherPartialOf<Nel<RefinedPredicateException>>, A>

  override fun applicativeError(): ApplicativeError<EitherPartialOf<Nel<RefinedPredicateException>>,
    Nel<RefinedPredicateException>> =
    Either.applicativeError()

  override fun invalidValueMsg(a: A): String = LessEqual.errorMsg(a)
}