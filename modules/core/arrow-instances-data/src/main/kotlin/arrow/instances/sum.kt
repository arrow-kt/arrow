package arrow.instances

import arrow.Kind
import arrow.data.Sum
import arrow.data.SumPartialOf
import arrow.data.fix
import arrow.instance
import arrow.typeclasses.Comonad
import arrow.typeclasses.Functor

@instance
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

@instance
interface FunctorSumInstance<F, G> : Functor<SumPartialOf<F, G>> {

  fun FF(): Functor<F>

  fun FG(): Functor<G>

  override fun <A, B> Kind<SumPartialOf<F, G>, A>.map(f: (A) -> B): Sum<F, G, B> =
      fix().map(FF(), FG(), f)
}

class SumContext<F, G>(val CF: Comonad<F>, val CG: Comonad<G>) : ComonadSumInstance<F, G> {
  override fun CF(): Comonad<F> = CF
  override fun CG(): Comonad<G> = CG
}

class SumContextPartiallyApplied<F, G>(val CF: Comonad<F>, val CG: Comonad<G>) {
  infix fun <A> extensions(f: SumContext<F, G>.() -> A): A =
      f(SumContext(CF, CG))
}

fun <F, G> ForSum(CF: Comonad<F>, CG: Comonad<G>): SumContextPartiallyApplied<F, G> =
    SumContextPartiallyApplied(CF, CG)