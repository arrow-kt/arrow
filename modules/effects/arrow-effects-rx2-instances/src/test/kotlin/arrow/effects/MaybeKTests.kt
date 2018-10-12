package arrow.effects

import arrow.effects.maybek.applicative.applicative
import arrow.effects.maybek.applicativeError.applicativeError
import arrow.effects.maybek.async.async
import arrow.effects.maybek.effect.effect
import arrow.effects.maybek.foldable.foldable
import arrow.effects.maybek.functor.functor
import arrow.effects.maybek.monad.monad
import arrow.effects.maybek.monadDefer.monadDefer
import arrow.effects.maybek.monadError.monadError
import arrow.effects.maybek.monadThrow.bindingCatch
import arrow.test.UnitSpec
import arrow.test.laws.*
import arrow.typeclasses.Eq
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldBe
import io.kotlintest.matchers.shouldNotBe
import io.reactivex.Maybe
import io.reactivex.observers.TestObserver
import io.reactivex.schedulers.Schedulers
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

@RunWith(KTestJUnitRunner::class)
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
      MonadDeferLaws.laws(MaybeK.monadDefer(), EQ(), EQ()),
      AsyncLaws.laws(MaybeK.async(), EQ(), EQ(), EQ()),
      AsyncLaws.laws(MaybeK.effect(), EQ(), EQ(), EQ())
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
  }
}
