package arrow.mtl.instances

import arrow.*
import arrow.data.*
import arrow.instances.KleisliMonadInstance
import arrow.mtl.MonadReader

@instance(Kleisli::class)
interface KleisliMonadReaderInstance<F, D> : KleisliMonadInstance<F, D>, MonadReader<KleisliPartialOf<F, D>, D> {

    override fun ask(): Kleisli<F, D, D> = Kleisli({ FF().pure(it) })

    override fun <A> local(f: (D) -> D, fa: KleisliOf<F, D, A>): Kleisli<F, D, A> = fa.reify().local(f)

}