package arrow.fx

import arrow.Kind
import arrow.fx.reactor.FluxK
import arrow.fx.reactor.FluxKOf
import arrow.fx.reactor.ForFluxK
import arrow.fx.reactor.extensions.fluxk.applicative.applicative
import arrow.fx.reactor.extensions.fluxk.async.async
import arrow.fx.reactor.extensions.fluxk.functor.functor
import arrow.fx.reactor.extensions.fluxk.monad.flatMap
import arrow.fx.reactor.extensions.fluxk.monad.monad
import arrow.fx.reactor.extensions.fluxk.timer.timer
import arrow.fx.reactor.extensions.fx
import arrow.fx.reactor.fix
import arrow.fx.reactor.k
import arrow.fx.reactor.value
import arrow.fx.typeclasses.ExitCase
import arrow.test.UnitSpec
import arrow.test.generators.GenK
import arrow.test.generators.throwable
import arrow.test.laws.AsyncLaws
import arrow.test.laws.TimerLaws
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import io.kotlintest.matchers.startWith
import io.kotlintest.properties.Gen
import io.kotlintest.shouldBe
import io.kotlintest.shouldNot
import io.kotlintest.shouldNotBe
import reactor.core.publisher.Flux
import reactor.core.scheduler.Schedulers
import reactor.test.test
import java.time.Duration
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class FluxKTest : UnitSpec() {

  fun <T> assertThreadNot(flux: Flux<T>, name: String): Flux<T> =
    flux.doOnNext { Thread.currentThread().name shouldNot startWith(name) }

  init {
    testLaws(
      TimerLaws.laws(FluxK.async(), FluxK.timer(), FluxK.eq()),
      AsyncLaws.laws(
        FluxK.async(),
        FluxK.functor(),
        FluxK.applicative(),
        FluxK.monad(),
        FluxK.genk(),
        FluxK.eqK(),
        testStackSafety = false
      )
      /*
       TODO: Traverse/Foldable instances are not lawful
       https://github.com/arrow-kt/arrow/issues/1882

      TraverseLaws.laws(FluxK.traverse(), FluxK.genk(), FluxK.eqK()),
       */
      /*
        TODO: MonadFilter instances are not lawsful
      https://github.com/arrow-kt/arrow/issues/1881

      MonadFilterLaws.laws(
        FluxK.monadFilter(),
        FluxK.functor(),
        FluxK.applicative(),
        FluxK.monad(),
        FluxK.genk(),
        FluxK.eqK()
      )
       */
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
          use = { FluxK.async<Nothing> { } },
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

    "FluxK async should be cancellable" {
      Promise.uncancelable<ForFluxK, Unit>(FluxK.async())
        .flatMap { latch ->
          FluxK {
            FluxK.async<Unit> { }
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
  }
}

private fun <T> FluxK.Companion.eq(): Eq<FluxKOf<T>> = object : Eq<FluxKOf<T>> {
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

private fun FluxK.Companion.genk() = object : GenK<ForFluxK> {
  override fun <A> genK(gen: Gen<A>): Gen<Kind<ForFluxK, A>> =
    Gen.oneOf(
      Gen.constant(Flux.empty<A>()),

      Gen.list(gen).map { Flux.fromIterable(it) },

      Gen.throwable().map { Flux.error<A>(it) }
    ).map { it.k() }
}

private fun FluxK.Companion.eqK(): EqK<ForFluxK> = object : EqK<ForFluxK> {
  override fun <A> Kind<ForFluxK, A>.eqK(other: Kind<ForFluxK, A>, EQ: Eq<A>): Boolean =
    (this.fix() to other.fix()).let {
      FluxK.eq<A>().run {
        it.first.eqv(it.second)
      }
    }
}
