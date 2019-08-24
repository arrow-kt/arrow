package arrow.fx.typeclasses

import arrow.Kind
import arrow.core.Either
import arrow.core.Right
import arrow.documented
import arrow.fx.internal.asyncContinuation
import arrow.typeclasses.MonadError
import arrow.typeclasses.MonadThrow
import arrow.typeclasses.MonadThrowFx
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.startCoroutine

/** A asynchronous computation that might fail. **/
typealias ProcF<F, A> = ((Either<Throwable, A>) -> Unit) -> Kind<F, Unit>

/** An asynchronous computation that might fail. **/
typealias Proc<A> = ((Either<Throwable, A>) -> Unit) -> Unit

/**
 * ank_macro_hierarchy(arrow.fx.typeclasses.Async)
 *
 * [Async] models how a data type runs an asynchronous computation that may fail.
 * Defined by the [Proc] signature, which is the consumption of a callback.
 **/
@documented
interface Async<F> : MonadDefer<F> {

  /**
   * Entry point for monad bindings which enables for comprehensions. The underlying impl is based on coroutines.
   * A coroutines is initiated and inside [AsyncContinuation] suspended yielding to [Monad.flatMap]. Once all the flatMap binds are completed
   * the underlying monad is returned from the act of executing the coroutine
   *
   * This one operates over [MonadError] instances that can support [Throwable] in their error type automatically lifting
   * errors as failed computations in their monadic context and not letting exceptions thrown as the regular monad binding does.
   *
   * This operation is cancellable by calling invoke on the [Disposable] return.
   * If [Disposable.invoke] is called the binding result will become a lifted [BindingCancellationException].
   */
  override val fx: AsyncFx<F>
    get() = object : AsyncFx<F> {
      override val async: Async<F> get() = this@Async
    }

  /**
   * Creates an instance of [F] that executes an asynchronous process on evaluation.
   *
   * This combinator can be used to wrap callbacks or other similar impure code.
   *
   * @param fa an asynchronous computation that might fail typed as [Proc].
   *
   * ```kotlin:ank:playground:extension
   * _imports_
   * import java.lang.RuntimeException
   *
   * object GithubService {
   *   fun getUsernames(callback: (List<String>?, Throwable?) -> Unit): Unit =
   *     callback(listOf("nomisRev", "raulraja", "pacoworks", "jorgecastilloprz"), null)
   * }
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   fun <F> Async<F>.getUsernames(): Kind<F, List<String>> =
   *     async { cb: (Either<Throwable, List<String>>) -> Unit ->
   *       GithubService.getUsernames { names, throwable ->
   *         when {
   *           names != null -> cb(Right(names))
   *           throwable != null -> cb(Left(throwable))
   *           else -> cb(Left(RuntimeException("Null result and no exception")))
   *         }
   *       }
   *     }
   *
   *   val result = _extensionFactory_.getUsernames()
   *   //sampleEnd
   *   println(result)
   * }
   * ```
   *
   * @see asyncF for a version that can suspend side effects in the registration function.
   */
  fun <A> async(fa: Proc<A>): Kind<F, A> =
    asyncF { cb -> later { fa(cb) } }

  /**
   * [async] variant that can suspend side effects in the provided registration function.
   *
   * The passed in function is injected with a side-effectful callback for signaling the final result of an asynchronous process.
   *
   * ```kotlin:ank:playground:extension
   * _imports_
   * import arrow.fx.*
   * import arrow.fx.typeclasses.Async
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   fun <F> Async<F>.makeCompleteAndGetPromiseInAsync() =
   *     asyncF<String> { cb: (Either<Throwable, String>) -> Unit ->
   *       Promise.uncancelable<F, String>(this).flatMap { promise ->
   *         promise.complete("Hello World!").flatMap {
   *           promise.get().map { str -> cb(Right(str)) }
   *         }
   *       }
   *     }
   *
   *   val result = _extensionFactory_.makeCompleteAndGetPromiseInAsync()
   *  //sampleEnd
   *  println(result)
   * }
   * ```
   *
   * @see async for a simpler, non suspending version.
   */
  fun <A> asyncF(k: ProcF<F, A>): Kind<F, A>

