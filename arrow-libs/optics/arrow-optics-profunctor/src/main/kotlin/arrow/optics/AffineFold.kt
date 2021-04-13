package arrow.optics

import arrow.core.Either
import arrow.optics.internal.Choice
import arrow.optics.internal.Pro
import arrow.optics.internal.Profunctor

typealias AffineFold<S, A> = Optic_<AffineFoldK, S, A>

fun <S, A> Optic.Companion.aFolding(
  f: (S) -> A?
): AffineFold<S, A> =
  object : AffineFold<S, A> {
    override fun <P> Profunctor<P>.transform(focus: Pro<P, A, A>): Pro<P, S, S> =
      (this as Choice<P>).run {
        focus.left<A, A, Any?>().lMap { s: S ->
          f(s)?.let { Either.Left(it) } ?: Either.Right(Unit)
        } as Pro<P, S, S> // Safe because AffineFoldK guarantees use of Forget
      }
  }
