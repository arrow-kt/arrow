package arrow.fx

import arrow.core.Try
import arrow.fx.rx2.ForSingleK
import arrow.fx.rx2.SingleK
import arrow.fx.rx2.SingleKOf
import arrow.fx.rx2.extensions.concurrent
import arrow.fx.rx2.extensions.fx
import arrow.fx.rx2.extensions.singlek.async.async
import arrow.fx.rx2.extensions.singlek.monad.flatMap
import arrow.fx.rx2.extensions.singlek.timer.timer
import arrow.fx.rx2.k
import arrow.fx.rx2.value
import arrow.fx.typeclasses.Dispatchers
import arrow.fx.typeclasses.ExitCase
import arrow.test.UnitSpec
import arrow.test.laws.ConcurrentLaws
import arrow.test.laws.TimerLaws
import arrow.typeclasses.Eq
import io.kotlintest.runner.junit4.KotlinTestRunner
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.reactivex.Single
import io.reactivex.observers.TestObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.rx2.asCoroutineDispatcher
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext

@RunWith(KotlinTestRunner::class)
class SingleKTests : UnitSpec() {

  fun <T> EQ(): Eq<SingleKOf<T>> = object : Eq<SingleKOf<T>> {
    override fun SingleKOf<T>.eqv(b: SingleKOf<T>): Boolean {
      val res1 = Try { value().timeout(5, TimeUnit.SECONDS).blockingGet() }
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

  val CS = SingleK.concurrent(object : Dispatchers<ForSingleK> {
    override fun default(): CoroutineContext = Schedulers.io().asCoroutineDispatcher()
  })

  init {
    testLaws(
      ConcurrentLaws.laws(CS, EQ(), EQ(), EQ(), testStackSafety = false),
      TimerLaws.laws(SingleK.async(), SingleK.timer(), EQ())
    )

    "Multi-thread Singles finish correctly" {
      val value: Single<Long> = SingleK.fx {
        val a = Single.timer(2, TimeUnit.SECONDS).k().bind()
        a
      }.value()

      val test: TestObserver<Long> = value.test()
      test.awaitDone(5, TimeUnit.SECONDS)
      test.assertTerminated().assertComplete().assertNoErrors().assertValue(0)
    }

    "Multi-thread Singles should run on their required threads" {
      val originalThread: Thread = Thread.currentThread()
      var threadRef: Thread? = null

      val value: Single<Long> = SingleK.fx {
        val a = Single.timer(2, TimeUnit.SECONDS, Schedulers.newThread()).k().bind()
        threadRef = Thread.currentThread()
        val b = Single.just(a).observeOn(Schedulers.newThread()).k().bind()
        b
      }.value()

      val test: TestObserver<Long> = value.test()
      val lastThread: Thread = test.awaitDone(5, TimeUnit.SECONDS).lastThread()
      val nextThread = (threadRef?.name ?: "")

      nextThread shouldNotBe originalThread.name
      lastThread.name shouldNotBe originalThread.name
      lastThread.name shouldNotBe nextThread
    }

    "Single dispose forces binding to cancel without completing too" {
      val value: Single<Long> = SingleK.fx {
        val a = Single.timer(3, TimeUnit.SECONDS).k().bind()
        a
      }.value()

      val test: TestObserver<Long> = value.doOnSubscribe { subscription ->
        Single.timer(1, TimeUnit.SECONDS).subscribe { _ ->
          subscription.dispose()
        }
      }.test()

      test.awaitTerminalEvent(5, TimeUnit.SECONDS)
      test.assertNotTerminated().assertNotComplete().assertNoErrors().assertNoValues()
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
