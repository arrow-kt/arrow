package arrow.fx.coroutines

import kotlinx.atomicfu.atomic
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import kotlin.coroutines.intrinsics.startCoroutineUninterceptedOrReturn
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED

sealed class ExitCase {
  object Completed : ExitCase()
  object Cancelled : ExitCase()
  data class Failure(val failure: Throwable) : ExitCase()
}

/**
 * Runs [f] in an uncancellable manner.
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

suspend fun <A> onCancel(
  fa: suspend () -> A,
  onCancel: suspend () -> Unit
): A = guaranteeCase(fa) { case ->
  when (case) {
    ExitCase.Cancelled -> onCancel.invoke()
    else -> Unit
  }
}

suspend fun <A> guarantee(
  fa: suspend () -> A,
  release: suspend () -> Unit
): A = guaranteeCase(fa) { release.invoke() }

suspend fun <A> guaranteeCase(
  fa: suspend () -> A,
  release: suspend (ExitCase) -> Unit
): A = bracketCase({ Unit }, { fa.invoke() }, { _, ex -> release(ex) })

suspend fun <A, B> bracket(
  acquire: suspend () -> A,
  use: suspend (A) -> B,
  release: suspend (A) -> Unit
): B = bracketCase(acquire, use, { a, _ -> release(a) })

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
