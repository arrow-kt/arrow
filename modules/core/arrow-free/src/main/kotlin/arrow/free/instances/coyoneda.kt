package arrow.free.instances

import arrow.*
import arrow.free.*
import arrow.typeclasses.Functor

@instance(Coyoneda::class)
interface CoyonedaFunctorInstance<F, G> : Functor<CoyonedaPartialOf<F, G>> {
    override fun <A, B> map(fa: CoyonedaOf<F, G, A>, f: (A) -> B): Coyoneda<F, G, B> = fa.fix().map(f)
}
