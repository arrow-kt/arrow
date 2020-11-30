package arrow.fx.coroutines

import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.intrinsics.intercepted
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * [scheduler] is **only** for internal use for the [sleep] implementation.
 * This way we can guarantee nothing besides sleeping ever occurs here.
 */
internal val scheduler: ScheduledExecutorService by lazy {
  Executors.newScheduledThreadPool(2) { r ->
    Thread(r).apply {
      name = "arrow-effect-scheduler-$id"
      isDaemon = true
    }
  }
}

/**
 * Sleeps for a given [duration] without blocking a thread.
 *
 * ```kotlin:ank:playground
 * import arrow.fx.coroutines.*
 *
 * suspend fun main(): Unit {
 *   sleep(5.seconds)
 *   println("Message after sleeping")
 * }
 * ```
 **/
suspend fun sleep(duration: Duration): Unit =
  if (duration.amount <= 0) Unit
  else cancellable { resumeWith ->
    val cancelRef = scheduler.schedule(
      { resumeWith(Result.success(Unit)) },
      duration.amount,
      duration.timeUnit
    )

    CancelToken { cancelRef.cancel(false); Unit }
  }

/**
 * Returns the result of [fa] within the specified [duration] or returns null.
 *
 * ```kotlin:ank:playground
 * import arrow.fx.coroutines.*
 *
 * suspend fun main(): Unit {
 *   timeOutOrNull(2.seconds) {
 *     sleep(5.seconds)
 *     "Message from lazy task"
 *   }.also(::println)
 *
 *   timeOutOrNull(2.seconds) {
 *     "Message from fast task"
 *   }.also(::println)
 * }
 * ```
 **/
suspend fun <A> timeOutOrNull(duration: Duration, fa: suspend () -> A): A? =
  if (duration.amount <= 0L) null
  else suspendCoroutineUninterceptedOrReturn { cont ->
    val conn = cont.context[SuspendConnection] ?: SuspendConnection.uncancellable
    val isActive = AtomicRefW(true) // keep track if already resumed

    // Create connections for fa and timer
    val faConn = SuspendConnection()
    val timerConn = SuspendConnection()

    // Register our new tokens to our parents connection
    conn.push(listOf(suspend { timerConn.cancel() }, suspend { faConn.cancel() }))

    // Launch timer on current thread (Unintercepted) with default ctx (sleep returns there), and timer connection
    // Launch on current thread, since it will immediately fork to sleeper scheduler, and free current Thread
    suspend { sleep(duration) }.startCoroutineUnintercepted(CancellableContinuation(ComputationPool + timerConn) { timeOut ->
      // If isActive then we want trigger cancel `fa` on default ctx, don't intercept since sleep already returned to default ctx
      // Resume on intercepted continuation to return to original context
      if (isActive.compareAndSet(true, false)) {
        timeOut.fold({
          suspend { faConn.cancel() }.startCoroutineUnintercepted(Continuation(ComputationPool) {
            it.fold({ cont.intercepted().resume(null) }, cont.intercepted()::resumeWithException)
          })
        }, cont.intercepted()::resumeWithException)
      } else Unit
    })

    // Start unintercepted with current context, and new connection
    fa.startCoroutineUnintercepted(Continuation(cont.context + faConn) { res ->
      // If isActive then trigger cancellation on original ctx, and don't intercept since we're still on the original ctx
      // No need to resumme on an intercepted continuation since we're still on the original ctx
      if (isActive.compareAndSet(true, false)) {
        suspend { timerConn.cancel() }.startCoroutineUnintercepted(Continuation(cont.context + SuspendConnection.uncancellable) {
          it.fold({ cont.resumeWith(res) }, { e -> cont.resumeWithException(Platform.composeErrors(e, res)) })
        })
      } else Unit
    })

    COROUTINE_SUSPENDED
  }
