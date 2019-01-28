package arrow.effects.typeclasses

import arrow.Kind
import arrow.core.*
import arrow.effects.CancelToken
import arrow.effects.KindConnection
import arrow.effects.data.internal.BindingCancellationException
import arrow.typeclasses.MonadContinuation
import java.util.concurrent.atomic.AtomicReference
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.startCoroutine

/** A connected asynchronous computation that might fail. **/
typealias ConnectedProcF<F, A> = (KindConnection<F>, ((Either<Throwable, A>) -> Unit)) -> Kind<F, Unit>

/** A connected asynchronous computation that might fail. **/
typealias ConnectedProc<F, A> = (KindConnection<F>, ((Either<Throwable, A>) -> Unit)) -> Unit

/**
 * ank_macro_hierarchy(arrow.effects.typeclasses.Concurrent)
 *
 * Type class for async data types that are cancelable and can be started concurrently.
 */
interface Concurrent<F> : Async<F> {

  /**
   * Creates a cancelable instance of [F] that executes an asynchronous process on evaluation.
   * This combinator can be used to wrap callbacks or other similar impure code that requires cancellation code.
   *
   * ```kotlin:ank:playground:extension
   * _imports_
   * import java.lang.RuntimeException
   *
   * typealias Callback = (List<String>?, Throwable?) -> Unit
   *
   * class Id
   * object GithubService {
   *   private val listeners: MutableMap<Id, Callback> = mutableMapOf()
   *   fun getUsernames(callback: (List<String>?, Throwable?) -> Unit): Id {
   *     val id = Id()
   *     listeners[id] = callback
   *     //execute operation and call callback at some point in future
   *     return id
   *   }
   *
   *   fun unregisterCallback(id: Id): Unit {
   *     listeners.remove(id)
   *   }
   * }
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   fun <F> Concurrent<F>.getUsernames(): Kind<F, List<String>> =
   *     async { conn: KindConnection<F>, cb: (Either<Throwable, List<String>>) -> Unit ->
   *       val id = GithubService.getUsernames { names, throwable ->
   *         when {
   *           names != null -> cb(Right(names))
   *           throwable != null -> cb(Left(throwable))
   *           else -> cb(Left(RuntimeException("Null result and no exception")))
   *         }
   *       }
   *
   *       conn.push(_delay_({ GithubService.unregisterCallback(id) }))
   *       conn.push(_delay_({ println("Everything we push to the cancellation stack will execute on cancellation") }))
   *     }
   *
   *   val result = _extensionFactory_.getUsernames()
   *   //sampleEnd
   *   println(result)
   * }
   * ```
   *
   * @param fa an asynchronous computation that might fail typed as [ConnectedProc].
   * @see asyncF for a version that can suspend side effects in the registration function.
   */
  fun <A> async(fa: ConnectedProc<F, A>): Kind<F, A> =
    asyncF { conn, cb -> delay { fa(conn, cb) } }

  /**
   * Creates a cancelable instance of [F] that executes an asynchronous process on evaluation.
   * This combinator can be used to wrap callbacks or other similar impure code that requires cancellation code.
   *
   * ```kotlin:ank:playground:extension
   * _imports_
   * import java.lang.RuntimeException
   *
   * typealias Callback = (List<String>?, Throwable?) -> Unit
   *
   * class Id
   * object GithubService {
   *   private val listeners: MutableMap<Id, Callback> = mutableMapOf()
   *   fun getUsernames(callback: (List<String>?, Throwable?) -> Unit): Id {
   *     val id = Id()
   *     listeners[id] = callback
   *     //execute operation and call callback at some point in future
   *     return id
   *   }
   *
   *   fun unregisterCallback(id: Id): Unit {
   *     listeners.remove(id)
   *   }
   * }
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   fun <F> Concurrent<F>.getUsernames(): Kind<F, List<String>> =
   *     asyncF { conn: KindConnection<F>, cb: (Either<Throwable, List<String>>) -> Unit ->
   *       delay {
   *         val id = GithubService.getUsernames { names, throwable ->
   *           when {
   *             names != null -> cb(Right(names))
   *             throwable != null -> cb(Left(throwable))
   *             else -> cb(Left(RuntimeException("Null result and no exception")))
   *           }
   *         }
   *
   *         conn.push(_delay_({ GithubService.unregisterCallback(id) }))
   *         conn.push(_delay_({ println("Everything we push to the cancellation stack will execute on cancellation") }))
   *       }
   *     }
   *
   *   val result = _extensionFactory_.getUsernames()
   *   //sampleEnd
   *   println(result)
   * }
   * ```
   *
   * @param fa a deferred asynchronous computation that might fail typed as [ConnectedProcF].
   * @see async for a version that can suspend side effects in the registration function.
   */
  fun <A> asyncF(fa: ConnectedProcF<F, A>): Kind<F, A>

