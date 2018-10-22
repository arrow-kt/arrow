package arrow.free.instances

import arrow.*
import arrow.deprecation.ExtensionsDSLDeprecated
import arrow.free.*
import arrow.typeclasses.Functor

@extension
interface CoyonedaFunctorInstance<F, G> : Functor<CoyonedaPartialOf<F, G>> {
  override fun <A, B> Kind<CoyonedaPartialOf<F, G>, A>.map(f: (A) -> B): Coyoneda<F, G, B> = fix().map(f)
}

class CoyonedaContext<F, G> : CoyonedaFunctorInstance<F, G>

class CoyonedaContextPartiallyApplied<F, G> {
  @Deprecated(ExtensionsDSLDeprecated)
  infix fun <A> extensions(f: CoyonedaContext<F, G>.() -> A): A =
    f(CoyonedaContext())
}

fun <F, G> ForCoyoneda(): CoyonedaContextPartiallyApplied<F, G> =
  CoyonedaContextPartiallyApplied()