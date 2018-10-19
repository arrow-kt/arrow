package arrow.instances

import arrow.Kind
import arrow.data.Store
import arrow.data.StorePartialOf
import arrow.data.fix
import arrow.deprecation.ExtensionsDSLDeprecated
import arrow.extension
import arrow.typeclasses.Comonad
import arrow.typeclasses.Functor

@extension
interface StoreComonadInstance<S> : Comonad<StorePartialOf<S>> {
  override fun <A, B> Kind<StorePartialOf<S>, A>.coflatMap(f: (Kind<StorePartialOf<S>, A>) -> B): Store<S, B> =
      fix().coflatMap(f)

  override fun <A> Kind<StorePartialOf<S>, A>.extract(): A =
      fix().extract()

  override fun <A, B> Kind<StorePartialOf<S>, A>.map(f: (A) -> B): Store<S, B> =
      fix().map(f)
}

@extension
interface StoreFunctorInstance<S> : Functor<StorePartialOf<S>> {
  override fun <A, B> Kind<StorePartialOf<S>, A>.map(f: (A) -> B): Store<S, B> =
      fix().map(f)
}

class StoreContext<S> : StoreComonadInstance<S>

class StoreContextPartiallyApplied<S> {
  @Deprecated(ExtensionsDSLDeprecated)
  infix fun <A> extensions(f: StoreContext<S>.() -> A): A =
      f(StoreContext())
}

fun <S> ForStore(): StoreContextPartiallyApplied<S> =
    StoreContextPartiallyApplied()
