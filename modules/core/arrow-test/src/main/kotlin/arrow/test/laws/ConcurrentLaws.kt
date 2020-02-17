package arrow.test.laws

import arrow.Kind
import arrow.core.Left
import arrow.core.ListK
import arrow.core.Right
import arrow.core.Tuple2
import arrow.core.extensions.eq
import arrow.core.extensions.listk.traverse.traverse
import arrow.core.extensions.tuple2.eq.eq
import arrow.core.identity
import arrow.core.k
import arrow.core.toT
import arrow.fx.MVar
import arrow.fx.Promise
import arrow.fx.Semaphore
import arrow.fx.Timer
import arrow.fx.typeclasses.CancelToken
import arrow.fx.typeclasses.Concurrent
import arrow.fx.typeclasses.ExitCase
import arrow.test.generators.GenK
import arrow.test.generators.applicativeError
import arrow.test.generators.either
import arrow.test.generators.throwable
import arrow.typeclasses.Apply
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import arrow.typeclasses.Functor
import arrow.typeclasses.Selective
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import io.kotlintest.shouldBe
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit.MILLISECONDS
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.CoroutineContext

object ConcurrentLaws {

  private fun <F> concurrentLaws(
    CF: Concurrent<F>,
    EQK: EqK<F>,
    ctx: CoroutineContext
  ): List<Law> {

    val EQ = EQK.liftEq(Int.eq())
    val EQ_UNIT = EQK.liftEq(Eq.any())

    return listOf(
      Law("Concurrent Laws: cancel on bracket releases") { CF.cancelOnBracketReleases(EQ, ctx) },
      Law("Concurrent Laws: acquire is not cancelable") { CF.acquireBracketIsNotCancelable(EQ, ctx) },
      Law("Concurrent Laws: release is not cancelable") { CF.releaseBracketIsNotCancelable(EQ, ctx) },
      Law("Concurrent Laws: cancel on guarantee runs finalizer") { CF.guaranteeFinalizerOnCancel(EQ, ctx) },
      Law("Concurrent Laws: release is not cancelable") { CF.guaranteeFinalizerIsNotCancelable(EQ, ctx) },
      Law("Concurrent Laws: cancel on onCancel runs finalizer") { CF.onCancelFinalizerOnCancel(EQ, ctx) },
      Law("Concurrent Laws: async cancelable coherence") { CF.asyncCancelableCoherence(EQ) },
      Law("Concurrent Laws: cancelable cancelableF coherence") { CF.cancelableCancelableFCoherence(EQ) },
      Law("Concurrent Laws: cancelable should run CancelToken on cancel") { CF.cancelableReceivesCancelSignal(EQ, ctx) },
      Law("Concurrent Laws: cancelableF should run CancelToken on cancel") { CF.cancelableFReceivesCancelSignal(EQ, ctx) },
      Law("Concurrent Laws: asyncF register can be cancelled") { CF.asyncFRegisterCanBeCancelled(EQ, ctx) },
      Law("Concurrent Laws: start join is identity") { CF.startJoinIsIdentity(EQ, ctx) },
      Law("Concurrent Laws: join is idempotent") { CF.joinIsIdempotent(EQ, ctx) },
      Law("Concurrent Laws: start cancel is unit") { CF.startCancelIsUnit(EQ_UNIT, ctx) },
      Law("Concurrent Laws: uncancelable mirrors source") { CF.uncancelableMirrorsSource(EQ) },
      Law("Concurrent Laws: race pair mirrors left winner") { CF.racePairMirrorsLeftWinner(EQ, ctx) },
      Law("Concurrent Laws: race pair mirrors right winner") { CF.racePairMirrorsRightWinner(EQ, ctx) },
      Law("Concurrent Laws: race pair can cancel loser") { CF.racePairCanCancelsLoser(EQ, ctx) },
      Law("Concurrent Laws: race pair can join left") { CF.racePairCanJoinLeft(EQ, ctx) },
      Law("Concurrent Laws: race pair can join right") { CF.racePairCanJoinRight(EQ, ctx) },
      Law("Concurrent Laws: cancelling race pair cancels both") { CF.racePairCancelCancelsBoth(EQ, ctx) },
      Law("Concurrent Laws: race triple mirrors left winner") { CF.raceTripleMirrorsLeftWinner(EQ, ctx) },
      Law("Concurrent Laws: race triple mirrors middle winner") { CF.raceTripleMirrorsMiddleWinner(EQ, ctx) },
      Law("Concurrent Laws: race triple mirrors right winner") { CF.raceTripleMirrorsRightWinner(EQ, ctx) },
      Law("Concurrent Laws: race triple can cancel loser") { CF.raceTripleCanCancelsLoser(EQ, ctx) },
      Law("Concurrent Laws: race triple can join left") { CF.raceTripleCanJoinLeft(EQ, ctx) },
      Law("Concurrent Laws: race triple can join middle") { CF.raceTripleCanJoinMiddle(EQ, ctx) },
      Law("Concurrent Laws: race triple can join right") { CF.raceTripleCanJoinRight(EQ, ctx) },
      Law("Concurrent Laws: cancelling race triple cancels all") { CF.raceTripleCancelCancelsAll(EQ, ctx) },
      Law("Concurrent Laws: race mirrors left winner") { CF.raceMirrorsLeftWinner(EQ, ctx) },
      Law("Concurrent Laws: race mirrors right winner") { CF.raceMirrorsRightWinner(EQ, ctx) },
      Law("Concurrent Laws: race cancels loser") { CF.raceCancelsLoser(EQ, ctx) },
      Law("Concurrent Laws: race cancels both") { CF.raceCancelCancelsBoth(EQ, ctx) },
      Law("Concurrent Laws: parallel map cancels both") { CF.parMapCancelCancelsBoth(EQ, ctx) },
      Law("Concurrent Laws: action concurrent with pure value is just action") { CF.actionConcurrentWithPureValueIsJustAction(EQ, ctx) },
      Law("Concurrent Laws: parTraverse can traverse effectful computations") { CF.parTraverseCanTraverseEffectfullComputations(EQ) },
      Law("Concurrent Laws: parTraverse results in the correct error") { CF.parTraverseResultsInTheCorrectError(EQ_UNIT) },
      Law("Concurrent Laws: parTraverse forks the effects") { CF.parTraverseForksTheEffects(EQ_UNIT) },
      Law("Concurrent Laws: parSequence forks the effects") { CF.parSequenceForksTheEffects(EQ_UNIT) },
      Law("Concurrent Laws: onError is run when error is raised") { CF.onErrorIsRunWhenErrorIsRaised(EQ_UNIT, ctx) },
      Law("Concurrent Laws: onError is not run when completes normally") { CF.onErrorIsNotRunByDefault(EQK.liftEq(Tuple2.eq(Int.eq(), Boolean.eq())), ctx) },
      Law("Concurrent Laws: onError outer and inner finalizer is run when error is raised") { CF.outerAndInnerOnErrorIsRun(EQK.liftEq(Int.eq()), ctx) }
    )
  }

