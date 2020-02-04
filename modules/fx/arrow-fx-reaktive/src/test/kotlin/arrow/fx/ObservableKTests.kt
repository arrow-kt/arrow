package arrow.fx

import arrow.Kind
import arrow.core.Try
import arrow.fx.reaktive.ForObservableK
import arrow.fx.reaktive.ObservableK
import arrow.fx.reaktive.ObservableKOf
import arrow.fx.reaktive.extensions.concurrent
import arrow.fx.reaktive.extensions.fx
import arrow.fx.reaktive.extensions.observablek.applicative.applicative
import arrow.fx.reaktive.extensions.observablek.async.async
import arrow.fx.reaktive.extensions.observablek.functor.functor
import arrow.fx.reaktive.extensions.observablek.monad.flatMap
import arrow.fx.reaktive.extensions.observablek.monad.monad
import arrow.fx.reaktive.extensions.observablek.timer.timer
import arrow.fx.reaktive.fix
import arrow.fx.reaktive.k
import arrow.fx.reaktive.value
import arrow.fx.typeclasses.ExitCase
import arrow.test.generators.GenK
import arrow.test.generators.throwable
import arrow.test.laws.ConcurrentLaws
import arrow.test.laws.TimerLaws
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import com.badoo.reaktive.maybe.blockingGet
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.asObservable
import com.badoo.reaktive.observable.doOnBeforeDispose
import com.badoo.reaktive.observable.doOnBeforeSubscribe
import com.badoo.reaktive.observable.firstOrComplete
import com.badoo.reaktive.observable.observableOfEmpty
import com.badoo.reaktive.observable.observableOfError
import com.badoo.reaktive.observable.observableTimer
import com.badoo.reaktive.observable.subscribe
import com.badoo.reaktive.observable.timeout
import com.badoo.reaktive.scheduler.computationScheduler
import com.badoo.reaktive.test.base.assertDisposed
import com.badoo.reaktive.test.base.assertNotError
import com.badoo.reaktive.test.observable.TestObservableObserver
import com.badoo.reaktive.test.observable.assertComplete
import com.badoo.reaktive.test.observable.assertNotComplete
import com.badoo.reaktive.test.observable.assertValue
import com.badoo.reaktive.test.observable.test
import io.kotlintest.properties.Gen
import io.kotlintest.shouldBe
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeUnit.SECONDS

class ObservableKTests : ReaktiveSpec() {

  init {
    testLaws(
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
    )

    "Multi-thread Observables finish correctly" {
      val value: Observable<Long> = ObservableK.fx {
        val a = observableTimer(SECONDS.toMillis(2L), computationScheduler).k().bind()
        a
      }.value()

      val test: TestObservableObserver<Long> = value.test()
      test.awaitDone(5, SECONDS)
      test.assertValue(2000L)
    }

    "Observable dispose forces binding to cancel without completing too" {
      val value: Observable<Long> = ObservableK.fx {
        val a = observableTimer(SECONDS.toMillis(3), computationScheduler).k().bind()
        a
      }.value()
      val test: TestObservableObserver<Long> = value.doOnBeforeSubscribe { disposable ->
        observableTimer(SECONDS.toMillis(1), computationScheduler).subscribe {
          disposable.dispose()
        }
      }.test()
      test.awaitTerminated(5, SECONDS)

      test.assertDisposed()
      test.assertNotComplete()
      test.assertNotError()
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

    "ObservableK should cancel KindConnection on dispose" {
      Promise.uncancelable<ForObservableK, Unit>(ObservableK.async()).flatMap { latch ->
        ObservableK {
          ObservableK.cancelable<Unit> {
            latch.complete(Unit)
          }.observable.subscribe().dispose()
        }.flatMap { latch.get() }
      }.value()
        .test().apply {
          assertValue(Unit)
          assertComplete()
        }
    }

    "ObservableK async should be cancellable" {
      Promise.uncancelable<ForObservableK, Unit>(ObservableK.async())
        .flatMap { latch ->
          ObservableK {
            ObservableK.async<Unit> { }
              .value()
              .doOnBeforeDispose { latch.complete(Unit).value().subscribe() }
              .subscribe()
              .dispose()
          }.flatMap { latch.get() }
        }.value()
        .test().apply {
          assertValue(Unit)
          assertComplete()
        }
    }
  }
}

private fun <T> ObservableK.Companion.eq(): Eq<ObservableKOf<T>> = object : Eq<ObservableKOf<T>> {
  override fun ObservableKOf<T>.eqv(b: ObservableKOf<T>): Boolean {
    val res1 = Try { value().timeout(SECONDS.toMillis(5), computationScheduler).firstOrComplete().blockingGet() }
    val res2 = Try { b.value().timeout(SECONDS.toMillis(5), computationScheduler).firstOrComplete().blockingGet() }
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

private fun ObservableK.Companion.genk() = object : GenK<ForObservableK> {
  override fun <A> genK(gen: Gen<A>): Gen<Kind<ForObservableK, A>> =
    Gen.oneOf(
      Gen.constant(observableOfEmpty()),
      Gen.throwable().map { observableOfError<A>(it) },
      Gen.list(gen).map { it.asObservable() }
    ).map { it.k() }
}
