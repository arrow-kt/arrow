package arrow.optics

import arrow.core.Either
import arrow.core.identity
import arrow.optics.internal.Choice
import arrow.optics.internal.Pro
import arrow.optics.internal.Profunctor
import arrow.optics.internal.Strong

typealias AffineTraversal<S, A> = PAffineTraversal<S, S, A, A>
typealias PAffineTraversal<S, T, A, B> = Optic<AffineTraversalK, S, T, A, B>

fun <S, T, A, B> Optic.Companion.aTraversing(
  match: (S) -> Either<T, A>,
  update: (S, B) -> T
): PAffineTraversal<S, T, A, B> =
  object : PAffineTraversal<S, T, A, B> {
    override fun <P> Profunctor<P>.transform(focus: Pro<P, A, B>): Pro<P, S, T> {
      val r = (this as Choice<P>).run {
        focus.left<A, B, T>()
      }
      return (this as Strong<P>).run {
        r.first<Either<A, T>, Either<B, T>, S>().dimap({ s ->
          match(s) to s
        }, { (e, s) ->
          e.fold({ b -> update(s, b) }, ::identity)
        })
      }
    }
  }
