package arrow.fx

import arrow.Kind
import arrow.core.left
import arrow.fx.reaktive.ForSingleK
import arrow.fx.reaktive.SingleK
import arrow.fx.reaktive.SingleKOf
import arrow.fx.reaktive.extensions.concurrent
import arrow.fx.reaktive.extensions.fx
import arrow.fx.reaktive.extensions.singlek.applicative.applicative
import arrow.fx.reaktive.extensions.singlek.applicativeError.attempt
import arrow.fx.reaktive.extensions.singlek.async.async
import arrow.fx.reaktive.extensions.singlek.functor.functor
import arrow.fx.reaktive.extensions.singlek.monad.flatMap
import arrow.fx.reaktive.extensions.singlek.monad.monad
import arrow.fx.reaktive.extensions.singlek.timer.timer
import arrow.fx.reaktive.fix
import arrow.fx.reaktive.k
import arrow.fx.reaktive.unsafeRunSync
import arrow.fx.reaktive.value
import arrow.fx.typeclasses.ExitCase
import arrow.test.generators.GenK
import arrow.test.generators.throwable
import arrow.test.laws.ConcurrentLaws
import arrow.test.laws.TimerLaws
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import com.badoo.reaktive.scheduler.computationScheduler
import com.badoo.reaktive.scheduler.newThreadScheduler
import com.badoo.reaktive.single.Single
import com.badoo.reaktive.single.blockingGet
import com.badoo.reaktive.single.doOnBeforeDispose
import com.badoo.reaktive.single.doOnBeforeSubscribe
import com.badoo.reaktive.single.doOnBeforeSuccess
import com.badoo.reaktive.single.observeOn
import com.badoo.reaktive.single.singleOf
import com.badoo.reaktive.single.singleOfError
import com.badoo.reaktive.single.singleTimer
import com.badoo.reaktive.single.subscribe
import com.badoo.reaktive.single.timeout
import com.badoo.reaktive.test.base.assertDisposed
import com.badoo.reaktive.test.base.assertNotError
import com.badoo.reaktive.test.single.TestSingleObserver
import com.badoo.reaktive.test.single.assertNotSuccess
import com.badoo.reaktive.test.single.assertSuccess
import com.badoo.reaktive.test.single.test
import io.kotlintest.properties.Gen
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class SingleKTests : ReaktiveSpec() {

  init {
    testLaws(
      ConcurrentLaws.laws(
        SingleK.concurrent(),
        SingleK.functor(),
        SingleK.applicative(),
        SingleK.monad(),
        SingleK.genK(),
        SingleK.eqK(),
        testStackSafety = false
      ),
      TimerLaws.laws(SingleK.async(), SingleK.timer(), SingleK.eq())
    )

    "Multi-thread Singles finish correctly" {
      val value: Single<Long> = SingleK.fx {
        val a = singleTimer(TimeUnit.SECONDS.toMillis(2), computationScheduler).k().bind()
        a
      }.value()

      val test: TestSingleObserver<Long> = value.test()
      test.awaitDone(5, TimeUnit.SECONDS)
      test.assertSuccess(2000L)
    }

    "Multi-thread Singles should run on their required threads" {
      val originalThread: Thread = Thread.currentThread()
      var threadRef: Thread? = null

      val value: Single<Long> = SingleK.fx {
        val a = singleTimer(TimeUnit.SECONDS.toMillis(2), newThreadScheduler).k().bind()
        threadRef = Thread.currentThread()
        val b = singleOf(a).observeOn(newThreadScheduler).k().bind()
        b
      }.value()

      var lastThread: Thread? = null
      val test: TestSingleObserver<Long> = value.doOnBeforeSuccess { lastThread = Thread.currentThread() }.test()
      test.awaitDone(5, TimeUnit.SECONDS)
      val nextThread = (threadRef?.name ?: "")

      nextThread shouldNotBe originalThread.name
      lastThread!!.name shouldNotBe originalThread.name
      lastThread!!.name shouldNotBe nextThread
    }

    "Single dispose forces binding to cancel without completing too" {
      val value: Single<Long> = SingleK.fx {
        val a = singleTimer(TimeUnit.SECONDS.toMillis(3), computationScheduler).k().bind()
        a
      }.value()

      val test: TestSingleObserver<Long> = value.doOnBeforeSubscribe { disposable ->
        singleTimer(TimeUnit.SECONDS.toMillis(1), computationScheduler).subscribe {
          disposable.dispose()
        }
      }.test()

      test.awaitTerminated(5, TimeUnit.SECONDS)
      test.assertDisposed()
      test.assertNotSuccess()
      test.assertNotError()
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
        .assertSuccess(Unit)
    }

    "SingleK async should be cancellable" {
      Promise.uncancelable<ForSingleK, Unit>(SingleK.async())
        .flatMap { latch ->
          SingleK {
            SingleK.async<Unit> { }
              .value()
              .doOnBeforeDispose { latch.complete(Unit).value().subscribe() }
              .subscribe()
              .dispose()
          }.flatMap { latch.get() }
        }.value()
        .test()
        .assertSuccess(Unit)
    }

    "SingleK should suspend" {
      SingleK.fx {
        val s = effect { singleOf(1).k().suspended() }.bind()

        s shouldBe 1
      }.unsafeRunSync()
    }

    "Error SingleK should suspend" {
      val error = IllegalArgumentException()

      SingleK.fx {
        val s = effect { singleOfError<Int>(error).k().suspended() }.attempt().bind()

        s shouldBe error.left()
      }.unsafeRunSync()
    }
  }
}

private fun SingleK.Companion.genK() = object : GenK<ForSingleK> {
  override fun <A> genK(gen: Gen<A>): Gen<Kind<ForSingleK, A>> =
    Gen.oneOf(
      gen.map { singleOf(it) },
      Gen.throwable().map { singleOfError<A>(it) }
    ).map {
      it.k()
    }
}

private fun <T> SingleK.Companion.eq(): Eq<SingleKOf<T>> = object : Eq<SingleKOf<T>> {
  override fun SingleKOf<T>.eqv(b: SingleKOf<T>): Boolean {
    val res1 = attempt().value().timeout(TimeUnit.SECONDS.toMillis(5L), computationScheduler).blockingGet()
    val res2 = b.attempt().value().timeout(TimeUnit.SECONDS.toMillis(5), computationScheduler).blockingGet()
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

private fun SingleK.Companion.eqK() = object : EqK<ForSingleK> {
  override fun <A> Kind<ForSingleK, A>.eqK(other: Kind<ForSingleK, A>, EQ: Eq<A>): Boolean =
    (this.fix() to other.fix()).let {
      SingleK.eq<A>().run {
        it.first.eqv(it.second)
      }
    }
}
