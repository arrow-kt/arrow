package arrow.fx.coroutines

import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.isActive
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.suspendCoroutine
import kotlin.coroutines.startCoroutine

/**
 * Creates a cancellable `suspend` function that executes an asynchronous process on evaluation.
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
@Deprecated("Use suspendCancellableCoroutine")
suspend fun <A> cancellable(cb: ((Result<A>) -> Unit) -> CancelToken): A =
  suspendCancellableCoroutine { cont ->
    cont.context.ensureActive()
    val token = cb(cont::resumeWith)
    if (!cont.context.isActive) Platform.unsafeRunSync { token.invoke() }
    else cont.invokeOnCancellation { Platform.unsafeRunSync { token.invoke() } }
  }

/**
 * Creates a cancellable `suspend` function that executes an asynchronous process on evaluation.
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
@Deprecated("Use suspendCancellableCoroutine")
suspend fun <A> cancellableF(cb: suspend ((Result<A>) -> Unit) -> CancelToken): A =
  suspendCancellableCoroutine { cont ->
    val active = AtomicRefW(true)
    val cont1 = object : Continuation<A> {
      override val context: CoroutineContext = cont.context

      override fun resumeWith(result: Result<A>) {
        if (active.compareAndSet(true, false)) {
          cont.resumeWith(result)
        }
      }
    }

    suspend {
      withContext(NonCancellable) { cb(cont1::resumeWith) }
    }.startCoroutine(Continuation(cont.context) { res ->
      if (active.value && !cont.context.isActive) Platform.unsafeRunSync { res.getOrThrow().invoke() }
      else cont.invokeOnCancellation { Platform.unsafeRunSync { res.getOrThrow().invoke() } }
    })
  }

suspend fun <A> never(): A =
  suspendCancellableCoroutine<Nothing> {}

suspend fun unit(): Unit = Unit
