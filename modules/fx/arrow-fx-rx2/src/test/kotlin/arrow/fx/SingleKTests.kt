package arrow.fx

import arrow.fx.rx2.ForSingleK
import arrow.fx.rx2.SingleK
import arrow.fx.rx2.SingleKOf
import arrow.fx.rx2.extensions.concurrent
import arrow.fx.rx2.extensions.fx
import arrow.fx.rx2.extensions.singlek.applicativeError.attempt
import arrow.fx.rx2.extensions.singlek.async.async
import arrow.fx.rx2.extensions.singlek.monad.flatMap
import arrow.fx.rx2.extensions.singlek.timer.timer
import arrow.fx.rx2.k
import arrow.fx.rx2.value
import arrow.fx.typeclasses.ExitCase
import arrow.test.laws.ConcurrentLaws
import arrow.test.laws.TimerLaws
import arrow.test.laws.forFew
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen
import io.kotlintest.shouldBe
import io.reactivex.Single
import io.reactivex.observers.TestObserver
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class SingleKTests : RxJavaSpec() {

  private val awaitDelay = 300L

  fun <T> EQ(): Eq<SingleKOf<T>> = object : Eq<SingleKOf<T>> {
    override fun SingleKOf<T>.eqv(b: SingleKOf<T>): Boolean {
      val res1 = attempt().value().timeout(5, TimeUnit.SECONDS).blockingGet()
      val res2 = b.attempt().value().timeout(5, TimeUnit.SECONDS).blockingGet()
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

  init {
    testLaws(
      ConcurrentLaws.laws(SingleK.concurrent(), EQ(), EQ(), EQ(), testStackSafety = false),
      TimerLaws.laws(SingleK.async(), SingleK.timer(), EQ())
    )

    "fx should defer evaluation until subscribed" {
      var run = false
      val value = SingleK.fx {
        run = true
      }.value()

      run shouldBe false
      value.subscribe()
      run shouldBe true
    }

    "Multi-thread Singles finish correctly" {
      forFew(10, Gen.choose(10L, 50)) { delay ->
        SingleK.fx {
          val a = Single.timer(delay, TimeUnit.MILLISECONDS).k().bind()
          a
        }.value()
          .test()
          .awaitDone(delay + awaitDelay, TimeUnit.MILLISECONDS)
          .assertTerminated()
          .assertComplete()
          .assertNoErrors()
          .assertValue(0)
          .let { true }
      }
    }

    "Multi-thread Singles should run on their required threads" {
      forFew(10, Gen.choose(10L, 50)) { delay ->
        val originalThread: Thread = Thread.currentThread()
        var threadRef: Thread? = null

        val value: Single<Long> = SingleK.fx {
          val a = Single.timer(delay, TimeUnit.MILLISECONDS, Schedulers.io()).k().bind()
          threadRef = Thread.currentThread()
          val b = Single.just(a).observeOn(Schedulers.computation()).k().bind()
          b
        }.value()

        val test: TestObserver<Long> = value.test()
        val lastThread: Thread = test.awaitDone(delay + awaitDelay, TimeUnit.MILLISECONDS).lastThread()
        val nextThread = (threadRef?.name ?: "")

        nextThread != originalThread.name && lastThread.name != originalThread.name && lastThread.name != nextThread
      }
    }

    "Single dispose forces binding to cancel without completing too" {
      forFew(5, Gen.choose(10L, 50)) { delay ->
        val value: Single<Long> = SingleK.fx {
          val a = Single.timer(delay + awaitDelay, TimeUnit.MILLISECONDS).k().bind()
          a
        }.value()

        val test: TestObserver<Long> = value.doOnSubscribe { subscription ->
          Single.timer(delay, TimeUnit.MILLISECONDS).subscribe { _ ->
            subscription.dispose()
          }
        }.test()

        test.awaitTerminalEvent(delay + (2 * awaitDelay), TimeUnit.MILLISECONDS)
        test.assertNotTerminated()
          .assertNotComplete()
          .assertNoErrors()
          .assertNoValues()
          .let { true }
      }
    }

    "SingleK bracket cancellation should release resource with cancel exit status" {
      lateinit var ec: ExitCase<Throwable>
      val countDownLatch = CountDownLatch(1)

      SingleK.just(Unit)
        .bracketCase(
          use = { SingleK.async<Nothing> { } },
          release = { _, exitCase ->
            SingleK {
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

    "SingleK should cancel KindConnection on dispose" {
      Promise.uncancelable<ForSingleK, Unit>(SingleK.async()).flatMap { latch ->
        SingleK {
          SingleK.cancelable<Unit> {
            latch.complete(Unit)
          }.single.subscribe().dispose()
        }.flatMap { latch.get() }
      }.value()
        .test()
        .assertValue(Unit)
        .awaitTerminalEvent(100, TimeUnit.MILLISECONDS)
    }

    "SingleK async should be cancellable" {
      Promise.uncancelable<ForSingleK, Unit>(SingleK.async())
        .flatMap { latch ->
          SingleK {
            SingleK.async<Unit> { }
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
