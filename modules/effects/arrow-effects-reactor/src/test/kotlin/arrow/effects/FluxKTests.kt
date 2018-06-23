package arrow.effects

import arrow.test.UnitSpec
import arrow.test.laws.AsyncLaws
import arrow.test.laws.FoldableLaws
import arrow.test.laws.TraverseLaws
import arrow.typeclasses.Eq
import arrow.typeclasses.binding
import arrow.typeclasses.bindingCatch
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldNotBe
import org.hamcrest.CoreMatchers.not
import org.hamcrest.CoreMatchers.startsWith
import org.hamcrest.MatcherAssert.assertThat
import org.junit.runner.RunWith
import reactor.core.publisher.Flux
import reactor.core.scheduler.Schedulers
import reactor.test.test
import java.time.Duration

@RunWith(KTestJUnitRunner::class)
class FluxKTest : UnitSpec() {

  fun <T> assertThreadNot(flux: Flux<T>, name: String): Flux<T> =
      flux.doOnNext { assertThat(Thread.currentThread().name, not(startsWith(name))) }

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
        AsyncLaws.laws(FluxK.async(), EQ(), EQ()),
        FoldableLaws.laws(FluxK.foldable(), { FluxK.just(it) }, Eq.any()),
        TraverseLaws.laws(FluxK.traverse(), FluxK.functor(), { FluxK.just(it) }, EQ())
    )

    "Multi-thread Fluxes finish correctly" {
      val value: Flux<Int> = FluxK.monadErrorFlat().bindingCatch {
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
      val value: Flux<Long> = FluxK.monadErrorFlat().bindingCatch {
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
      val value: Flux<Long> = FluxK.monadErrorFlat().binding {
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

  }

}