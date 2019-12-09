package arrow.free.extensions

import arrow.Kind
import arrow.extension
import arrow.free.Coyoneda
import arrow.free.CoyonedaPartialOf
import arrow.free.fix
import arrow.typeclasses.Functor
import arrow.undocumented

@extension
@undocumented
interface CoyonedaFunctor<F, G> : Functor<CoyonedaPartialOf<F, G>> {
  override fun <A, B> Kind<CoyonedaPartialOf<F, G>, A>.map(f: (A) -> B): Coyoneda<F, G, B> = fix().map(f)
}
