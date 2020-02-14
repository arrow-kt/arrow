package arrow.fx.typeclasses

import arrow.Kind
import arrow.core.Either
import arrow.documented
import arrow.fx.typeclasses.ExitCase.Error
import arrow.typeclasses.MonadError

sealed class ExitCase<out E> {

  object Completed : ExitCase<Nothing>() {
    override fun toString() = "ExitCase.Completed"
  }

  object Canceled : ExitCase<Nothing>() {
    override fun toString() = "ExitCase.Canceled"
  }

  data class Error<out E>(val e: E) : ExitCase<E>()
}

fun <E> Either<E, *>.toExitCase() =
  fold(::Error) { ExitCase.Completed }

/**
 * ank_macro_hierarchy(arrow.fx.typeclasses.Bracket)
 *
 * Extension of MonadError exposing the [bracket] operation, a generalized abstracted pattern of safe resource
 * acquisition and release in the face of errors or interruption.
 *
 * @define The functions receiver here (Kind<F, A>) would stand for the "acquireParam", and stands for an action that
 * "acquires" some expensive resource, that needs to be used and then discarded.
 *
 * @define use is the action that uses the newly allocated resource and that will provide the final result.
 */
@documented
interface Bracket<F, E> : MonadError<F, E> {

  /**
   * A way to safely acquire a resource and release in the face of errors and cancellation.
   * It uses [ExitCase] to distinguish between different exit cases when releasing the acquired resource.
   *
   * @param use is the action to consume the resource and produce an [F] with the result.
   * Once the resulting [F] terminates, either successfully, error or cancelled.
   *
   * @param release the allocated resource after the resulting [F] of [use] is terminates.
   *
   * ```kotlin:ank:playground:extension
   * _imports_
   * _imports_monaddefer_
   *
   * class File(url: String) {
   *   fun open(): File = this
   *   fun close(): Unit {}
   *   override fun toString(): String = "This file contains some interesting content!"
   * }
   *
   * fun openFile(uri: String): Kind<F, File> = _later_({ File(uri).open() })
   * fun closeFile(file: File): Kind<F, Unit> = _later_({ file.close() })
   * fun fileToString(file: File): Kind<F, String> = _later_({ file.toString() })
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   val release: (File, ExitCase<Throwable>) -> Kind<F, Unit> = { file, exitCase ->
   *       when (exitCase) {
   *         is ExitCase.Completed -> { /* do something */ }
   *         is ExitCase.Canceled -> { /* do something */ }
   *         is ExitCase.Error -> { /* do something */ }
   *       }
   *       closeFile(file)
   *   }
   *
   *   val use: (File) -> Kind<F, String> = { file: File -> fileToString(file) }
   *
   *   val safeComputation = openFile("data.json")._bracketCase_(release, use)
   *   //sampleEnd
   *   println(safeComputation)
   * }
   *  ```
   */
  fun <A, B> Kind<F, A>.bracketCase(release: (A, ExitCase<E>) -> Kind<F, Unit>, use: (A) -> Kind<F, B>): Kind<F, B>

  /**
   * Meant for specifying tasks with safe resource acquisition and release in the face of errors and interruption.
   * It would be the the equivalent of `try/catch/finally` statements in mainstream imperative languages for resource
   * acquisition and release.
   *
   * @param release is the action that's supposed to release the allocated resource after `use` is done, irregardless
   * of its exit condition.
   *
   * ```kotlin:ank:playground:extension
   * _imports_
   * _imports_monaddefer_
   *
   * class File(url: String) {
   *   fun open(): File = this
   *   fun close(): Unit {}
   *   override fun toString(): String = "This file contains some interesting content!"
   * }
   *
   * fun openFile(uri: String): Kind<F, File> = _later_({ File(uri).open() })
   * fun closeFile(file: File): Kind<F, Unit> = _later_({ file.close() })
   * fun fileToString(file: File): Kind<F, String> = _later_({ file.toString() })
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   val safeComputation = openFile("data.json")._bracket_({ file: File -> closeFile(file) }, { file -> fileToString(file) })
   *   //sampleEnd
   *   println(safeComputation)
   * }
   * ```
   */
  fun <A, B> Kind<F, A>.bracket(release: (A) -> Kind<F, Unit>, use: (A) -> Kind<F, B>): Kind<F, B> =
    bracketCase({ a, _ -> release(a) }, use)

  /**
   * Meant for ensuring a given task continues execution even when interrupted.
   */
  fun <A> Kind<F, A>.uncancelable(): Kind<F, A> =
    bracket({ just<Unit>(Unit) }, { just(it) })

  /**
   * Executes the given `finalizer` when the source is finished, either in success or in error, or if canceled.
   *
   * As best practice, it's not a good idea to release resources via `guaranteeCase` in polymorphic code.
   * Prefer [bracket] for the acquisition and release of resources.
   *
   * @see [guaranteeCase] for the version that can discriminate between termination conditions
   *
   * @see [bracket] for the more general operation
   */
  fun <A> Kind<F, A>.guarantee(finalizer: Kind<F, Unit>): Kind<F, A> =
    guaranteeCase { finalizer }

  /**
   * Executes the given `finalizer` when the source is finished, either in success or in error, or if canceled, allowing
   * for differentiating between exit conditions. That's thanks to the [ExitCase] argument of the finalizer.
   *
   * As best practice, it's not a good idea to release resources via `guaranteeCase` in polymorphic code.
   * Prefer [bracketCase] for the acquisition and release of resources.
   *
   * @see [guarantee] for the simpler version
   *
   * @see [bracketCase] for the more general operation
   *
   */
  fun <A> Kind<F, A>.guaranteeCase(finalizer: (ExitCase<E>) -> Kind<F, Unit>): Kind<F, A> =
    just<Unit>(Unit).bracketCase({ _, e -> finalizer(e) }, { this })

  /**
   * Executes the given [finalizer] when the source is canceled, allowing registering a cancellation token.
   *
   * Useful for wiring cancellation tokens between fibers, building inter-op with other effect systems or testing.
   */
  fun <A> Kind<F, A>.onCancel(finalizer: Kind<F, Unit>): Kind<F, A> =
    guaranteeCase { case ->
      when (case) {
        ExitCase.Canceled -> finalizer
        else -> just<Unit>(Unit)
      }
    }

  /**
   * Executes the given `finalizer` with the given error when the source is finished in error.
   */
  fun <A> Kind<F, A>.onError(finalizer: (E) -> Kind<F, Unit>): Kind<F, A> =
      guaranteeCase { case ->
        when (case) {
          is ExitCase.Error -> finalizer(case.e)
          else -> just<Unit>(Unit)
        }
      }
}
