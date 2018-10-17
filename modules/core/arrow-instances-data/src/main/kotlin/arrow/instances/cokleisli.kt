package arrow.instances

import arrow.Kind
import arrow.data.Cokleisli
import arrow.data.CokleisliOf
import arrow.data.ForCokleisli
import arrow.data.fix
import arrow.instance
import arrow.typeclasses.*

@instance(Cokleisli::class)
interface CokleisliContravariantInstance<F, D> : Contravariant<Conested<Kind<ForCokleisli, F>, D>> {
  override fun <A, B> Kind<Conested<Kind<ForCokleisli, F>, D>, A>.contramap(f: (B) -> A): Kind<Conested<Kind<ForCokleisli, F>, D>, B> =
      counnest().fix().lmap(f).conest()

  fun <A, B> CokleisliOf<F, A, D>.contramapC(f: (B) -> A): CokleisliOf<F, B, D> =
      conest().contramap(f).counnest()
}

@instance(Cokleisli::class)
interface CokleisliProfunctorInstance<F> : Profunctor<Kind<ForCokleisli, F>> {
  override fun <A, B, C, D> CokleisliOf<F, A, B>.dimap(fl: (C) -> A, fr: (B) -> D): Cokleisli<F, C, D> =
    fix().bimap(fl, fr)
}
