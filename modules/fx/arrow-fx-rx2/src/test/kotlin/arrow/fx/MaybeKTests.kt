package arrow.fx

import arrow.fx.rx2.ForMaybeK
import arrow.fx.rx2.MaybeK
import arrow.fx.rx2.MaybeKOf
import arrow.fx.rx2.extensions.concurrent
import arrow.fx.rx2.extensions.fx
import arrow.fx.rx2.extensions.maybek.async.async
import arrow.fx.rx2.extensions.maybek.monad.flatMap
import arrow.fx.rx2.extensions.maybek.monadFilter.monadFilter
import arrow.fx.rx2.extensions.maybek.timer.timer
import arrow.fx.rx2.k
import arrow.fx.rx2.value
import arrow.fx.typeclasses.ExitCase
import arrow.test.laws.ConcurrentLaws
import arrow.test.laws.MonadFilterLaws
import arrow.test.laws.TimerLaws
import arrow.typeclasses.Eq
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.reactivex.Maybe
import io.reactivex.observers.TestObserver
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class MaybeKTests : RxJavaSpec() {

  fun <T> EQ(): Eq<MaybeKOf<T>> = object : Eq<MaybeKOf<T>> {
    override fun MaybeKOf<T>.eqv(b: MaybeKOf<T>): Boolean {
      val res1 = arrow.core.Try { value().timeout(5, TimeUnit.SECONDS).blockingGet() }
      val res2 = arrow.core.Try { b.value().timeout(5, TimeUnit.SECONDS).blockingGet() }
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
      TimerLaws.laws(MaybeK.async(), MaybeK.timer(), EQ()),
      ConcurrentLaws.laws(MaybeK.concurrent(), EQ(), EQ(), EQ(), testStackSafety = false),
      MonadFilterLaws.laws(MaybeK.monadFilter(), { Maybe.just(it).k() }, EQ())
    )

    "fx should defer evaluation until subscribed" {
      var run = false
      val value = MaybeK.fx {
        run = true
      }.value()

      run shouldBe false
      value.subscribe()
      run shouldBe true
    }

    "Multi-thread Maybes finish correctly" {
      val value: Maybe<Long> = MaybeK.fx {
        val a = Maybe.timer(2, TimeUnit.SECONDS).k().bind()
        a
      }.value()

      val test: TestObserver<Long> = value.test()
      test.awaitDone(5, TimeUnit.SECONDS)
      test.assertTerminated().assertComplete().assertNoErrors().assertValue(0)
    }

    "Multi-thread Maybes should run on their required threads" {
      val originalThread: Thread = Thread.currentThread()
      var threadRef: Thread? = null

      val value: Maybe<Long> = MaybeK.fx {
        val a = Maybe.timer(2, TimeUnit.SECONDS, Schedulers.newThread()).k().bind()
        threadRef = Thread.currentThread()
        val b = Maybe.just(a).observeOn(Schedulers.newThread()).k().bind()
        b
      }.value()

      val test: TestObserver<Long> = value.test()
      val lastThread: Thread = test.awaitDone(5, TimeUnit.SECONDS).lastThread()
      val nextThread = (threadRef?.name ?: "")

      nextThread shouldNotBe originalThread.name
      lastThread.name shouldNotBe originalThread.name
      lastThread.name shouldNotBe nextThread
    }

    "Maybe dispose forces binding to cancel without completing too" {
      val value: Maybe<Long> = MaybeK.fx {
        val a = Maybe.timer(3, TimeUnit.SECONDS).k().bind()
        a
      }.value()

      val test: TestObserver<Long> = value.doOnSubscribe { subscription ->
        Maybe.timer(1, TimeUnit.SECONDS).subscribe { _ ->
          subscription.dispose()
        }
      }.test()

      test.awaitTerminalEvent(5, TimeUnit.SECONDS)
      test.assertNotTerminated().assertNotComplete().assertNoErrors().assertNoValues()
    }

    "Folding over empty Maybe runs ifEmpty lambda" {
      val emptyMaybe = Maybe.empty<String>().k()
      val foldedToEmpty = emptyMaybe.fold({ true }, { false })
      foldedToEmpty shouldBe true
    }

    "Folding over non-empty Maybe runs ifSome lambda" {
      val maybe = Maybe.just(1).k()
      val foldedToSome = maybe.fold({ false }, { true })
      foldedToSome shouldBe true
    }

    "MaybeK bracket cancellation should release resource with cancel exit status" {
      lateinit var ec: ExitCase<Throwable>
      val countDownLatch = CountDownLatch(1)
      MaybeK.just(Unit)
        .bracketCase(
          use = { MaybeK.async<Nothing> { } },
          release = { _, exitCase ->
            MaybeK {
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

    "MaybeK should cancel KindConnection on dispose" {
      Promise.uncancelable<ForMaybeK, Unit>(MaybeK.async()).flatMap { latch ->
        MaybeK {
          MaybeK.cancelable<Unit> {
            latch.complete(Unit)
          }.maybe.subscribe().dispose()
        }.flatMap { latch.get() }
      }.value()
        .test()
        .assertValue(Unit)
        .awaitTerminalEvent(100, TimeUnit.MILLISECONDS)
    }

    "MaybeK async should be cancellable" {
      Promise.uncancelable<ForMaybeK, Unit>(MaybeK.async())
        .flatMap { latch ->
          MaybeK {
            MaybeK.async<Unit> { }
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