  fun <F> laws(
    CF: Concurrent<F>,
    T: Timer<F>,
    GENK: GenK<F>,
    EQK: EqK<F>,
    ctx: CoroutineContext = CF.dispatchers().default(),
    testStackSafety: Boolean = true
  ): List<Law> =
    AsyncLaws.laws(CF, GENK, EQK, testStackSafety) +
      TimerLaws.laws(CF, T, EQK) +
      concurrentLaws(CF, EQK, ctx)

  fun <F> laws(
    CF: Concurrent<F>,
    T: Timer<F>,
    FF: Functor<F>,
    AP: Apply<F>,
    SL: Selective<F>,
    GENK: GenK<F>,
    EQK: EqK<F>,
    ctx: CoroutineContext = CF.dispatchers().default(),
    testStackSafety: Boolean = true
  ): List<Law> =
    AsyncLaws.laws(CF, FF, AP, SL, GENK, EQK, testStackSafety) +
      TimerLaws.laws(CF, T, EQK) +
      concurrentLaws(CF, EQK, ctx)

  fun <F> Concurrent<F>.cancelOnBracketReleases(EQ: Eq<Kind<F, Int>>, ctx: CoroutineContext) {
    forAll(Gen.int()) { i ->
      fx.concurrent {
        val startLatch = Promise<F, Unit>(this@cancelOnBracketReleases).bind() // A promise that `use` was executed
        val exitLatch = Promise<F, Int>(this@cancelOnBracketReleases).bind() // A promise that `release` was executed

        val (_, cancel) = just(i).bracketCase(
          use = { a -> startLatch.complete(Unit).flatMap { never<Int>() } },
          release = { r, exitCase ->
            when (exitCase) {
              is ExitCase.Canceled -> exitLatch.complete(r) // Fulfil promise that `release` was executed with Canceled
              else -> unit()
            }
          }
        ).fork(ctx).bind() // Fork execution, allowing us to cancel it later

        startLatch.get().bind() // Waits on promise of `use`
        cancel.fork(ctx).bind() // Cancel bracketCase
        val waitExit = exitLatch.get().bind() // Observes cancellation via bracket's `release`

        waitExit
      }.equalUnderTheLaw(just(i), EQ)
    }
  }

  fun <F> Concurrent<F>.acquireBracketIsNotCancelable(EQ: Eq<Kind<F, Int>>, ctx: CoroutineContext) =
    forAll(Gen.int(), Gen.int()) { a, b ->
      fx.concurrent {
        val mvar = MVar(a, this@acquireBracketIsNotCancelable).bind()
        mvar.take().bind()
        val p = Promise.uncancelable<F, Unit>(this@acquireBracketIsNotCancelable).bind()
        val task = p.complete(Unit).flatMap { mvar.put(b) }
          .bracket(use = { never<Int>() }, release = { unit() })
        val (_, cancel) = task.fork(ctx).bind()
        p.get().bind()
        cancel.fork(ctx).bind()
        continueOn(ctx)
        mvar.take().bind()
      }.equalUnderTheLaw(just(b), EQ)
    }

