package arrow.fx

import arrow.Kind
import arrow.core.left
import arrow.fx.reactor.ForMonoK
import arrow.fx.reactor.MonoK
import arrow.fx.reactor.MonoKOf
import arrow.fx.reactor.extensions.fx
import arrow.fx.reactor.extensions.monok.applicative.applicative
import arrow.fx.reactor.extensions.monok.async.async
import arrow.fx.reactor.extensions.monok.functor.functor
import arrow.fx.reactor.extensions.monok.monad.flatMap
import arrow.fx.reactor.extensions.monok.monad.monad
import arrow.fx.reactor.extensions.monok.timer.timer
import arrow.fx.reactor.fix
import arrow.fx.reactor.k
import arrow.fx.reactor.unsafeRunSync
import arrow.fx.reactor.value
import arrow.fx.typeclasses.ExitCase
import arrow.test.UnitSpec
import arrow.test.generators.GenK
import arrow.test.laws.AsyncLaws
import arrow.test.laws.TimerLaws
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import io.kotlintest.matchers.startWith
import io.kotlintest.properties.Gen
import io.kotlintest.shouldBe
import io.kotlintest.shouldNot
import io.kotlintest.shouldNotBe
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import reactor.test.expectError
import reactor.test.test
import java.time.Duration
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class MonoKTest : UnitSpec() {

  fun <T> assertThreadNot(mono: Mono<T>, name: String): Mono<T> =
    mono.doOnNext { Thread.currentThread().name shouldNot startWith(name) }

  fun <T> EQ(): Eq<MonoKOf<T>> = object : Eq<MonoKOf<T>> {
    override fun MonoKOf<T>.eqv(b: MonoKOf<T>): Boolean =
      try {
        this.value().block() == b.value().block()
      } catch (throwable: Throwable) {
        val errA = try {
          this.value().block()
          throw IllegalArgumentException()
        } catch (err: Throwable) {
          err
        }

        val errB = try {
          b.value().block()
          throw IllegalStateException()
        } catch (err: Throwable) {
          err
        }

        errA == errB
      }
  }

  fun EQK() = object : EqK<ForMonoK> {
    override fun <A> Kind<ForMonoK, A>.eqK(other: Kind<ForMonoK, A>, EQ: Eq<A>): Boolean =
      EQ<A>().run {
        this@eqK.fix().eqv(other.fix())
      }
  }

  init {
    testLaws(
      AsyncLaws.laws(MonoK.async(), MonoK.functor(), MonoK.applicative(), MonoK.monad(), MonoK.genK(), EQK(), testStackSafety = false),
      TimerLaws.laws(MonoK.async(), MonoK.timer(), EQK())
    )

    "Multi-thread Monos finish correctly" {
      val value: Mono<Long> = MonoK.fx {
        val a = Mono.just(0L).delayElement(Duration.ofSeconds(2)).k().bind()
        a
      }.value()

      value.test()
        .expectNext(0)
        .verifyComplete()
    }

    "Multi-thread Monos should run on their required threads" {
      val originalThread = Thread.currentThread()
      var threadRef: Thread? = null
      val value: Mono<Long> = MonoK.fx {
        val a = Mono.just(0L)
          .delayElement(Duration.ofSeconds(2), Schedulers.newSingle("newThread"))
          .k()
          .bind()
        threadRef = Thread.currentThread()
        val b = Mono.just(a)
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

    "Mono dispose forces binding to cancel without completing too" {
      val value: Mono<Long> = MonoK.fx {
        val a = Mono.just(0L).delayElement(Duration.ofSeconds(3)).k().bind()
        a
      }.value()

      val test = value.doOnSubscribe { subscription ->
        Mono.just(0L).delayElement(Duration.ofSeconds(1))
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

    "MonoK bracket cancellation should release resource with cancel exit status" {
      lateinit var ec: ExitCase<Throwable>
      val countDownLatch = CountDownLatch(1)

      MonoK.just(Unit)
        .bracketCase(
          use = { MonoK.async<Nothing> { _, _ -> } },
          release = { _, exitCase ->
            MonoK {
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

    "MonoK should cancel KindConnection on dispose" {
      Promise.uncancelable<ForMonoK, Unit>(MonoK.async()).flatMap { latch ->
        MonoK {
          MonoK.async<Unit> { conn, _ ->
            conn.push(latch.complete(Unit))
          }.mono.subscribe().dispose()
        }.flatMap { latch.get() }
      }.value()
        .test()
        .expectNext(Unit)
        .expectComplete()
    }

    "MonoK async should be cancellable" {
      Promise.uncancelable<ForMonoK, Unit>(MonoK.async())
        .flatMap { latch ->
          MonoK {
            MonoK.async<Unit> { _, _ -> }
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
      MonoK.async<Unit> { connection, _ ->
        connection.cancel().value().subscribe()
      }.value()
        .test()
        .expectError(ConnectionCancellationException::class)
    }

    "MonoK should suspend" {
      MonoK.fx {
        val s = effect { Mono.just(1).k().suspended()!! }.bind()

        s shouldBe 1
      }.unsafeRunSync()
    }

    "Error MonoK should suspend" {
      val error = IllegalArgumentException()

      MonoK.fx {
        val s = effect { Mono.error<Int>(error).k().suspended()!! }.attempt().bind()

        s shouldBe error.left()
      }.unsafeRunSync()
    }

    "Empty MonoK should suspend" {
      MonoK.fx {
        val s = effect { Mono.empty<Int>().k().suspended() }.bind()

        s shouldBe null
      }.unsafeRunSync()
    }
  }
}

fun MonoK.Companion.genK() = object : GenK<ForMonoK> {
  override fun <A> genK(gen: Gen<A>): Gen<Kind<ForMonoK, A>> =
    Gen.oneOf(
      gen.map {
        Mono.just(it)
      },
      Gen.constant(Mono.empty())
    ).map { it.k() }
}
