package arrow.effects

import arrow.effects.monok.async.async
import arrow.effects.monok.monadThrow.bindingCatch
import arrow.effects.typeclasses.ExitCase
import arrow.test.UnitSpec
import arrow.test.laws.AsyncLaws
import arrow.typeclasses.Eq
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.Spec
import io.kotlintest.matchers.shouldNotBe
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.hamcrest.CoreMatchers.startsWith
import org.hamcrest.MatcherAssert.assertThat
import org.junit.runner.RunWith
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import reactor.test.test
import java.time.Duration

@RunWith(KTestJUnitRunner::class)
class MonoKTest : UnitSpec() {

  fun <T> assertThreadNot(mono: Mono<T>, name: String): Mono<T> =
    mono.doOnNext { assertThat(Thread.currentThread().name, not(startsWith(name))) }

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

  override fun interceptSpec(context: Spec, spec: () -> Unit) {
    println("MonoK: Skipping sync laws for stack safety because they are not supported. See https://github.com/reactor/reactor-core/issues/1441")
    super.interceptSpec(context, spec)
  }

  init {
    testLaws(
      AsyncLaws.laws(MonoK.async(), EQ(), EQ(), testStackSafety = false)
    )

    "Multi-thread Singles finish correctly" {
      val value: Mono<Long> = bindingCatch {
        val a = Mono.just(0L).delayElement(Duration.ofSeconds(2)).k().bind()
        a
      }.value()

      value.test()
        .expectNext(0)
        .verifyComplete()
    }

    "Multi-thread Fluxes should run on their required threads" {
      val originalThread = Thread.currentThread()
      var threadRef: Thread? = null
      val value: Mono<Long> = bindingCatch {
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


    "Single dispose forces binding to cancel without completing too" {
      val value: Mono<Long> = bindingCatch {
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

    "Mono bracket cancellation should release resource with cancel exit status" {
      lateinit var ec: ExitCase<Throwable>

      val mono = Mono.just(0L)
        .k()
        .bracketCase(
          use = { MonoK.just(it) },
          release = { _, exitCase -> ec = exitCase; MonoK.just(Unit) }
        )
        .value()
        .delayElement(Duration.ofSeconds(3))

      val test = mono.doOnSubscribe { subscription ->
        Mono.just(0L).delayElement(Duration.ofSeconds(1))
          .subscribe { subscription.cancel() }
      }.test()

      test
        .thenAwait(Duration.ofSeconds(5))
        .then { assertThat(ec, `is`(ExitCase.Cancelled as ExitCase<Throwable>)) }
        .thenCancel()
        .verify()
    }
  }
}
