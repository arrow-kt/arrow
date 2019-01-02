package arrow.free.extensions

import arrow.*

import arrow.free.*
import arrow.typeclasses.Functor

@extension
@undocumented
interface YonedaFunctorInstance<U> : Functor<YonedaPartialOf<U>> {
  override fun <A, B> Kind<YonedaPartialOf<U>, A>.map(f: (A) -> B): Yoneda<U, B> = fix().map(f)
}
