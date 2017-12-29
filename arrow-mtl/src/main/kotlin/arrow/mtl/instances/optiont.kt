package arrow.mtl.instances

import arrow.*
import arrow.core.Option
import arrow.data.OptionT
import arrow.instances.OptionTFunctorInstance
import arrow.instances.OptionTTraverseInstance

@instance(OptionT::class)
interface OptionTFunctorFilterInstance<F> : OptionTFunctorInstance<F>, FunctorFilter<OptionTKindPartial<F>> {

    override fun <A, B> mapFilter(fa: OptionTKind<F, A>, f: (A) -> Option<B>): OptionT<F, B> =
            fa.ev().mapFilter(f, FF())

}

@instance(OptionT::class)
interface OptionTTraverseFilterInstance<F> :
        OptionTTraverseInstance<F>,
        TraverseFilter<OptionTKindPartial<F>> {

    override fun FFF(): TraverseFilter<F>

    override fun <G, A, B> traverseFilter(fa: OptionTKind<F, A>, f: (A) -> HK<G, Option<B>>, GA: Applicative<G>): HK<G, OptionT<F, B>> =
            fa.ev().traverseFilter(f, GA, FFF())

}