package arrow.fx

import arrow.Kind
import arrow.core.left
import arrow.fx.reaktive.ForMaybeK
import arrow.fx.reaktive.MaybeK
import arrow.fx.reaktive.MaybeKOf
import arrow.fx.reaktive.extensions.concurrent
import arrow.fx.reaktive.extensions.fx
import arrow.fx.reaktive.extensions.maybek.applicative.applicative
import arrow.fx.reaktive.extensions.maybek.async.async
import arrow.fx.reaktive.extensions.maybek.functor.functor
import arrow.fx.reaktive.extensions.maybek.monad.flatMap
import arrow.fx.reaktive.extensions.maybek.monad.monad
import arrow.fx.reaktive.extensions.maybek.timer.timer
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
import com.badoo.reaktive.maybe.Maybe
import com.badoo.reaktive.maybe.blockingGet
import com.badoo.reaktive.maybe.doOnBeforeDispose
import com.badoo.reaktive.maybe.doOnBeforeSubscribe
import com.badoo.reaktive.maybe.doOnBeforeSuccess
import com.badoo.reaktive.maybe.maybeOf
import com.badoo.reaktive.maybe.maybeOfEmpty
import com.badoo.reaktive.maybe.maybeOfError
import com.badoo.reaktive.maybe.maybeTimer
import com.badoo.reaktive.maybe.observeOn
import com.badoo.reaktive.maybe.subscribe
import com.badoo.reaktive.maybe.timeout
import com.badoo.reaktive.scheduler.computationScheduler
import com.badoo.reaktive.scheduler.newThreadScheduler
import com.badoo.reaktive.test.base.assertDisposed
import com.badoo.reaktive.test.base.assertNotError
import com.badoo.reaktive.test.maybe.TestMaybeObserver
import com.badoo.reaktive.test.maybe.assertNotComplete
import com.badoo.reaktive.test.maybe.assertNotSuccess
import com.badoo.reaktive.test.maybe.assertSuccess
import com.badoo.reaktive.test.maybe.test
import io.kotlintest.properties.Gen
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class MaybeKTests : ReaktiveSpec() {

  init {
    testLaws(
      TimerLaws.laws(MaybeK.async(), MaybeK.timer(), MaybeK.eq()),

      ConcurrentLaws.laws(
        MaybeK.concurrent(),
        MaybeK.functor(),
        MaybeK.applicative(),
        MaybeK.monad(),
        MaybeK.genk(),
        MaybeK.eqK(),
        testStackSafety = false
      )

      /*
      TODO: MonadFilter instances are not lawsful
      https://github.com/arrow-kt/arrow/issues/1881

      MonadFilterLaws.laws(
        MaybeK.monadFilter(),
        MaybeK.functor(),
        MaybeK.applicative(),
        MaybeK.monad(),
        MaybeK.genk(),
        MaybeK.eqK()
      )
       */
    )

    "Multi-thread Maybes finish correctly" {
      val value: Maybe<Long> = MaybeK.fx {
        val a = maybeTimer(TimeUnit.SECONDS.toMillis(2), computationScheduler).k().bind()
        a
      }.value()

      val test: TestMaybeObserver<Long> = value.test()
      test.awaitDone(5, TimeUnit.SECONDS)
      test.assertSuccess(2000L)
    }

    "Multi-thread Maybes should run on their required threads" {
      val originalThread: Thread = Thread.currentThread()
      var threadRef: Thread? = null

      val value: Maybe<Long> = MaybeK.fx {
        val a = maybeTimer(TimeUnit.SECONDS.toMillis(2), newThreadScheduler).k().bind()
        threadRef = Thread.currentThread()
        val b = maybeOf(a).observeOn(newThreadScheduler).k().bind()
        b
      }.value()

      var lastThread: Thread? = null
      val test: TestMaybeObserver<Long> = value.doOnBeforeSuccess { lastThread = Thread.currentThread() }.test()
      test.awaitDone(5, TimeUnit.SECONDS)
      val nextThread = (threadRef?.name ?: "")

      nextThread shouldNotBe originalThread.name
      lastThread!!.name shouldNotBe originalThread.name
      lastThread!!.name shouldNotBe nextThread
    }

    "Maybe dispose forces binding to cancel without completing too" {
      val value: Maybe<Long> = MaybeK.fx {
        val a = maybeTimer(TimeUnit.SECONDS.toMillis(3), computationScheduler).k().bind()
        a
      }.value()

      val test: TestMaybeObserver<Long> = value.doOnBeforeSubscribe { disposable ->
        maybeTimer(TimeUnit.SECONDS.toMillis(1), computationScheduler).subscribe {
          disposable.dispose()
        }
      }.test()

      test.awaitTerminated(5, TimeUnit.SECONDS)
      test.assertDisposed()
      test.assertNotSuccess()
      test.assertNotComplete()
      test.assertNotError()
    }

    "Folding over empty Maybe runs ifEmpty lambda" {
      val emptyMaybe = maybeOfEmpty<String>().k()
      val foldedToEmpty = emptyMaybe.fold({ true }, { false })
      foldedToEmpty shouldBe true
    }

    "Folding over non-empty Maybe runs ifSome lambda" {
      val maybe = maybeOf(1).k()
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
        .assertSuccess(Unit)
    }

    "MaybeK async should be cancellable" {
      Promise.uncancelable<ForMaybeK, Unit>(MaybeK.async())
        .flatMap { latch ->
          MaybeK {
            MaybeK.async<Unit> { }
              .value()
              .doOnBeforeDispose { latch.complete(Unit).value().subscribe() }
              .subscribe()
              .dispose()
          }.flatMap { latch.get() }
        }.value()
        .test()
        .assertSuccess(Unit)
    }

    "MaybeK should suspend" {
      MaybeK.fx {
        val s = effect { maybeOf(1).k().suspended() }.bind()

        s shouldBe 1
      }.unsafeRunSync()
    }

    "Error MaybeK should suspend" {
      val error = IllegalArgumentException()

      MaybeK.fx {
        val s = effect { maybeOfError<Int>(error).k().suspended() }.attempt().bind()

        s shouldBe error.left()
      }.unsafeRunSync()
    }

    "Empty MaybeK should suspend" {
      MaybeK.fx {
        val s = effect { maybeOfEmpty<Int>().k().suspended() }.bind()

        s shouldBe null
      }.unsafeRunSync()
    }
  }
}