  fun <F> Concurrent<F>.releaseBracketIsNotCancelable(EQ: Eq<Kind<F, Int>>, ctx: CoroutineContext) =
    forAll(Gen.int(), Gen.int()) { a, b ->
      fx.concurrent {
        val mvar = MVar(a, this@releaseBracketIsNotCancelable).bind()
        val p = Promise.uncancelable<F, Unit>(this@releaseBracketIsNotCancelable).bind()
        val task = p.complete(Unit)
          .bracket(use = { never<Int>() }, release = { mvar.put(b) })
        val (_, cancel) = task.fork(ctx).bind()
        p.get().bind()
        cancel.fork(ctx).bind()
        continueOn(ctx)
        mvar.take().bind()
        mvar.take().bind()
      }.equalUnderTheLaw(just(b), EQ)
    }

  fun <F> Concurrent<F>.guaranteeFinalizerOnCancel(EQ: Eq<Kind<F, Int>>, ctx: CoroutineContext) =
    forAll(Gen.int()) { i ->
      fx.concurrent {
        val startLatch = Promise<F, Unit>(this@guaranteeFinalizerOnCancel).bind() // A promise that `use` was executed
        val exitLatch = Promise<F, Int>(this@guaranteeFinalizerOnCancel).bind() // A promise that `release` was executed

        val (_, cancel) = startLatch.complete(Unit).flatMap { never<Int>() }
          .guaranteeCase { exitCase ->
            when (exitCase) {
              is ExitCase.Canceled -> exitLatch.complete(i) // Fulfil promise that `release` was executed with Canceled
              else -> unit()
            }
          }.fork(ctx).bind() // Fork execution, allowing us to cancel it later

        startLatch.get().bind() // Waits on promise of `use`
        cancel.fork(ctx).bind() // Cancel bracketCase
        val waitExit = exitLatch.get().bind() // Observes cancellation via bracket's `release`
        waitExit
      }.equalUnderTheLaw(just(i), EQ)
    }

  fun <F> Concurrent<F>.onCancelFinalizerOnCancel(EQ: Eq<Kind<F, Int>>, ctx: CoroutineContext) =
    forAll(Gen.int()) { i ->
      fx.concurrent {
        val startLatch = Promise<F, Unit>(this@onCancelFinalizerOnCancel).bind() // A promise that `use` was executed
        val exitLatch = Promise<F, Int>(this@onCancelFinalizerOnCancel).bind() // A promise that `release` was executed

        val (_, cancel) = startLatch.complete(Unit).flatMap { never<Int>() }
          .onCancel(exitLatch.complete(i)) // Fulfil promise that `release` was executed with Canceled
          .fork(ctx).bind() // Fork execution, allowing us to cancel it later

        startLatch.get().bind() // Waits on promise of `use`
        cancel.fork(ctx).bind() // Cancel bracketCase
        val waitExit = exitLatch.get().bind() // Observes cancellation via bracket's `release`
        waitExit
      }.equalUnderTheLaw(just(i), EQ)
    }

  fun <F> Concurrent<F>.guaranteeFinalizerIsNotCancelable(EQ: Eq<Kind<F, Int>>, ctx: CoroutineContext) =
    forAll(Gen.int(), Gen.int()) { a, b ->
      fx.concurrent {
        val mvar = MVar(a, this@guaranteeFinalizerIsNotCancelable).bind()
        val p = Promise.uncancelable<F, Unit>(this@guaranteeFinalizerIsNotCancelable).bind()
        val task = p.complete(Unit).followedBy(never<Int>()).guaranteeCase { mvar.put(b) }
        val (_, cancel) = task.fork(ctx).bind()
        p.get().bind()
        cancel.fork(ctx).bind()
        continueOn(ctx)
        mvar.take().bind()
        mvar.take().bind()
      }.equalUnderTheLaw(just(b), EQ)
    }

  fun <F> Concurrent<F>.asyncCancelableCoherence(EQ: Eq<Kind<F, Int>>): Unit =
    forAll(Gen.either(Gen.throwable(), Gen.int())) { eith ->
      async<Int> { cb -> cb(eith) }
        .equalUnderTheLaw(cancelable { cb -> cb(eith); just<Unit>(Unit) }, EQ)
    }

  fun <F> Concurrent<F>.cancelableCancelableFCoherence(EQ: Eq<Kind<F, Int>>): Unit =
    forAll(Gen.either(Gen.throwable(), Gen.int())) { eith ->
      cancelable<Int> { cb -> cb(eith); just<Unit>(Unit) }
        .equalUnderTheLaw(cancelableF { cb -> later { cb(eith); just<Unit>(Unit) } }, EQ)
    }

