package arrow.mtl.instances

import arrow.Kind
import arrow.core.Option
import arrow.data.*
import arrow.instance
import arrow.instances.OptionTFunctorInstance
import arrow.instances.OptionTTraverseInstance
import arrow.mtl.FunctorFilter
import arrow.mtl.TraverseFilter
import arrow.mtl.syntax.traverseFilter
import arrow.typeclasses.Applicative

@instance(OptionT::class)
interface OptionTFunctorFilterInstance<F> : OptionTFunctorInstance<F>, FunctorFilter<OptionTPartialOf<F>> {

    override fun <A, B> mapFilter(fa: OptionTOf<F, A>, f: (A) -> Option<B>): OptionT<F, B> =
            fa.fix().mapFilter(f, FF())

}

@instance(OptionT::class)
interface OptionTTraverseFilterInstance<F> :
        OptionTTraverseInstance<F>,
        TraverseFilter<OptionTPartialOf<F>> {

    override fun FFF(): TraverseFilter<F>

    override fun <G, A, B> traverseFilter(fa: OptionTOf<F, A>, f: (A) -> Kind<G, Option<B>>, GA: Applicative<G>): Kind<G, OptionT<F, B>> =
            fa.fix().traverseFilter(f, GA, FFF())

}
