package arrow.test.laws

import arrow.Kind
import arrow.core.Either
import arrow.core.identity
import arrow.effects.MVar
import arrow.effects.Promise
import arrow.effects.Semaphore
import arrow.effects.typeclasses.Concurrent
import arrow.effects.typeclasses.ExitCase
import arrow.test.generators.genEither
import arrow.test.generators.genThrowable
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import kotlinx.coroutines.Dispatchers
import java.lang.RuntimeException
import kotlin.coroutines.CoroutineContext

object ConcurrentLaws {

  fun <F> laws(CF: Concurrent<F>,
               EQ: Eq<Kind<F, Int>>,
               EQ_EITHER: Eq<Kind<F, Either<Throwable, Int>>>,
               EQ_UNIT: Eq<Kind<F, Unit>>,
               ctx: CoroutineContext = Dispatchers.Default,
               testStackSafety: Boolean = true): List<Law> =
    AsyncLaws.laws(CF, EQ, EQ_EITHER, testStackSafety) + listOf(
      Law("Concurrent Laws: cancel on bracket releases") { CF.cancelOnBracketReleases(EQ, ctx) },
//      Law("Concurrent Laws: async cancelable coherence") { CF.asyncCancelableCoherence(EQ) },
//      Law("Concurrent Laws: async cancelable receives cancel signal") { CF.asyncCancelableReceivesCancelSignal(EQ) },
//      Law("Concurrent Laws: asyncF register can be cancelled") { CF.asyncFRegisterCanBeCancelled(EQ) },
      Law("Concurrent Laws: start join is identity") { CF.startJoinIsIdentity(EQ, ctx) },
      Law("Concurrent Laws: join is idempotent") { CF.joinIsIdempotent(EQ, ctx) },
      Law("Concurrent Laws: start cancel is unit") { CF.startCancelIsUnit(EQ_UNIT, ctx) },
      Law("Concurrent Laws: uncancelable mirrors source") { CF.uncancelableMirrorsSource(EQ) },
//      Law("Concurrent Laws: uncancelable prevents cancelation") { CF.uncancelablePreventsCancelation(EQ) },
//      Law("Concurrent Laws: acquire is not cancelable") { CF.acquireIsNotCancelable(EQ, ctx) },
//      Law("Concurrent Laws: release is not cancelable") { CF.releaseIsNotCancelable(EQ, ctx) },
      Law("Concurrent Laws: race mirrors left winner") { CF.raceMirrorsLeftWinner(EQ, ctx) },
      Law("Concurrent Laws: race mirrors right winner") { CF.raceMirrorsRightWinner(EQ, ctx) },
      Law("Concurrent Laws: race cancels loser") { CF.raceCancelsLoser(EQ, ctx) },
      Law("Concurrent Laws: race cancels both") { CF.raceCancelsBoth(EQ, ctx) },
      Law("Concurrent Laws: race pair mirrors left winner") { CF.racePairMirrorsLeftWinner(EQ, ctx) },
      Law("Concurrent Laws: race pair mirrors right winner") { CF.racePairMirrorsRightWinner(EQ, ctx) },
      Law("Concurrent Laws: race pair cancels loser") { CF.racePairCancelsLoser(EQ, ctx) },
      Law("Concurrent Laws: race pair can join left") { CF.racePairCanJoinLeft(EQ, ctx) },
      Law("Concurrent Laws: race pair can join right") { CF.racePairCanJoinRight(EQ, ctx) },
      Law("Concurrent Laws: cancelling race pair cancels both") { CF.racePairCancelsBoth(EQ, ctx) },
      Law("Concurrent Laws: action concurrent with pure value is just action") { CF.actionConcurrentWithPureValueIsJustAction(EQ, ctx) }
    )


