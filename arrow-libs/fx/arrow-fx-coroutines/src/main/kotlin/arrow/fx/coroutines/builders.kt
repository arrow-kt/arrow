package arrow.fx.coroutines

import kotlin.coroutines.suspendCoroutine
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED

/**
 * Create a cancellable `suspend` function that executes an asynchronous process on evaluation.
 * This combinator can be used to wrap callbacks or other similar impure code that requires cancellation code.
 *
 * ```kotlin:ank:playground
 * import arrow.fx.coroutines.*
 * import java.lang.RuntimeException
 * import java.util.concurrent.Executors
 * import java.util.concurrent.ScheduledFuture
 * import java.util.concurrent.TimeUnit
 *
 * typealias Callback = (List<String>?, Throwable?) -> Unit
 *
 * class GithubId
 * object GithubService {
 *   private val listeners: MutableMap<GithubId, ScheduledFuture<*>> = mutableMapOf()
 *   fun getUsernames(callback: Callback): GithubId {
 *     val id = GithubId()
 *     val future = Executors.newScheduledThreadPool(1).run {
 *       schedule({
 *         callback(listOf("Arrow"), null)
 *         shutdown()
 *       }, 2, TimeUnit.SECONDS)
 *     }
 *     listeners[id] = future
 *     return id
 *   }
 *   fun unregisterCallback(id: GithubId): Unit {
 *     listeners[id]?.cancel(false)
 *     listeners.remove(id)
 *   }
 * }
 *
 * suspend fun main(): Unit {
 *   //sampleStart
 *   suspend fun getUsernames(): List<String> =
 *     cancellable { cb: (Result<List<String>>) -> Unit ->
 *       val id = GithubService.getUsernames { names, throwable ->
 *         when {
 *           names != null -> cb(Result.success(names))
 *           throwable != null -> cb(Result.failure(throwable))
 *           else -> cb(Result.failure(RuntimeException("Null result and no exception")))
 *         }
 *       }
 *       CancelToken { GithubService.unregisterCallback(id) }
 *     }
 *   val result = getUsernames()
 *   //sampleEnd
 *   println(result)
 * }
 * ```
 *
 * @param cb an asynchronous computation that might fail.
 * @see suspendCoroutine for wrapping impure APIs without cancellation
 * @see cancellableF for wrapping impure APIs using a suspend with cancellation
 */
suspend fun <A> cancellable(cb: ((Result<A>) -> Unit) -> CancelToken): A =
  suspendCoroutine { cont ->
    val conn = cont.context.connection()
    val cbb2 = Platform.onceOnly(conn, cont::resumeWith)

    val cancellable = ForwardCancellable()
    conn.push(cancellable.cancel())

    if (conn.isNotCancelled()) {
      cancellable.complete(
        try {
          cb(cbb2)
        } catch (throwable: Throwable) {
          cbb2(Result.failure(throwable.nonFatalOrThrow()))
          CancelToken.unit
        }
      )
    } else cancellable.complete(CancelToken.unit)
  }

/**
 * Create a cancellable `suspend` function that executes an asynchronous process on evaluation.
 * This combinator can be used to wrap callbacks or other similar impure code that requires cancellation code.
 *
 * The suspending [cb] runs in an uncancellable manner, acquiring [CancelToken] as a resource.
 * If cancellation signal is received while [cb] is running, then the [CancelToken] will be triggered as soon as it's returned.
 *
 * ```kotlin:ank:playground
 * import arrow.core.*
 * import arrow.fx.coroutines.*
 * import java.lang.RuntimeException
 * import kotlin.coroutines.Continuation
 * import kotlin.coroutines.EmptyCoroutineContext
 * import kotlin.coroutines.startCoroutine
 *
 * typealias Callback = (List<String>?, Throwable?) -> Unit
 *
 * class GithubId
 * object GithubService {
 *   private val listeners: MutableMap<GithubId, Fiber<*>> = mutableMapOf()
 *   suspend fun getUsernames(callback: Callback): GithubId {
 *     val id = GithubId()
 *     val fiber = ForkConnected { sleep(2.seconds); callback(listOf("Arrow"), null) }
 *     listeners[id] = fiber
 *     return id
 *   }
 *   fun unregisterCallback(id: GithubId): Unit {
 *     suspend { listeners[id]?.cancel() }
 *       .startCoroutine(Continuation(EmptyCoroutineContext) { }) // Launch and forget
 *     listeners.remove(id)
 *   }
 * }
 *
 * suspend fun main(): Unit {
 *   //sampleStart
 *   suspend fun getUsernames(): List<String> =
 *     cancellableF { cb: (Result<List<String>>) -> Unit ->
 *       val id = GithubService.getUsernames { names, throwable ->
 *         when {
 *           names != null -> cb(Result.success(names))
 *           throwable != null -> cb(Result.failure(throwable))
 *           else -> cb(Result.failure(RuntimeException("Null result and no exception")))
 *         }
 *       }
 *       CancelToken { GithubService.unregisterCallback(id) }
 *     }
 *   val result = getUsernames()
 *   //sampleEnd
 *   println(result)
 * }
 * ```
 *
 * @param cb an asynchronous computation that might fail.
 * @see suspendCoroutine for wrapping impure APIs without cancellation
 * @see cancellable for wrapping impure APIs with cancellation
 */
suspend fun <A> cancellableF(cb: suspend ((Result<A>) -> Unit) -> CancelToken): A =
  suspendCoroutine { cont ->
    val conn = cont.context.connection()

    val state = AtomicRefW<((Result<Unit>) -> Unit)?>(null)
    val cb1 = { a: Result<A> ->
      try {
        cont.resumeWith(a)
      } finally {
        // compareAndSet can only succeed in case the operation is already finished
        // and no cancellation token was installed yet
        if (!state.compareAndSet(null, { Unit })) {
          val cb2 = state.value
          state.lazySet(null)
          cb2?.invoke(Result.success(Unit))
        }
      }
    }

    val conn2 = SuspendConnection()
    conn.push(conn2.cancelToken())

    suspend {
      // Until we've got a cancellation token, the task needs to be evaluated
      // uninterruptedly, otherwise risking a leak, hence the bracket
      // TODO CREATE KotlinTracker issue using CancelToken here breaks something in compilation
      bracketCase<suspend () -> Unit, Unit>(
        acquire = { cb(cb1).cancel },
        use = { waitUntilCallbackInvoked(state) },
        release = { token, ex ->
          when (ex) {
            ExitCase.Cancelled -> token.invoke()
            else -> Unit
          }
        }
      )
    }.startCoroutineCancellable(CancellableContinuation(cont.context, conn2) {
      // TODO send CancelToken exception to Enviroment
      it.fold({ arrow.core.identity(it) }, Throwable::printStackTrace)
    })
  }

private suspend fun waitUntilCallbackInvoked(state: AtomicRefW<((Result<Unit>) -> Unit)?>) =
  suspendCoroutineUninterceptedOrReturn<Unit> { cont ->
    // compareAndSet succeeds if CancelToken returned & callback not invoked, suspend
    if (state.compareAndSet(null, cont::resumeWith)) COROUTINE_SUSPENDED
    else Unit // k invoked before token returned, finish immediately
  }

suspend fun <A> never(): A =
  suspendCoroutine<Nothing> {}

suspend fun unit(): Unit = Unit
