package arrow.free.extensions

import arrow.Kind
import arrow.extension
import arrow.free.Yoneda
import arrow.free.YonedaPartialOf
import arrow.free.fix
import arrow.typeclasses.Functor
import arrow.undocumented

@extension
@undocumented
interface YonedaFunctor<U> : Functor<YonedaPartialOf<U>> {
  override fun <A, B> Kind<YonedaPartialOf<U>, A>.map(f: (A) -> B): Yoneda<U, B> = fix().map(f)
}