  /**
   * Create a new [F] that upon execution starts the receiver [F] within a [Fiber] on [ctx].
   *
   * ```kotlin:ank:playground
   * import arrow.effects.*
   * import arrow.effects.extensions.io.async.async
   * import arrow.effects.extensions.io.monad.binding
   * import kotlinx.coroutines.Dispatchers
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   binding {
   *     val promise = Promise.uncancelable<ForIO, Int>(IO.async()).bind()
   *     val fiber = promise.get().startF(Dispatchers.Default).bind()
   *     promise.complete(1).bind()
   *     fiber.join().bind()
   *   }.unsafeRunSync() == 1
   *   //sampleEnd
   * }
   * ```
   *
   * @receiver [F] to execute on [ctx] within a new suspended [F].
   * @param ctx [CoroutineContext] to execute the source [F] on.
   * @return [F] with suspended execution of source [F] on context [ctx].
   */
  fun <A> Kind<F, A>.startF(ctx: CoroutineContext): Kind<F, Fiber<F, A>>

  /**
   * Race two tasks concurrently within a new [F].
   * Race results in a winner and the other, yet to finish task running in a [Fiber].
   *
   * ```kotlin:ank:playground
   * import arrow.effects.*
   * import arrow.effects.extensions.io.async.async
   * import arrow.effects.extensions.io.monad.binding
   * import arrow.effects.typeclasses.*
   * import kotlinx.coroutines.Dispatchers
   * import java.lang.RuntimeException
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   binding {
   *     val promise = Promise.uncancelable<ForIO, Int>(IO.async()).bind()
   *     val racePair = IO.racePair(Dispatchers.Default, promise.get(), IO.unit).bind()
   *     racePair.fold(
   *       { IO.raiseError<Int>(RuntimeException("Promise.get cannot win before complete")) },
   *       { (a: Fiber<ForIO, Int>, _) -> promise.complete(1).flatMap { a.join() } }
   *     ).bind()
   *   }.unsafeRunSync() == 1
   *   //sampleEnd
   * }
   * ```
   *
   * @param ctx [CoroutineContext] to execute the source [F] on.
   * @param fa task to participate in the race
   * @param fb task to participate in the race
   * @return [F] either [Left] with product of the winner's result [fa] and still running task [fb],
   *   or [Right] with product of running task [fa] and the winner's result [fb].
   *
   * @see raceN for a simpler version that cancels loser.
   */
  fun <A, B> racePair(ctx: CoroutineContext, fa: Kind<F, A>, fb: Kind<F, B>): Kind<F, RacePair<F, A, B>>


  /**
   * Race three tasks concurrently within a new [F].
   * Race results in a winner and the others, yet to finish task running in a [Fiber].
   *
   * ```kotlin:ank:playground
   * import arrow.effects.*
   * import arrow.effects.extensions.io.async.async
   * import arrow.effects.extensions.io.monad.binding
   * import arrow.effects.typeclasses.*
   * import kotlinx.coroutines.Dispatchers
   * import java.lang.RuntimeException
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   binding {
   *     val promise = Promise.uncancelable<ForIO, Int>(IO.async()).bind()
   *     val raceTriple = IO.raceTriple(Dispatchers.Default, promise.get(), IO.unit, IO.never).bind()
   *     raceTriple.fold(
   *       { IO.raiseError<Int>(RuntimeException("Promise.get cannot win before complete")) },
   *       { (a: Fiber<ForIO, Int>, _, _) -> promise.complete(1).flatMap { a.join() } },
   *       { IO.raiseError<Int>(RuntimeException("never cannot win before complete")) }
   *     ).bind()
   *   }.unsafeRunSync() == 1
   *   //sampleEnd
   * }
   * ```
   *
   * @param ctx [CoroutineContext] to execute the source [F] on.
   * @param fa task to participate in the race
   * @param fb task to participate in the race
   * @param fc task to participate in the race
   * @return [RaceTriple]
   *
   * @see [arrow.effects.typeclasses.Concurrent.raceN] for a simpler version that cancels losers.
   */
  fun <A, B, C> raceTriple(ctx: CoroutineContext, fa: Kind<F, A>, fb: Kind<F, B>, fc: Kind<F, C>): Kind<F, RaceTriple<F, A, B, C>>

