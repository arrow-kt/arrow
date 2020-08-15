package arrow.fx.coroutines

import kotlinx.atomicfu.atomic
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import kotlin.coroutines.intrinsics.startCoroutineUninterceptedOrReturn
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED

sealed class ExitCase {
  object Completed : ExitCase() {
    override fun toString(): String =
      "ExitCase.Completed"
  }
  object Cancelled : ExitCase() {
    override fun toString(): String =
      "ExitCase.Cancelled"
  }
  data class Failure(val failure: Throwable) : ExitCase()
}

/**
 * Runs [f] in an uncancellable manner.
 * If [f] gets cancelled, it will back-pressure the cancelling operation until finished.
 *
 * ```kotlin:ank:playground
 * import arrow.fx.coroutines.*
 *
 * suspend fun main(): Unit {
 *   //sampleStart
 *   val n = timeOutOrNull(10.milliseconds) {
 *     uncancellable { sleep(100.milliseconds) }
 *   } // takes 100.milliseconds, and returns null
 *
 *   //sampleEnd
 *   println("n: $n")
 * }
 * ```
 */
suspend fun <A> uncancellable(f: suspend () -> A): A =
  suspendCoroutineUninterceptedOrReturn sc@{ cont ->
    val conn = cont.context.connection()

    val deferredRelease = ForwardCancellable()
    conn.push(deferredRelease.cancel())

    if (conn.isNotCancelled()) {
      val uncancellable = cont.context + SuspendConnection.uncancellable

      return@sc f.startCoroutineUninterceptedOrReturn(Continuation(uncancellable) {
        deferredRelease.complete(CancelToken.unit)
        cont.resumeWith(it)
      })
    } else {
      deferredRelease.complete(CancelToken.unit)
      COROUTINE_SUSPENDED
    }
  }

/**
 * Registers an [onCancel] handler after [fa].
 * [onCancel] is guaranteed to be called in case of cancellation, otherwise it's ignored.
 *
 * Useful for wiring cancellation tokens between fibers, building inter-op with other effect systems or testing.
 *
 * @param fa program that you want to register handler on
 * @param onCancel handler to run when [fa] gets cancelled.
 * @see guarantee for registering a handler that is guaranteed to always run.
 * @see guaranteeCase for registering a handler that executes for any [ExitCase].
 */
