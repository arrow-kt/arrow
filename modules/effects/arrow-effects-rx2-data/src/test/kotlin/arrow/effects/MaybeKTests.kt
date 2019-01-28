package arrow.effects

import arrow.effects.rx2.*
import arrow.effects.rx2.extensions.maybek.applicative.applicative
import arrow.effects.rx2.extensions.maybek.applicativeError.applicativeError
import arrow.effects.rx2.extensions.maybek.async.async
import arrow.effects.rx2.extensions.maybek.effect.effect
import arrow.effects.rx2.extensions.maybek.foldable.foldable
import arrow.effects.rx2.extensions.maybek.functor.functor
import arrow.effects.rx2.extensions.maybek.monad.flatMap
import arrow.effects.rx2.extensions.maybek.monad.monad
import arrow.effects.rx2.extensions.maybek.monadDefer.monadDefer
import arrow.effects.rx2.extensions.maybek.monadError.monadError
import arrow.effects.rx2.extensions.maybek.monadThrow.bindingCatch
import arrow.effects.typeclasses.ExitCase
import arrow.test.UnitSpec
import arrow.test.laws.*
import arrow.typeclasses.Eq
import io.kotlintest.runner.junit4.KotlinTestRunner
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.reactivex.Maybe
import io.reactivex.observers.TestObserver
import io.reactivex.schedulers.Schedulers
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(KotlinTestRunner::class)
class MaybeKTests : UnitSpec() {

  fun <T> EQ(): Eq<MaybeKOf<T>> = object : Eq<MaybeKOf<T>> {
    override fun MaybeKOf<T>.eqv(b: MaybeKOf<T>): Boolean =
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
      FunctorLaws.laws(MaybeK.functor(), { MaybeK.just(it) }, EQ()),
      ApplicativeLaws.laws(MaybeK.applicative(), EQ()),
      MonadLaws.laws(MaybeK.monad(), EQ()),
      FoldableLaws.laws(MaybeK.foldable(), { MaybeK.just(it) }, Eq.any()),
      MonadErrorLaws.laws(MaybeK.monadError(), EQ(), EQ(), EQ()),
      ApplicativeErrorLaws.laws(MaybeK.applicativeError(), EQ(), EQ(), EQ()),
      MonadDeferLaws.laws(MaybeK.monadDefer(), EQ(), EQ(), EQ(), testStackSafety = false),
      AsyncLaws.laws(MaybeK.async(), EQ(), EQ(), testStackSafety = false),
      AsyncLaws.laws(MaybeK.effect(), EQ(), EQ(), testStackSafety = false)
    )

    "Multi-thread Maybes finish correctly" {
      val value: Maybe<Long> = bindingCatch {
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

      val value: Maybe<Long> = bindingCatch {
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
      val value: Maybe<Long> = bindingCatch {
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
          use = { MaybeK.async<Nothing> { _, _ -> } },
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
          MaybeK.async<Unit> { conn, _ ->
            conn.push(latch.complete(Unit))
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
            MaybeK.async<Unit> { _, _ -> }
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
      MaybeK.async<Unit> { connection, _ ->
        connection.cancel().value().subscribe()
      }.value()
        .test()
        .assertError(ConnectionCancellationException)
    }

  }

}