  /**
   * Creates a cancelable [F] instance that executes an asynchronous process on evaluation.
   * Derived from [async] and [bracketCase].
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
   *     println("Canceled, closing NetworkApi")
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
   *   //sampleEnd
   * }
   * ```
   * @see cancelableF for a version that can safely suspend impure callback registration code.
   */
  fun <A> cancelable(k: ((Either<Throwable, A>) -> Unit) -> CancelToken<F>): Kind<F, A> =
    cancelableF { cb -> delay { k(cb) } }

  /**
   * Builder to create a cancelable [F] instance that executes an asynchronous process on evaluation.
   * Function derived from [async] and [bracketCase].
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
          is ExitCase.Canceled -> token
          else -> just(Unit)
        }
      })
    }

  /**
   * Map two tasks in parallel within a new [F] on [ctx].
   *
   * ```kotlin:ank:playground
   * import arrow.effects.extensions.io.concurrent.parMapN
   * import arrow.effects.extensions.io.monadDefer.delay
   * import kotlinx.coroutines.Dispatchers
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   val result = parMapN(Dispatchers.Default,
   *     delay { "First one is on ${Thread.currentThread().name}" },
   *     delay { "Second one is on ${Thread.currentThread().name}" }
   *   ) { a, b ->
   *     "$a\n$b"
   *   }
   *   //sampleEnd
   *   println(result.unsafeRunSync())
   * }
   * ```
   *
   * @param ctx [CoroutineContext] to execute the source [F] on.
   * @param fa value to parallel map
   * @param fb value to parallel map
   * @param f function to map/combine value [A] and [B]
   * @return [F] with the result of function [f].
   *
   * @see racePair for a version that does not await all results to be finished.
   */
  fun <A, B, C> parMapN(ctx: CoroutineContext, fa: Kind<F, A>, fb: Kind<F, B>, f: (A, B) -> C): Kind<F, C> =
    racePair(ctx, fa, fb).flatMap {
      it.fold(
        { (a, fiberB) -> fiberB.join().map { b -> f(a, b) } },
        { (fiberA, b) -> fiberA.join().map { a -> f(a, b) } }
      )
    }

  /**
   * @see parMapN
   */
  fun <A, B, C, D> parMapN(ctx: CoroutineContext, fa: Kind<F, A>, fb: Kind<F, B>, fc: Kind<F, C>, f: (A, B, C) -> D): Kind<F, D> =
    raceTriple(ctx, fa, fb, fc).flatMap {
      it.fold(
        { (a, fiberB, fiberC) -> fiberB.join().flatMap { b -> fiberC.join().map { c -> f(a, b, c) } } },
        { (fiberA, b, fiberC) -> fiberA.join().flatMap { a -> fiberC.join().map { c -> f(a, b, c) } } },
        { (fiberA, fiberB, c) -> fiberA.join().flatMap { a -> fiberB.join().map { b -> f(a, b, c) } } }
      )
    }

  /**
   * @see parMapN
   */
  fun <A, B, C, D, E> parMapN(
    ctx: CoroutineContext,
    fa: Kind<F, A>,
    fb: Kind<F, B>,
    fc: Kind<F, C>,
    fd: Kind<F, D>,
    f: (A, B, C, D) -> E): Kind<F, E> =
    parMapN(ctx,
      parMapN(ctx, fa, fb, ::Tuple2),
      parMapN(ctx, fc, fd, ::Tuple2)) { (a, b), (c, d) ->
      f(a, b, c, d)
    }

  /**
   * @see parMapN
   */
  fun <A, B, C, D, E, G> parMapN(
    ctx: CoroutineContext,
    fa: Kind<F, A>,
    fb: Kind<F, B>,
    fc: Kind<F, C>,
    fd: Kind<F, D>,
    fe: Kind<F, E>,
    f: (A, B, C, D, E) -> G): Kind<F, G> =
    parMapN(ctx,
      parMapN(ctx, fa, fb, fc, ::Tuple3),
      parMapN(ctx, fd, fe, ::Tuple2)) { (a, b, c), (d, e) ->
      f(a, b, c, d, e)
    }

