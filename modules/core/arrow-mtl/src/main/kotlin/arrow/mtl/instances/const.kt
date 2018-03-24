package arrow.mtl.instances

import arrow.Kind
import arrow.core.Option
import arrow.data.Const
import arrow.data.ConstOf
import arrow.data.ConstPartialOf
import arrow.data.fix
import arrow.free.instances.ConstTraverseInstance
import arrow.instance
import arrow.mtl.typeclasses.TraverseFilter
import arrow.typeclasses.Applicative

@instance(Const::class)
interface ConstTraverseFilterInstance<X> : ConstTraverseInstance<X>, TraverseFilter<ConstPartialOf<X>> {

    override fun <T, U> map(fa: ConstOf<X, T>, f: (T) -> U): Const<X, U> = fa.fix().retag()

    override fun <G, A, B> Applicative<G>.traverseFilter(fa: Kind<ConstPartialOf<X>, A>, f: (A) -> Kind<G, Option<B>>): Kind<G, ConstOf<X, B>> =
            fa.fix().traverseFilter(f, this)
}
