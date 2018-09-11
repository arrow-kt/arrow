package arrow.instances

import arrow.Kind
import arrow.data.Sum
import arrow.data.SumPartialOf
import arrow.data.fix
import arrow.instance
import arrow.typeclasses.Comonad
import arrow.typeclasses.Functor

@instance(Sum::class)
interface ComonadSumInstance<F, G> : Comonad<SumPartialOf<F, G>> {

  fun CF(): Comonad<F>

  fun CG(): Comonad<G>

  override fun <A, B> Kind<SumPartialOf<F, G>, A>.coflatMap(f: (Kind<SumPartialOf<F, G>, A>) -> B): Kind<SumPartialOf<F, G>, B> =
      fix().extend(f, CF(), CG())

  override fun <A> Kind<SumPartialOf<F, G>, A>.extract(): A =
      fix().extract(CF(), CG())

  override fun <A, B> Kind<SumPartialOf<F, G>, A>.map(f: (A) -> B): Kind<SumPartialOf<F, G>, B> =
      fix().map(f, CF(), CG())
}

@instance(Sum::class)
interface FunctorSumInstance<F, G> : Functor<SumPartialOf<F, G>> {

  fun FF(): Functor<F>

  fun FG(): Functor<G>

  override fun <A, B> Kind<SumPartialOf<F, G>, A>.map(f: (A) -> B): Kind<SumPartialOf<F, G>, B> =
      fix().map(f, FF(), FG())
}
