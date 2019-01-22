package arrow.effects.typeclasses.suspended

import arrow.Kind
import arrow.effects.typeclasses.MonadDefer
import arrow.typeclasses.suspended.ListTraverseSyntax

interface MonadDeferSyntax<F> : BracketSyntax<F, Throwable>, MonadDefer<F>, ListTraverseSyntax<F> {

  override fun <A> f(fa: suspend () -> A): Kind<F, A> =
    defer { super<BracketSyntax>.f(fa) }

  suspend fun <A> effect(f: suspend () -> A): A = f()

  private suspend fun <A> deferring(fb: MonadDefer<F>.() -> Kind<F, A>): A =
    run<MonadDefer<F>, Kind<F, A>> { fb(this) }.bind()

  suspend fun <A> defer(unit: Unit = Unit, fb: () -> Kind<F, A>): A =
    deferring { defer(fb) }

}