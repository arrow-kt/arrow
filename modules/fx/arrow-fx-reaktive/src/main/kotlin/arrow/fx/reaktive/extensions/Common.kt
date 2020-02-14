package arrow.fx.reaktive.extensions

import arrow.fx.RacePair
import arrow.fx.RaceTriple
import arrow.fx.reaktive.connectAndGetDisposable
import arrow.fx.typeclasses.Fiber
import com.badoo.reaktive.coroutinesinterop.asScheduler
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.maybe.Maybe
import com.badoo.reaktive.maybe.MaybeCallbacks
import com.badoo.reaktive.maybe.MaybeObserver
import com.badoo.reaktive.maybe.asObservable
import com.badoo.reaktive.maybe.doOnBeforeFinally
import com.badoo.reaktive.maybe.filter
import com.badoo.reaktive.maybe.map
import com.badoo.reaktive.maybe.maybe
import com.badoo.reaktive.maybe.merge
import com.badoo.reaktive.maybe.subscribeOn
import com.badoo.reaktive.observable.ConnectableObservable
import com.badoo.reaktive.observable.firstOrComplete
import com.badoo.reaktive.observable.replay
import kotlin.coroutines.CoroutineContext

internal fun <F, A, B> CoroutineContext.racePair(fa: Maybe<A>, fb: Maybe<B>, fiberFactory: FiberFactory<F>): Maybe<RacePair<F, A, B>> =
  asScheduler().let { scheduler ->
    maybe { emitter ->
      val connectableA: ConnectableObservable<A> = fa.subscribeOn(scheduler).asObservable().replay(bufferSize = 1)
      val connectableB: ConnectableObservable<B> = fb.subscribeOn(scheduler).asObservable().replay(bufferSize = 1)
      val disposableA = connectableA.connectAndGetDisposable()
      val disposableB = connectableB.connectAndGetDisposable()
      val maybeA = connectableA.firstOrComplete().filter { !disposableA.isDisposed }
      val maybeB = connectableB.firstOrComplete().filter { !disposableB.isDisposed }
      var isResultEmitted = false

      merge(
        maybeA.map { RacePairResult.First(it) },
        maybeB.map { RacePairResult.Second(it) }
      )
        .firstOrComplete()
        .map {
          isResultEmitted = true
          // Race condition here: the chain can be disposed before the fiber reaches its listener
          when (it) {
            is RacePairResult.First -> RacePair.First(winner = it.value, fiberB = fiberFactory(maybeB, disposableB))
            is RacePairResult.Second -> RacePair.Second(fiberA = fiberFactory(maybeA, disposableA), winner = it.value)
          }
        }
        .doOnBeforeFinally {
          if (!isResultEmitted) {
            disposableA.dispose()
            disposableB.dispose()
          }
        }
        .subscribe(
          object : MaybeObserver<RacePair<F, A, B>>, MaybeCallbacks<RacePair<F, A, B>> by emitter {
            override fun onSubscribe(disposable: Disposable) {
              emitter.setDisposable(disposable)
            }
          }
        )
    }
  }

internal fun <F, A, B, C> CoroutineContext.raceTriple(fa: Maybe<A>, fb: Maybe<B>, fc: Maybe<C>, fiberFactory: FiberFactory<F>): Maybe<RaceTriple<F, A, B, C>> =
  asScheduler().let { scheduler ->
    maybe { emitter ->
      val observableA: ConnectableObservable<A> = fa.subscribeOn(scheduler).asObservable().replay(bufferSize = 1)
      val observableB: ConnectableObservable<B> = fb.subscribeOn(scheduler).asObservable().replay(bufferSize = 1)
      val observableC: ConnectableObservable<C> = fc.subscribeOn(scheduler).asObservable().replay(bufferSize = 1)
      val disposableA = observableA.connectAndGetDisposable()
      val disposableB = observableB.connectAndGetDisposable()
      val disposableC = observableC.connectAndGetDisposable()
      val maybeA = observableA.firstOrComplete().filter { !disposableA.isDisposed }
      val maybeB = observableB.firstOrComplete().filter { !disposableB.isDisposed }
      val maybeC = observableC.firstOrComplete().filter { !disposableC.isDisposed }
      var isResultEmitted = false

      merge(
        maybeA.map { RaceTripleResult.First(it) },
        maybeB.map { RaceTripleResult.Second(it) },
        maybeC.map { RaceTripleResult.Third(it) }
      )
        .firstOrComplete()
        .map {
          isResultEmitted = true
          // Race condition here: the chain can be disposed before the fiber reaches its listener
          when (it) {
            is RaceTripleResult.First -> RaceTriple.First(winner = it.value, fiberB = fiberFactory(maybeB, disposableB), fiberC = fiberFactory(maybeC, disposableC))
            is RaceTripleResult.Second -> RaceTriple.Second(fiberA = fiberFactory(maybeA, disposableA), winner = it.value, fiberC = fiberFactory(maybeC, disposableC))
            is RaceTripleResult.Third -> RaceTriple.Third(fiberA = fiberFactory(maybeA, disposableA), fiberB = fiberFactory(maybeB, disposableB), winner = it.value)
          }
        }
        .doOnBeforeFinally {
          if (!isResultEmitted) {
            disposableA.dispose()
            disposableB.dispose()
            disposableC.dispose()
          }
        }
        .subscribe(
          object : MaybeObserver<RaceTriple<F, A, B, C>>, MaybeCallbacks<RaceTriple<F, A, B, C>> by emitter {
            override fun onSubscribe(disposable: Disposable) {
              emitter.setDisposable(disposable)
            }
          }
        )
    }
  }

internal interface FiberFactory<F> {
  operator fun <A> invoke(join: Maybe<A>, disposable: Disposable): Fiber<F, A>
}

internal sealed class RacePairResult<out A, out B> {
  class First<A>(val value: A) : RacePairResult<A, Nothing>()
  class Second<B>(val value: B) : RacePairResult<Nothing, B>()
}

internal sealed class RaceTripleResult<out A, out B, out C> {
  class First<A>(val value: A) : RaceTripleResult<A, Nothing, Nothing>()
  class Second<B>(val value: B) : RaceTripleResult<Nothing, B, Nothing>()
  class Third<C>(val value: C) : RaceTripleResult<Nothing, Nothing, C>()
}