  /**
   * @see parMapN
   */
  fun <A, B, C, D, E, G, H> parMapN(
    ctx: CoroutineContext,
    fa: Kind<F, A>,
    fb: Kind<F, B>,
    fc: Kind<F, C>,
    fd: Kind<F, D>,
    fe: Kind<F, E>,
    fg: Kind<F, G>,
    f: (A, B, C, D, E, G) -> H): Kind<F, H> =
    parMapN(ctx,
      parMapN(ctx, fa, fb, fc, ::Tuple3),
      parMapN(ctx, fd, fe, fg, ::Tuple3)) { (a, b, c), (d, e, g) ->
      f(a, b, c, d, e, g)
    }

  /**
   * @see parMapN
   */
  fun <A, B, C, D, E, G, H, I> parMapN(
    ctx: CoroutineContext,
    fa: Kind<F, A>,
    fb: Kind<F, B>,
    fc: Kind<F, C>,
    fd: Kind<F, D>,
    fe: Kind<F, E>,
    fg: Kind<F, G>,
    fh: Kind<F, H>,
    f: (A, B, C, D, E, G, H) -> I): Kind<F, I> =
    parMapN(ctx,
      parMapN(ctx, fa, fb, fc, ::Tuple3),
      parMapN(ctx, fd, fe, ::Tuple2),
      parMapN(ctx, fg, fh, ::Tuple2)) { (a, b, c), (d, e), (g, h) ->
      f(a, b, c, d, e, g, h)
    }

  /**
   * @see parMapN
   */
  fun <A, B, C, D, E, G, H, I, J> parMapN(
    ctx: CoroutineContext,
    fa: Kind<F, A>,
    fb: Kind<F, B>,
    fc: Kind<F, C>,
    fd: Kind<F, D>,
    fe: Kind<F, E>,
    fg: Kind<F, G>,
    fh: Kind<F, H>,
    fi: Kind<F, I>, f: (A, B, C, D, E, G, H, I) -> J): Kind<F, J> =
    parMapN(ctx,
      parMapN(ctx, fa, fb, fc, ::Tuple3),
      parMapN(ctx, fd, fe, fg, ::Tuple3),
      parMapN(ctx, fh, fi, ::Tuple2)) { (a, b, c), (d, e, g), (h, i) ->
      f(a, b, c, d, e, g, h, i)
    }

  /**
   * @see parMapN
   */
  fun <A, B, C, D, E, G, H, I, J, K> parMapN(
    ctx: CoroutineContext,
    fa: Kind<F, A>,
    fb: Kind<F, B>,
    fc: Kind<F, C>,
    fd: Kind<F, D>,
    fe: Kind<F, E>,
    fg: Kind<F, G>,
    fh: Kind<F, H>,
    fi: Kind<F, I>,
    fj: Kind<F, J>,
    f: (A, B, C, D, E, G, H, I, J) -> K): Kind<F, K> =
    parMapN(ctx,
      parMapN(ctx, fa, fb, fc, ::Tuple3),
      parMapN(ctx, fd, fe, fg, ::Tuple3),
      parMapN(ctx, fh, fi, fj, ::Tuple3)) { (a, b, c), (d, e, g), (h, i, j) ->
      f(a, b, c, d, e, g, h, i, j)
    }

  /**
   * Race two tasks concurrently within a new [F] on [ctx].
   * At the end of the race it automatically cancels the loser.
   *
   * ```kotlin:ank:playground
   * import arrow.effects.*
   * import arrow.effects.extensions.io.concurrent.raceN
   * import arrow.effects.extensions.io.monad.binding
   * import kotlinx.coroutines.Dispatchers
   * import java.lang.RuntimeException
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   binding {
   *     val eitherGetOrUnit = raceN(Dispatchers.Default, IO.never, IO.just(5)).bind()
   *     eitherGetOrUnit.fold(
   *       { IO.raiseError<Int>(RuntimeException("Never always loses race")) },
   *       IO.Companion::just
   *     ).bind()
   *   }.unsafeRunSync()
   *   //sampleEnd
   * }
   * ```
   *
   * @param ctx [CoroutineContext] to execute the source [F] on.
   * @param fa task to participate in the race
   * @param fb task to participate in the race
   * @return [F] either [Left] if [fa] won the race,
   *   or [Right] if [fb] won the race.
   *
   * @see racePair for a version that does not automatically cancel the loser.
   */
  fun <A, B> raceN(
    ctx: CoroutineContext,
    fa: Kind<F, A>,
    fb: Kind<F, B>): Kind<F, Race2<A, B>> =
    racePair(ctx, fa, fb).flatMap {
      it.fold({ (a, b) ->
        b.cancel().map { a.left() }
      }, { (a, b) ->
        a.cancel().map { b.right() }
      })
    }

