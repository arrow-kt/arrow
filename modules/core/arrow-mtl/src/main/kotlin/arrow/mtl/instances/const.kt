package arrow.mtl.instances

import arrow.Kind
import arrow.core.Option
import arrow.data.Const
import arrow.data.ConstKind
import arrow.data.ConstKindPartial
import arrow.data.reify
import arrow.free.instances.ConstTraverseInstance
import arrow.instance
import arrow.mtl.TraverseFilter
import arrow.typeclasses.Applicative

@instance(Const::class)
interface ConstTraverseFilterInstance<X> : ConstTraverseInstance<X>, TraverseFilter<ConstKindPartial<X>> {

    override fun <T, U> map(fa: ConstKind<X, T>, f: (T) -> U): Const<X, U> = fa.reify().retag()

    override fun <G, A, B> traverseFilter(fa: ConstKind<X, A>, f: (A) -> Kind<G, Option<B>>, GA: Applicative<G>): Kind<G, ConstKind<X, B>> =
            fa.reify().traverseFilter(f, GA)
}