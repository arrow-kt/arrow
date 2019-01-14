package arrow.effects

import arrow.effects.rx2.*
import arrow.effects.rx2.extensions.asyncDrop
import arrow.effects.rx2.extensions.asyncError
import arrow.effects.rx2.extensions.asyncLatest
import arrow.effects.rx2.extensions.asyncMissing
import arrow.effects.rx2.extensions.flowablek.async.async
import arrow.effects.rx2.extensions.flowablek.functor.functor
import arrow.effects.rx2.extensions.flowablek.monad.flatMap
import arrow.effects.rx2.extensions.flowablek.monadThrow.bindingCatch
import arrow.effects.rx2.extensions.flowablek.traverse.traverse
import arrow.effects.typeclasses.ExitCase
import arrow.test.UnitSpec
import arrow.test.laws.AsyncLaws
import arrow.test.laws.TraverseLaws
import arrow.typeclasses.Eq
import io.kotlintest.runner.junit4.KotlinTestRunner
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subscribers.TestSubscriber
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(KotlinTestRunner::class)
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

    testLaws(AsyncLaws.laws(FlowableK.async(), EQ(), EQ(), testStackSafety = false))
    // FIXME(paco) #691
    //testLaws(AsyncLaws.laws(FlowableK.async(), EQ(), EQ()))
    //testLaws(AsyncLaws.laws(FlowableK.async(), EQ(), EQ()))

    testLaws(AsyncLaws.laws(FlowableK.asyncDrop(), EQ(), EQ(), testStackSafety = false))
    // FIXME(paco) #691
    //testLaws(AsyncLaws.laws(FlowableK.asyncDrop(), EQ(), EQ()))
    //testLaws(AsyncLaws.laws(FlowableK.asyncDrop(), EQ(), EQ()))

    testLaws(AsyncLaws.laws(FlowableK.asyncError(), EQ(), EQ(), testStackSafety = false))
    // FIXME(paco) #691
    //testLaws(AsyncLaws.laws(FlowableK.asyncError(), EQ(), EQ()))
    //testLaws(AsyncLaws.laws(FlowableK.asyncError(), EQ(), EQ()))

    testLaws(AsyncLaws.laws(FlowableK.asyncLatest(), EQ(), EQ(), testStackSafety = false))
    // FIXME(paco) #691
    //testLaws(AsyncLaws.laws(FlowableK.asyncLatest(), EQ(), EQ()))
    //testLaws(AsyncLaws.laws(FlowableK.asyncLatest(), EQ(), EQ()))

    testLaws(AsyncLaws.laws(FlowableK.asyncMissing(), EQ(), EQ(), testStackSafety = false))
    // FIXME(paco) #691
    //testLaws(AsyncLaws.laws(FlowableK.asyncMissing(), EQ(), EQ()))
    //testLaws(AsyncLaws.laws(FlowableK.asyncMissing(), EQ(), EQ()))

    testLaws(TraverseLaws.laws(FlowableK.traverse(), FlowableK.functor(), { FlowableK.just(it) }, EQ()))

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
      val countDownLatch = CountDownLatch(1)

      FlowableK.just(Unit)
        .bracketCase(
          use = { FlowableK.async<Nothing>({ _, _ -> }) },
          release = { _, exitCase ->
            FlowableK {
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

    "FlowableK should cancel KindConnection on dispose" {
      Promise.uncancelable<ForFlowableK, Unit>(FlowableK.async()).flatMap { latch ->
        FlowableK {
          FlowableK.async<Unit>(fa = { conn, _ ->
            conn.push(latch.complete(Unit))
          }).flowable.subscribe().dispose()
        }.flatMap { latch.get() }
      }.value()
        .test()
        .assertValue(Unit)
        .awaitTerminalEvent(100, TimeUnit.MILLISECONDS)
    }

    "FlowableK async should be cancellable" {
      Promise.uncancelable<ForFlowableK, Unit>(FlowableK.async())
        .flatMap { latch ->
          FlowableK {
            FlowableK.async<Unit>(fa = { _, _ -> })
              .value()
              .doOnCancel { latch.complete(Unit).value().subscribe() }
              .subscribe()
              .dispose()
          }.flatMap { latch.get() }
        }.value()
    }

    "KindConnection can cancel upstream" {
      FlowableK.async<Unit>(fa = { connection, _ ->
        connection.cancel().value().subscribe()
      }).value()
        .test()
        .assertError(ConnectionCancellationException)
    }

  }
}
