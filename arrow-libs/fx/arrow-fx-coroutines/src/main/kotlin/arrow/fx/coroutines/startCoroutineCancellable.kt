package arrow.fx.coroutines

import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.intrinsics.createCoroutineUnintercepted
import kotlin.coroutines.intrinsics.intercepted
import kotlin.coroutines.resume

/**
 * Type to constraint [startCoroutineCancellable] to the [CancellableContinuation] constructor.
 */
interface CancellableContinuation<A> : Continuation<A>

/** Constructor for [CancellableContinuation] */
@Suppress("FunctionName")
inline fun <A> CancellableContinuation(
  ctx: CoroutineContext = ComputationPool,
  crossinline resumeWith: (Result<A>) -> Unit
): CancellableContinuation<A> = CancellableContinuation(ctx, SuspendConnection(), resumeWith)

/**
 * Starts a coroutine without a receiver and with result type [A].
 * This function creates and starts a new, fresh instance of suspendable cancellable computation every time it is invoked.
 * The [completion] continuation is invoked when the coroutine completes with a result or an exception.
 *
 * @returns Disposable handler to cancel the started suspendable cancellable computation.
 */
fun <A> (suspend () -> A).startCoroutineCancellable(completion: CancellableContinuation<A>): Disposable {
  val conn = completion.context.connection()
  createCoroutineUnintercepted(completion).intercepted().resume(Unit)
  return conn.toDisposable()
}

/**
 *
 * Constructor that allows us to launch a [CancellableContinuation] on an existing [SuspendConnection].
 */
@PublishedApi
@Suppress("FunctionName")
internal inline fun <A> CancellableContinuation(
  ctx: CoroutineContext = ComputationPool,
  conn: SuspendConnection,
  crossinline resumeWith: (Result<A>) -> Unit
): CancellableContinuation<A> = object : CancellableContinuation<A> {
  override val context: CoroutineContext = conn + ctx // Faster in case ctx is EmptyCoroutineContext
  override fun resumeWith(result: Result<A>) {
    if (conn.isNotCancelled()) resumeWith(result)
  }
}
