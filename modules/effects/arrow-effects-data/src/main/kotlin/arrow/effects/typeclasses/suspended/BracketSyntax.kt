package arrow.effects.typeclasses.suspended

import arrow.Kind
import arrow.effects.typeclasses.Bracket
import arrow.effects.typeclasses.ExitCase
import arrow.typeclasses.suspended.MonadErrorSyntax

interface BracketSyntax<F, E> :
  MonadErrorSyntax<F, E>,
  Bracket<F, E> {

  private suspend fun <A> bracketing(fb: suspend Bracket<F, E>.() -> Kind<F, A>): A =
    run<Bracket<F, E>, Kind<F, A>> { fb(this) }.bind()

  suspend fun <A, B> bracketCase(
    f: suspend () -> A,
    release: suspend (A, ExitCase<E>) -> Unit,
    use: suspend (A) -> B
  ): B =
    bracketing { f.liftM().bracketCase(release.flatLiftM(), use.flatLiftM()) }

  suspend fun <A, B> bracket(
    f: suspend () -> A,
    release: suspend (A) -> Unit,
    use: suspend (A) -> B
  ): B =
    bracketing { f.liftM().bracket(release.flatLiftM(), use.flatLiftM()) }

  suspend fun <A> uncancelable(f: suspend () -> A): A =
    bracketing { f.liftM().uncancelable() }

  suspend fun <A> guarantee(
    f: suspend () -> A,
    finalizer: suspend () -> Unit
  ): A =
    bracketing { f.liftM().guarantee(finalizer.liftM()) }

  suspend fun <A> Kind<F, A>.guaranteeCase(
    unit: Unit = Unit,
    finalizer: suspend (ExitCase<E>) -> Unit
  ): A =
    bracketing { guaranteeCase(finalizer.flatLiftM()) }

}