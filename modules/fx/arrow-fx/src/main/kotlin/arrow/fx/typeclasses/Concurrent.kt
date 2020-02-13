package arrow.fx.typeclasses

import arrow.Kind
import arrow.core.Either
import arrow.core.Left
import arrow.core.ListK
import arrow.core.Right
import arrow.core.Tuple2
import arrow.core.Tuple3
import arrow.core.internal.AtomicRefW
import arrow.core.extensions.listk.traverse.traverse
import arrow.core.fix
import arrow.core.identity
import arrow.core.k
import arrow.fx.internal.parMap2
import arrow.fx.internal.parMap3
import arrow.typeclasses.Applicative
import arrow.fx.MVar
import arrow.fx.Promise
import arrow.fx.Race2
import arrow.fx.Race3
import arrow.fx.Race4
import arrow.fx.Race5
import arrow.fx.Race6
import arrow.fx.Race7
import arrow.fx.Race8
import arrow.fx.Race9
import arrow.fx.RacePair
import arrow.fx.RaceTriple
import arrow.fx.Semaphore
import arrow.fx.Timer
import arrow.fx.internal.TimeoutException
import arrow.typeclasses.Traverse
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.startCoroutine

typealias CancelToken<F> = Kind<F, Unit>

/**
 * ank_macro_hierarchy(arrow.fx.typeclasses.Concurrent)
 *
 * Type class for async data types that are cancelable and can be started concurrently.
 */
interface Concurrent<F> : Async<F> {

  fun dispatchers(): Dispatchers<F>

  fun timer(): Timer<F> = Timer(this)

  fun parApplicative(): Applicative<F> = ParApplicative(null)

  fun parApplicative(ctx: CoroutineContext): Applicative<F> = ParApplicative(ctx)

  /**
   * Entry point for monad bindings which enables for comprehensions. The underlying impl is based on coroutines.
   * A coroutines is initiated and inside [ConcurrentContinuation] suspended yielding to [Monad.flatMap]. Once all the flatMap binds are completed
   * the underlying monad is returned from the act of executing the coroutine
   *
   * This one operates over [Concurrent] instances
   *
   * This operation is cancellable by calling invoke on the [Disposable] return.
   * If [Disposable.invoke] is called the binding result will become a lifted [BindingCancellationException].
   */
  override val fx: ConcurrentFx<F>
    get() = object : ConcurrentFx<F> {
      override val M: Concurrent<F> = this@Concurrent
    }

  /**
   * Create a new [F] that upon execution starts the receiver [F] within a [Fiber] on [this@fork].
   *
   * ```kotlin:ank:playground
   * import arrow.Kind
   * import arrow.fx.*
   * import arrow.fx.extensions.io.concurrent.concurrent
   * import arrow.fx.typeclasses.Concurrent
   * import kotlinx.coroutines.Dispatchers
   *
   * fun main(args: Array<String>) {
   *   fun <F> Concurrent<F>.example(): Kind<F, Unit> =
   *   //sampleStart
   *     fx.concurrent {
   *       val (join, cancel) = !effect {
   *         println("Hello from a fiber on ${Thread.currentThread().name}")
   *       }.fork(Dispatchers.Default)
   *     }
   *
   *   //sampleEnd
   *   IO.concurrent().example().fix().unsafeRunSync()
   * }
   * ```
   *
   * @receiver [F] to [fork] within a new context [F].
   * @param ctx to execute the source [F] on.
   * @return [F] with suspended execution of source [F] on context [ctx].
   * @see fork for a version that defaults to the default dispatcher.
   */
  fun <A> Kind<F, A>.fork(ctx: CoroutineContext): Kind<F, Fiber<F, A>>

  /** @see fork **/
  fun <A> Kind<F, A>.fork(): Kind<F, Fiber<F, A>> = fork(dispatchers().default())

