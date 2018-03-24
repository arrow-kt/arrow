package arrow.mtl.instances

import arrow.Kind
import arrow.data.Kleisli
import arrow.data.KleisliPartialOf
import arrow.data.fix
import arrow.instance
import arrow.instances.KleisliMonadInstance
import arrow.mtl.typeclasses.MonadReader

@instance(Kleisli::class)
interface KleisliMonadReaderInstance<F, D> : KleisliMonadInstance<F, D>, MonadReader<KleisliPartialOf<F, D>, D> {

    override fun ask(): Kleisli<F, D, D> = Kleisli({ FF().pure(it) })

    override fun <A> Kind<KleisliPartialOf<F, D>, A>.local(f: (D) -> D): Kleisli<F, D, A> = fix().local(f)

}
