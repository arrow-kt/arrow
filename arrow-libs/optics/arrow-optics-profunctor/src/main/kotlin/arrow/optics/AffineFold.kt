package arrow.optics

import arrow.core.Either
import arrow.optics.internal.Choice
import arrow.optics.internal.Pro
import arrow.optics.internal.Profunctor

typealias AffineFold<S, A> = Optic<AffineFoldK, Any?, S, Nothing, A, Nothing>

fun <S, A> Optic.Companion.aFolding(
  f: (S) -> A?
): AffineFold<S, A> =
  object : AffineFold<S, A> {
    override fun <P, J> Profunctor<P>.transform(focus: Pro<P, J, A, Nothing>): Pro<P, (Any?) -> J, S, Nothing> =
      (this as Choice<P>).run {
        focus.left<J, A, Nothing, Any?>().lMap { s: S ->
          f(s)?.let { Either.Left(it) } ?: Either.Right(Unit)
        } as Pro<P, J, S, Nothing> // Safe because AffineFoldK guarantees use of Forget
      }.ixMap { it(Unit) }
  }

typealias IxAffineFold<I, S, A> = Optic<AffineFoldK, I, S, Nothing, A, Nothing>

fun <I, S, A> Optic.Companion.ixAFolding(
  f: (S) -> Pair<I, A>?
): IxAffineFold<I, S, A> =
  object : IxAffineFold<I, S, A> {
    override fun <P, J> Profunctor<P>.transform(focus: Pro<P, J, A, Nothing>): Pro<P, (I) -> J, S, Nothing> =
      (this as Choice<P>).run {
        focus.left<J, A, Nothing, Any?>().lMap { s: S ->
          f(s)?.let { Either.Left(it.second) } ?: Either.Right(Unit)
        } as Pro<P, (I) -> J, S, Nothing> // Safe because AffineFoldK guarantees use of Forget
      }.ixMap { f -> { i: I -> f(i) } }
  }