  /**
   * @see raceN
   */
  fun <A, B, C> raceN(
    ctx: CoroutineContext,
    fa: Kind<F, A>,
    fb: Kind<F, B>,
    fc: Kind<F, C>): Kind<F, Race3<A, B, C>> =
    raceTriple(ctx, fa, fb, fc).flatMap {
      it.fold(
        { (a, fiberB, fiberC) -> fiberB.cancel().flatMap { fiberC.cancel().map { Left(Left(a)) } } },
        { (fiberA, b, fiberC) -> fiberA.cancel().flatMap { fiberC.cancel().map { Left(Right(b)) } } },
        { (fiberA, fiberB, c) -> fiberA.cancel().flatMap { fiberB.cancel().map { Right(c) } } }
      )
    }

  /**
   * @see raceN
   */
  fun <A, B, C, D> raceN(
    ctx: CoroutineContext,
    a: Kind<F, A>,
    b: Kind<F, B>,
    c: Kind<F, C>,
    d: Kind<F, D>): Kind<F, Race4<A, B, C, D>> =
    raceN(ctx,
      raceN(ctx, a, b),
      raceN(ctx, c, d)
    )

  /**
   * @see raceN
   */
  fun <A, B, C, D, E> raceN(
    ctx: CoroutineContext,
    a: Kind<F, A>,
    b: Kind<F, B>,
    c: Kind<F, C>,
    d: Kind<F, D>,
    e: Kind<F, E>): Kind<F, Race5<A, B, C, D, E>> =
    raceN(ctx,
      raceN(ctx, a, b, c),
      raceN(ctx, d, e)
    )

  /**
   * @see raceN
   */
  fun <A, B, C, D, E, G> raceN(
    ctx: CoroutineContext,
    a: Kind<F, A>,
    b: Kind<F, B>,
    c: Kind<F, C>,
    d: Kind<F, D>,
    e: Kind<F, E>,
    g: Kind<F, G>): Kind<F, Race6<A, B, C, D, E, G>> =
    raceN(ctx,
      raceN(ctx, a, b, c),
      raceN(ctx, d, e, g)
    )

  /**
   * @see raceN
   */
  fun <A, B, C, D, E, G, H> raceN(
    ctx: CoroutineContext,
    a: Kind<F, A>,
    b: Kind<F, B>,
    c: Kind<F, C>,
    d: Kind<F, D>,
    e: Kind<F, E>,
    g: Kind<F, G>,
    h: Kind<F, H>): Kind<F, Race7<A, B, C, D, E, G, H>> =
    raceN(ctx,
      raceN(ctx, a, b, c),
      raceN(ctx, d, e),
      raceN(ctx, g, h)
    )

  /**
   * @see raceN
   */
  fun <A, B, C, D, E, G, H, I> raceN(
    ctx: CoroutineContext,
    a: Kind<F, A>,
    b: Kind<F, B>,
    c: Kind<F, C>,
    d: Kind<F, D>,
    e: Kind<F, E>,
    g: Kind<F, G>,
    h: Kind<F, H>,
    i: Kind<F, I>): Kind<F, Race8<A, B, C, D, E, G, H, I>> =
    raceN(ctx,
      raceN(ctx, a, b),
      raceN(ctx, c, d),
      raceN(ctx, e, g),
      raceN(ctx, h, i)
    )

  /**
   * @see raceN
   */
  fun <A, B, C, D, E, G, H, I, J> raceN(
    ctx: CoroutineContext,
    a: Kind<F, A>,
    b: Kind<F, B>,
    c: Kind<F, C>,
    d: Kind<F, D>,
    e: Kind<F, E>,
    g: Kind<F, G>,
    h: Kind<F, H>,
    i: Kind<F, I>,
    j: Kind<F, J>): Kind<F, Race9<A, B, C, D, E, G, H, I, J>> =
    raceN(ctx,
      raceN(ctx, a, b, c),
      raceN(ctx, d, e),
      raceN(ctx, g, h),
      raceN(ctx, i, j)
    )

