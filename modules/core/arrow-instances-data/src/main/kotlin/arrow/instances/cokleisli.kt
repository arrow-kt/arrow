package arrow.instances

import arrow.Kind2
import arrow.data.Cokleisli
import arrow.data.ForCokleisli
import arrow.instance
import arrow.typeclasses.Profunctor

@instance(Cokleisli::class)
interface CokleisliProfunctorInstance : Profunctor<ForCokleisli> {
    override fun <A, B, C, D> Kind2<ForCokleisli, A, B>.dimap(fl: (C) -> A, fr: (B) -> D): Kind2<ForCokleisli, C, D> =
        fix().bimap(fl, fr)
}
