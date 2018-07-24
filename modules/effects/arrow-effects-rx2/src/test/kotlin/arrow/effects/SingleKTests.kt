package arrow.effects

import arrow.test.UnitSpec
import arrow.test.laws.*
import arrow.typeclasses.Eq
import arrow.typeclasses.bindingCatch
import io.kotlintest.runner.junit4.KotlinTestRunner
import io.kotlintest.shouldNotBe
import io.reactivex.Single
import io.reactivex.observers.TestObserver
import io.reactivex.schedulers.Schedulers
import org.junit.runner.RunWith
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
      MonadSuspendLaws.laws(SingleK.monadDefer(), EQ(), EQ(), EQ()),
      AsyncLaws.laws(SingleK.async(), EQ(), EQ(), EQ()),
      AsyncLaws.laws(SingleK.effect(), EQ(), EQ(), EQ())
    )

    "Multi-thread Singles finish correctly" {
      val value: Single<Long> = SingleK.monadError().bindingCatch {
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

      val value: Single<Long> = SingleK.monadError().bindingCatch {
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
      val value: Single<Long> = SingleK.monadError().bindingCatch {
        val a = Single.timer(3, TimeUnit.SECONDS).k().bind()
        a
      }.value()

      val test: TestObserver<Long> = value.doOnSubscribe { subscription ->
        Single.timer(1, TimeUnit.SECONDS).subscribe { a ->
          subscription.dispose()
        }
      }.test()

      test.awaitTerminalEvent(5, TimeUnit.SECONDS)
      test.assertNotTerminated().assertNotComplete().assertNoErrors().assertNoValues()
    }
  }
}
