package arrow.fx.coroutines

import arrow.core.nonFatalOrThrow
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext

public sealed class ExitCase {
  public object Completed : ExitCase() {
    override fun toString(): String =
      "ExitCase.Completed"
  }

  public data class Cancelled(val exception: CancellationException) : ExitCase()
  public data class Failure(val failure: Throwable) : ExitCase()
}

/**
 * Registers an [onCancel] handler after [fa].
 * [onCancel] is guaranteed to be called in case of cancellation, otherwise it's ignored.
 *
 * This function is useful for wiring cancellation tokens between fibers, building inter-op with other effect systems or testing.
 *
 * @param fa program that you want to register handler on
 * @param onCancel handler to run when [fa] gets cancelled.
 * @see guarantee for registering a handler that is guaranteed to always run.
 * @see guaranteeCase for registering a handler that executes for any [ExitCase].
 */
public suspend inline fun <A> onCancel(
  fa: suspend () -> A,
  crossinline onCancel: suspend () -> Unit
): A = guaranteeCase(fa) { case ->
  when (case) {
    is ExitCase.Cancelled -> onCancel.invoke()
    else -> Unit
  }
}

/**
 * Guarantees execution of a given [finalizer] after [fa] regardless of success, error or cancellation.
 *
 * As best practice, it's not a good idea to release resources via [guarantee].
 * since [guarantee] doesn't properly model acquiring, using and releasing resources.
 * It only models scheduling of a finalizer after a given suspending program,
 * so you should prefer [Resource] or [bracket] which captures acquiring,
 * using and releasing into 3 separate steps to ensure resource safety.
 *
 * @param fa program that you want to register handler on
 * @param finalizer handler to run after [fa].
 * @see guaranteeCase for registering a handler that tracks the [ExitCase] of [fa].
 */
public suspend inline fun <A> guarantee(
  fa: suspend () -> A,
  crossinline finalizer: suspend () -> Unit
): A {
  val res = try {
    fa.invoke()
  } catch (e: CancellationException) {
    runReleaseAndRethrow(e) { finalizer() }
  } catch (t: Throwable) {
    runReleaseAndRethrow(t.nonFatalOrThrow()) { finalizer() }
  }
  withContext(NonCancellable) { finalizer() }
  return res
}

/**
 * Guarantees execution of a given [finalizer] after [fa] regardless of success, error or cancellation, allowing
 * for differentiating between exit conditions with the [ExitCase] argument of the finalizer.
 *
 * As best practice, it's not a good idea to release resources via [guaranteeCase].
 * since [guaranteeCase] doesn't properly model acquiring, using and releasing resources.
 * It only models scheduling of a finalizer after a given suspending program,
 * so you should prefer [Resource] or [bracketCase] which captures acquiring,
 * using and releasing into 3 separate steps to ensure resource safety.
 *
 * @param fa program that you want to register handler on
 * @param finalizer handler to run after [fa].
 * @see guarantee for registering a handler that ignores the [ExitCase] of [fa].
 */
public suspend inline fun <A> guaranteeCase(
  fa: suspend () -> A,
  crossinline finalizer: suspend (ExitCase) -> Unit
): A {
  val res = try {
    fa()
  } catch (e: CancellationException) {
    runReleaseAndRethrow(e) { finalizer(ExitCase.Cancelled(e)) }
  } catch (t: Throwable) {
    runReleaseAndRethrow(t.nonFatalOrThrow()) { finalizer(ExitCase.Failure(t.nonFatalOrThrow())) }
  }
  withContext(NonCancellable) { finalizer(ExitCase.Completed) }
  return res
}

/**
 * Describes a task with safe resource acquisition and release in the face of errors and interruption.
 * It would be the equivalent of an async capable `try/catch/finally` statements in mainstream imperative languages for resource
 * acquisition and release.
 *
 * @param acquire is the action to acquire the resource.
 *
 * @param use is the action to consume the resource and produce a result.
 * Once the resulting suspend program terminates, either successfully, error or disposed,
 * the [release] function will run to clean up the resources.
 *
 * @param release is the action that's supposed to release the allocated resource after `use` is done, irregardless
 * of its exit condition.
 *
 * ```kotlin:ank:playground
 * import arrow.fx.coroutines.*
 *
 * class File(url: String) {
 *   fun open(): File = this
 *   fun close(): Unit {}
 *   override fun toString(): String = "This file contains some interesting content!"
 * }
 *
 * suspend fun openFile(uri: String): File = File(uri).open()
 * suspend fun closeFile(file: File): Unit = file.close()
 * suspend fun fileToString(file: File): String = file.toString()
 *
 * suspend fun main(): Unit {
 *   //sampleStart
 *   val res = bracket(
 *     acquire = { openFile("data.json") },
 *     use = { file -> fileToString(file) },
 *     release = { file: File -> closeFile(file) }
 *   )
 *   //sampleEnd
 *   println(res)
 * }
 * ```
 */
