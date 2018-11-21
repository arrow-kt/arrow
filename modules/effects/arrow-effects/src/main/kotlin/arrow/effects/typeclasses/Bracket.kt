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
 * ank_macro_hierarchy(arrow.effects.typeclasses.Bracket)
 *
 * Extension of MonadError exposing the [bracket] operation, a generalized abstracted pattern of safe resource
 * acquisition and release in the face of errors or interruption.
 *
 * @define The functions receiver here (Kind<F, A>) would stand for the "acquireParam", and stands for an action that
 * "acquires" some expensive resource, that needs to be used and then discarded.
 *
 * @define use is the action that uses the newly allocated resource and that will provide the final result.
 */
interface Bracket<F, E> : MonadError<F, E> {

  /**
   * A generalized version of [bracket] which uses [ExitCase] to distinguish between different exit cases when
   * releasing the acquired resource.
   *
   * @param release is the action supposed to release the allocated resource after `use` is done, by observing and
   * acting on its exit condition.
   *
   * {: data-executable='true'}
   *
   * ```kotlin:ank
   * import arrow.effects.IO
   * import arrow.effects.typeclasses.ExitCase
   *
   * class File(url: String) {
   *   fun open(): File = this
   *   fun close(): Unit {}
   *   override fun toString(): String = "This file contains some interesting content!"
   * }
   *
   * fun openFile(uri: String): IO<File> = IO { File(uri).open() }
   * fun closeFile(file: File): IO<Unit> = IO { file.close() }
   * fun fileToString(file: File): IO<String> = IO { file.toString() }
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   val safeComputation = openFile("data.json").bracketCase(
   *     release = { file, exitCase ->
   *       when (exitCase) {
   *         is ExitCase.Completed -> { /* do something */ }
   *         is ExitCase.Cancelled -> { /* do something */ }
   *         is ExitCase.Error -> { /* do something */ }
   *       }
   *       closeFile(file)
   *    },
   *    use = { file -> fileToString(file) }
   *  )
   *  //sampleEnd
   *  println(safeComputation)
   *  }
   *  ```
   *
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
   * {: data-executable='true'}
   *
   * ```kotlin:ank
   * import arrow.effects.IO
   *
   * class File(url: String) {
   *   fun open(): File = this
   *   fun close(): Unit {}
   *   override fun toString(): String = "This file contains some interesting content!"
   * }
   *
   * fun openFile(uri: String): IO<File> = IO { File(uri).open() }
   * fun closeFile(file: File): IO<Unit> = IO { file.close() }
   * fun fileToString(file: File): IO<String> = IO { file.toString() }
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   val safeComputation = openFile("data.json").bracket(
   *     release = { file -> closeFile(file) },
   *     use = { file -> fileToString(file) }
   *   )
   *   //sampleEnd
   *   println(safeComputation)
   * }
   * ```
   *
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
    bracket({ finalizer }, { this })

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
    bracketCase({ _, e -> finalizer(e) }, { this })
}
