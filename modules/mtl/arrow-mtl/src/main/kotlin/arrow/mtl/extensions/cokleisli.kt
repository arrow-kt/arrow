package arrow.mtl.extensions

import arrow.Kind
import arrow.mtl.Cokleisli
import arrow.mtl.CokleisliOf
import arrow.mtl.ForCokleisli
import arrow.mtl.fix
import arrow.extension
import arrow.typeclasses.Conested
import arrow.typeclasses.Contravariant
import arrow.typeclasses.Profunctor
import arrow.typeclasses.conest
import arrow.typeclasses.counnest
import arrow.typeclasses.fix

@extension
interface CokleisliContravariant<F, D> : Contravariant<Conested<Kind<ForCokleisli, F>, D>> {
  override fun <A, B> Kind<Conested<Kind<ForCokleisli, F>, D>, A>.contramap(f: (B) -> A): Kind<Conested<Kind<ForCokleisli, F>, D>, B> =
    counnest().fix().lmap(f).conest()

  fun <A, B> CokleisliOf<F, A, D>.contramapC(f: (B) -> A): CokleisliOf<F, B, D> =
    conest().contramap(f).counnest()
}

@extension
interface CokleisliProfunctor<F> : Profunctor<Kind<ForCokleisli, F>> {
  override fun <A, B, C, D> CokleisliOf<F, A, B>.dimap(fl: (C) -> A, fr: (B) -> D): Cokleisli<F, C, D> =
    fix().bimap(fl, fr)
}