public suspend inline fun <A, B> bracket(
  crossinline acquire: suspend () -> A,
  use: suspend (A) -> B,
  crossinline release: suspend (A) -> Unit
): B {
  val acquired = withContext(NonCancellable) {
    acquire()
  }

  val res = try {
    use(acquired)
  } catch (e: CancellationException) {
    runReleaseAndRethrow(e) { release(acquired) }
  } catch (t: Throwable) {
    runReleaseAndRethrow(t.nonFatalOrThrow()) { release(acquired) }
  }

  withContext(NonCancellable) { release(acquired) }
  return res
}

/**
 * A way to safely acquire a resource and release in the face of errors and cancellation.
 * It uses [ExitCase] to distinguish between different exit cases when releasing the acquired resource.
 *
 * [bracketCase] exists out of three stages:
 *   1. acquisition
 *   2. consumption
 *   3. releasing
 *
 * 1. Resource acquisition is **NON CANCELLABLE**.
 *   If resource acquisition fails, meaning no resource was actually successfully acquired then we short-circuit the effect.
 *   As the resource was not acquired, it is not possible to [use] or [release] it.
 *   If it is successful we pass the result to stage 2 [use].
 *
 * 2. Resource consumption is like any other `suspend` effect. The key difference here is that it's wired in such a way that
 *   [release] **will always** be called either on [ExitCase.Cancelled], [ExitCase.Failure] or [ExitCase.Completed].
 *   If it failed, then the resulting [suspend] from [bracketCase] will be the error; otherwise the result of [use] will be returned.
 *
 * 3. Resource releasing is **NON CANCELLABLE**, otherwise it could result in leaks.
 *   In the case it throws an exception, the resulting [suspend] will be either such error, or a composed error if one occurred in the [use] stage.
 *
 * @param acquire is the action to acquire the resource.
 *
 * @param use is the action to consume the resource and produce a result.
 * Once the resulting suspend program terminates, either successfully, error or disposed,
 * the [release] function will run to clean up the resources.
 *
 * @param release is the action to release the allocated resource after [use] terminates.
 *
 * ```kotlin:ank:playground
 * import arrow.fx.coroutines.*
 *
 * class File(url: String) {
 *   fun open(): File = this
 *   fun close(): Unit {}
 * }
 *
 * suspend fun File.content(): String =
 *     "This file contains some interesting content!"
 * suspend fun openFile(uri: String): File = File(uri).open()
 * suspend fun closeFile(file: File): Unit = file.close()
 *
 * suspend fun main(): Unit {
 *   //sampleStart
 *   val res = bracketCase(
 *     acquire = { openFile("data.json") },
 *     use = { file -> file.content() },
 *     release = { file, exitCase ->
 *       when (exitCase) {
 *         is ExitCase.Completed -> println("File closed with $exitCase")
 *         is ExitCase.Cancelled -> println("Program cancelled with $exitCase")
 *         is ExitCase.Failure -> println("Program failed with $exitCase")
 *       }
 *       closeFile(file)
 *     }
 *   )
 *   //sampleEnd
 *   println(res)
 * }
 *  ```
 */
public suspend inline fun <A, B> bracketCase(
  crossinline acquire: suspend () -> A,
  use: suspend (A) -> B,
  crossinline release: suspend (A, ExitCase) -> Unit
): B {
  val acquired = withContext(NonCancellable) {
    acquire()
  }

  val res = try {
    use(acquired)
  } catch (e: CancellationException) {
    runReleaseAndRethrow(e) { release(acquired, ExitCase.Cancelled(e)) }
  } catch (t: Throwable) {
    runReleaseAndRethrow(t.nonFatalOrThrow()) { release(acquired, ExitCase.Failure(t.nonFatalOrThrow())) }
  }

  withContext(NonCancellable) { release(acquired, ExitCase.Completed) }

  return res
}

@PublishedApi
internal suspend inline fun runReleaseAndRethrow(original: Throwable, crossinline f: suspend () -> Unit): Nothing {
  try {
    withContext(NonCancellable) {
      f()
    }
  } catch (e: Throwable) {
    original.addSuppressed(e.nonFatalOrThrow())
  }
  throw original
}