  fun <F> Concurrent<F>.cancelableReceivesCancelSignal(EQ: Eq<Kind<F, Int>>, ctx: CoroutineContext) =
    forAll(Gen.int()) { i ->
      fx.concurrent {
        val release = Promise.uncancelable<F, Int>(this@cancelableReceivesCancelSignal).bind()
        val cancelToken: CancelToken<F> = release.complete(i)
        val latch = CountDownLatch(1)

        val (_, cancel) = cancelable<Unit> {
          latch.countDown()
          cancelToken
        }.fork(ctx).bind()

        ctx.shift().followedBy(asyncF<Unit> { cb ->
          later { latch.await(500, MILLISECONDS) }
            .map { cb(Right(Unit)) }
        }).bind()

        cancel.bind()
        release.get().bind()
      }.equalUnderTheLaw(just(i), EQ)
    }

  fun <F> Concurrent<F>.cancelableFReceivesCancelSignal(EQ: Eq<Kind<F, Int>>, ctx: CoroutineContext) =
    forAll(Gen.int()) { i ->
      fx.concurrent {
        val release = Promise<F, Int>(this@cancelableFReceivesCancelSignal).bind()
        val latch = Promise<F, Unit>(this@cancelableFReceivesCancelSignal).bind()
        val async = cancelableF<Unit> {
          latch.complete(Unit)
            .map { release.complete(i) }
        }
        val (_, cancel) = async.fork(ctx).bind()
        asyncF<Unit> { cb -> latch.get().map { cb(Right(it)) } }.bind()
        cancel.bind()
        release.get().bind()
      }.equalUnderTheLaw(just(i), EQ)
    }

  fun <F> Concurrent<F>.asyncFRegisterCanBeCancelled(EQ: Eq<Kind<F, Int>>, ctx: CoroutineContext) =
    forAll(Gen.int()) { i ->
      fx.concurrent {
        val release = Promise<F, Int>(this@asyncFRegisterCanBeCancelled).bind()
        val acquire = Promise<F, Unit>(this@asyncFRegisterCanBeCancelled).bind()
        val task = asyncF<Unit> {
          acquire.complete(Unit).bracket(use = { never<Unit>() }, release = { release.complete(i) })
        }
        val (_, cancel) = task.fork(ctx).bind()
        acquire.get().bind()
        cancel.fork(ctx).bind()
        release.get().bind()
      }.equalUnderTheLaw(just(i), EQ)
    }

  fun <F> Concurrent<F>.startJoinIsIdentity(EQ: Eq<Kind<F, Int>>, ctx: CoroutineContext): Unit =
    forAll(Gen.int().applicativeError(this)) { fa ->
      fa.fork(ctx).flatMap { it.join() }.equalUnderTheLaw(fa, EQ)
    }

  fun <F> Concurrent<F>.joinIsIdempotent(EQ: Eq<Kind<F, Int>>, ctx: CoroutineContext) =
    forAll(Gen.int()) { i ->
      Promise<F, Int>(this@joinIsIdempotent).flatMap { p ->
        p.complete(i).fork(ctx)
          .flatMap { (join, _) -> join.followedBy(join) }
          .flatMap { p.get() }
      }.equalUnderTheLaw(just(i), EQ)
    }

  fun <F> Concurrent<F>.startCancelIsUnit(EQ_UNIT: Eq<Kind<F, Unit>>, ctx: CoroutineContext): Unit =
    forAll(Gen.int().applicativeError(this)) { fa ->
      fa.fork(ctx).flatMap { (_, cancel) -> cancel }
        .equalUnderTheLaw(just<Unit>(Unit), EQ_UNIT)
    }

  fun <F> Concurrent<F>.uncancelableMirrorsSource(EQ: Eq<Kind<F, Int>>): Unit =
    forAll(Gen.int()) { i ->
      just(i).uncancelable().equalUnderTheLaw(just(i), EQ)
    }

  fun <F> Concurrent<F>.raceMirrorsLeftWinner(EQ: Eq<Kind<F, Int>>, ctx: CoroutineContext): Unit =
    forAll(Gen.int().applicativeError(this)) { fa ->
      ctx.raceN(fa, never<Int>()).flatMap { either ->
        either.fold({ just(it) }, { raiseError(IllegalStateException("never() finished race")) })
      }.equalUnderTheLaw(fa, EQ)
    }

  fun <F> Concurrent<F>.raceMirrorsRightWinner(EQ: Eq<Kind<F, Int>>, ctx: CoroutineContext): Unit =
    forAll(Gen.int().applicativeError(this)) { fa ->
      ctx.raceN(never<Int>(), fa).flatMap { either ->
        either.fold({ raiseError<Int>(IllegalStateException("never() finished race")) }, { just(it) })
      }.equalUnderTheLaw(fa, EQ)
    }

  fun <F> Concurrent<F>.raceCancelsLoser(EQ: Eq<Kind<F, Int>>, ctx: CoroutineContext) =
    forAll(Gen.either(Gen.throwable(), Gen.string()), Gen.bool(), Gen.int()) { eith, leftWins, i ->
      fx.concurrent {
        val s = Semaphore(0L, this@raceCancelsLoser).bind()
        val promise = Promise.uncancelable<F, Int>(this@raceCancelsLoser).bind()
        val winner = s.acquire().flatMap { async<String> { cb -> cb(eith) } }
        val loser = s.release().bracket(use = { never<Int>() }, release = { promise.complete(i) })
        val race =
          if (leftWins) ctx.raceN(winner, loser)
          else ctx.raceN(loser, winner)

        race.attempt().flatMap { promise.get() }.bind()
      }.equalUnderTheLaw(just(i), EQ)
    }

