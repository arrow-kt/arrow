package arrow.mtl.instances

import arrow.Kind
import arrow.core.Option
import arrow.data.Const
import arrow.data.ConstOf
import arrow.data.ConstPartialOf
import arrow.data.reify
import arrow.free.instances.ConstTraverseInstance
import arrow.instance
import arrow.mtl.TraverseFilter
import arrow.typeclasses.Applicative

@instance(Const::class)
interface ConstTraverseFilterInstance<X> : ConstTraverseInstance<X>, TraverseFilter<ConstPartialOf<X>> {

    override fun <T, U> map(fa: ConstOf<X, T>, f: (T) -> U): Const<X, U> = fa.extract().retag()

    override fun <G, A, B> traverseFilter(fa: ConstOf<X, A>, f: (A) -> Kind<G, Option<B>>, GA: Applicative<G>): Kind<G, ConstOf<X, B>> =
            fa.extract().traverseFilter(f, GA)
}
