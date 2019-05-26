package arrow.mtl.extensions

import arrow.Kind
import arrow.core.Option
import arrow.core.extensions.ConstTraverse
import arrow.extension
import arrow.mtl.typeclasses.TraverseFilter
import arrow.core.typeclasses.Applicative
import arrow.core.typeclasses.Const
import arrow.core.typeclasses.ConstOf
import arrow.core.typeclasses.ConstPartialOf
import arrow.core.typeclasses.fix

@extension
interface ConstTraverseFilter<X> : TraverseFilter<ConstPartialOf<X>>, ConstTraverse<X> {

  override fun <T, U> Kind<ConstPartialOf<X>, T>.map(f: (T) -> U): Const<X, U> = fix().retag()

  override fun <G, A, B> Kind<ConstPartialOf<X>, A>.traverseFilter(AP: Applicative<G>, f: (A) -> Kind<G, Option<B>>): Kind<G, ConstOf<X, B>> =
    fix().traverseFilter(AP, f)
}
