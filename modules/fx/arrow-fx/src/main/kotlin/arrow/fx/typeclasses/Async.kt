package arrow.fx.typeclasses

import arrow.Kind
import arrow.core.Either
import arrow.core.Right
import arrow.documented
import arrow.fx.extensions.io.async.async
import arrow.fx.internal.asyncContinuation
import arrow.typeclasses.MonadError
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.startCoroutine

/** An asynchronous computation that might fail with a specific error type. **/
typealias ProcEF<F, E, A> = ((Either<E, A>) -> Unit) -> Kind<F, Unit>

/** An asynchronous computation that might fail with a specific error type. **/
typealias ProcE<E, A> = ((Either<E, A>) -> Unit) -> Unit

/** An asynchronous computation that might fail. **/
typealias ProcF<F, A> = ProcEF<F, Throwable, A>

/** An asynchronous computation that might fail. **/
typealias Proc<A> = ProcE<Throwable, A>

/**
 * ank_macro_hierarchy(arrow.fx.typeclasses.Async)
 *
 * [Async] models how a data type runs an asynchronous computation that may fail.
 * Defined by the [ProcE] signature, which is the consumption of a callback.
 **/
@documented
interface Async<F, E> : MonadDefer<F, E> {

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
  override val fx: AsyncFx<F, E>
    get() = object : AsyncFx<F, E> {
      override val async: Async<F, E> get() = this@Async
    }

  /**
   * Creates an instance of [F] that executes an asynchronous process on evaluation.
   *
   * This combinator can be used to wrap callbacks or other similar impure code.
   *
   * @param fa an asynchronous computation that might fail typed as [ProcE].
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
   *   fun <F> Async<F, Throwable>.getUsernames(): Kind<F, List<String>> =
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
  fun <A> async(fa: ProcE<E, A>): Kind<F, A> =
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
   *   fun <F> Async<F, Throwable>.makeCompleteAndGetPromiseInAsync() =
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
  fun <A> asyncF(k: ProcEF<F, E, A>): Kind<F, A>

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
   *   fun <F> Async<F, Throwable>.runOnDefaultDispatcher(): Kind<F, String> =
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
   *   fun <F> Async<F, Throwable>.invokeOnDefaultDispatcher(): Kind<F, String> =
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
        t.raiseThrowableNonFatal<A>()
      }
    }

  /**
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
   *   fun <F> Async<F, Throwable>.delayASuspendedEffect(): Kind<F, String> =
   *     _effect_ { helloWorld() }
   *
   *   val result = _extensionFactory_.delayASuspendedEffect()
   *   //sampleEnd
   *   println(result)
   * }
   * ```
   */
  fun <A> Async<F, Throwable>.effect(f: suspend () -> A): Kind<F, A> =
    effect(EmptyCoroutineContext, f)

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
   *   fun <F> Async<F, Throwable>.invokeOnDefaultDispatcher(): Kind<F, String> =
   *     _effect_(Dispatchers.Default, { getThreadSuspended() })
   *
   *   val result = _extensionFactory_.invokeOnDefaultDispatcher()
   *   //sampleEnd
   *   println(result)
   * }
   * ```
   */
  fun <A> Async<F, Throwable>.effect(ctx: CoroutineContext, f: suspend () -> A): Kind<F, A> =
    async { cb ->
      f.startCoroutine(asyncContinuation(ctx, cb))
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
   *   fun <F> Async<F, Throwable>.invokeOnDefaultDispatcher(): Kind<F, String> =
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
  fun <A> laterOrRaise(ctx: CoroutineContext, f: () -> Either<E, A>): Kind<F, A> =
    defer(ctx) { f().fold(::raiseError, ::just) }

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
  suspend fun AsyncSyntax<F, E>.continueOn(ctx: CoroutineContext): Unit =
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
    later(this, mapUnit)

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
    async { }
}

internal val mapToUnit: (Any?) -> Unit = { Unit }
internal val mapUnit: () -> Unit = { Unit }
internal val rightUnit = Right(Unit)
internal val unitCallback = { cb: (Either<Nothing, Unit>) -> Unit -> cb(rightUnit) }

interface AsyncFx<F, E> : MonadDeferFx<F, E> {
  val async: Async<F, E>
  override val ME: MonadDefer<F, E> get() = async
  fun <A> async(c: suspend AsyncSyntax<F, E>.() -> A): Kind<F, A> {
    val continuation = AsyncContinuation<F, A, E>(async)
    val wrapReturn: suspend AsyncSyntax<F, E>.() -> Kind<F, A> = { just(c()) }
    wrapReturn.startCoroutine(continuation, continuation)
    return continuation.returnedMonad()
  }
}
