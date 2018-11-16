package arrow.test.laws

import arrow.Kind
import arrow.core.Either
import arrow.core.identity
import arrow.effects.typeclasses.Concurrent
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object ConcurrentLaws {

  //TODO Add additional laws when `Semaphore` & `Promise` is added.
  fun <F> laws(CF: Concurrent<F>,
               EQ: Eq<Kind<F, Int>>,
               EQ_EITHER: Eq<Kind<F, Either<Throwable, Int>>>,
               EQ_UNIT: Eq<Kind<F, Unit>>): List<Law> =
    AsyncLaws.laws(CF, EQ, EQ_EITHER, EQ) + listOf(
//      Law("Concurrent Laws: cancel on bracket releases") { CF.cancelOnBracketReleases(EQ) },
//      Law("Concurrent Laws: async cancelable coherence") { CF.asyncCancelableCoherence(EQ) },
//      Law("Concurrent Laws: async cancelable receives cancel signal") { CF.asyncCancelableReceivesCancelSignal(EQ) },
//      Law("Concurrent Laws: asyncF register can be cancelled") { CF.asyncFRegisterCanBeCancelled(EQ) },
      Law("Concurrent Laws: start join is identity") { CF.startJoinIsIdentity(EQ) },
//      Law("Concurrent Laws: join is idempotent") { CF.joinIsIdempotent(EQ) },
      Law("Concurrent Laws: start cancel is unit") { CF.startCancelIsUnit(EQ_UNIT) },
      Law("Concurrent Laws: uncancelable mirrors source") { CF.uncancelableMirrorsSource(EQ) },
//      Law("Concurrent Laws: uncancelable prevents cancelation") { CF.uncancelablePreventsCancelation(EQ) },
//      Law("Concurrent Laws: acquire is not cancelable") { CF.acquireIsNotCancelable(EQ) },
//      Law("Concurrent Laws: release is not cancelable") { CF.releaseIsNotCancelable(EQ) },
      Law("Concurrent Laws: race mirrors left winner") { CF.raceMirrorsLeftWinner(EQ) },
      Law("Concurrent Laws: race mirrors right winner") { CF.raceMirrorsRightWinner(EQ) },
//      Law("Concurrent Laws: race cancels loser") { CF.raceCancelsLoser(EQ) },
//      Law("Concurrent Laws: race cancels both") { CF.raceCancelsBoth(EQ) },
      Law("Concurrent Laws: race pair mirrors left winner") { CF.racePairMirrorsLeftWinner(EQ) },
      Law("Concurrent Laws: race pair mirrors right winner") { CF.racePairMirrorsRightWinner(EQ) },
//      Law("Concurrent Laws: race pair cancels loser") { CF.racePairCancelsLoser(EQ) },
//      Law("Concurrent Laws: race pair can join left") { CF.racePairCanJoinLeft(EQ) },
//      Law("Concurrent Laws: race pair can join right") { CF.racePairCanJoinRight(EQ) },
//      Law("Concurrent Laws: race pair can join both") { CF.racePairCancelsBoth(EQ) },
      Law("Concurrent Laws: action concurrent with pure value is just action") { CF.actionConcurrentWithPureValueIsJustAction(EQ) }
    )

  fun <F> Concurrent<F>.startJoinIsIdentity(EQ: Eq<Kind<F, Int>>): Unit =
    forAll(Gen.int()) { i ->
      val fa = just(i)
      fa.startF().flatMap { it.join }.equalUnderTheLaw(fa, EQ)
    }

  fun <F> Concurrent<F>.startCancelIsUnit(EQ_UNIT: Eq<Kind<F, Unit>>): Unit =
    forAll(Gen.int()) { i ->
      just(i).startF().flatMap { it.cancel }.equalUnderTheLaw(just(Unit), EQ_UNIT)
    }

  fun <F> Concurrent<F>.uncancelableMirrorsSource(EQ: Eq<Kind<F, Int>>): Unit =
    forAll(Gen.int()) { i ->
      just(i).uncancelable().equalUnderTheLaw(just(i), EQ)
    }

  fun <F> Concurrent<F>.raceMirrorsLeftWinner(EQ: Eq<Kind<F, Int>>): Unit =
    forAll(Gen.int()) { i ->
      val fa = just(i)
      race(fa, never<Int>()).flatMap { either ->
        either.fold({ just(it) }, { raiseError(IllegalStateException("never() finished race")) })
      }.equalUnderTheLaw(fa, EQ)
    }

  fun <F> Concurrent<F>.raceMirrorsRightWinner(EQ: Eq<Kind<F, Int>>): Unit =
    forAll(Gen.int()) { i ->
      val fa = just(i)
      race(never<Int>(), fa).flatMap { either ->
        either.fold({ raiseError<Int>(IllegalStateException("never() finished race")) }, { just(it) })
      }.equalUnderTheLaw(fa, EQ)
    }

  fun <F> Concurrent<F>.racePairMirrorsLeftWinner(EQ: Eq<Kind<F, Int>>): Unit =
    forAll(Gen.int()) { i ->
      val fa = just(i)
      val never = never<Int>()
      val received = racePair(fa, never).flatMap { either ->
        either.fold({ (a, fiberB) ->
          fiberB.cancel.map { a }
        }, { raiseError(AssertionError("never() finished race")) })
      }

      received.equalUnderTheLaw(race(fa, never).map { it.fold(::identity, ::identity) }, EQ)
    }

  fun <F> Concurrent<F>.racePairMirrorsRightWinner(EQ: Eq<Kind<F, Int>>): Unit =
    forAll(Gen.int()) { i ->
      val fa = just(i)
      val never = never<Int>()
      val received = racePair(never, fa).flatMap { either ->
        either.fold({
          raiseError<Int>(AssertionError("never() finished race"))
        }, { (fiberA, b) -> fiberA.cancel.map { b } })
      }

      received.equalUnderTheLaw(race(never, fa).map { it.fold(::identity, ::identity) }, EQ)
    }

  fun <F> Concurrent<F>.actionConcurrentWithPureValueIsJustAction(EQ: Eq<Kind<F, Int>>): Unit =
    forAll(Gen.string(), Gen.int()) { s, i ->
      just(s).startF().flatMap { (join, _) ->
        just(i).flatMap { x ->
          join.map { x }
        }
      }.equalUnderTheLaw(just(i), EQ)
    }

}
