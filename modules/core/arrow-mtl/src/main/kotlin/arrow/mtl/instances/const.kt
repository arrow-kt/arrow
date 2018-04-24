package arrow.mtl.instances

import arrow.Kind
import arrow.core.Option
import arrow.instance
import arrow.instances.ConstTraverseInstance
import arrow.mtl.typeclasses.TraverseFilter
import arrow.typeclasses.*

@instance(Const::class)
interface ConstTraverseFilterInstance<X> : ConstTraverseInstance<X>, TraverseFilter<ConstPartialOf<X>> {

  override fun <T, U> Kind<ConstPartialOf<X>, T>.map(f: (T) -> U): Const<X, U> = fix().retag()

  override fun <G, A, B> Kind<ConstPartialOf<X>, A>.traverseFilter(AP: Applicative<G>, f: (A) -> Kind<G, Option<B>>): Kind<G, ConstOf<X, B>> =
    fix().traverseFilter(AP, f)
}
