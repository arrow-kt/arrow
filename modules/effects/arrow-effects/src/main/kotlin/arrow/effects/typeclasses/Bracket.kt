package arrow.effects.typeclasses

import arrow.Kind
import arrow.core.Either
import arrow.effects.typeclasses.ExitCase.Error
import arrow.typeclasses.MonadError

sealed class ExitCase<out E> {

  object Completed : ExitCase<Nothing>()

  object Cancelled : ExitCase<Nothing>()

  data class Error<out E>(val e: E) : ExitCase<E>()
}

fun <E> Either<E, *>.toExitCase() =
  fold(::Error) { ExitCase.Completed }

/**
 * Extension of MonadError exposing the [[bracket]] operation, a generalized abstracted pattern of safe resource
 * acquisition and release in the face of errors or interruption.
 *
 * @define The functions receiver here (Kind<F, A>) would stand for the "acquireParam", and stands for an action that
 * "acquires" some expensive resource, that needs to be used and then discarded.
 *
 * @define use is the action that uses the newly allocated resource and that will provide the final result.
 */
interface Bracket<F, E> : MonadError<F, E> {

  /**
   * A generalized version of [[bracket]] which uses [[ExitCase]] to distinguish between different exit cases when
   * releasing the acquired resource.
   *
   * @param release is the action supposed to release the allocated resource after `use` is done, by observing and
   * acting on its exit condition.
   */
  fun <A, B> Kind<F, A>.bracketCase(use: (A) -> Kind<F, B>, release: (A, ExitCase<E>) -> Kind<F, Unit>): Kind<F, B>

  /**
   * Meant for specifying tasks with safe resource acquisition and release in the face of errors and interruption.
   * It would be the the equivalent of `try/catch/finally` statements in mainstream imperative languages for resource
   * acquisition and release.
   *
   * @param release is the action that's supposed to release the allocated resource after `use` is done, irregardless
   * of its exit condition.
   */
  fun <A, B> Kind<F, A>.bracket(use: (A) -> Kind<F, B>, release: (A) -> Kind<F, Unit>): Kind<F, B> =
    bracketCase(use) { a, _ -> release(a) }

  /**
   * Meant for ensuring a given task continues execution even when interrupted.
   */
  fun <A> Kind<F, A>.uncancelable(): Kind<F, A> =
    bracket({ just(it) }, { just<Unit>(Unit) })

  /**
   * Executes the given `finalizer` when the source is finished, either in success or in error, or if canceled.
   *
   * As best practice, it's not a good idea to release resources via `guaranteeCase` in polymorphic code.
   * Prefer [[bracket]] for the acquisition and release of resources.
   *
   * @see [[guaranteeCase]] for the version that can discriminate between termination conditions
   *
   * @see [[bracket]] for the more general operation
   */
  fun <A> Kind<F, A>.guarantee(finalizer: Kind<F, Unit>): Kind<F, A> =
    bracket({ _ -> this }, { _ -> finalizer })

  /**
   * Executes the given `finalizer` when the source is finished, either in success or in error, or if canceled, allowing
   * for differentiating between exit conditions. That's thanks to the [[ExitCase]] argument of the finalizer.
   *
   * As best practice, it's not a good idea to release resources via `guaranteeCase` in polymorphic code.
   * Prefer [[bracketCase]] for the acquisition and release of resources.
   *
   * @see [[guarantee]] for the simpler version
   *
   * @see [[bracketCase]] for the more general operation
   */
  fun <A> Kind<F, A>.guaranteeCase(finalizer: (ExitCase<E>) -> Kind<F, Unit>): Kind<F, A> =
    bracketCase({ _ -> this }, { _, e -> finalizer(e) })
}
