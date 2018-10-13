package arrow.mtl.instances

import arrow.Kind
import arrow.core.Option
import arrow.deprecation.ExtensionsDSLDeprecated
import arrow.extension
import arrow.instances.ConstApplicativeInstance
import arrow.instances.ConstTraverseInstance
import arrow.mtl.typeclasses.TraverseFilter
import arrow.typeclasses.*

@extension
interface ConstTraverseFilterInstance<X> : TraverseFilter<ConstPartialOf<X>>, ConstTraverseInstance<X> {

  override fun <T, U> Kind<ConstPartialOf<X>, T>.map(f: (T) -> U): Const<X, U> = fix().retag()

  override fun <G, A, B> Kind<ConstPartialOf<X>, A>.traverseFilter(AP: Applicative<G>, f: (A) -> Kind<G, Option<B>>): Kind<G, ConstOf<X, B>> =
    fix().traverseFilter(AP, f)
}

class ConstMtlContext<A>(val MA: Monoid<A>) : ConstApplicativeInstance<A>, ConstTraverseFilterInstance<A> {
  override fun MA(): Monoid<A> = MA

  override fun <T, U> Kind<ConstPartialOf<A>, T>.map(f: (T) -> U): Const<A, U> =
    fix().retag()
}

class ConstMtlContextPartiallyApplied<L>(val MA: Monoid<L>) {
  @Deprecated(ExtensionsDSLDeprecated)
  infix fun <A> extensions(f: ConstMtlContext<L>.() -> A): A =
    f(ConstMtlContext(MA))
}

fun <L> ForConst(MA: Monoid<L>): ConstMtlContextPartiallyApplied<L> =
  ConstMtlContextPartiallyApplied(MA)