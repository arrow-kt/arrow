package arrow.optics

import arrow.optics.internal.Forget
import arrow.optics.internal.Functor
import arrow.optics.internal.IxForget
import arrow.optics.internal.IxLinearF
import arrow.optics.internal.Kind
import arrow.optics.internal.Pro
import arrow.optics.internal.Profunctor
import arrow.optics.internal.Strong
import arrow.optics.internal.fix

typealias Getter<S, A> = Optic_<GetterK, Any?, S, A>

fun <S, A> Optic.Companion.get(f: (S) -> A): Getter<S, A> =
  object : Getter<S, A> {
    override fun <P, J> Profunctor<P>.transform(focus: Pro<P, J, A, A>): Pro<P, (Any?) -> J, S, S> =
      // Safe cast because GetterK ensures we always use Forget for P
      (focus.lMap(f) as Pro<P, J, S, S>).ixMap { it(Unit) }
  }

typealias IxGetter<I, S, A> = Optic_<GetterK, I, S, A>

fun <S, I, A> Optic.Companion.ixGet(ff: (S) -> Pair<I, A>): IxGetter<I, S, A> =
  object : IxGetter<I, S, A> {
    override fun <P, J> Profunctor<P>.transform(focus: Pro<P, J, A, A>): Pro<P, (I) -> J, S, S> =
      (this as Strong<P>).run {
        focus.ilinear(object : IxLinearF<I, S, S, A, A> {
          override fun <F> invoke(FF: Functor<F>, s: S, f: (I, A) -> Kind<F, A>): Kind<F, S> =
            ff(s).let { (i, a) -> FF.map(f(i, a)) { s } }
        })
      }
  }

fun <K : GetterK, I, S, A> S.view(optic: Optic_<K, I, S, A>): A =
  Forget.strong<A>().run { optic.run { transform(Forget<A, Nothing, A, A> { it }) } }
    .fix().f(this)

fun <K : GetterK, I, S, A> S.ixView(optic: Optic_<K, I, S, A>): Pair<I, A> =
  IxForget.strong<Pair<I, A>>().run { optic.run { transform(IxForget { i: I, a: A -> i to a }) } }
    .fix().f({ it }, this)
