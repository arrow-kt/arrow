package arrow.data.extensions

import arrow.Kind
import arrow.data.Sum
import arrow.data.SumPartialOf
import arrow.data.fix

import arrow.extension
import arrow.typeclasses.Comonad
import arrow.typeclasses.Eq
import arrow.typeclasses.Functor
import arrow.typeclasses.Hash
import arrow.undocumented

@extension
@undocumented
interface SumComonad<F, G> : Comonad<SumPartialOf<F, G>> {

  fun CF(): Comonad<F>

  fun CG(): Comonad<G>

  override fun <A, B> Kind<SumPartialOf<F, G>, A>.coflatMap(f: (Kind<SumPartialOf<F, G>, A>) -> B): Sum<F, G, B> =
      fix().coflatmap(CF(), CG(), f)

  override fun <A> Kind<SumPartialOf<F, G>, A>.extract(): A =
      fix().extract(CF(), CG())

  override fun <A, B> Kind<SumPartialOf<F, G>, A>.map(f: (A) -> B): Sum<F, G, B> =
      fix().map(CF(), CG(), f)
}

@extension
@undocumented
interface SumFunctor<F, G> : Functor<SumPartialOf<F, G>> {

  fun FF(): Functor<F>

  fun FG(): Functor<G>

  override fun <A, B> Kind<SumPartialOf<F, G>, A>.map(f: (A) -> B): Sum<F, G, B> =
      fix().map(FF(), FG(), f)
}

@extension
interface SumEq<F, G, A> : Eq<Sum<F, G, A>> {
  fun EQF(): Eq<Kind<F, A>>
  fun EQG(): Eq<Kind<G, A>>

  override fun Sum<F, G, A>.eqv(b: Sum<F, G, A>): Boolean =
    EQF().run { left.eqv(b.left) } &&
      EQG().run { right.eqv(b.right) }
}

@extension
interface SumHash<F, G, A> : Hash<Sum<F, G, A>>, SumEq<F, G, A> {
  fun HF(): Hash<Kind<F, A>>
  fun HG(): Hash<Kind<G, A>>

  override fun EQF(): Eq<Kind<F, A>> = HF()
  override fun EQG(): Eq<Kind<G, A>> = HG()

  override fun Sum<F, G, A>.hash(): Int = 31 * HF().run { left.hash() } + HG().run { right.hash() }
}