  fun <F> Concurrent<F>.cancelOnBracketReleases(EQ: Eq<Kind<F, Int>>, ctx: CoroutineContext) {
    forAll(Gen.int()) { i ->
      binding {
        val startLatch = Promise.uncancelable<F, Int>(this@cancelOnBracketReleases).bind()
        val exitLatch = Promise.uncancelable<F, Int>(this@cancelOnBracketReleases).bind()

        val fiber = just(i).bracketCase(
          use = { a -> startLatch.complete(a).flatMap { never<Int>() } },
          release = { r, exitCase ->
            when (exitCase) {
              is ExitCase.Cancelled -> exitLatch.complete(r)
              else -> just(Unit)
            }
          }
        ).startF(ctx).bind()

        val waitStart = startLatch.get.bind()
        fiber.cancel().bind()
        val waitExit = exitLatch.get.bind()

        waitStart + waitExit
      }.equalUnderTheLaw(just(i + i), EQ)
    }
  }

  fun <F> Concurrent<F>.startJoinIsIdentity(EQ: Eq<Kind<F, Int>>, ctx: CoroutineContext): Unit =
    forAll(Gen.int()) { i ->
      val fa = just(i)
      fa.startF(ctx).flatMap { it.join() }.equalUnderTheLaw(fa, EQ)
    }

  fun <F> Concurrent<F>.joinIsIdempotent(EQ: Eq<Kind<F, Int>>, ctx: CoroutineContext) =
    forAll(Gen.int()) { i ->
      Promise.uncancelable<F, Int>(this@joinIsIdempotent).flatMap { p ->
        p.complete(i).startF(ctx).flatMap { fiber ->
          fiber.join().flatMap { fiber.join() }.flatMap { p.get }
        }
      }.equalUnderTheLaw(just(i), EQ)
    }

  fun <F> Concurrent<F>.startCancelIsUnit(EQ_UNIT: Eq<Kind<F, Unit>>, ctx: CoroutineContext): Unit =
    forAll(Gen.int()) { i ->
      just(i).startF(ctx).flatMap { it.cancel() }.equalUnderTheLaw(just(Unit), EQ_UNIT)
    }

  fun <F> Concurrent<F>.uncancelableMirrorsSource(EQ: Eq<Kind<F, Int>>): Unit =
    forAll(Gen.int()) { i ->
      just(i).uncancelable().equalUnderTheLaw(just(i), EQ)
    }

  fun <F> Concurrent<F>.acquireIsNotCancelable(EQ: Eq<Kind<F, Int>>, ctx: CoroutineContext) =
    forAll(Gen.int(), Gen.int()) { a, b ->
      binding {
        val mvar = MVar.uncancelableOf(a, this@acquireIsNotCancelable).bind()
        val p = Promise.uncancelable<F, Unit>(this@acquireIsNotCancelable).bind()
        val task = p.complete(Unit).flatMap { mvar.put(b) }.bracket(use = { never<Int>()}, release = { just(Unit) })
        val fiber = task.startF(ctx).bind()
        p.get.bind()
        fiber.cancel().startF(ctx).bind()
        continueOn(ctx)
        mvar.take().bind()
        mvar.take().bind()
      }.equalUnderTheLaw(just(b), EQ)
    }

  fun <F> Concurrent<F>.releaseIsNotCancelable(EQ: Eq<Kind<F, Int>>, ctx: CoroutineContext) =
    forAll(Gen.int(), Gen.int()) { a, b ->
      binding {
        val mvar = MVar.uncancelableOf(a, this@releaseIsNotCancelable).bind()
        val p = Promise.uncancelable<F, Unit>(this@releaseIsNotCancelable).bind()
        val task = p.complete(Unit).bracket(use= { never<Int>() }, release = { mvar.put(b) })
        val fiber = task.startF(ctx).bind()
        p.get.bind()
        fiber.cancel().startF(ctx).bind()
        continueOn(ctx)
        mvar.take().bind()
        mvar.take().bind()
      }.equalUnderTheLaw(just(b), EQ)
    }