private fun <T> MaybeK.Companion.eq(): Eq<MaybeKOf<T>> = object : Eq<MaybeKOf<T>> {
  override fun MaybeKOf<T>.eqv(b: MaybeKOf<T>): Boolean {
    val res1 = arrow.core.Try { value().timeout(TimeUnit.SECONDS.toMillis(5), computationScheduler).blockingGet() }
    val res2 = arrow.core.Try { b.value().timeout(TimeUnit.SECONDS.toMillis(5), computationScheduler).blockingGet() }
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

private fun MaybeK.Companion.eqK() = object : EqK<ForMaybeK> {
  override fun <A> Kind<ForMaybeK, A>.eqK(other: Kind<ForMaybeK, A>, EQ: Eq<A>): Boolean =
    (this.fix() to other.fix()).let {
      MaybeK.eq<A>().run {
        it.first.eqv(it.second)
      }
    }
}

private fun MaybeK.Companion.genk() = object : GenK<ForMaybeK> {
  override fun <A> genK(gen: Gen<A>): Gen<Kind<ForMaybeK, A>> =
    Gen.oneOf(
      Gen.constant(maybeOfEmpty()),

      gen.map {
        maybeOf(it)
      },

      Gen.throwable().map {
        maybeOfError<A>(it)
      }
    ).map {
      it.k()
    }
}
