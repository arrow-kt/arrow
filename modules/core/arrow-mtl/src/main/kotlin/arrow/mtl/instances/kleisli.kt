package arrow.mtl.instances

import arrow.data.Kleisli
import arrow.data.KleisliOf
import arrow.data.KleisliPartialOf
import arrow.data.fix
import arrow.instance
import arrow.instances.KleisliMonadInstance
import arrow.mtl.typeclasses.MonadReader

@instance(Kleisli::class)
interface KleisliMonadReaderInstance<F, D> : KleisliMonadInstance<F, D>, MonadReader<KleisliPartialOf<F, D>, D> {

    override fun ask(): Kleisli<F, D, D> = Kleisli({ FF().pure(it) })

    override fun <A> local(f: (D) -> D, fa: KleisliOf<F, D, A>): Kleisli<F, D, A> = fa.fix().local(f)

}