  fun <F> Concurrent<F>.raceCancelCancelsBoth(EQ: Eq<Kind<F, Int>>, ctx: CoroutineContext) =
    forAll(Gen.int(), Gen.int()) { a, b ->
      fx.concurrent {
        val s = Semaphore(0L, this@raceCancelCancelsBoth).bind()
        val pa = Promise<F, Int>(this@raceCancelCancelsBoth).bind()
        val pb = Promise<F, Int>(this@raceCancelCancelsBoth).bind()

        val loserA = s.release().bracket(use = { never<String>() }, release = { pa.complete(a) })
        val loserB = s.release().bracket(use = { never<Int>() }, release = { pb.complete(b) })

        val (_, cancelRace) = ctx.raceN(loserA, loserB).fork(ctx).bind()
        s.acquireN(2L).flatMap { cancelRace }.bind()
        pa.get().bind() + pb.get().bind()
      }.equalUnderTheLaw(just(a + b), EQ)
    }

  fun <F> Concurrent<F>.racePairMirrorsLeftWinner(EQ: Eq<Kind<F, Int>>, ctx: CoroutineContext): Unit =
    forAll(Gen.int().applicativeError(this)) { fa ->
      val never = never<Int>()
      val received = ctx.racePair(fa, never).flatMap { either ->
        either.fold({ a, fiberB ->
          fiberB.cancel().map { a }
        }, { _, _ -> raiseError(AssertionError("never() finished race")) })
      }

      received.equalUnderTheLaw(ctx.raceN(fa, never).map { it.fold(::identity, ::identity) }, EQ)
    }

  fun <F> Concurrent<F>.racePairMirrorsRightWinner(EQ: Eq<Kind<F, Int>>, ctx: CoroutineContext): Unit =
    forAll(Gen.int().applicativeError(this)) { fa ->
      val never = never<Int>()
      val received = ctx.racePair(never, fa).flatMap { either ->
        either.fold({ _, _ ->
          raiseError<Int>(AssertionError("never() finished race"))
        }, { fiberA, b -> fiberA.cancel().map { b } })
      }

      received.equalUnderTheLaw(ctx.raceN(never, fa).map { it.fold(::identity, ::identity) }, EQ)
    }

  fun <F> Concurrent<F>.racePairCanCancelsLoser(EQ: Eq<Kind<F, Int>>, ctx: CoroutineContext) =
    forAll(Gen.either(Gen.throwable(), Gen.string()), Gen.bool(), Gen.int()) { eith, leftWinner, i ->
      val received = fx.concurrent {
        val s = Semaphore(0L, this@racePairCanCancelsLoser).bind()
        val p = Promise.uncancelable<F, Int>(this@racePairCanCancelsLoser).bind()
        val winner = s.acquire().flatMap { async<String> { cb -> cb(eith) } }
        val loser = s.release().bracket(use = { never<String>() }, release = { p.complete(i) })
        val race = if (leftWinner) ctx.racePair(winner, loser)
        else ctx.racePair(loser, winner)

        race.attempt()
          .flatMap { attempt ->
            attempt.fold({ p.get() },
              {
                it.fold(
                  { _, fiberB -> fiberB.cancel().fork(ctx).flatMap { p.get() } },
                  { fiberA, _ -> fiberA.cancel().fork(ctx).flatMap { p.get() } })
              })
          }.bind()
      }

      received.equalUnderTheLaw(just(i), EQ)
    }

  fun <F> Concurrent<F>.racePairCanJoinLeft(EQ: Eq<Kind<F, Int>>, ctx: CoroutineContext) =
    forAll(Gen.int()) { i ->
      Promise<F, Int>(this@racePairCanJoinLeft).flatMap { p ->
        ctx.racePair(p.get(), unit()).flatMap { eith ->
          eith.fold(
            { unit, _ -> just(unit) },
            { fiber, _ -> p.complete(i).flatMap { fiber.join() } }
          )
        }
      }.equalUnderTheLaw(just(i), EQ)
    }

  fun <F> Concurrent<F>.racePairCanJoinRight(EQ: Eq<Kind<F, Int>>, ctx: CoroutineContext) =
    forAll(Gen.int()) { i ->
      Promise<F, Int>(this@racePairCanJoinRight).flatMap { p ->
        ctx.racePair(unit(), p.get()).flatMap { eith ->
          eith.fold(
            { _, fiber -> p.complete(i).flatMap { fiber.join() } },
            { _, unit -> just(unit) }
          )
        }
      }.equalUnderTheLaw(just(i), EQ)
    }

