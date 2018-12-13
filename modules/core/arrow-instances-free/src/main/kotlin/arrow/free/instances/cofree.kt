package arrow.free.instances

import arrow.Kind
import arrow.deprecation.ExtensionsDSLDeprecated
import arrow.free.Cofree
import arrow.free.CofreeOf
import arrow.free.CofreePartialOf
import arrow.free.fix
import arrow.extension
import arrow.typeclasses.Comonad
import arrow.typeclasses.Functor

@extension
interface CofreeFunctorInstance<S> : Functor<CofreePartialOf<S>> {
  override fun <A, B> Kind<CofreePartialOf<S>, A>.map(f: (A) -> B): Cofree<S, B> = fix().map(f)
}

@extension
interface CofreeComonadInstance<S> : Comonad<CofreePartialOf<S>>, CofreeFunctorInstance<S> {
  override fun <A, B> Kind<CofreePartialOf<S>, A>.coflatMap(f: (Kind<CofreePartialOf<S>, A>) -> B): Cofree<S, B> = fix().coflatMap(f)

  override fun <A> CofreeOf<S, A>.extract(): A = fix().extract()

  override fun <A> Kind<CofreePartialOf<S>, A>.duplicate(): Kind<CofreePartialOf<S>, Cofree<S, A>> = fix().duplicate()
}

class CofreeContext<S> : CofreeComonadInstance<S>

class CofreeContextPartiallyApplied<S> {
  @Deprecated(ExtensionsDSLDeprecated)
  infix fun <A> extensions(f: CofreeContext<S>.() -> A): A =
    f(CofreeContext())
}

fun <S> ForCofree(): CofreeContextPartiallyApplied<S> =
  CofreeContextPartiallyApplied()