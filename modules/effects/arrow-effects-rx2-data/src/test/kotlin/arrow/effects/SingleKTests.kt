package arrow.effects

import arrow.effects.rx2.*
import arrow.effects.rx2.extensions.singlek.applicative.applicative
import arrow.effects.rx2.extensions.singlek.applicativeError.applicativeError
import arrow.effects.rx2.extensions.singlek.async.async
import arrow.effects.rx2.extensions.singlek.effect.effect
import arrow.effects.rx2.extensions.singlek.functor.functor
import arrow.effects.rx2.extensions.singlek.monad.flatMap
import arrow.effects.rx2.extensions.singlek.monad.monad
import arrow.effects.rx2.extensions.singlek.monadError.monadError
import arrow.effects.rx2.extensions.singlek.monadThrow.bindingCatch
import arrow.effects.typeclasses.ExitCase
import arrow.test.UnitSpec
import arrow.test.laws.*
import arrow.typeclasses.Eq
import io.kotlintest.runner.junit4.KotlinTestRunner
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.reactivex.Single
import io.reactivex.observers.TestObserver
import io.reactivex.schedulers.Schedulers
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(KotlinTestRunner::class)
class SingleKTests : UnitSpec() {

  fun <T> EQ(): Eq<SingleKOf<T>> = object : Eq<SingleKOf<T>> {
    override fun SingleKOf<T>.eqv(b: SingleKOf<T>): Boolean =
      try {
        this.value().blockingGet() == b.value().blockingGet()
      } catch (throwable: Throwable) {
        val errA = try {
          this.value().blockingGet()
          throw IllegalArgumentException()
        } catch (err: Throwable) {
          err
        }
        val errB = try {
          b.value().blockingGet()
          throw IllegalStateException()
        } catch (err: Throwable) {
          err
        }
        errA == errB
      }

  }

  init {
    testLaws(
      FunctorLaws.laws(SingleK.functor(), { SingleK.just(it) }, EQ()),
      ApplicativeLaws.laws(SingleK.applicative(), EQ()),
      MonadLaws.laws(SingleK.monad(), EQ()),
      MonadErrorLaws.laws(SingleK.monadError(), EQ(), EQ(), EQ()),
      ApplicativeErrorLaws.laws(SingleK.applicativeError(), EQ(), EQ(), EQ()),
      AsyncLaws.laws(SingleK.async(), EQ(), EQ(), testStackSafety = false),
      AsyncLaws.laws(SingleK.effect(), EQ(), EQ(), testStackSafety = false)
    )

    "Multi-thread Singles finish correctly" {
      val value: Single<Long> = bindingCatch {
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

      val value: Single<Long> = bindingCatch {
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
      val value: Single<Long> = bindingCatch {
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
          use = { SingleK.async<Nothing> { _, _ -> } },
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
          SingleK.async<Unit> { conn, _ ->
            conn.push(latch.complete(Unit))
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
            SingleK.async<Unit> { _, _ -> }
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

    "KindConnection can cancel upstream" {
      SingleK.async<Unit> { connection, _ ->
        connection.cancel().value().subscribe()
      }.value()
        .test()
        .assertError(ConnectionCancellationException)
    }

  }

}
