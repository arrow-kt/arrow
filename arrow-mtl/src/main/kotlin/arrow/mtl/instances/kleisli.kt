package arrow.mtl.instances

import arrow.*
import arrow.data.*
import arrow.instances.KleisliMonadInstance

@instance(Kleisli::class)
interface KleisliMonadReaderInstance<F, D> : KleisliMonadInstance<F, D>, MonadReader<KleisliKindPartial<F, D>, D> {

    override fun ask(): Kleisli<F, D, D> = Kleisli({ FF().pure(it) })

    override fun <A> local(f: (D) -> D, fa: KleisliKind<F, D, A>): Kleisli<F, D, A> = fa.ev().local(f)

}