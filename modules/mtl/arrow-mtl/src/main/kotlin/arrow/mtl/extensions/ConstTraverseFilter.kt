package arrow.mtl.extensions

import arrow.Kind
import arrow.core.Option
import arrow.core.extensions.ConstTraverse
import arrow.extension
import arrow.mtl.typeclasses.TraverseFilter
import arrow.typeclasses.Applicative
import arrow.typeclasses.Const
import arrow.typeclasses.ConstOf
import arrow.typeclasses.ConstPartialOf
import arrow.typeclasses.fix

@extension
interface ConstTraverseFilter<X> : TraverseFilter<ConstPartialOf<X>>, ConstTraverse<X> {

  override fun <T, U> Kind<ConstPartialOf<X>, T>.map(f: (T) -> U): Const<X, U> = fix().retag()

  override fun <G, A, B> Kind<ConstPartialOf<X>, A>.traverseFilter(AP: Applicative<G>, f: (A) -> Kind<G, Option<B>>): Kind<G, ConstOf<X, B>> =
    fix().traverseFilter(AP, f)
}