  /**
   * Continue the evaluation on provided [CoroutineContext]
   *
   * @param ctx [CoroutineContext] to run evaluation on
   *
   * ```kotlin:ank:playground:extension
   * _imports_
   * import kotlinx.coroutines.Dispatchers
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   fun <F> Async<F>.runOnDefaultDispatcher(): Kind<F, String> =
   *     _just_(Unit)._continueOn_(Dispatchers.Default).flatMap {
   *       _later_({ Thread.currentThread().name })
   *     }
   *
   *   val result = _extensionFactory_.runOnDefaultDispatcher()
   *   //sampleEnd
   *   println(result)
   * }
   * ```
   */
  fun <A> Kind<F, A>.continueOn(ctx: CoroutineContext): Kind<F, A>

  /**
   * Delay a computation on provided [CoroutineContext].
   *
   * @param ctx [CoroutineContext] to run evaluation on.
   *
   * ```kotlin:ank:playground:extension
   * _imports_
   * import kotlinx.coroutines.Dispatchers
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   fun <F> Async<F>.invokeOnDefaultDispatcher(): Kind<F, String> =
   *     _later_(Dispatchers.Default, { Thread.currentThread().name })
   *
   *   val result = _extensionFactory_.invokeOnDefaultDispatcher()
   *   //sampleEnd
   *   println(result)
   * }
   * ```
   */
  fun <A> later(ctx: CoroutineContext, f: () -> A): Kind<F, A> =
    defer(ctx) {
      try {
        just(f())
      } catch (t: Throwable) {
        t.raiseNonFatal<A>()
      }
    }

  /**
>>>>>>> Fix extension processor suspend fun args (#1555)
   * Delay a suspended effect.
   *
   * ```kotlin:ank:playground:extension
   * _imports_
   * import kotlinx.coroutines.Dispatchers
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   suspend fun helloWorld(): Unit = println("Hello World!")
   *
   *   fun <F> Async<F>.delayASuspendedEffect(): Kind<F, String> =
   *     _effect_ { helloWorld() }
   *
   *   val result = _extensionFactory_.delayASuspendedEffect()
   *   //sampleEnd
   *   println(result)
   * }
   * ```
   */
  fun <A> effect(f: suspend () -> A): Kind<F, A> =
    async {
      f.startCoroutine(asyncContinuation(EmptyCoroutineContext, it))
    }

  /**
   * Delay a suspended effect on provided [CoroutineContext].
   *
   * @param ctx [CoroutineContext] to run evaluation on.
   *
   * ```kotlin:ank:playground:extension
   * _imports_
   * import kotlinx.coroutines.Dispatchers
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   suspend fun getThreadSuspended(): String = Thread.currentThread().name
   *
   *   fun <F> Async<F>.invokeOnDefaultDispatcher(): Kind<F, String> =
   *     _effect_(Dispatchers.Default, { getThreadSuspended() })
   *
   *   val result = _extensionFactory_.invokeOnDefaultDispatcher()
   *   //sampleEnd
   *   println(result)
   * }
   * ```
   */
  fun <A> effect(ctx: CoroutineContext, f: suspend () -> A): Kind<F, A> =
    async {
      f.startCoroutine(asyncContinuation(ctx, it))
    }

