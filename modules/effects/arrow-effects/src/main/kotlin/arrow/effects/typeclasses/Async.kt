package arrow.effects.typeclasses

import arrow.Kind
import arrow.core.*
import arrow.effects.CancelToken
import arrow.core.Either
import arrow.documented
import arrow.core.Right
import arrow.effects.Promise
import arrow.typeclasses.MonadContinuation
import java.util.concurrent.atomic.AtomicReference
import arrow.core.left
import arrow.effects.*
import arrow.effects.internal.asyncIOContinuation
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.startCoroutine
import kotlin.coroutines.suspendCoroutine

/** A cancellable asynchronous computation that might fail. **/
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
   * ```kotlin:ank:playground:extension:playground:extension
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
   * import arrow.effects.typeclasses.Async
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   fun <F> Async<F>.makeCompleteAndGetPromiseInAsync() =
   *     asyncF<String> { cb: (Either<Throwable, String>) -> Unit ->
   *       Promise.uncancelable<F, String>(this).flatMap { promise ->
   *         promise.complete("Hello World!").flatMap {
   *           promise.get.map { str -> cb(Right(str)) }
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
    ctx.shift().flatMap { delay(f) }

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
    ctx.shift().flatMap { defer(f) }

  /**
   * Shift evaluation to provided [CoroutineContext].
   *
   * @param ctx [CoroutineContext] to run evaluation on.
   *
   * ```kotlin:ank:playground
   * import arrow.effects.*
   * import arrow.effects.instances.io.async.async
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
    just(Unit).continueOn(this) as Kind<F, Unit>

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

  /**
   * Creates a cancelable [F] instance that executes an asynchronous process on evaluation.
   * Derived from [async] and [bracketCase] so does not require [F] to be cancelable.
   *
   * **NOTE**: Only for a cancelable type can [bracketCase] ever call `cancel` but that's of no concern for
   * non-cancelable types as `cancel` never should be called.
   *
   * ```kotlin:ank:playground:extension
   * _imports_
   * _imports_monaddefer_
   *
   * import kotlinx.coroutines.Dispatchers.Default
   * import kotlinx.coroutines.async
   * import kotlinx.coroutines.GlobalScope
   *
   * object Account
   *
   * //Some impure API or code
   * class NetworkService {
   *   fun getAccounts(
   *     successCallback: (List<Account>) -> Unit,
   *     failureCallback: (Throwable) -> Unit) {
   *
   *       GlobalScope.async(Default) {
   *         println("Making API call")
   *         kotlinx.coroutines.delay(500)
   *         successCallback(listOf(Account))
   *       }
   *   }
   *
   *   fun cancel(): Unit = kotlinx.coroutines.runBlocking {
   *     println("Cancelled, closing NetworkApi")
   *     kotlinx.coroutines.delay(500)
   *     println("Closed NetworkApi")
   *   }
   * }
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   val getAccounts = Default._shift_().flatMap {
   *     _extensionFactory_.cancelable<List<Account>> { cb ->
   *       val service = NetworkService()
   *       service.getAccounts(
   *         successCallback = { accs -> cb(Right(accs)) },
   *         failureCallback = { e -> cb(Left(e)) })
   *
   *       _delay_({ service.cancel() })
   *     }
   *   }
   *
   *
   *   //sampleEnd
   * }
   * ```
   * @see cancelableF for a version that can safely suspend impure callback registration code.
   *     F.asyncF[A] { cb =>
   */
  fun <A> cancelable(k: ((Either<Throwable, A>) -> Unit) -> CancelToken<F>): Kind<F, A> =
    cancelableF { cb -> delay { k(cb) } }

  /**
   * Creates a cancelable [F] instance that executes an asynchronous process on evaluation.
   * Derived from [async] and [bracketCase] so does not require [F] to be cancelable.
   *
   * **NOTE**: Only for a cancelable type can [bracketCase] ever call `cancel` but that's of no concern for
   * non-cancelable types as `cancel` never should be called.
   *
   * ```kotlin:ank:playground:extension
   * _imports_
   * _imports_monaddefer_
   * import kotlinx.coroutines.async
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   val result = _extensionFactory_.cancelableF<String> { cb ->
   *     delay {
   *       val deferred = kotlinx.coroutines.GlobalScope.async {
   *         kotlinx.coroutines.delay(1000)
   *         cb(Right("Hello from ${Thread.currentThread().name}"))
   *       }
   *
   *       delay({ deferred.cancel().let { Unit } })
   *     }
   *   }
   *
   *   println(result) //Run with `fix().unsafeRunSync()`
   *
   *   val result2 = _extensionFactory_.cancelableF<Unit> { cb ->
   *     delay {
   *       println("Doing something that can be cancelled.")
   *       delay({ println("Cancelling the task") })
   *     }
   *   }
   *
   *   println(result2) //Run with `fix().unsafeRunAsyncCancellable { }.invoke()`
   *   //sampleEnd
   * }
   * ```
   *
   * @see cancelable for a simpler non-suspending version.
   */
  fun <A> cancelableF(k: ((Either<Throwable, A>) -> Unit) -> Kind<F, CancelToken<F>>): Kind<F, A> =
    asyncF { cb ->
      val state = AtomicReference<(Either<Throwable, Unit>) -> Unit>(null)
      val cb1 = { r: Either<Throwable, A> ->
        try {
          cb(r)
        } finally {
          if (!state.compareAndSet(null, mapUnit)) {
            val cb2 = state.get()
            state.lazySet(null)
            cb2(rightUnit)
          }
        }
      }

      k(cb1).bracketCase(use = {
        async<Unit> { cb ->
          if (!state.compareAndSet(null, cb)) cb(rightUnit)
        }
      }, release = { token, exitCase ->
        when (exitCase) {
          is ExitCase.Cancelled -> token
          else -> just(Unit)
        }
      })
    }

}

internal val mapUnit: (Any?) -> Unit = { Unit }
internal val rightUnit = Right(Unit)
internal val unitCallback = { cb: (Either<Throwable, Unit>) -> Unit -> cb(rightUnit) }
