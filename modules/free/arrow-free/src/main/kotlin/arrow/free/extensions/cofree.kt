package arrow.free.extensions

import arrow.Kind

import arrow.free.Cofree
import arrow.free.CofreeOf
import arrow.free.CofreePartialOf
import arrow.free.fix
import arrow.extension
import arrow.typeclasses.Comonad
import arrow.typeclasses.Functor
import arrow.undocumented

@extension
@undocumented
interface CofreeFunctor<S> : Functor<CofreePartialOf<S>> {
  override fun <A, B> Kind<CofreePartialOf<S>, A>.map(f: (A) -> B): Cofree<S, B> = fix().map(f)
}

@extension
interface CofreeComonad<S> : Comonad<CofreePartialOf<S>>, CofreeFunctor<S> {
  override fun <A, B> Kind<CofreePartialOf<S>, A>.coflatMap(f: (Kind<CofreePartialOf<S>, A>) -> B): Cofree<S, B> = fix().coflatMap(f)

  override fun <A> CofreeOf<S, A>.extract(): A = fix().extract()

  override fun <A> Kind<CofreePartialOf<S>, A>.duplicate(): Kind<CofreePartialOf<S>, Cofree<S, A>> = fix().duplicate()
}