  fun <F> Concurrent<F>.racePairCancelCancelsBoth(EQ: Eq<Kind<F, Int>>, ctx: CoroutineContext) =
    forAll(Gen.int(), Gen.int()) { a, b ->
      fx.concurrent {
        val s = Semaphore(0L, this@racePairCancelCancelsBoth).bind()
        val pa = Promise<F, Int>(this@racePairCancelCancelsBoth).bind()
        val pb = Promise<F, Int>(this@racePairCancelCancelsBoth).bind()

        val loserA: Kind<F, Int> = s.release().bracket(use = { never<Int>() }, release = { pa.complete(a) })
        val loserB: Kind<F, Int> = s.release().bracket(use = { never<Int>() }, release = { pb.complete(b) })

        val (_, cancelRacePair) = ctx.racePair(loserA, loserB).fork(ctx).bind()

        s.acquireN(2L).flatMap { cancelRacePair }.bind()
        pa.get().bind() + pb.get().bind()
      }.equalUnderTheLaw(just(a + b), EQ)
    }

  fun <F> Concurrent<F>.raceTripleMirrorsLeftWinner(EQ: Eq<Kind<F, Int>>, ctx: CoroutineContext) =
    forAll(Gen.int().applicativeError(this)) { fa ->
      val never = never<Int>()
      val received = ctx.raceTriple(fa, never, never).flatMap { either ->
        either.fold(
          { a, fiberB, fiberC -> fiberB.cancel().followedBy(fiberC.cancel()).map { a } },
          { _, _, _ -> raiseError(AssertionError("never() finished race")) },
          { _, _, _ -> raiseError(AssertionError("never() finished race")) })
      }

      received.equalUnderTheLaw(ctx.raceN(fa, never, never).map { it.fold(::identity, ::identity, ::identity) }, EQ)
    }

  fun <F> Concurrent<F>.raceTripleMirrorsMiddleWinner(EQ: Eq<Kind<F, Int>>, ctx: CoroutineContext) =
    forAll(Gen.int().applicativeError(this)) { fa ->
      val never = never<Int>()
      val received = ctx.raceTriple(never, fa, never).flatMap { either ->
        either.fold(
          { _, _, _ -> raiseError<Int>(AssertionError("never() finished race")) },
          { fiberA, b, fiberC -> fiberA.cancel().followedBy(fiberC.cancel()).map { b } },
          { _, _, _ -> raiseError(AssertionError("never() finished race")) })
      }

      received.equalUnderTheLaw(ctx.raceN(never, fa, never).map { it.fold(::identity, ::identity, ::identity) }, EQ)
    }

  fun <F> Concurrent<F>.raceTripleMirrorsRightWinner(EQ: Eq<Kind<F, Int>>, ctx: CoroutineContext) =
    forAll(Gen.int().applicativeError(this)) { fa ->
      val never = never<Int>()
      val received = ctx.raceTriple(never, never, fa).flatMap { either ->
        either.fold(
          { _, _, _ -> raiseError<Int>(AssertionError("never() finished race")) },
          { _, _, _ -> raiseError(AssertionError("never() finished race")) },
          { fiberA, fiberB, c -> fiberA.cancel().followedBy(fiberB.cancel()).map { c } })
      }

      received.equalUnderTheLaw(ctx.raceN(never, never, fa).map { it.fold(::identity, ::identity, ::identity) }, EQ)
    }

  fun <F> Concurrent<F>.raceTripleCanCancelsLoser(EQ: Eq<Kind<F, Int>>, ctx: CoroutineContext) =
    forAll(Gen.either(Gen.throwable(), Gen.string()), Gen.from(listOf(1, 2, 3)), Gen.int(), Gen.int()) { eith, leftWinner, a, b ->
      val received = fx.concurrent {
        val s = Semaphore(0L, this@raceTripleCanCancelsLoser).bind()
        val pa = Promise.uncancelable<F, Int>(this@raceTripleCanCancelsLoser).bind()
        val pb = Promise.uncancelable<F, Int>(this@raceTripleCanCancelsLoser).bind()

        val winner = s.acquireN(2).flatMap { async<String> { cb -> cb(eith) } }
        val loser = s.release().bracket(use = { never<String>() }, release = { pa.complete(a) })
        val loser2 = s.release().bracket(use = { never<String>() }, release = { pb.complete(b) })

        val race = when (leftWinner) {
          1 -> ctx.raceTriple(winner, loser, loser2)
          2 -> ctx.raceTriple(loser, winner, loser2)
          else -> ctx.raceTriple(loser, loser2, winner)
        }

        val combinePromises = pa.get().flatMap { a -> pb.get().map { b -> a + b } }

        race.attempt()
          .flatMap { attempt ->
            attempt.fold({ combinePromises },
              {
                it.fold(
                  { _, fiberB, fiberC ->
                    fiberB.cancel().followedBy(fiberC.cancel()).fork(ctx).flatMap { combinePromises }
                  },
                  { fiberA, _, fiberC ->
                    fiberA.cancel().followedBy(fiberC.cancel()).fork(ctx).flatMap { combinePromises }
                  },
                  { fiberA, fiberB, _ ->
                    fiberA.cancel().followedBy(fiberB.cancel()).fork(ctx).flatMap { combinePromises }
                  })
              })
          }.bind()
      }

      received.equalUnderTheLaw(just(a + b), EQ)
    }

