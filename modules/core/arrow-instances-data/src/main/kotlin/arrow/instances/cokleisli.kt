package arrow.instances

import arrow.Kind
import arrow.data.Cokleisli
import arrow.data.CokleisliOf
import arrow.data.ForCokleisli
import arrow.data.fix
import arrow.instance
import arrow.typeclasses.Profunctor

@instance(Cokleisli::class)
interface CokleisliProfunctorInstance<F> : Profunctor<Kind<ForCokleisli, F>> {
  override fun <A, B, C, D> CokleisliOf<F, A, B>.dimap(fl: (C) -> A, fr: (B) -> D): Cokleisli<F, C, D> =
    fix().bimap(fl, fr)
}
