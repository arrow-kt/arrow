package arrow.optics

import arrow.optics.internal.Forget
import arrow.optics.internal.Pro
import arrow.optics.internal.Profunctor
import arrow.optics.internal.fix

typealias Getter<S, A> = Optic_<GetterK, S, A>

fun <S, A> Optic.Companion.get(f: (S) -> A): Getter<S, A> =
  object : Getter<S, A> {
    override fun <P> Profunctor<P>.transform(focus: Pro<P, A, A>): Pro<P, S, S> =
      // Safe cast because GetterK ensures we always use Forget for P
      focus.lMap(f) as Pro<P, S, S>
  }

fun <K : GetterK, S, A> S.view(optic: Optic_<K, S, A>): A =
  Forget.strong<A>().run { optic.run { transform(Forget { it }) } }
    .fix().f(this)