  fun <F> Concurrent<F>.raceMirrorsLeftWinner(EQ: Eq<Kind<F, Int>>, ctx: CoroutineContext): Unit =
    forAll(Gen.int()) { i ->
      val fa = just(i)
      raceN(ctx, fa, never<Int>()).flatMap { either ->
        either.fold({ just(it) }, { raiseError(IllegalStateException("never() finished race")) })
      }.equalUnderTheLaw(fa, EQ)
    }

  fun <F> Concurrent<F>.raceMirrorsRightWinner(EQ: Eq<Kind<F, Int>>, ctx: CoroutineContext): Unit =
    forAll(Gen.int()) { i ->
      val fa = just(i)
      raceN(ctx, never<Int>(), fa).flatMap { either ->
        either.fold({ raiseError<Int>(IllegalStateException("never() finished race")) }, { just(it) })
      }.equalUnderTheLaw(fa, EQ)
    }

  fun <F> Concurrent<F>.raceCancelsLoser(EQ: Eq<Kind<F, Int>>, ctx: CoroutineContext) =
    forAll(genEither(genThrowable(), Gen.string()), Gen.bool(), Gen.int()) { eith, leftWins, i ->
      binding {
        val s = Semaphore.uncancelable(0L, this@raceCancelsLoser).bind()
        val promise = Promise.uncancelable<F, Int>(this@raceCancelsLoser).bind()
        val winner = s.acquire().flatMap { async<String> { cb -> cb(eith) } }
        val loser = s.release().bracket(use = { never<Int>() }, release = { promise.complete(i) })
        val race =
          if (leftWins) raceN(ctx, winner, loser)
          else raceN(ctx, loser, winner)

        race.attempt().flatMap { promise.get }.bind()
      }.equalUnderTheLaw(just(i), EQ)
    }

  fun <F> Concurrent<F>.raceCancelsBoth(EQ: Eq<Kind<F, Int>>, ctx: CoroutineContext) =
    forAll(Gen.int(), Gen.int()) { a, b ->
      binding {
        val ss = Semaphore.uncancelable(0L, this@raceCancelsBoth).bind()
        val pa = Promise.uncancelable<F, Int>(this@raceCancelsBoth).bind()
        val pb = Promise.uncancelable<F, Int>(this@raceCancelsBoth).bind()

        val loserA = ss.release().bracket(use = { never<String>() }, release = { pa.complete(a) })
        val loserB = ss.release().bracket(use = { never<Int>() }, release = { pb.complete(b) })

        val race = raceN(ctx, loserA, loserB).startF(ctx).bind()
        ss.acquireN(2L).flatMap { race.cancel() }.bind()

        pa.get.bind() + pb.get.bind()
      }.equalUnderTheLaw(just(a + b), EQ)
    }

  fun <F> Concurrent<F>.racePairMirrorsLeftWinner(EQ: Eq<Kind<F, Int>>, ctx: CoroutineContext): Unit =
    forAll(Gen.int()) { i ->
      val fa = just(i)
      val never = never<Int>()
      val received = racePair(ctx, fa, never).flatMap { either ->
        either.fold({ (a, fiberB) ->
          fiberB.cancel().map { a }
        }, { raiseError(AssertionError("never() finished race")) })
      }

      received.equalUnderTheLaw(raceN(ctx, fa, never).map { it.fold(::identity, ::identity) }, EQ)
    }

  fun <F> Concurrent<F>.racePairMirrorsRightWinner(EQ: Eq<Kind<F, Int>>, ctx: CoroutineContext): Unit =
    forAll(Gen.int()) { i ->
      val fa = just(i)
      val never = never<Int>()
      val received = racePair(ctx, never, fa).flatMap { either ->
        either.fold({
          raiseError<Int>(AssertionError("never() finished race"))
        }, { (fiberA, b) -> fiberA.cancel().map { b } })
      }

      received.equalUnderTheLaw(raceN(ctx, never, fa).map { it.fold(::identity, ::identity) }, EQ)
    }

