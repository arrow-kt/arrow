package arrow.fx.coroutines

import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.intrinsics.createCoroutineUnintercepted
import kotlin.coroutines.intrinsics.intercepted
import kotlin.coroutines.resume

/**
 * Type to constraint [startCoroutineCancellable] to the [CancellableContinuation] constructor.
 */
@Deprecated("Use KotlinX structured concurrency as unsafe Environment to launch side-effects from non-suspending code")
abstract class CancellableContinuation<A> internal constructor() : Continuation<A>

/** Constructor for [CancellableContinuation] */
@Suppress("FunctionName")
@Deprecated("Use KotlinX structured concurrency as unsafe Environment to launch side-effects from non-suspending code")
fun <A> CancellableContinuation(
  ctx: CoroutineContext = Dispatchers.Default,
  resumeWith: (Result<A>) -> Unit
): CancellableContinuation<A> = CancellableContinuation(ctx, SuspendConnection(), resumeWith)

/**
 * Starts a coroutine without a receiver and with result type [A].
 * This function creates and starts a new, fresh instance of suspendable cancellable computation every time it is invoked.
 * The [completion] continuation is invoked when the coroutine completes with a result or an exception.
 *
 * @returns Disposable handler to cancel the started suspendable cancellable computation.
 */
@Deprecated("Use KotlinX structured concurrency as unsafe Environment to launch side-effects from non-suspending code")
fun <A> (suspend () -> A).startCoroutineCancellable(completion: CancellableContinuation<A>): Disposable {
  val conn = completion.context[SuspendConnection] ?: SuspendConnection.uncancellable
  createCoroutineUnintercepted(completion).intercepted().resume(Unit)
  return {
    Platform.unsafeRunSync { conn.cancel() }
  }
}

/**
 *
 * Constructor that allows us to launch a [CancellableContinuation] on an existing [SuspendConnection].
 */
@Suppress("FunctionName")
@Deprecated("Use KotlinX structured concurrency as unsafe Environment to launch side-effects from non-suspending code")
internal fun <A> CancellableContinuation(
  ctx: CoroutineContext = Dispatchers.Default,
  conn: SuspendConnection,
  resumeWith: (Result<A>) -> Unit
): CancellableContinuation<A> = object : CancellableContinuation<A>() {
  override val context: CoroutineContext = conn + ctx // Faster in case ctx is EmptyCoroutineContext
  override fun resumeWith(result: Result<A>) {
    if (conn.isNotCancelled()) resumeWith(result)
  }
}