  fun <F> Concurrent<F>.raceTripleCanJoinLeft(EQ: Eq<Kind<F, Int>>, ctx: CoroutineContext) =
    forAll(Gen.int()) { i ->
      Promise<F, Int>(this@raceTripleCanJoinLeft).flatMap { p ->
        ctx.raceTriple(p.get(), unit(), never<Unit>()).flatMap { result ->
          result.fold(
            { _, _, _ -> raiseError<Int>(AssertionError("Promise#get can never win race")) },
            { fiber, _, _ -> p.complete(i).flatMap { fiber.join() } },
            { _, _, _ -> raiseError(AssertionError("never() can never win race")) }
          )
        }
      }.equalUnderTheLaw(just(i), EQ)
    }

  fun <F> Concurrent<F>.raceTripleCanJoinMiddle(EQ: Eq<Kind<F, Int>>, ctx: CoroutineContext) =
    forAll(Gen.int()) { i ->
      Promise<F, Int>(this@raceTripleCanJoinMiddle).flatMap { p ->
        ctx.raceTriple(unit(), p.get(), never<Unit>()).flatMap { result ->
          result.fold(
            { _, fiber, _ -> p.complete(i).flatMap { fiber.join() } },
            { _, _, _ -> raiseError(AssertionError("Promise#get can never win race")) },
            { _, _, _ -> raiseError(AssertionError("never() can never win race")) }
          )
        }
      }.equalUnderTheLaw(just(i), EQ)
    }

  fun <F> Concurrent<F>.raceTripleCanJoinRight(EQ: Eq<Kind<F, Int>>, ctx: CoroutineContext) =
    forAll(Gen.int()) { i ->
      Promise<F, Int>(this@raceTripleCanJoinRight).flatMap { p ->
        ctx.raceTriple(unit(), never<Unit>(), p.get()).flatMap { result ->
          result.fold(
            { _, _, fiber -> p.complete(i).flatMap { fiber.join() } },
            { _, _, _ -> raiseError(AssertionError("never() can never win race")) },
            { _, _, _ -> raiseError(AssertionError("Promise#get can never win race")) }
          )
        }
      }.equalUnderTheLaw(just(i), EQ)
    }

  fun <F> Concurrent<F>.raceTripleCancelCancelsAll(EQ: Eq<Kind<F, Int>>, ctx: CoroutineContext) =
    forAll(Gen.int(), Gen.int(), Gen.int()) { a, b, c ->
      fx.concurrent {
        val s = Semaphore(0L, this@raceTripleCancelCancelsAll).bind()
        val pa = Promise<F, Int>(this@raceTripleCancelCancelsAll).bind()
        val pb = Promise<F, Int>(this@raceTripleCancelCancelsAll).bind()
        val pc = Promise<F, Int>(this@raceTripleCancelCancelsAll).bind()

        val loserA: Kind<F, Int> = s.release().bracket(use = { never<Int>() }, release = { pa.complete(a) })
        val loserB: Kind<F, Int> = s.release().bracket(use = { never<Int>() }, release = { pb.complete(b) })
        val loserC: Kind<F, Int> = s.release().bracket(use = { never<Int>() }, release = { pc.complete(c) })

        val (_, cancelRacePair) = ctx.raceTriple(loserA, loserB, loserC).fork(ctx).bind()

        s.acquireN(3L).flatMap { cancelRacePair }.bind()
        pa.get().bind() + pb.get().bind() + pc.get().bind()
      }.equalUnderTheLaw(just(a + b + c), EQ)
    }

  fun <F> Concurrent<F>.parMapCancelCancelsBoth(EQ: Eq<Kind<F, Int>>, ctx: CoroutineContext) =
    forAll(Gen.int(), Gen.int()) { a, b ->
      fx.concurrent {
        val s = Semaphore(0L, this@parMapCancelCancelsBoth).bind()
        val pa = Promise<F, Int>(this@parMapCancelCancelsBoth).bind()
        val pb = Promise<F, Int>(this@parMapCancelCancelsBoth).bind()

        val loserA = s.release().bracket(use = { never<String>() }, release = { pa.complete(a) })
        val loserB = s.release().bracket(use = { never<Int>() }, release = { pb.complete(b) })

        val (_, cancelParMapN) = ctx.parMapN(loserA, loserB, ::Tuple2).fork(ctx).bind()
        s.acquireN(2L).flatMap { cancelParMapN }.bind()
        pa.get().bind() + pb.get().bind()
      }.equalUnderTheLaw(just(a + b), EQ)
    }

  fun <F> Concurrent<F>.actionConcurrentWithPureValueIsJustAction(EQ: Eq<Kind<F, Int>>, ctx: CoroutineContext): Unit =
    forAll(Gen.int().map(::just), Gen.int()) { fa, i ->
      i.just().fork(ctx).flatMap { (join, _) ->
        fa.flatMap {
          join.map { i }
        }
      }.equalUnderTheLaw(just(i), EQ)
    }

