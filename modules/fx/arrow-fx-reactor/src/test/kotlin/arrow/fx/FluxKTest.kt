package arrow.fx

import arrow.fx.reactor.FluxK
import arrow.fx.reactor.FluxKOf
import arrow.fx.reactor.ForFluxK
import arrow.fx.reactor.extensions.fluxk.async.async
import arrow.fx.reactor.extensions.fluxk.foldable.foldable
import arrow.fx.reactor.extensions.fluxk.functor.functor
import arrow.fx.reactor.extensions.fluxk.monad.flatMap
import arrow.fx.reactor.extensions.fluxk.monadFilter.monadFilter
import arrow.fx.reactor.extensions.fluxk.timer.timer
import arrow.fx.reactor.extensions.fluxk.traverse.traverse
import arrow.fx.reactor.extensions.fx
import arrow.fx.reactor.k
import arrow.fx.reactor.value
import arrow.fx.typeclasses.ExitCase
import arrow.test.UnitSpec
import arrow.test.laws.AsyncLaws
import arrow.test.laws.FoldableLaws
import arrow.test.laws.MonadFilterLaws
import arrow.test.laws.TimerLaws
import arrow.test.laws.TraverseLaws
import arrow.typeclasses.Eq
import io.kotlintest.matchers.startWith
import io.kotlintest.shouldBe
import io.kotlintest.shouldNot
import io.kotlintest.shouldNotBe
import reactor.core.publisher.Flux
import reactor.core.scheduler.Schedulers
import reactor.test.expectError
import reactor.test.test
import java.time.Duration
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class FluxKTest : UnitSpec() {

  fun <T> assertThreadNot(flux: Flux<T>, name: String): Flux<T> =
    flux.doOnNext { Thread.currentThread().name shouldNot startWith(name) }

  fun <T> EQ(): Eq<FluxKOf<T>> = object : Eq<FluxKOf<T>> {
    override fun FluxKOf<T>.eqv(b: FluxKOf<T>): Boolean =
      try {
        this.value().blockFirst() == b.value().blockFirst()
      } catch (throwable: Throwable) {
        val errA = try {
          this.value().blockFirst()
          throw IllegalArgumentException()
        } catch (err: Throwable) {
          err
        }

        val errB = try {
          b.value().blockFirst()
          throw IllegalStateException()
        } catch (err: Throwable) {
          err
        }

        errA == errB
      }
  }

  init {

    testLaws(
      TimerLaws.laws(FluxK.async(), FluxK.timer(), EQ()),
      AsyncLaws.laws(FluxK.async(), EQ(), EQ(), testStackSafety = false),
      FoldableLaws.laws(FluxK.foldable(), { FluxK.just(it) }, Eq.any()),
      TraverseLaws.laws(FluxK.traverse(), FluxK.functor(), { FluxK.just(it) }, EQ()),
      MonadFilterLaws.laws(FluxK.monadFilter(), { Flux.just(it).k() }, EQ())
    )

    "fx should defer evaluation until subscribed" {
      var run = false
      val value = FluxK.fx {
        run = true
      }.value()

      run shouldBe false
      value.subscribe()
      run shouldBe true
    }

    "Multi-thread Fluxes finish correctly" {
      val value: Flux<Int> = FluxK.fx {
        val a = Flux.just(0).delayElements(Duration.ofSeconds(2)).k().bind()
        a
      }.value()

      value.test()
        .expectNext(0)
        .verifyComplete()
    }

    "Multi-thread Fluxes should run on their required threads" {
      val originalThread: Thread = Thread.currentThread()
      var threadRef: Thread? = null
      val value: Flux<Long> = FluxK.fx {
        val a = Flux.just(0L)
          .delayElements(Duration.ofSeconds(2), Schedulers.newSingle("newThread"))
          .k()
          .bind()
        threadRef = Thread.currentThread()
        val b = Flux.just(a)
          .subscribeOn(Schedulers.newSingle("anotherThread"))
          .k()
          .bind()
        b
      }.value()

      val nextThread = (threadRef?.name ?: "")

      value.test()
        .expectNextCount(1)
        .verifyComplete()
      nextThread shouldNotBe originalThread.name
      assertThreadNot(value, originalThread.name)
      assertThreadNot(value, nextThread)
    }

    "Flux cancellation forces binding to cancel without completing too" {
      val value: Flux<Long> = FluxK.fx {
        val a = Flux.just(0L).delayElements(Duration.ofSeconds(3)).k().bind()
        a
      }.value()

      val test = value.doOnSubscribe { subscription ->
        Flux.just(0L).delayElements(Duration.ofSeconds(1))
          .subscribe { subscription.cancel() }
      }.test()

      test
        .thenAwait(Duration.ofSeconds(5))
        .expectNextCount(0)
        .thenCancel()
        .verifyThenAssertThat()
        .hasNotDroppedElements()
        .hasNotDroppedErrors()
    }

    "FluxK bracket cancellation should release resource with cancel exit status" {
      lateinit var ec: ExitCase<Throwable>
      val countDownLatch = CountDownLatch(1)

      FluxK.just(Unit)
        .bracketCase(
          use = { FluxK.async<Nothing> { _, _ -> } },
          release = { _, exitCase ->
            FluxK {
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

    "FluxK should cancel KindConnection on dispose" {
      Promise.uncancelable<ForFluxK, Unit>(FluxK.async()).flatMap { latch ->
        FluxK {
          FluxK.async<Unit> { conn, _ ->
            conn.push(latch.complete(Unit))
          }.flux.subscribe().dispose()
        }.flatMap { latch.get() }
      }.value()
        .test()
        .expectNext(Unit)
        .expectComplete()
    }

    "FluxK async should be cancellable" {
      Promise.uncancelable<ForFluxK, Unit>(FluxK.async())
        .flatMap { latch ->
          FluxK {
            FluxK.async<Unit> { _, _ -> }
              .value()
              .doOnCancel { latch.complete(Unit).value().subscribe() }
              .subscribe()
              .dispose()
          }.flatMap { latch.get() }
        }.value()
        .test()
        .expectNext(Unit)
        .expectComplete()
    }

    "KindConnection can cancel upstream" {
      FluxK.async<Unit> { connection, _ ->
        connection.cancel().value().subscribe()
      }.value()
        .test()
        .expectError(ConnectionCancellationException::class)
    }
  }
}
