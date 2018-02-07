package arrow.mtl.instances

import arrow.*
import arrow.core.*
import arrow.data.*
import arrow.instances.*
import arrow.mtl.FunctorFilter
import arrow.mtl.TraverseFilter
import arrow.mtl.syntax.traverseFilter
import arrow.typeclasses.Applicative

@instance(OptionT::class)
interface OptionTFunctorFilterInstance<F> : OptionTFunctorInstance<F>, FunctorFilter<OptionTKindPartial<F>> {

    override fun <A, B> mapFilter(fa: OptionTKind<F, A>, f: (A) -> Option<B>): OptionT<F, B> =
            fa.reify().mapFilter(f, FF())

}

@instance(OptionT::class)
interface OptionTTraverseFilterInstance<F> :
        OptionTTraverseInstance<F>,
        TraverseFilter<OptionTKindPartial<F>> {

    override fun FFF(): TraverseFilter<F>

    override fun <G, A, B> traverseFilter(fa: OptionTKind<F, A>, f: (A) -> Kind<G, Option<B>>, GA: Applicative<G>): Kind<G, OptionT<F, B>> =
            fa.reify().traverseFilter(f, GA, FFF())

}