package arrow.effects.typeclasses.suspended

import arrow.Kind
import arrow.effects.typeclasses.MonadDefer
import arrow.typeclasses.suspended.IterableTraverseSyntax

interface MonadDeferSyntax<F> : BracketSyntax<F, Throwable>, MonadDefer<F>, IterableTraverseSyntax<F> {

  override fun <A> effect(fa: suspend () -> A): Kind<F, A> =
    super<BracketSyntax>.effect(fa)

}