  /**
   * Delay a computation on provided [CoroutineContext].
   *
   * @param ctx [CoroutineContext] to run evaluation on.
   *
   * ```kotlin:ank:playground:extension
   * _imports_
   * import kotlinx.coroutines.Dispatchers
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   fun <F> Async<F>.invokeOnDefaultDispatcher(): Kind<F, String> =
   *     _defer_(Dispatchers.Default, { effect { Thread.currentThread().name } })
   *
   *   val result = _extensionFactory_.invokeOnDefaultDispatcher().fix().unsafeRunSync()
   *   //sampleEnd
   *   println(result)
   * }
   * ```
   */
  fun <A> defer(ctx: CoroutineContext, f: () -> Kind<F, A>): Kind<F, A> =
    just(Unit).continueOn(ctx).flatMap { defer(f) }

  /**
   * Delay a computation on provided [CoroutineContext].
   *
   * @param ctx [CoroutineContext] to run evaluation on.
   *
   */
  fun <A> laterOrRaise(ctx: CoroutineContext, f: () -> Either<Throwable, A>): Kind<F, A> =
    defer(ctx) { f().fold({ raiseError<A>(it) }, { just(it) }) }

  /**
   * Shift evaluation to provided [CoroutineContext].
   *
   * @param ctx [CoroutineContext] to run evaluation on.
   *
   * ```kotlin:ank:playground
   * import arrow.fx.*
   * import arrow.fx.extensions.io.async.async
   * import kotlinx.coroutines.Dispatchers
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   IO.async().run {
   *     val result = fx.monad {
   *       continueOn(Dispatchers.Default)
   *       Thread.currentThread().name
   *     }.fix().unsafeRunSync()
   *
   *     println(result)
   *   }
   *   //sampleEnd
   * }
   * ```
   */
  suspend fun AsyncSyntax<F>.continueOn(ctx: CoroutineContext): Unit =
    ctx.shift().bind()

  /**
   * Shift evaluation to provided [CoroutineContext].
   *
   * @receiver [CoroutineContext] to run evaluation on.
   *
   * ```kotlin:ank:playground:extension
   * _imports_
   * import kotlinx.coroutines.Dispatchers
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   _extensionFactory_.run {
   *     val result = Dispatchers.Default._shift_().map {
   *       Thread.currentThread().name
   *     }
   *
   *     println(result)
   *   }
   *   //sampleEnd
   * }
   * ```
   */
  fun CoroutineContext.shift(): Kind<F, Unit> =
    effect(this) { Unit }

  /**
   * Task that never finishes evaluating.
   *
   * ```kotlin:ank:playground:extension
   * _imports_
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   val i = _extensionFactory_.never<Int>()
   *
   *   println(i)
   *   //sampleEnd
   * }
   * ```
   */
  fun <A> never(): Kind<F, A> =
    async(mapUnit)

  /**
   * Helper function that provides an easy way to construct a suspend effect
   *
   * ```kotlin:ank:playground:extension
   * _imports_
   * import kotlinx.coroutines.Dispatchers
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   suspend fun logAndIncrease(s: String): Int {
   *      println(s)
   *      return s.toInt() + 1
   *   }
   *
   *   val result = _extensionFactory_.effect(Dispatchers.Default) { Thread.currentThread().name }.effectMap { s: String -> logAndIncrease(s) }
   *   //sampleEnd
   *   println(result)
   * }
   * ```
   */
  fun <A, B> Kind<F, A>.effectMap(f: suspend (A) -> B): Kind<F, B> =
    flatMap { a -> effect { f(a) } }
}

internal val mapUnit: (Any?) -> Unit = { Unit }
internal val rightUnit = Right(Unit)
internal val unitCallback = { cb: (Either<Throwable, Unit>) -> Unit -> cb(rightUnit) }

interface AsyncFx<F> : MonadThrowFx<F> {
  val async: Async<F>
  override val ME: MonadThrow<F> get() = async
  fun <A> async(c: suspend AsyncSyntax<F>.() -> A): Kind<F, A> {
    val continuation = AsyncContinuation<F, A>(async)
    val wrapReturn: suspend AsyncSyntax<F>.() -> Kind<F, A> = { just(c()) }
    wrapReturn.startCoroutine(continuation, continuation)
    return continuation.returnedMonad()
  }
}
