package arrow.effects.typeclasses

import arrow.Kind
import arrow.core.*
import arrow.effects.CancelToken
import arrow.core.Either
import arrow.typeclasses.MonadContinuation
import java.util.concurrent.atomic.AtomicReference
import kotlin.coroutines.CoroutineContext

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
interface Async<F> : MonadDefer<F> {

  /**
   * Creates an instance of [F] that executes an asynchronous process on evaluation.
   *
   * This combinator can be used to wrap callbacks or other similar impure code.
   *
   * @param fa an asynchronous computation that might fail typed as [Proc].
   *
   * {: data-executable='true'}
   * ```kotlin:ank
   * import arrow.Kind
   * import arrow.core.*
   * import arrow.effects.*
   * import arrow.effects.instances.io.async.async
   * import arrow.effects.typeclasses.Async
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
   *   val result = IO.async().getUsernames()
   *     .fix()
   *     .unsafeRunSync()
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
   * {: data-executable='true'}
   * ```kotlin:ank
   * import arrow.core.*
   * import arrow.effects.*
   * import arrow.effects.instances.io.async.async
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
   *   val result = IO.async().makeCompleteAndGetPromiseInAsync().fix().unsafeRunSync()
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
   * {: data-executable='true'}
   * ```kotlin:ank
   * import arrow.Kind
   * import arrow.effects.*
   * import arrow.effects.deferredk.async.async
   * import arrow.effects.typeclasses.Async
   * import kotlinx.coroutines.Dispatchers
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   fun <F> Async<F>.runOnDefaultDispatcher(): Kind<F, String> =
   *     just(Unit).continueOn(Dispatchers.Default).flatMap {
   *       delay { Thread.currentThread().name }
   *     }
   *
   *   val result = DeferredK.async().runOnDefaultDispatcher().fix().unsafeRunSync()
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
   * {: data-executable='true'}
   * ```kotlin:ank
   * import arrow.Kind
   * import arrow.effects.*
   * import arrow.effects.instances.io.async.async
   * import arrow.effects.typeclasses.Async
   * import kotlinx.coroutines.Dispatchers
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   fun <F> Async<F>.invokeOnDefaultDispatcher(): Kind<F, String> =
   *     delay(Dispatchers.Default) { Thread.currentThread().name }
   *
   *   val result = IO.async().invokeOnDefaultDispatcher().fix().unsafeRunSync()
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
   * {: data-executable='true'}
   * ```kotlin:ank
   * import arrow.Kind
   * import arrow.effects.*
   * import arrow.effects.instances.io.async.async
   * import arrow.effects.typeclasses.Async
   * import kotlinx.coroutines.Dispatchers
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   fun <F> Async<F>.invokeOnDefaultDispatcher(): Kind<F, String> =
   *     defer(Dispatchers.Default) { delay { Thread.currentThread().name } }
   *
   *   val result = IO.async().invokeOnDefaultDispatcher().fix().unsafeRunSync()
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
   * {: data-executable='true'}
   * ```kotlin:ank
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
   * {: data-executable='true'}
   * ```kotlin:ank
   * import arrow.effects.*
   * import arrow.effects.instances.io.async.async
   * import kotlinx.coroutines.Dispatchers
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   IO.async().run {
   *     val result = Dispatchers.Default.shift().map {
   *       Thread.currentThread().name
   *     }.unsafeRunSync()
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
   * {: data-executable='true'}
   * ```kotlin:ank
   * import arrow.effects.*
   * import arrow.effects.instances.io.async.async
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   val i = IO.async().never<Int>()
   *     .fix().unsafeRunSync()
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
   * {: data-executable='true'}
   * ```kotlin:ank
   * import arrow.core.Left
   * import arrow.core.Right
   * import arrow.effects.*
   * import arrow.effects.instances.io.async.async
   * import arrow.effects.instances.io.async.shift
   * import kotlinx.coroutines.runBlocking
   * import kotlinx.coroutines.Dispatchers.Default
   * import kotlinx.coroutines.async
   * import kotlinx.coroutines.delay
   *
   * object Account
   *
   * //Some impure API or code
   * class NetworkService {
   *   fun getAccounts(successCallback: (List<Account>) -> Unit,
   *     failureCallback: (Throwable) -> Unit) {
   *       kotlinx.coroutines.GlobalScope.async(Default) {
   *         println("Making API call")
   *         delay(500)
   *         successCallback(listOf(Account))
   *       }
   *   }
   *
   *   fun cancel(): Unit = kotlinx.coroutines.runBlocking {
   *     println("Cancelled, closing NetworkApi")
   *     delay(500)
   *     println("Closed NetworkApi")
   *   }
   * }
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   val getAccounts = Default.shift().flatMap {
   *     IO.async().cancelable<List<Account>> { cb ->
   *       val service = NetworkService()
   *       service.getAccounts(
   *         successCallback = { accs -> cb(Right(accs)) },
   *         failureCallback = { e -> cb(Left(e)) })
   *
   *       IO { service.cancel() }
   *     }
   *   }.fix().unsafeRunAsyncCancellable { println(it) }
   *
   *   runBlocking {
   *     delay(250)
   *     getAccounts.invoke() //Cancel API call
   *   }
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
   * {: data-executable='true'}
   * ```kotlin:ank
   * import arrow.core.Right
   * import arrow.effects.*
   * import arrow.effects.instances.io.async.async
   * import kotlinx.coroutines.GlobalScope
   * import kotlinx.coroutines.async
   * import kotlinx.coroutines.delay

   * fun main(args: Array<String>) {
   *   //sampleStart
   *   val result = IO.async().cancelableF<String> { cb ->
   *     IO {
   *       val deferred = GlobalScope.async {
   *         delay(1000)
   *         cb(Right("Hello from ${Thread.currentThread().name}"))
   *       }
   *
   *       IO { deferred.cancel().let { Unit } }
   *     }
   *   }.fix().unsafeRunSync()
   *   //sampleEnd
   *   println(result)
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
