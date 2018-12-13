package arrow.instances

import arrow.Kind
import arrow.data.Sum
import arrow.data.SumPartialOf
import arrow.data.fix
import arrow.deprecation.ExtensionsDSLDeprecated
import arrow.extension
import arrow.typeclasses.Comonad
import arrow.typeclasses.Eq
import arrow.typeclasses.Functor
import arrow.typeclasses.Hash

@extension
interface ComonadSumInstance<F, G> : Comonad<SumPartialOf<F, G>> {

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
interface FunctorSumInstance<F, G> : Functor<SumPartialOf<F, G>> {

  fun FF(): Functor<F>

  fun FG(): Functor<G>

  override fun <A, B> Kind<SumPartialOf<F, G>, A>.map(f: (A) -> B): Sum<F, G, B> =
      fix().map(FF(), FG(), f)
}

@extension
interface EqSumInstance<F, G, A> : Eq<Sum<F, G, A>> {
  fun EQF(): Eq<Kind<F, A>>
  fun EQG(): Eq<Kind<G, A>>

  override fun Sum<F, G, A>.eqv(b: Sum<F, G, A>): Boolean =
    EQF().run { left.eqv(b.left) } &&
      EQG().run { right.eqv(b.right) }
}

@extension
interface HashSumInstance<F, G, A> : Hash<Sum<F, G, A>>, EqSumInstance<F, G, A> {
  fun HF(): Hash<Kind<F, A>>
  fun HG(): Hash<Kind<G, A>>

  override fun EQF(): Eq<Kind<F, A>> = HF()
  override fun EQG(): Eq<Kind<G, A>> = HG()

  override fun Sum<F, G, A>.hash(): Int = 31 * HF().run { left.hash() } + HG().run { right.hash() }
}

class SumContext<F, G>(val CF: Comonad<F>, val CG: Comonad<G>) : ComonadSumInstance<F, G> {
  override fun CF(): Comonad<F> = CF
  override fun CG(): Comonad<G> = CG
}

class SumContextPartiallyApplied<F, G>(val CF: Comonad<F>, val CG: Comonad<G>) {
  @Deprecated(ExtensionsDSLDeprecated)
  infix fun <A> extensions(f: SumContext<F, G>.() -> A): A =
      f(SumContext(CF, CG))
}

fun <F, G> ForSum(CF: Comonad<F>, CG: Comonad<G>): SumContextPartiallyApplied<F, G> =
    SumContextPartiallyApplied(CF, CG)