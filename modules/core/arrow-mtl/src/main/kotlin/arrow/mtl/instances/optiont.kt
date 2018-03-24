package arrow.mtl.instances

import arrow.Kind
import arrow.core.Option
import arrow.data.*
import arrow.instance
import arrow.instances.OptionTFunctorInstance
import arrow.instances.OptionTTraverseInstance
import arrow.mtl.typeclasses.FunctorFilter
import arrow.mtl.typeclasses.TraverseFilter
import arrow.mtl.syntax.traverseFilter
import arrow.typeclasses.Applicative

@instance(OptionT::class)
interface OptionTFunctorFilterInstance<F> : OptionTFunctorInstance<F>, FunctorFilter<OptionTPartialOf<F>> {

    override fun <A, B> arrow.Kind<arrow.data.OptionTPartialOf<F>, A>.mapFilter(f: (A) -> arrow.core.Option<B>): OptionT<F, B> =
            this@mapFilter.fix().mapFilter(f, this@OptionTFunctorFilterInstance.FF())

}

@instance(OptionT::class)
interface OptionTTraverseFilterInstance<F> :
        OptionTTraverseInstance<F>,
        TraverseFilter<OptionTPartialOf<F>> {

    override fun FFF(): TraverseFilter<F>

    override fun <G, A, B> Applicative<G>.traverseFilter(fa: Kind<OptionTPartialOf<F>, A>, f: (A) -> Kind<G, Option<B>>): Kind<G, OptionT<F, B>> =
            fa.fix().traverseFilter(f, this, FFF())

}