  fun <F> Concurrent<F>.parTraverseCanTraverseEffectfullComputations(EQ: Eq<Kind<F, Int>>): Unit =
    forFew(10, Gen.int()) {
      val finalValue = 100
      fx.concurrent {
        val ref = !Ref(0)
        !ListK((0 until finalValue).toList()).parTraverse(ListK.traverse()) {
          ref.update(Int::inc)
        }
        !ref.get() - finalValue
      }.equalUnderTheLaw(just(0), EQ)
    }

  fun <F> Concurrent<F>.parTraverseForksTheEffects(EQ: Eq<Kind<F, Unit>>): Unit =
    forFew(10, Gen.int()) {
      fx.concurrent {
        val promiseA = !Promise<F, Unit>(this)
        val promiseB = !Promise<F, Unit>(this)
        val promiseC = !Promise<F, Unit>(this)
        !ListK(listOf(
          promiseA.get().bracket(use = { promiseC.complete(Unit) }, release = { unit() }),
          promiseB.get().followedBy(promiseA.complete(Unit)).bracket(use = { unit() }, release = { unit() }),
          promiseB.complete(Unit).followedBy(promiseC.get()).bracket(use = { unit() }, release = { unit() })
        )).parTraverse(ListK.traverse(), ::identity).unit()
      }.equalUnderTheLaw(unit(), EQ)
    }

  val TestError = RuntimeException("TestError")

  fun <F> Concurrent<F>.parTraverseResultsInTheCorrectError(EQ: Eq<Kind<F, Unit>>): Unit =
    forAll(Gen.choose(0, 10)) { killOn ->
      (10 downTo 0).toList().k().parTraverse(ListK.traverse()) { i ->
        if (i == killOn) raiseError(TestError)
        else unit()
      }.unit().attempt()
        .map { it shouldBe Left(TestError) }
        .equalUnderTheLaw(unit(), EQ)
    }

  fun <F> Concurrent<F>.parSequenceForksTheEffects(EQ: Eq<Kind<F, Unit>>): Unit =
    forFew(10, Gen.int()) {
      fx.concurrent {
        val promiseA = !Promise<F, Unit>(this)
        val promiseB = !Promise<F, Unit>(this)
        val promiseC = !Promise<F, Unit>(this)
        !ListK(listOf(
          promiseA.get().bracket(use = { promiseC.complete(Unit) }, release = { unit() }),
          promiseB.get().followedBy(promiseA.complete(Unit)).bracket(use = { unit() }, release = { unit() }),
          promiseB.complete(Unit).followedBy(promiseC.get()).bracket(use = { unit() }, release = { unit() })
        )).parSequence(ListK.traverse()).unit()
      }.equalUnderTheLaw(unit(), EQ)
    }

  fun <F> Concurrent<F>.onErrorIsRunWhenErrorIsRaised(EQ: Eq<Kind<F, Unit>>, ctx: CoroutineContext) =
    forAll(Gen.throwable()) { expected ->
      fx.concurrent {

        val startLatch = Promise<F, Unit>(this@onErrorIsRunWhenErrorIsRaised).bind()
        val errorLatch = Promise<F, Throwable>(this@onErrorIsRunWhenErrorIsRaised).bind()

        startLatch.complete(Unit).flatMap { raiseError<Exception>(expected) }
          .onError(errorLatch::complete)
          .fork(ctx).bind()

        startLatch.get().bind() // Waits on promise of `use`

        val waitExit = errorLatch.get().bind()
        !effect { waitExit shouldBe expected }
      }.equalUnderTheLaw(Unit.just(), EQ)
    }

  fun <F> Concurrent<F>.onErrorIsNotRunByDefault(EQ: Eq<Kind<F, Tuple2<Int, Boolean>>>, ctx: CoroutineContext) =
    forAll(Gen.int()) { i ->

      val CF = this@onErrorIsNotRunByDefault
      fx.concurrent {

        val startLatch = Promise<F, Int>(CF).bind()
        val onErrorRun = Ref(false).bind()

        val (completed, _) = startLatch.complete(i)
          .onError { onErrorRun.set(true) }
          .fork(ctx).bind()

        completed.bind()

        startLatch.get().bind() toT onErrorRun.get().bind()
      }.equalUnderTheLaw(just(i toT false), EQ)
    }

  fun <F> Concurrent<F>.outerAndInnerOnErrorIsRun(EQ: Eq<Kind<F, Int>>, ctx: CoroutineContext) =
    fx.concurrent {
      val CF = this@outerAndInnerOnErrorIsRun
      val latch = Promise<F, Unit>(CF).bind()
      val counter = AtomicInteger(0)
      val incrementCounter = CF.later {
        counter.getAndIncrement()
        Unit
      }

      just(Unit).flatMap {
        raiseError<Unit>(RuntimeException("failed"))
          .onError { incrementCounter }
      }.onError { incrementCounter }
        .guarantee(latch.complete(Unit))
        .fork(ctx).bind()

      latch.get().bind()

      counter.get()
    }.equalUnderTheLaw(just(2), EQ)
}
