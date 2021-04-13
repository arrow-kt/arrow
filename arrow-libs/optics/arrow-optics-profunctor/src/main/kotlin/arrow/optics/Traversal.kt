package arrow.optics

import arrow.optics.internal.Applicative
import arrow.optics.internal.Kind
import arrow.optics.internal.Pro
import arrow.optics.internal.Profunctor
import arrow.optics.internal.Star
import arrow.optics.internal.Traversing
import arrow.optics.internal.WanderF
import arrow.optics.internal.fix

typealias Traversal<S, A> = PTraversal<S, S, A, A>
typealias PTraversal<S, T, A, B> = Optic<TraversalK, S, T, A, B>

fun <S, T, A, B> Optic.Companion.traversing(
  f: WanderF<S, T, A, B>
): PTraversal<S, T, A, B> =
  object : PTraversal<S, T, A, B> {
    override fun <P> Profunctor<P>.transform(focus: Pro<P, A, B>): Pro<P, S, T> =
      (this as Traversing<P>).run {
        focus.wander(f)
      }
  }

fun <K : TraversalK, S, T, A, B, F> S.traverseOf(
  optic: Optic<K, S, T, A, B>,
  AF: Applicative<F>,
  f: (A) -> Kind<F, B>
): Kind<F, T> =
  Star.traversing(AF).run { optic.run { transform(Star(f)) } }.fix().f(this)