  fun <F> Concurrent<F>.racePairCancelsLoser(EQ: Eq<Kind<F, Int>>, ctx: CoroutineContext) =
    forAll(genEither(genThrowable(), Gen.string()), Gen.bool(), Gen.int()) { eith, leftWinner, i ->
      val received = binding {
        val s = Semaphore.uncancelable(0L, this@racePairCancelsLoser).bind()
        val p = Promise.uncancelable<F, Int>(this@racePairCancelsLoser).bind()
        val winner = s.acquire().flatMap { async<String> { cb -> cb(eith) } }
        val loser = s.release().bracket(release = { p.complete(i) }, use = { never<String>() })
        val race = if (leftWinner) racePair(ctx, winner, loser)
        else racePair(ctx, loser, winner)

        race.attempt()
          .flatMap { attempt ->
            attempt.fold(
              { p.get },
              {
                it.fold(
                  { (_, fiberB) -> fiberB.cancel().startF(ctx).flatMap { p.get } },
                  { (fiberA, _) -> fiberA.cancel().startF(ctx).flatMap { p.get } }
                )
              }
            )
          }.bind()
      }

      received.equalUnderTheLaw(just(i), EQ)
    }

  fun <F> Concurrent<F>.racePairCanJoinLeft(EQ: Eq<Kind<F, Int>>, ctx: CoroutineContext) =
    forAll(Gen.int()) { i ->
      Promise.uncancelable<F, Int>(this@racePairCanJoinLeft).flatMap { p ->
        racePair(ctx, p.get, just(Unit)).flatMap { eith ->
          eith.fold(
            { (unit, _) -> just(unit) },
            { (fiber, _) -> p.complete(i).flatMap { fiber.join() } }
          )
        }
      }.equalUnderTheLaw(just(i), EQ)
    }

  fun <F> Concurrent<F>.racePairCanJoinRight(EQ: Eq<Kind<F, Int>>, ctx: CoroutineContext) =
    forAll(Gen.int()) { i ->
      Promise.uncancelable<F, Int>(this@racePairCanJoinRight).flatMap { p ->
        racePair(ctx, just(Unit), p.get).flatMap { eith ->
          eith.fold(
            { (_, fiber) -> p.complete(i).flatMap { fiber.join() } },
            { (_, unit) -> just(unit) }
          )
        }
      }.equalUnderTheLaw(just(i), EQ)
    }

  fun <F> Concurrent<F>.racePairCancelsBoth(EQ: Eq<Kind<F, Int>>, ctx: CoroutineContext) =
    forAll(Gen.int(), Gen.int()) { a, b ->
      binding {
        val s = Semaphore.uncancelable(0L, this@racePairCancelsBoth).bind()
        val pa = Promise.uncancelable<F, Int>(this@racePairCancelsBoth).bind()
        val loserA: Kind<F, Int> = s.release().bracket(use = { never<Int>() }, release = { pa.complete(a) })
        val pb = Promise.uncancelable<F, Int>(this@racePairCancelsBoth).bind()
        val loserB: Kind<F, Int> = s.release().bracket(use = { never<Int>() }, release = { pb.complete(b) })
        val race = racePair(ctx, loserA, loserB).startF(ctx).bind()
        s.acquireN(2L).flatMap { race.cancel() }.bind()
        pa.get.bind() + pb.get.bind()
      }.equalUnderTheLaw(just(a + b), EQ)
    }

  fun <F> Concurrent<F>.actionConcurrentWithPureValueIsJustAction(EQ: Eq<Kind<F, Int>>, ctx: CoroutineContext): Unit =
    forAll(Gen.string(), Gen.int()) { s, i ->
      just(s).startF(ctx).flatMap { (join, _) ->
        just(i).flatMap { x ->
          join.map { x }
        }
      }.equalUnderTheLaw(just(i), EQ)
    }

}
