package arrow.effects

import arrow.effects.flowablek.async.async
import arrow.effects.flowablek.foldable.foldable
import arrow.effects.flowablek.functor.functor
import arrow.effects.flowablek.monadThrow.bindingCatch
import arrow.effects.flowablek.traverse.traverse
import arrow.effects.typeclasses.ExitCase
import arrow.test.UnitSpec
import arrow.test.laws.AsyncLaws
import arrow.test.laws.FoldableLaws
import arrow.test.laws.TraverseLaws
import arrow.typeclasses.Eq
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldNotBe
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subscribers.TestSubscriber
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

@RunWith(KTestJUnitRunner::class)
class FlowableKTests : UnitSpec() {

  fun <T> EQ(): Eq<FlowableKOf<T>> = object : Eq<FlowableKOf<T>> {
    override fun FlowableKOf<T>.eqv(b: FlowableKOf<T>): Boolean =
      try {
        this.value().blockingFirst() == b.value().blockingFirst()
      } catch (throwable: Throwable) {
        val errA = try {
          this.value().blockingFirst()
          throw IllegalArgumentException()
        } catch (err: Throwable) {
          err
        }
        val errB = try {
          b.value().blockingFirst()
          throw IllegalStateException()
        } catch (err: Throwable) {
          err
        }
        errA == errB
      }

  }

  init {

    testLaws(AsyncLaws.laws(FlowableK.async(), EQ(), EQ()))
    // FIXME(paco) #691
    //testLaws(AsyncLaws.laws(FlowableK.async(), EQ(), EQ()))
    //testLaws(AsyncLaws.laws(FlowableK.async(), EQ(), EQ()))

    testLaws(AsyncLaws.laws(FlowableK.asyncDrop(), EQ(), EQ()))
    // FIXME(paco) #691
    //testLaws(AsyncLaws.laws(FlowableK.asyncDrop(), EQ(), EQ()))
    //testLaws(AsyncLaws.laws(FlowableK.asyncDrop(), EQ(), EQ()))

    testLaws(AsyncLaws.laws(FlowableK.asyncError(), EQ(), EQ()))
    // FIXME(paco) #691
    //testLaws(AsyncLaws.laws(FlowableK.asyncError(), EQ(), EQ()))
    //testLaws(AsyncLaws.laws(FlowableK.asyncError(), EQ(), EQ()))

    testLaws(AsyncLaws.laws(FlowableK.asyncLatest(), EQ(), EQ()))
    // FIXME(paco) #691
    //testLaws(AsyncLaws.laws(FlowableK.asyncLatest(), EQ(), EQ()))
    //testLaws(AsyncLaws.laws(FlowableK.asyncLatest(), EQ(), EQ()))

    testLaws(AsyncLaws.laws(FlowableK.asyncMissing(), EQ(), EQ()))
    // FIXME(paco) #691
    //testLaws(AsyncLaws.laws(FlowableK.asyncMissing(), EQ(), EQ()))
    //testLaws(AsyncLaws.laws(FlowableK.asyncMissing(), EQ(), EQ()))

    testLaws(
      FoldableLaws.laws(FlowableK.foldable(), { FlowableK.just(it) }, Eq.any()),
      TraverseLaws.laws(FlowableK.traverse(), FlowableK.functor(), { FlowableK.just(it) }, EQ())
    )

    "Multi-thread Flowables finish correctly" {
      val value: Flowable<Long> = bindingCatch {
        val a = Flowable.timer(2, TimeUnit.SECONDS).k().bind()
        a
      }.value()
      val test: TestSubscriber<Long> = value.test()
      test.awaitDone(5, TimeUnit.SECONDS)
      test.assertTerminated().assertComplete().assertNoErrors().assertValue(0)
    }

    "Multi-thread Observables should run on their required threads" {
      val originalThread: Thread = Thread.currentThread()
      var threadRef: Thread? = null
      val value: Flowable<Long> = bindingCatch {
        val a = Flowable.timer(2, TimeUnit.SECONDS, Schedulers.newThread()).k().bind()
        threadRef = Thread.currentThread()
        val b = Flowable.just(a).observeOn(Schedulers.newThread()).k().bind()
        b
      }.value()
      val test: TestSubscriber<Long> = value.test()
      val lastThread: Thread = test.awaitDone(5, TimeUnit.SECONDS).lastThread()
      val nextThread = (threadRef?.name ?: "")

      nextThread shouldNotBe originalThread.name
      lastThread.name shouldNotBe originalThread.name
      lastThread.name shouldNotBe nextThread
    }

    "Flowable cancellation forces binding to cancel without completing too" {
      val value: Flowable<Long> = bindingCatch {
        val a = Flowable.timer(3, TimeUnit.SECONDS).k().bind()
        a
      }.value()
      val test: TestSubscriber<Long> = value.doOnSubscribe { subscription ->
        Flowable.timer(1, TimeUnit.SECONDS).subscribe {
          subscription.cancel()
        }
      }.test()
      test.awaitTerminalEvent(5, TimeUnit.SECONDS)
      test.assertNotTerminated().assertNotComplete().assertNoErrors().assertNoValues()
    }

    "FlowableK bracket cancellation should release resource with cancel exit status" {
      lateinit var ec: ExitCase<Throwable>

      val flowable = Flowable.just(0L)
        .k()
        .bracketCase(
          use = { FlowableK.just(it) },
          release = { _, exitCase -> ec = exitCase; FlowableK.just(Unit) }
        )
        .value()
        .delay(3, TimeUnit.SECONDS)
        .doOnSubscribe { subscription ->
          Flowable.just(0L).delay(1, TimeUnit.SECONDS)
            .subscribe {
              subscription.cancel()
            }
        }

      flowable.test().await(5, TimeUnit.SECONDS)
      assertThat(ec, `is`(ExitCase.Cancelled as ExitCase<Throwable>))
    }
  }
}