suspend fun <A> onCancel(
  fa: suspend () -> A,
  onCancel: suspend () -> Unit
): A = guaranteeCase(fa) { case ->
  when (case) {
    ExitCase.Cancelled -> onCancel.invoke()
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
suspend fun <A> guarantee(
  fa: suspend () -> A,
  finalizer: suspend () -> Unit
): A = guaranteeCase(fa) { finalizer.invoke() }

/**
 * Guarantees execution of a given [finalizer] after [fa] regardless of success, error or cancellation., allowing
 * for differentiating between exit conditions with to the [ExitCase] argument of the finalizer.
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
suspend fun <A> guaranteeCase(
  fa: suspend () -> A,
  finalizer: suspend (ExitCase) -> Unit
): A = bracketCase({ Unit }, { fa.invoke() }, { _, ex -> finalizer(ex) })

/**
 * Meant for specifying tasks with safe resource acquisition and release in the face of errors and interruption.
 * It would be the equivalent of an async capable `try/catch/finally` statements in mainstream imperative languages for resource
 * acquisition and release.
 *
 * @param acquire the action to acquire the resource
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
suspend fun <A, B> bracket(
  acquire: suspend () -> A,
  use: suspend (A) -> B,
  release: suspend (A) -> Unit
): B = bracketCase(acquire, use, { a, _ -> release(a) })

/**
 * A way to safely acquire a resource and release in the face of errors and cancellation.
 * It uses [ExitCase] to distinguish between different exit cases when releasing the acquired resource.
 *
 * [bracketCase] exists out of a three stages:
 *   1. acquisition
 *   2. consumption
 *   3. releasing
 *
 * 1. Resource acquisition is **NON CANCELLABLE**.
 *   If resource acquisition fails, meaning no resource was actually successfully acquired then we short-circuit the effect.
 *   Reason being, we cannot [release] what we did not `acquire` first. Same reason we cannot call [use].
 *   If it is successful we pass the result to stage 2 [use].
 *
 * 2. Resource consumption is like any other `suspend` effect. The key difference here is that it's wired in such a way that
 *   [release] **will always** be called either on [ExitCase.Cancelled], [ExitCase.Failure] or [ExitCase.Completed].
 *   If it failed than the resulting [suspend] from [bracketCase] will be the error, otherwise the result of [use].
 *
 * 3. Resource releasing is **NON CANCELLABLE**, otherwise it could result in leaks.
 *   In the case it throws the resulting [suspend] will be either the error or a composed error if one occurred in the [use] stage.
 *
 * @param acquire the action to acquire the resource
 *
 * @param use is the action to consume the resource and produce a result.
 * Once the resulting suspend program terminates, either successfully, error or disposed,
 * the [release] function will run to clean up the resources.
 *
 * @param release the allocated resource after [use] terminates.
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
suspend fun <A, B> bracketCase(
  acquire: suspend () -> A,
  use: suspend (A) -> B,
  release: suspend (A, ExitCase) -> Unit
): B = suspendCoroutineUninterceptedOrReturn { cont ->
  val conn = cont.context.connection()

  val deferredRelease = ForwardCancellable()
  conn.push(deferredRelease.cancel())

  // Race-condition check, avoiding starting the bracket if the connection
  // was cancelled already, to ensure that `cancel` really blocks if we
  // start `acquire` — n.b. `isCancelled` is visible here due to `push`

  if (!conn.isCancelled()) {
    // Note `acquire` is uncancellable (in other words it is disconnected from our SuspendConnection)
    val uncancellable = cont.context + SuspendConnection.uncancellable

    val acquiredOrSuspended = acquire.startCoroutineUninterceptedOrReturn(
      BracketAcquireContinuation(
        uncancellable,
        cont,
        use,
        release,
        deferredRelease
      )
    )

    if (acquiredOrSuspended != COROUTINE_SUSPENDED) {
      val usedAndReleasedOrSuspended =
        launchUseAndRelease(acquiredOrSuspended as A, uncancellable, cont, use, release, deferredRelease)

      if (usedAndReleasedOrSuspended != COROUTINE_SUSPENDED) (usedAndReleasedOrSuspended as Result<B>).fold(
        { it },
        { throw it })
      else COROUTINE_SUSPENDED
    } else COROUTINE_SUSPENDED
  } else {
    deferredRelease.complete(CancelToken.unit)
    COROUTINE_SUSPENDED
  }
}

private class BracketAcquireContinuation<A, B>(
  val uncancellableContext: CoroutineContext,
  val uCont: Continuation<B>,
  val use: suspend (A) -> B,
  val release: suspend (A, ExitCase) -> Unit,
  val deferredRelease: ForwardCancellable
) : Continuation<A> {
  override val context: CoroutineContext = uncancellableContext

  override fun resumeWith(result: Result<A>) {
    result.fold({ a ->
      val usedAndReleasedOrSuspended =
        launchUseAndRelease(a, uncancellableContext, uCont, use, release, deferredRelease)

      // Use & Release immediately returned
      if (usedAndReleasedOrSuspended != COROUTINE_SUSPENDED) {
        uCont.resumeWith(usedAndReleasedOrSuspended as Result<B>)
      }
    }, { e -> uCont.resumeWith(Result.failure(e)) })
  }
}

/**
 * Launches `use`, and register `release` to run afterwards.
 *
 * Returns either immediately [Result] after `release` ran,
 * or [COROUTINE_SUSPENDED] in the case the `Coroutine` suspended.
 */
private fun <A, B> launchUseAndRelease(
  a: A,
  uncancellableContext: CoroutineContext,
  uCont: Continuation<B>,
  use: suspend (A) -> B,
  release: suspend (A, ExitCase) -> Unit,
  deferredRelease: ForwardCancellable
): Any? {
  val fb = suspend { use(a) }
  val frame = BracketUseContinuation(a, uCont, release, uncancellableContext)
  deferredRelease.complete(frame.cancel)

  val x = try {
    val res = fb.startCoroutineUninterceptedOrReturn(frame)
    if (res == COROUTINE_SUSPENDED) return COROUTINE_SUSPENDED
    else Result.success(res as B)
  } catch (e: Throwable) {
    Result.failure<B>(e.nonFatalOrThrow())
  }

  return launchRelease(a, x, uCont, release, uncancellableContext)
}

/**
 * `Continuation` that registers a `release` function to be executed after its `resumeWith` is called.
 * It's cancel signal needs to be registered to it's `uCont.context.connection()` using [ForwardCancellable].
 */
private class BracketUseContinuation<A, B>(
  val a: A,
  val uCont: Continuation<B>,
  val release: suspend (A, ExitCase) -> Unit,
  val uncancellableContext: CoroutineContext
) : Continuation<B> {

  override val context: CoroutineContext = uCont.context

  // Guard used for thread-safety, to ensure the idempotency
  // of the release; otherwise `release` can be called twice
  private val waitsForResult = atomic(true)

  suspend fun release(c: ExitCase): Unit = release(a, c)

  private suspend fun applyRelease(e: ExitCase): Unit {
    if (waitsForResult.compareAndSet(true, false)) release(e)
    else Unit
  }

  val cancel: CancelToken = CancelToken { applyRelease(ExitCase.Cancelled) }

  override fun resumeWith(result: Result<B>) {
    val releasedOrSuspended = try {
      launchRelease(a, result, uCont, release, uncancellableContext)
    } catch (e: Throwable) {
      Result.failure<B>(e.nonFatalOrThrow())
    }

    if (releasedOrSuspended != COROUTINE_SUSPENDED) uCont.resumeWith(releasedOrSuspended as Result<B>)
  }
}

/**
 * Returns either a [Result] of [B] or [COROUTINE_SUSPENDED].
 *
 * Result of B can be:
 *  - Result [B] of `use` or [Throwable] of `release`
 *  - Result [Throwable] of `use`, optionally composed with the [Throwable] of `release`
 *  => `Unit` result of `release` is **always** ignored.
 */
private fun <A, B> launchRelease(
  a: A,
  result: Result<B>,
  uCont: Continuation<B>,
  release: suspend (A, ExitCase) -> Unit,
  uncancellableContext: CoroutineContext
): Any? {
  val active = AtomicBooleanW(true)

  val frame = BracketReleaseContinuation(result, uCont, uncancellableContext)
  val released = suspend {
    result.fold(
      { if (active.compareAndSet(true, false)) release(a, ExitCase.Completed) },
      { e -> if (active.compareAndSet(true, false)) release(a, ExitCase.Failure(e)) }
    )
  }

  return try {
    val res = released.startCoroutineUninterceptedOrReturn(frame)
    if (res == COROUTINE_SUSPENDED) return COROUTINE_SUSPENDED
    else result
  } catch (e2: Throwable) { // Should compose this error with `Result<B>`
    Result.failure<B>(result.fold({ e2 }, { e -> Platform.composeErrors(e, e2) }))
  }
}

@Suppress("RESULT_CLASS_IN_RETURN_TYPE")
private class BracketReleaseContinuation<B>(
  val b: Result<B>,
  val uCont: Continuation<B>,
  uncancellableContext: CoroutineContext
) : Continuation<Unit> {

  override val context: CoroutineContext = uncancellableContext

  override fun resumeWith(result: Result<Unit>) {
    // Release returned `Unit` or `Throwable`
    val res = b.fold({
      result.fold({ b }, { e -> Result.failure(e) })
    }, { e ->
      result.fold({ b }, { e2 -> Result.failure(Platform.composeErrors(e, e2)) })
    })

    uCont.resumeWith(res)
  }
}
