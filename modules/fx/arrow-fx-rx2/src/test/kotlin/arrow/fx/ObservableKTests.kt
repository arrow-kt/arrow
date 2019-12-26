package arrow.fx

import arrow.Kind
import arrow.core.Try
import arrow.fx.rx2.ForObservableK
import arrow.fx.rx2.ObservableK
import arrow.fx.rx2.ObservableKOf
import arrow.fx.rx2.extensions.concurrent
import arrow.fx.rx2.extensions.fx
import arrow.fx.rx2.extensions.observablek.applicative.applicative
import arrow.fx.rx2.extensions.observablek.async.async
import arrow.fx.rx2.extensions.observablek.functor.functor
import arrow.fx.rx2.extensions.observablek.monad.flatMap
import arrow.fx.rx2.extensions.observablek.monad.monad
import arrow.fx.rx2.extensions.observablek.timer.timer
import arrow.fx.rx2.fix
import arrow.fx.rx2.k
import arrow.fx.rx2.value
import arrow.fx.typeclasses.ExitCase
import arrow.test.generators.GenK
import arrow.test.generators.throwable
import arrow.test.laws.ConcurrentLaws
import arrow.test.laws.TimerLaws
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import io.kotlintest.properties.Gen
import io.kotlintest.shouldBe
import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeUnit.SECONDS

class ObservableKTests : RxJavaSpec() {

  init {
    testLaws(
      /*
      TODO: Traverse/Foldable instances are not lawful
       https://github.com/arrow-kt/arrow/issues/1882
            TraverseLaws.laws(ObservableK.traverse(), ObservableK.genk(), ObservableK.eqK()),
       */
      ConcurrentLaws.laws(
        ObservableK.concurrent(),
        ObservableK.functor(),
        ObservableK.applicative(),
        ObservableK.monad(),
        ObservableK.genk(),
        ObservableK.eqK(),
        testStackSafety = false
      ),
      TimerLaws.laws(ObservableK.async(), ObservableK.timer(), ObservableK.eq())

      /*
      TODO: MonadFilter instances are not lawsful
      https://github.com/arrow-kt/arrow/issues/1881

      MonadFilterLaws.laws(ObservableK.monadFilter(), ObservableK.functor(), ObservableK.applicative(), ObservableK.monad(), GENK(), EQK())
       */
    )

    "fx should defer evaluation until subscribed" {
      var run = false
      val value = ObservableK.fx {
        run = true
      }.value()

      run shouldBe false
      value.subscribe()
      run shouldBe true
    }

    "Multi-thread Observables finish correctly" {
      val value: Observable<Long> = ObservableK.fx {
        val a = Observable.timer(2, SECONDS).k().bind()
        a
      }.value()

      val test: TestObserver<Long> = value.test()
      test.awaitDone(5, SECONDS)
      test.assertTerminated().assertComplete().assertNoErrors().assertValue(0)
    }

    "Observable cancellation forces binding to cancel without completing too" {
      val value: Observable<Long> = ObservableK.fx {
        val a = Observable.timer(3, SECONDS).k().bind()
        a
      }.value()
      val test: TestObserver<Long> = value.doOnSubscribe { subscription -> Observable.timer(1, SECONDS).subscribe { subscription.dispose() } }.test()
      test.awaitTerminalEvent(5, SECONDS)

      test.assertNotTerminated().assertNotComplete().assertNoErrors().assertNoValues()
    }

    "ObservableK bracket cancellation should release resource with cancel exit status" {
      lateinit var ec: ExitCase<Throwable>
      val countDownLatch = CountDownLatch(1)

      ObservableK.just(Unit)
        .bracketCase(
          use = { ObservableK.async<Nothing> { } },
          release = { _, exitCase ->
            ObservableK {
              ec = exitCase
              countDownLatch.countDown()
            }
          }
        )
        .value()
        .subscribe()
        .dispose()

      countDownLatch.await(100, TimeUnit.MILLISECONDS)
      ec shouldBe ExitCase.Canceled
    }

    "ObservableK.cancelable should cancel CancelToken on dispose" {
      Promise.uncancelable<ForObservableK, Unit>(ObservableK.async()).flatMap { latch ->
        ObservableK {
          ObservableK.cancelable<Unit> {
            latch.complete(Unit)
          }.observable.subscribe().dispose()
        }.flatMap { latch.get() }
      }.value()
        .test()
        .assertValue(Unit)
        .awaitTerminalEvent(100, TimeUnit.MILLISECONDS)
    }

    "ObservableK async should be cancellable" {
      Promise.uncancelable<ForObservableK, Unit>(ObservableK.async())
        .flatMap { latch ->
          ObservableK {
            ObservableK.async<Unit> { }
              .value()
              .doOnDispose { latch.complete(Unit).value().subscribe() }
              .subscribe()
              .dispose()
          }.flatMap { latch.get() }
        }.value()
        .test()
        .assertValue(Unit)
        .awaitTerminalEvent(100, TimeUnit.MILLISECONDS)
    }
  }
}

private fun <T> ObservableK.Companion.eq(): Eq<ObservableKOf<T>> = object : Eq<ObservableKOf<T>> {
  override fun ObservableKOf<T>.eqv(b: ObservableKOf<T>): Boolean {
    val res1 = Try { value().timeout(5, SECONDS).blockingFirst() }
    val res2 = Try { b.value().timeout(5, SECONDS).blockingFirst() }
    return res1.fold({ t1 ->
      res2.fold({ t2 ->
        (t1::class.java == t2::class.java)
      }, { false })
    }, { v1 ->
      res2.fold({ false }, {
        v1 == it
      })
    })
  }
}

private fun ObservableK.Companion.eqK() = object : EqK<ForObservableK> {
  override fun <A> Kind<ForObservableK, A>.eqK(other: Kind<ForObservableK, A>, EQ: Eq<A>): Boolean =
    (this.fix() to other.fix()).let {
      ObservableK.eq<A>().run {
        it.first.eqv(it.second)
      }
    }
}

private fun <A> Gen.Companion.observableK(gen: Gen<A>) =
  Gen.oneOf(
    Gen.constant(Observable.empty<A>()),

    Gen.throwable().map {
      Observable.error<A>(it)
    },

    Gen.list(gen).map {
      Observable.fromIterable(it)
    }).map { it.k() }

private fun ObservableK.Companion.genk() = object : GenK<ForObservableK> {
  override fun <A> genK(gen: Gen<A>): Gen<Kind<ForObservableK, A>> =
    Gen.observableK(gen) as Gen<Kind<ForObservableK, A>>
}
