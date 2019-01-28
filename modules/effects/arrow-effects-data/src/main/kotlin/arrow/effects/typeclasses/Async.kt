package arrow.effects.typeclasses

import arrow.Kind
import arrow.core.Either
import arrow.core.Right
import arrow.documented
import arrow.typeclasses.MonadContinuation
import kotlin.coroutines.CoroutineContext

/** A asynchronous computation that might fail. **/
typealias ProcF<F, A> = ((Either<Throwable, A>) -> Unit) -> Kind<F, Unit>

/** An asynchronous computation that might fail. **/
typealias Proc<A> = ((Either<Throwable, A>) -> Unit) -> Unit

/**
 * ank_macro_hierarchy(arrow.effects.typeclasses.Async)
 *
 * [Async] models how a data type runs an asynchronous computation that may fail.
 * Defined by the [Proc] signature, which is the consumption of a callback.
 **/
@documented
interface Async<F> : MonadDefer<F> {

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
    asyncF { cb -> delay { fa(cb) } }

  /**
   * [async] variant that can suspend side effects in the provided registration function.
   *
   * The passed in function is injected with a side-effectful callback for signaling the final result of an asynchronous process.
   *
   * ```kotlin:ank:playground:extension
   * _imports_
   * import arrow.effects.*
   * import arrow.effects.typeclasses.Async
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
   *       _delay_({ Thread.currentThread().name })
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
   *     _delay_(Dispatchers.Default, { Thread.currentThread().name })
   *
   *   val result = _extensionFactory_.invokeOnDefaultDispatcher()
   *   //sampleEnd
   *   println(result)
   * }
   * ```
   */
  fun <A> delay(ctx: CoroutineContext, f: () -> A): Kind<F, A> =
    defer(ctx) {
      try {
        just(f())
      } catch (t: Throwable) {
        raiseError<A>(t)
      }
    }

  @Deprecated("Use delay instead",
    ReplaceWith("delay(ctx, f)", "arrow.effects.typeclasses.Async"))
  operator fun <A> invoke(ctx: CoroutineContext, f: () -> A): Kind<F, A> =
    delay(ctx, f)

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
   *     _defer_(Dispatchers.Default, { delay { Thread.currentThread().name } })
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
   * Shift evaluation to provided [CoroutineContext].
   *
   * @param ctx [CoroutineContext] to run evaluation on.
   *
   * ```kotlin:ank:playground
   * import arrow.effects.*
   * import arrow.effects.extensions.io.async.async
   * import kotlinx.coroutines.Dispatchers
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   IO.async().run {
   *     val result = binding {
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
  suspend fun <A> MonadContinuation<F, A>.continueOn(ctx: CoroutineContext): Unit =
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
    delay(this) { Unit }

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

internal val mapUnit: (Any?) -> Unit = { Unit }
internal val rightUnit = Right(Unit)
internal val unitCallback = { cb: (Either<Throwable, Unit>) -> Unit -> cb(rightUnit) }
