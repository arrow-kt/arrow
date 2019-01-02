package arrow.free.extensions

import arrow.*

import arrow.free.*
import arrow.typeclasses.Functor

@extension
@undocumented
interface CoyonedaFunctorInstance<F, G> : Functor<CoyonedaPartialOf<F, G>> {
  override fun <A, B> Kind<CoyonedaPartialOf<F, G>, A>.map(f: (A) -> B): Coyoneda<F, G, B> = fix().map(f)
}
