package arrow.mtl.instances

import arrow.Kind
import arrow.core.Option
import arrow.free.Const
import arrow.free.ConstOf
import arrow.free.ConstPartialOf
import arrow.free.fix
import arrow.free.instances.ConstTraverseInstance
import arrow.instance
import arrow.mtl.typeclasses.TraverseFilter
import arrow.typeclasses.Applicative

@instance(Const::class)
interface ConstTraverseFilterInstance<X> : ConstTraverseInstance<X>, TraverseFilter<ConstPartialOf<X>> {

  override fun <T, U> Kind<ConstPartialOf<X>, T>.map(f: (T) -> U): Const<X, U> = fix().retag()

  override fun <G, A, B> Kind<ConstPartialOf<X>, A>.traverseFilter(AP: Applicative<G>, f: (A) -> Kind<G, Option<B>>): Kind<G, ConstOf<X, B>> =
    fix().traverseFilter(f, AP)
}
