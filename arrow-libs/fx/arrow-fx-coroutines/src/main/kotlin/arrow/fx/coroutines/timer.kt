package arrow.fx.coroutines

import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import kotlin.coroutines.Continuation
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.startCoroutine
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import kotlin.coroutines.intrinsics.startCoroutineUninterceptedOrReturn
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.intrinsics.intercepted
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
    val conn = cont.context.connection()
    val isActive = AtomicRefW(true) // keep track if already resumed

    // Create connections for fa and timer
    val faConn = SuspendConnection()
    val timerConn = SuspendConnection()

    // Register our new tokens to our parents connection
    conn.push(listOf(timerConn.cancelToken(), faConn.cancelToken()))

    suspend { sleep(duration) }.startCoroutineCancellable(
      CancellableContinuation(
        EmptyCoroutineContext,
        timerConn
      ) { timeOut ->
        timeOut.fold({
          if (isActive.compareAndSet(true, false)) {
            faConn.cancelToken().cancel.startCoroutine(Continuation(EmptyCoroutineContext) {
              it.fold({ cont.intercepted().resume(null) }, cont.intercepted()::resumeWithException)
            })
          }
        }, cont.intercepted()::resumeWithException)
      })

    // Start unintercepted with timeOut connection attached to parents CoroutineContext
    val x = fa.startCoroutineUninterceptedOrReturn(Continuation(cont.context + faConn) {
      if (isActive.compareAndSet(true, false)) cont.resumeWith(it)
    })

    if (x != COROUTINE_SUSPENDED && isActive.compareAndSet(true, false)) x
    else COROUTINE_SUSPENDED
  }