  /**
   * Overload for [Async.asyncF]
   *
   * @see [Async.asyncF]
   */
  override fun <A> asyncF(k: ProcF<F, A>): Kind<F, A> =
    asyncF { _, cb -> k(cb) }

  /**
   * Overload for [Async.async]
   *
   * @see [Async.async]
   */
  override fun <A> async(fa: Proc<A>): Kind<F, A> =
    async { _, cb -> fa(cb) }

  /**
   * Entry point for monad bindings which enables for comprehensions. The underlying impl is based on coroutines.
   * A coroutines is initiated and inside [ConcurrentCancellableContinuation] suspended yielding to [Monad.flatMap]. Once all the flatMap binds are completed
   * the underlying monad is returned from the act of executing the coroutine
   *
   * This one operates over [Concurrent] instances
   *
   * This operation is cancellable by calling invoke on the [Disposable] return.
   * If [Disposable.invoke] is called the binding result will become a lifted [BindingCancellationException].
   */
  fun <B> bindingConcurrent(c: suspend ConcurrentCancellableContinuation<F, *>.() -> B): Tuple2<Kind<F, B>, Disposable> {
    val continuation = ConcurrentCancellableContinuation<F, B>(this)
    val wrapReturn: suspend ConcurrentCancellableContinuation<F, *>.() -> Kind<F, B> = { just(c()) }
    wrapReturn.startCoroutine(continuation, continuation)
    return continuation.returnedMonad() toT continuation.disposable()
  }

  override fun <B> binding(c: suspend MonadContinuation<F, *>.() -> B): Kind<F, B> =
    bindingCancellable { c() }.a


}

/** Alias for `Either` structure to provide consistent signature for race methods. */
typealias RacePair<F, A, B> = Either<Tuple2<A, Fiber<F, B>>, Tuple2<Fiber<F, A>, B>>

/** Alias for nested `Either` structures to provide nicer signatures and overload with a convenience [fold] method. */
typealias RaceTriple<F, A, B, C> = Either<Tuple3<A, Fiber<F, B>, Fiber<F, C>>, Either<Tuple3<Fiber<F, A>, B, Fiber<F, C>>, Tuple3<Fiber<F, A>, Fiber<F, B>, C>>>

/** A convenience [fold] method to provide a nicer API to work with race results. */
@Suppress("UNUSED_PARAMETER")
inline fun <F, A, B, C, D> RaceTriple<F, A, B, C>.fold(
  ifA: (Tuple3<A, Fiber<F, B>, Fiber<F, C>>) -> D,
  ifB: (Tuple3<Fiber<F, A>, B, Fiber<F, C>>) -> D,
  ifC: (Tuple3<Fiber<F, A>, Fiber<F, B>, C>) -> D,
  dummy: Unit = Unit
): D = when (this) {
  is Either.Left -> ifA(this.a)
  is Either.Right -> when (val b = this.b) {
    is Either.Left -> ifB(b.a)
    is Either.Right -> ifC(b.b)
  }
}

/** Alias for `Either` structure to provide consistent signature for race methods. */
typealias Race2<A, B> = Either<A, B>

/** Alias for nested `Either` structures to provide nicer signatures and overload with a convenience [fold] method. */
typealias Race3<A, B, C> = Either<Either<A, B>, C>

/** Alias for nested `Either` structures to provide nicer signatures and overload with a convenience [fold] method. */
typealias Race4<A, B, C, D> = Either<Either<A, B>, Either<C, D>>

/** Alias for nested `Either` structures to provide nicer signatures and overload with a convenience [fold] method. */
typealias Race5<A, B, C, D, E> = Either<Race3<A, B, C>, Race2<D, E>>

/** Alias for nested `Either` structures to provide nicer signatures and overload with a convenience [fold] method. */
typealias Race6<A, B, C, D, E, G> = Either<Race3<A, B, C>, Race3<D, E, G>>

/** Alias for nested `Either` structures to provide nicer signatures and overload with a convenience [fold] method. */
typealias Race7<A, B, C, D, E, G, H> = Race3<Race3<A, B, C>, Race2<D, E>, Race2<G, H>>