  /**
   * Race two tasks concurrently within a new [F].
   * Race results in a winner and the other, yet to finish task running in a [Fiber].
   *
   * ```kotlin:ank:playground
   * import arrow.Kind
   * import arrow.fx.*
   * import arrow.fx.extensions.io.concurrent.concurrent
   * import arrow.fx.typeclasses.*
   * import kotlinx.coroutines.Dispatchers
   *
   * fun main(args: Array<String>) {
   *   fun <F> Concurrent<F>.example(): Kind<F, String> =
   *     //sampleStart
   *     fx.concurrent {
   *       val racePair = !Dispatchers.Default.racePair(never<Int>(), just("Hello World!"))
   *       racePair.fold(
   *         { _, _ -> "never cannot win race" },
   *         { _, winner -> winner }
   *       )
   *   }
   *   //sampleEnd
   *
   *   val r = IO.concurrent().example().fix().unsafeRunSync()
   *   println("Race winner result is: $r")
   * }
   * ```
   *
   * @param this@racePair [CoroutineContext] to execute the source [F] on.
   * @param fa task to participate in the race
   * @param fb task to participate in the race
   * @return [F] either [Left] with product of the winner's result [fa] and still running task [fb],
   *   or [Right] with product of running task [fa] and the winner's result [fb].
   *
   * @see raceN for a simpler version that cancels loser.
   */
  fun <A, B> CoroutineContext.racePair(fa: Kind<F, A>, fb: Kind<F, B>): Kind<F, RacePair<F, A, B>>

