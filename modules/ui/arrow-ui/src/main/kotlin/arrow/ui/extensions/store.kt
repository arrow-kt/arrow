package arrow.ui.extensions

import arrow.Kind
import arrow.ui.Store
import arrow.ui.StorePartialOf
import arrow.ui.fix
import arrow.extension
import arrow.typeclasses.Comonad
import arrow.typeclasses.Functor
import arrow.undocumented

@extension
@undocumented
interface StoreComonad<S> : Comonad<StorePartialOf<S>> {
  override fun <A, B> Kind<StorePartialOf<S>, A>.coflatMap(f: (Kind<StorePartialOf<S>, A>) -> B): Store<S, B> =
      fix().coflatMap(f)

  override fun <A> Kind<StorePartialOf<S>, A>.extract(): A =
      fix().extract()

  override fun <A, B> Kind<StorePartialOf<S>, A>.map(f: (A) -> B): Store<S, B> =
      fix().map(f)
}

@extension
@undocumented
interface StoreFunctor<S> : Functor<StorePartialOf<S>> {
  override fun <A, B> Kind<StorePartialOf<S>, A>.map(f: (A) -> B): Store<S, B> =
      fix().map(f)
}