/** Alias for nested `Either` structures to provide nicer signatures and overload with a convenience [fold] method. */
typealias Race8<A, B, C, D, E, G, H, I> = Race4<Race2<A, B>, Race2<C, D>, Race2<E, G>, Race2<H, I>>

/** Alias for nested `Either` structures to provide nicer signatures and overload with a convenience [fold] method. */
typealias Race9<A, B, C, D, E, G, H, I, J> = Race4<Race3<A, B, C>, Race2<D, E>, Race2<G, H>, Race2<I, J>>

/** A convenience [fold] method to provide a nicer API to work with race results. */
inline fun <A, B, C, D> Race3<A, B, C>.fold(
  ifA: (A) -> D,
  ifB: (B) -> D,
  ifC: (C) -> D
): D = when (this) {
  is Either.Left -> this.a.fold(ifA, ifB)
  is Either.Right -> ifC(this.b)
}

/** A convenience [fold] method to provide a nicer API to work with race results. */
inline fun <A, B, C, D, E> Race4<A, B, C, D>.fold(
  ifA: (A) -> E,
  ifB: (B) -> E,
  ifC: (C) -> E,
  ifD: (D) -> E
): E = when (this) {
  is Either.Left -> this.a.fold(ifA, ifB)
  is Either.Right -> this.b.fold(ifC, ifD)
}

/** A convenience [fold] method to provide a nicer API to work with race results. */
inline fun <A, B, C, D, E, G> Race5<A, B, C, D, E>.fold(
  ifA: (A) -> G,
  ifB: (B) -> G,
  ifC: (C) -> G,
  ifD: (D) -> G,
  ifE: (E) -> G
): G = when (this) {
  is Either.Left -> this.a.fold(ifA, ifB, ifC)
  is Either.Right -> this.b.fold(ifD, ifE)
}

/** A convenience [fold] method to provide a nicer API to work with race results. */
inline fun <A, B, C, D, E, G, H> Race6<A, B, C, D, E, G>.fold(
  ifA: (A) -> H,
  ifB: (B) -> H,
  ifC: (C) -> H,
  ifD: (D) -> H,
  ifE: (E) -> H,
  ifG: (G) -> H
): H = when (this) {
  is Either.Left -> this.a.fold(ifA, ifB, ifC)
  is Either.Right -> this.b.fold(ifD, ifE, ifG)
}

/** A convenience [fold] method to provide a nicer API to work with race results. */
inline fun <A, B, C, D, E, G, H, I> Race7<A, B, C, D, E, G, H>.fold(
  ifA: (A) -> I,
  ifB: (B) -> I,
  ifC: (C) -> I,
  ifD: (D) -> I,
  ifE: (E) -> I,
  ifG: (G) -> I,
  ifH: (H) -> I
): I = when (this) {
  is Either.Left -> this.a.fold(ifA, ifB, ifC, ifD, ifE)
  is Either.Right -> this.b.fold(ifG, ifH)
}

/** A convenience [fold] method to provide a nicer API to work with race results. */
inline fun <A, B, C, D, E, G, H, I, J> Race8<A, B, C, D, E, G, H, I>.fold(
  ifA: (A) -> J,
  ifB: (B) -> J,
  ifC: (C) -> J,
  ifD: (D) -> J,
  ifE: (E) -> J,
  ifG: (G) -> J,
  ifH: (H) -> J,
  ifI: (I) -> J
): J = when (this) {
  is Either.Left -> this.a.fold(ifA, ifB, ifC, ifD)
  is Either.Right -> this.b.fold(ifE, ifG, ifH, ifI)
}

/** A convenience [fold] method to provide a nicer API to work with race results. */
inline fun <A, B, C, D, E, G, H, I, J, K> Race9<A, B, C, D, E, G, H, I, J>.fold(
  ifA: (A) -> K,
  ifB: (B) -> K,
  ifC: (C) -> K,
  ifD: (D) -> K,
  ifE: (E) -> K,
  ifG: (G) -> K,
  ifH: (H) -> K,
  ifI: (I) -> K,
  ifJ: (J) -> K
): K = when (this) {
  is Either.Left -> this.a.fold(ifA, ifB, ifC, ifD, ifE)
  is Either.Right -> this.b.fold(ifG, ifH, ifI, ifJ)
}