  /**
   * Race three tasks concurrently within a new [F].
   * Race results in a winner and the others, yet to finish task running in a [Fiber].
   *
   * ```kotlin:ank:playground
   * import arrow.Kind
   * import arrow.fx.*
   * import arrow.fx.extensions.io.concurrent.concurrent
   * import arrow.fx.typeclasses.*
   * import kotlinx.coroutines.Dispatchers
   *
   * fun main(args: Array<String>) {
   *   fun <F> Concurrent<F>.example(): Kind<F, String> =
   *   //sampleStart
   *     fx.concurrent {
   *       val raceResult = !Dispatchers.Default.raceTriple(never<Int>(), just("Hello World!"), never<Double>())
   *       raceResult.fold(
   *         { _, _, _ -> "never cannot win before complete" },
   *         { _, winner, _ -> winner },
   *         { _, _, _ -> "never cannot win before complete" }
   *       )
   *     }
   *   //sampleEnd
   *
   *   val r = IO.concurrent().example().fix().unsafeRunSync()
   *   println("Race winner result is: $r")
   * }
   * ```
   *
   * @param this@raceTriple [CoroutineContext] to execute the source [F] on.
   * @param fa task to participate in the race
   * @param fb task to participate in the race
   * @param fc task to participate in the race
   * @return [RaceTriple]
   *
   * @see [arrow.fx.typeclasses.Concurrent.raceN] for a simpler version that cancels losers.
   */
  fun <A, B, C> CoroutineContext.raceTriple(fa: Kind<F, A>, fb: Kind<F, B>, fc: Kind<F, C>): Kind<F, RaceTriple<F, A, B, C>>

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
   *       _later_({ service.cancel() })
   *     }
   *   }
   *
   *   //sampleEnd
   * }
   * ```
   * @see cancelableF for a version that can safely suspend impure callback registration code.
   */
  fun <A> cancelable(k: ((Either<Throwable, A>) -> Unit) -> CancelToken<F>): Kind<F, A> =
    cancelableF { cb ->
      val token = k(cb)
      later { token }
    }

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
   *     effect {
   *       val deferred = kotlinx.coroutines.GlobalScope.async {
   *         kotlinx.coroutines.delay(1000)
   *         cb(Right("Hello from ${Thread.currentThread().name}"))
   *       }
   *
   *       effect { deferred.cancel().let { Unit } }
   *     }
   *   }
   *
   *   println(result) //Run with `fix().unsafeRunSync()`
   *
   *   val result2 = _extensionFactory_.cancelableF<Unit> { cb ->
   *     effect {
   *       println("Doing something that can be cancelled.")
   *       effect  { println("Cancelling the task") }
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
      val state = AtomicRefW<((Either<Throwable, Unit>) -> Unit)?>(null)
      val cb1 = { r: Either<Throwable, A> ->
        try {
          cb(r)
        } finally {
          if (!state.compareAndSet(null, mapUnit)) {
            val cb2 = state.value!!
            state.lazySet(null)
            cb2(rightUnit)
          }
        }
      }

      k(cb1).bracketCase(use = {
        async<Unit> { cb ->
          if (!state.compareAndSet(null, cb)) {
            cb(rightUnit)
          }
        }
      }, release = { token, exitCase ->
        when (exitCase) {
          is ExitCase.Canceled -> token
          else -> just(Unit)
        }
      })
    }

  /**
   * Given a function which returns an [F] effect, run this effect in parallel for all the values in [G].
   *
   * ```kotlin:ank:playground:extension
   * import arrow.Kind
   * import arrow.core.k
   * import arrow.fx.IO
   * import arrow.fx.extensions.io.concurrent.concurrent
   * import arrow.fx.fix
   * import arrow.fx.typeclasses.Concurrent
   * import arrow.fx.typeclasses.milliseconds
   *
   * fun main(args: Array<String>) {
   *  fun <F> Concurrent<F>.processListInParallel(): Kind<F, List<Unit>> =
   *  //sampleStart
   *    listOf("1", "2", "3").k().parTraverse { s ->
   *      effect { s.toInt() }
   *        .flatMap { i ->
   *          val duration = (i * 200).milliseconds
   *          sleep(duration).followedBy(effect { println("Waited for $duration") })
   *        }
   *    }
   *
   *  //sampleEnd
   *    IO.concurrent().processListInParallel()
   *      .fix()
   *      .unsafeRunSync()
   * }
   * ```
   *
   * Note: Be careful when using this on very large collections or infinite sequences as this needs to fold the entire collection first before it can start executing anything. This may cause excessive memory usage, or even infinite looping (in case of infinite sequences).
   * Using [parTraverse] like this may imply that what you'd actually want is streaming, which will come to arrow in the future.
   */
  fun <G, A, B> Kind<G, A>.parTraverse(ctx: CoroutineContext, TG: Traverse<G>, f: (A) -> Kind<F, B>): Kind<F, Kind<G, B>> =
    TG.run { traverse(parApplicative(ctx), f) }

  /**
   * @see parTraverse
   */
  fun <G, A, B> Kind<G, A>.parTraverse(TG: Traverse<G>, f: (A) -> Kind<F, B>): Kind<F, Kind<G, B>> =
    TG.run { traverse(parApplicative(), f) }

  /**
   * @see parTraverse
   */
  fun <A, B> Iterable<A>.parTraverse(ctx: CoroutineContext, f: (A) -> Kind<F, B>): Kind<F, List<B>> =
    toList().k().parTraverse(ctx, ListK.traverse(), f).map { it.fix() }

  /**
   * @see parTraverse
   */
  fun <A, B> Iterable<A>.parTraverse(f: (A) -> Kind<F, B>): Kind<F, List<B>> =
    toList().k().parTraverse(ListK.traverse(), f).map { it.fix() }

  /**
   * Runs all the [F] effects of the [G] structure to invert the structure from Kind<F, Kind<G, A>> to Kind<G, Kind<F, A>>.
   *
   * ```
   * import arrow.Kind
   * import arrow.fx.IO
   * import arrow.fx.extensions.io.concurrent.concurrent
   * import arrow.fx.fix
   * import arrow.fx.typeclasses.Async
   * import arrow.fx.typeclasses.Concurrent
   *
   * data class User(val id: Int)
   *
   * fun main() {
   *   fun <F> Async<F>.getUserById(id: Int): Kind<F, User> =
   *     effect { User(id) }
   *
   *   fun <F> Concurrent<F>.processInParallel(): Kind<F, List<User>> =
   *   //sampleStart
   *     listOf(1, 2, 3)
   *       .map { id -> getUserById(id) }
   *       .parSequence()
   *  //sampleEnd
   *   IO.concurrent().processInParallel()
   *     .fix().unsafeRunSync()
   * }
   * ```
   *
   * Note: This uses [parTraverse] so the same caveats apply. Do not use this on huge collections or infinite sequences.
   * @see parTraverse
   */
  fun <G, A> Kind<G, Kind<F, A>>.parSequence(TG: Traverse<G>, ctx: CoroutineContext): Kind<F, Kind<G, A>> =
    parTraverse(ctx, TG, ::identity)

  /**
   * @see parSequence
   */
  fun <G, A> Kind<G, Kind<F, A>>.parSequence(TG: Traverse<G>): Kind<F, Kind<G, A>> =
    parTraverse(TG, ::identity)

  /**
   * @see parSequence
   */
  fun <A> Iterable<Kind<F, A>>.parSequence(ctx: CoroutineContext): Kind<F, List<A>> =
    toList().k().parTraverse(ctx, ListK.traverse(), ::identity).map { it.fix() }

  /**
   * @see parSequence
   */
  fun <A> Iterable<Kind<F, A>>.parSequence(): Kind<F, List<A>> =
    toList().k().parTraverse(ListK.traverse(), ::identity).map { it.fix() }

  /**
   * Map two tasks in parallel within a new [F] on [this@parMapN].
   *
   * ```kotlin:ank:playground
   * import arrow.Kind
   * import arrow.fx.IO
   * import kotlinx.coroutines.Dispatchers
   * import arrow.fx.typeclasses.Concurrent
   * import arrow.fx.extensions.io.concurrent.concurrent
   * import arrow.fx.fix
   *
   * fun main(args: Array<String>) {
   *   fun <F> Concurrent<F>.example(): Kind<F, String> {
   *   //sampleStart
   *     val result = Dispatchers.Default.parMapN(
   *       effect { "First one is on ${Thread.currentThread().name}" },
   *       effect { "Second one is on ${Thread.currentThread().name}" }
   *     ) { a, b ->
   *       "$a\n$b"
   *     }
   *   //sampleEnd
   *   return result
   *   }
   *
   *   IO.concurrent().example().fix().unsafeRunSync().let(::println)
   * }
   * ```
   *
   * @param this@parMapN [CoroutineContext] to execute the source [F] on.
   * @param fa value to parallel map
   * @param fb value to parallel map
   * @param f function to map/combine value [A] and [B]
   * @return [F] with the result of function [f].
   *
   * @see racePair for a version that does not await all results to be finished.
   */
  fun <A, B, C> CoroutineContext.parMapN(
    fa: Kind<F, A>,
    fb: Kind<F, B>,
    f: (A, B) -> C
  ): Kind<F, C> =
    parMap2(this, fa, fb, f)

  /**
   * @see parMapN
   */
  fun <A, B, C, D> CoroutineContext.parMapN(fa: Kind<F, A>, fb: Kind<F, B>, fc: Kind<F, C>, f: (A, B, C) -> D): Kind<F, D> =
    parMap3(this, fa, fb, fc, f)

  /**
   * @see parMapN
   */
  fun <A, B, C, D, E> CoroutineContext.parMapN(
    fa: Kind<F, A>,
    fb: Kind<F, B>,
    fc: Kind<F, C>,
    fd: Kind<F, D>,
    f: (A, B, C, D) -> E
  ): Kind<F, E> =
    parMapN(
      parMapN(fa, fb, ::Tuple2),
      parMapN(fc, fd, ::Tuple2)
    ) { (a, b), (c, d) ->
      f(a, b, c, d)
    }

  /**
   * @see parMapN
   */
  fun <A, B, C, D, E, G> CoroutineContext.parMapN(
    fa: Kind<F, A>,
    fb: Kind<F, B>,
    fc: Kind<F, C>,
    fd: Kind<F, D>,
    fe: Kind<F, E>,
    f: (A, B, C, D, E) -> G
  ): Kind<F, G> =
    parMapN(parMapN(fa, fb, fc, ::Tuple3),
      parMapN(fd, fe, ::Tuple2)
    ) { (a, b, c), (d, e) ->
      f(a, b, c, d, e)
    }

  /**
   * @see parMapN
   */
  fun <A, B, C, D, E, G, H> CoroutineContext.parMapN(
    fa: Kind<F, A>,
    fb: Kind<F, B>,
    fc: Kind<F, C>,
    fd: Kind<F, D>,
    fe: Kind<F, E>,
    fg: Kind<F, G>,
    f: (A, B, C, D, E, G) -> H
  ): Kind<F, H> =
    parMapN(parMapN(fa, fb, fc, ::Tuple3),
      parMapN(fd, fe, fg, ::Tuple3)
    ) { (a, b, c), (d, e, g) ->
      f(a, b, c, d, e, g)
    }

  /**
   * @see parMapN
   */
  fun <A, B, C, D, E, G, H, I> CoroutineContext.parMapN(
    fa: Kind<F, A>,
    fb: Kind<F, B>,
    fc: Kind<F, C>,
    fd: Kind<F, D>,
    fe: Kind<F, E>,
    fg: Kind<F, G>,
    fh: Kind<F, H>,
    f: (A, B, C, D, E, G, H) -> I
  ): Kind<F, I> =
    parMapN(parMapN(fa, fb, fc, ::Tuple3),
      parMapN(fd, fe, ::Tuple2),
      parMapN(fg, fh, ::Tuple2)) { (a, b, c), (d, e), (g, h) ->
      f(a, b, c, d, e, g, h)
    }

  /**
   * @see parMapN
   */
  fun <A, B, C, D, E, G, H, I, J> CoroutineContext.parMapN(
    fa: Kind<F, A>,
    fb: Kind<F, B>,
    fc: Kind<F, C>,
    fd: Kind<F, D>,
    fe: Kind<F, E>,
    fg: Kind<F, G>,
    fh: Kind<F, H>,
    fi: Kind<F, I>,
    f: (A, B, C, D, E, G, H, I) -> J
  ): Kind<F, J> =
    parMapN(parMapN(fa, fb, fc, ::Tuple3),
      parMapN(fd, fe, fg, ::Tuple3),
      parMapN(fh, fi, ::Tuple2)) { (a, b, c), (d, e, g), (h, i) ->
      f(a, b, c, d, e, g, h, i)
    }

  /**
   * @see parMapN
   */
  fun <A, B, C, D, E, G, H, I, J, K> CoroutineContext.parMapN(
    fa: Kind<F, A>,
    fb: Kind<F, B>,
    fc: Kind<F, C>,
    fd: Kind<F, D>,
    fe: Kind<F, E>,
    fg: Kind<F, G>,
    fh: Kind<F, H>,
    fi: Kind<F, I>,
    fj: Kind<F, J>,
    f: (A, B, C, D, E, G, H, I, J) -> K
  ): Kind<F, K> =
    parMapN(parMapN(fa, fb, fc, ::Tuple3),
      parMapN(fd, fe, fg, ::Tuple3),
      parMapN(fh, fi, fj, ::Tuple3)) { (a, b, c), (d, e, g), (h, i, j) ->
      f(a, b, c, d, e, g, h, i, j)
    }

  /**
   * Race two tasks concurrently within a new [F] on [this@raceN].
   * At the end of the race it automatically cancels the loser.
   *
   * ```kotlin:ank:playground
   * import arrow.Kind
   * import arrow.fx.*
   * import arrow.fx.typeclasses.Concurrent
   * import kotlinx.coroutines.Dispatchers
   * import arrow.fx.extensions.io.concurrent.concurrent
   *
   * fun main(args: Array<String>) {
   *   fun <F> Concurrent<F>.example(): Kind<F, String> {
   *     val never: Kind<F, Int> = cancelable { effect { println("Never got canelled for losing.") } }
   *
   *     //sampleStart
   *     val result = fx.concurrent {
   *       val eitherGetOrUnit = !Dispatchers.Default.raceN(never, just(5))
   *       eitherGetOrUnit.fold(
   *         { "Never always loses race" },
   *         { i -> "Race was won with $i" }
   *       )
   *     }
   *   //sampleEnd
   *   return result
   *   }
   *
   *   IO.concurrent().example().fix().unsafeRunSync().let(::println)
   * }
   * ```
   *
   * @param this@raceN [CoroutineContext] to execute the source [F] on.
   * @param fa task to participate in the race
   * @param fb task to participate in the race
   * @return [F] either [Left] if [fa] won the race,
   *   or [Right] if [fb] won the race.
   *
   * @see racePair for a version that does not automatically cancel the loser.
   */
  fun <A, B> CoroutineContext.raceN(
    fa: Kind<F, A>,
    fb: Kind<F, B>
  ): Kind<F, Race2<A, B>> =
    racePair(fa, fb).flatMap {
      it.fold({ a, (_, cancelB) ->
        cancelB.map { Left(a) }
      }, { (_, cancelA), b ->
        cancelA.map { Right(b) }
      })
    }

  /**
   * @see raceN
   */
  fun <A, B, C> CoroutineContext.raceN(
    fa: Kind<F, A>,
    fb: Kind<F, B>,
    fc: Kind<F, C>
  ): Kind<F, Race3<out A, out B, out C>> =
    raceTriple(fa, fb, fc).flatMap {
      it.fold(
        { a, fiberB, fiberC -> fiberB.cancel().flatMap { fiberC.cancel().map { Race3.First(a) } } },
        { fiberA, b, fiberC -> fiberA.cancel().flatMap { fiberC.cancel().map { Race3.Second(b) } } },
        { fiberA, fiberB, c -> fiberA.cancel().flatMap { fiberB.cancel().map { Race3.Third(c) } } }
      )
    }

  /**
   * @see raceN
   */
  fun <A, B, C, D> CoroutineContext.raceN(
    a: Kind<F, A>,
    b: Kind<F, B>,
    c: Kind<F, C>,
    d: Kind<F, D>
  ): Kind<F, Race4<out A, out B, out C, out D>> =
    raceN(
      raceN(a, b),
      raceN(c, d)
    ).map { res ->
      res.fold(
        { it.fold({ a -> Race4.First(a) }, { b -> Race4.Second(b) }) },
        { it.fold({ c -> Race4.Third(c) }, { d -> Race4.Fourth(d) }) }
      )
    }

  /**
   * @see raceN
   */
  fun <A, B, C, D, E> CoroutineContext.raceN(
    a: Kind<F, A>,
    b: Kind<F, B>,
    c: Kind<F, C>,
    d: Kind<F, D>,
    e: Kind<F, E>
  ): Kind<F, Race5<out A, out B, out C, out D, out E>> =
    raceN(
      raceN(a, b, c),
      raceN(d, e)
    ).map { res ->
      res.fold({
        it.fold({ a -> Race5.First(a) }, { b -> Race5.Second(b) }, { c -> Race5.Third(c) })
      }, {
        it.fold({ d -> Race5.Fourth(d) }, { e -> Race5.Fifth(e) })
      })
    }

  /**
   * @see raceN
   */
  fun <A, B, C, D, E, G> CoroutineContext.raceN(
    a: Kind<F, A>,
    b: Kind<F, B>,
    c: Kind<F, C>,
    d: Kind<F, D>,
    e: Kind<F, E>,
    g: Kind<F, G>
  ): Kind<F, Race6<out A, out B, out C, out D, out E, out G>> =
    raceN(
      raceN(a, b, c),
      raceN(d, e, g)
    ).map { res ->
      res.fold({
        it.fold({ a -> Race6.First(a) }, { b -> Race6.Second(b) }, { c -> Race6.Third(c) })
      }, {
        it.fold({ d -> Race6.Fourth(d) }, { e -> Race6.Fifth(e) }, { g -> Race6.Sixth(g) })
      })
    }

  /**
   * @see raceN
   */
  fun <A, B, C, D, E, G, H> CoroutineContext.raceN(
    a: Kind<F, A>,
    b: Kind<F, B>,
    c: Kind<F, C>,
    d: Kind<F, D>,
    e: Kind<F, E>,
    g: Kind<F, G>,
    h: Kind<F, H>
  ): Kind<F, Race7<out A, out B, out C, out D, out E, out G, out H>> =
    raceN(
      raceN(a, b, c),
      raceN(d, e),
      raceN(g, h)
    ).map { res ->
      res.fold({
        it.fold({ a -> Race7.First(a) }, { b -> Race7.Second(b) }, { c -> Race7.Third(c) })
      }, {
        it.fold({ d -> Race7.Fourth(d) }, { e -> Race7.Fifth(e) })
      }, {
        it.fold({ g -> Race7.Sixth(g) }, { h -> Race7.Seventh(h) })
      })
    }

  /**
   * @see raceN
   */
  fun <A, B, C, D, E, G, H, I> CoroutineContext.raceN(
    a: Kind<F, A>,
    b: Kind<F, B>,
    c: Kind<F, C>,
    d: Kind<F, D>,
    e: Kind<F, E>,
    g: Kind<F, G>,
    h: Kind<F, H>,
    i: Kind<F, I>
  ): Kind<F, Race8<out A, out B, out C, out D, out E, out G, out H, out I>> =
    raceN(
      raceN(a, b),
      raceN(c, d),
      raceN(e, g),
      raceN(h, i)
    ).map { res ->
      res.fold({
        it.fold({ a -> Race8.First(a) }, { b -> Race8.Second(b) })
      }, {
        it.fold({ c -> Race8.Third(c) }, { d -> Race8.Fourth(d) })
      }, {
        it.fold({ e -> Race8.Fifth(e) }, { g -> Race8.Sixth(g) })
      }, {
        it.fold({ h -> Race8.Seventh(h) }, { i -> Race8.Eighth(i) })
      })
    }

  /**
   * @see raceN
   */
  fun <A, B, C, D, E, G, H, I, J> CoroutineContext.raceN(
    a: Kind<F, A>,
    b: Kind<F, B>,
    c: Kind<F, C>,
    d: Kind<F, D>,
    e: Kind<F, E>,
    g: Kind<F, G>,
    h: Kind<F, H>,
    i: Kind<F, I>,
    j: Kind<F, J>
  ): Kind<F, Race9<out A, out B, out C, out D, out E, out G, out H, out I, out J>> =
    raceN(
      raceN(a, b, c),
      raceN(d, e),
      raceN(g, h),
      raceN(i, j)
    ).map { res ->
      res.fold({
        it.fold({ a -> Race9.First(a) }, { b -> Race9.Second(b) }, { c -> Race9.Third(c) })
      }, {
        it.fold({ d -> Race9.Fourth(d) }, { e -> Race9.Fifth(e) })
      }, {
        it.fold({ g -> Race9.Sixth(g) }, { h -> Race9.Seventh(h) })
      }, {
        it.fold({ i -> Race9.Eighth(i) }, { j -> Race9.Ninth(j) })
      })
    }

  /**
   * Create a pure [Promise] that is empty and can be filled exactly once.
   *
   * ```kotlin:ank:playground
   * import arrow.Kind
   * import arrow.fx.*
   * import arrow.fx.extensions.io.concurrent.concurrent
   * import arrow.fx.typeclasses.Concurrent
   * import arrow.fx.typeclasses.seconds
   *
   * fun main(args: Array<String>) {
   *   fun <F> Concurrent<F>.promiseExample(): Kind<F, Unit> =
   *     //sampleStart
   *     fx.concurrent {
   *       val promise = !Promise<String>()
   *       val (join, cancel) = !sleep(1.seconds)
   *         .followedBy(promise.complete("Hello World!"))
   *         .fork()
   *       val message = !join
   *       message
   *     }
   *
   *   //sampleEnd
   *   IO.concurrent().promiseExample()
   *     .fix().unsafeRunSync()
   * }
   * ```
   *
   * @see Promise for more details on usage
   */
  fun <A> Promise(): Kind<F, Promise<F, A>> =
    Promise(this)

  /**
   * Create a pure [Semaphore] that can be used to control access in a concurrent system.
   *
   * ```kotlin:ank:playground
   * import arrow.Kind
   * import arrow.fx.*
   * import arrow.fx.extensions.io.concurrent.concurrent
   * import arrow.fx.typeclasses.Concurrent
   *
   * fun main(args: Array<String>) {
   *   fun <F> Concurrent<F>.promiseExample(): Kind<F, Unit> =
   *     //sampleStart
   *     fx.concurrent {
   *       val semaphore = !Semaphore(4)
   *       !semaphore.acquireN(4)
   *       val left = !semaphore.available()
   *       !effect { println("There are $left permits left") }
   *       !semaphore.withPermit(effect { println("Fork & wait until permits become available") }).fork()
   *       !effect { println("Making permit available") }
   *         .followedBy(semaphore.releaseN(4))
   *     }
   *
   *   //sampleEnd
   *   IO.concurrent().promiseExample()
   *     .fix().unsafeRunSync()
   * }
   * ```
   *
   * @see Semaphore for more details on usage
   */
  fun Semaphore(n: Long): Kind<F, Semaphore<F>> =
    Semaphore(n, this)

  /**
   * Create a [MVar] or mutable variable structure to be used for thread-safe sharing, initialized to a value [a].
   *
   * ```kotlin:ank:playground
   * import arrow.Kind
   * import arrow.core.Option
   * import arrow.core.Tuple3
   * import arrow.fx.*
   * import arrow.fx.extensions.io.concurrent.concurrent
   * import arrow.fx.typeclasses.Concurrent
   *
   * fun main(args: Array<String>) {
   *   fun <F> Concurrent<F>.mvarExample(): Kind<F, Tuple3<Int, Option<Int>, Int>> =
   *   //sampleStart
   *     fx.concurrent {
   *       val mvar = !MVar(4)
   *       val four = !mvar.take()
   *       val empty = !mvar.tryTake()
   *       val (join, _) = !mvar.take().fork()
   *       !mvar.put(10)
   *       Tuple3(four, empty, !join)
   *     }
   *
   *   //sampleEnd
   *   IO.concurrent().mvarExample()
   *     .fix().unsafeRunSync().let(::println)
   * }
   * ```
   *
   * @see [MVar] for more usage details.
   */
  fun <A> MVar(a: A): Kind<F, MVar<F, A>> =
    MVar(a, this)

  /**
   * Create an empty [MVar] or mutable variable structure to be used for thread-safe sharing.
   *
   * @see MVar
   * @see [MVar] for more usage details.
   */
  fun <A> MVar(): Kind<F, MVar<F, A>> =
    MVar.empty(this)

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
  fun <B> bindingConcurrent(c: suspend ConcurrentContinuation<F, *>.() -> B): Kind<F, B> {
    val continuation = ConcurrentContinuation<F, B>(this)
    val wrapReturn: suspend ConcurrentContinuation<F, *>.() -> Kind<F, B> = { just(c()) }
    wrapReturn.startCoroutine(continuation, continuation)
    return continuation.returnedMonad()
  }

  /**
   *  Sleeps for a given [duration] without blocking a thread.
   *
   * @see Timer
   **/
  fun sleep(duration: Duration): Kind<F, Unit> = timer().sleep(duration)

  /**
   * Returns the result of [this] within the specified [duration] or the [default] value.
   *
   * ```kotlin:ank:playground
   * import arrow.*
   * import arrow.fx.*
   * import arrow.fx.typeclasses.*
   * import arrow.fx.extensions.io.concurrent.concurrent
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   fun <F> Concurrent<F>.timedOutWorld(): Kind<F, Unit> {
   *     val world = sleep(3.seconds).flatMap { effect { println("Hello World!") } }
   *     val fallbackWorld = effect { println("Hello from the backup") }
   *     return world.waitFor(1.seconds, fallbackWorld)
   *   }
   *   //sampleEnd
   *   IO.concurrent().timedOutWorld()
   *     .fix().unsafeRunSync()
   * }
   * ```
   **/
  fun <A> Kind<F, A>.waitFor(duration: Duration, default: Kind<F, A>): Kind<F, A> =
    EmptyCoroutineContext.raceN(sleep(duration), this).flatMap {
      it.fold(
        { default },
        { a -> just(a) }
      )
    }

  /**
   * Returns the result of [this] within the specified [duration] or the raises a [TimeoutException] exception.
   *
   * ```kotlin:ank:playground
   * import arrow.*
   * import arrow.fx.*
   * import arrow.fx.typeclasses.*
   * import arrow.fx.extensions.io.concurrent.concurrent
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   fun <F> Concurrent<F>.timedOutWorld(): Kind<F, Unit> {
   *     val world = sleep(1.seconds).flatMap { effect { println("Hello World!") } }
   *     return world.waitFor(3.seconds)
   *   }
   *   //sampleEnd
   *   IO.concurrent().timedOutWorld()
   *     .fix().unsafeRunSync()
   * }
   * ```
   **/
  fun <A> Kind<F, A>.waitFor(duration: Duration): Kind<F, A> =
    EmptyCoroutineContext.raceN(sleep(duration), this).flatMap {
      it.fold(
        { raiseError<A>(TimeoutException(duration.toString())) },
        { a -> just(a) }
      )
    }
}

interface ConcurrentFx<F> : AsyncFx<F> {
  override val M: Concurrent<F>
  // Deferring in order to lazily launch the coroutine so it doesn't eagerly run on declaring context
  fun <A> concurrent(c: suspend ConcurrentSyntax<F>.() -> A): Kind<F, A> = M.defer {
    val continuation = ConcurrentContinuation<F, A>(M)
    val wrapReturn: suspend ConcurrentSyntax<F>.() -> Kind<F, A> = { just(c()) }
    wrapReturn.startCoroutine(continuation, continuation)
    continuation.returnedMonad()
  }
}
