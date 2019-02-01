package arrow.effects.typeclasses.suspended

import arrow.Kind
import arrow.effects.typeclasses.Bracket
import arrow.effects.typeclasses.ExitCase
import arrow.typeclasses.suspended.MonadErrorSyntax

interface BracketSyntax<F, E> :
  MonadErrorSyntax<F, E>,
  Bracket<F, E> {

  private fun <A> bracketing(fb: Bracket<F, E>.() -> Kind<F, A>): Kind<F, A> =
    run<Bracket<F, E>, Kind<F, A>> { fb(this) }

  fun <A, B> bracketCase(
    f: suspend () -> A,
    release: suspend (A, ExitCase<E>) -> Unit,
    use: suspend (A) -> B
  ): Kind<F, B> =
    bracketing { f.effect().bracketCase(release.flatLiftM(), use.flatLiftM()) }

  fun <A, B> bracket(
    f: suspend () -> A,
    release: suspend (A) -> Unit,
    use: suspend (A) -> B
  ): Kind<F, B> =
    bracketing { f.effect().bracket(release.flatLiftM(), use.flatLiftM()) }

  fun <A> uncancelable(f: suspend () -> A): Kind<F, A> =
    bracketing { f.effect().uncancelable() }

  fun <A> guarantee(
    f: suspend () -> A,
    finalizer: suspend () -> Unit
  ): Kind<F, A> =
    bracketing { f.effect().guarantee(finalizer.effect()) }

  fun <A> Kind<F, A>.guaranteeCase(
    unit: Unit = Unit,
    finalizer: suspend (ExitCase<E>) -> Unit
  ): Kind<F, A> =
    bracketing { guaranteeCase(finalizer.flatLiftM()) }

}