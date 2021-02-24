package arrow.fx.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

/**
 * [Fiber] represents a pure value that contains a running `suspend () -> A`.
 *
 * You can think of fibers as being lightweight threads. A [Fiber] is a
 * concurrency primitive for doing cooperative multi-tasking.
 */
@Deprecated("Use kotlinx.coroutines.Deferred")
interface Fiber<A> {

  suspend fun join(): A
  suspend fun cancel(): Unit

  companion object {

    /**
     * [Fiber] constructor.
     *
     * @param join task that will await for the completion of the underlying Fiber.
     * @param cancel task that will trigger the cancellation.
     */
    operator fun <A> invoke(join: suspend () -> A, cancel: CancelToken): Fiber<A> = object : Fiber<A> {
      override suspend fun join(): A = join()
      override suspend fun cancel(): Unit = cancel.invoke()
      override fun toString(): String = "Fiber(join= $join, cancel= $cancel)"
    }
  }
}

internal fun <A> Fiber(promise: UnsafePromise<A>, conn: SuspendConnection): Fiber<A> =
  Fiber({ promise.join() }, CancelToken { conn.cancel() })

/**
 * Launches a new suspendable cancellable coroutine within a [Fiber].
 * It does so by connecting the created [Fiber]'s cancellation to the callers `suspend` scope.
 * If the caller of `ForkConnected` gets cancelled, then this [Fiber] will also get cancelled.
 *
 * ```kotlin:ank:playground
 * import arrow.fx.coroutines.*
 *
 * suspend fun main(): Unit {
 *   val parent = ForkConnected {
 *     ForkConnected { // cancellation connected to parent
 *        onCancel({ never<Unit>() }) {
 *          println("I got cancelled by my parent")
 *        }
 *     }
 *   }
 *   sleep(1.seconds)
 *   parent.cancel()
 * }
 * ```
 *
 * You can [Fiber.join] or [Fiber.cancel] the computation.
 * Cancelling this [Fiber] **will not** cancel its parent.
 */
@Deprecated(
  "Use Deferred with KotlinX Coroutines Structured Concurrency",
  ReplaceWith("async(ctx) { f() }", "kotlinx.coroutines.Deferred")
)
suspend fun <A> ForkConnected(ctx: CoroutineContext = Dispatchers.Default, f: suspend () -> A): Fiber<A> {
  val def = CoroutineScope(coroutineContext[Job] ?: Job()).async(ctx) {
    runCatching { f.invoke() }
  }

  return object : Fiber<A> {
    override suspend fun join(): A = def.await().getOrThrow()
    override suspend fun cancel() = def.cancelAndJoin()
  }
}

/** @see ForkConnected **/
@Deprecated(
  "Use Deferred with KotlinX Coroutines Structured Concurrency",
  ReplaceWith("async(ctx) { invoke() }", "kotlinx.coroutines.Deferred")
)
suspend fun <A> (suspend () -> A).forkConnected(ctx: CoroutineContext = Dispatchers.Default): Fiber<A> =
  ForkConnected(ctx, this)

/**
 * Launches a new suspendable cancellable coroutine within a [Fiber].
 * It does so by connecting the created [Fiber]'s cancellation to the provided [interruptWhen].
 * If the [interruptWhen] signal gets triggered, then this [Fiber] will get cancelled.
 *
 * You can still cancel the [Fiber] independent from the [interruptWhen] token;
 * whichever one comes first cancels the [Fiber].
 *
 * This function is meant to integrate with 3rd party cancellation system such as Android.
 *
 * ```kotlin:ank:playground
 * import arrow.fx.coroutines.*
 *
 * tailrec suspend fun parallelProcess(): Unit {
 *   println(System.currentTimeMillis())
 *   sleep(1.seconds)
 *   parallelProcess()
 * }
 *
 * suspend fun main(): Unit {
 *   val switch = Promise<Unit>()
 *   val switcher = suspend {
 *     sleep(5.seconds)
 *     switch.complete(Unit)
 *   }
 *
 *   ::parallelProcess.forkScoped(interruptWhen = switch::get)
 *   switcher.forkConnected()
 * }
 * ```
 */
// TODO provide proper deprecation annotation
suspend fun <A> ForkScoped(
  ctx: CoroutineContext = Dispatchers.Default,
  interruptWhen: suspend () -> Unit,
  f: suspend () -> A
): Fiber<A> {
  val scope = CoroutineScope(ctx)
  val res = scope.async { f.invoke() }
  scope.launch { interruptWhen.invoke(); res.cancelAndJoin() }
  return res.toFiber()
}

/** @see ForkScoped */
// TODO provide proper deprecation annotation
suspend fun <A> (suspend () -> A).forkScoped(
  ctx: CoroutineContext = Dispatchers.Default,
  interruptWhen: suspend () -> Unit
): Fiber<A> = ForkScoped(ctx, interruptWhen, this)

/**
 * Launches a new suspendable cancellable coroutine within a [Fiber].
 * You can [Fiber.join] or [Fiber.cancel] the computation.
 *
 * **BEWARE**: you immediately leak the [Fiber] when launching without connection control.
 * Use [ForkConnected] or safely launch the fiber as a [Resource] or using [bracketCase].
 *
 * @see ForkConnected for a fork operation that wires cancellation to its parent in a safe way.
 */
// TODO provide proper deprecation annotation
suspend fun <A> ForkAndForget(ctx: CoroutineContext = Dispatchers.Default, f: suspend () -> A): Fiber<A> =
  f.forkAndForget(ctx)

/** @see ForkAndForget */
// TODO provide proper deprecation annotation
suspend fun <A> (suspend () -> A).forkAndForget(ctx: CoroutineContext = Dispatchers.Default): Fiber<A> =
  CoroutineScope(ctx).async {
    invoke()
  }.toFiber()

fun <A> Deferred<A>.toFiber(): Fiber<A> =
  object : Fiber<A> {
    override suspend fun join(): A = await()
    override suspend fun cancel() = cancelAndJoin()
  }
