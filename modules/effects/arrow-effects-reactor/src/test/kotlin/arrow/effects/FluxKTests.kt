package arrow.effects

import arrow.effecs.*
import arrow.test.UnitSpec
import arrow.test.laws.AsyncLaws
import arrow.test.laws.FoldableLaws
import arrow.test.laws.TraverseLaws
import arrow.typeclasses.*
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldNotBe
import org.junit.runner.RunWith
import reactor.core.publisher.Flux
import reactor.test.test
import java.time.Duration

@RunWith(KTestJUnitRunner::class)
class FluxKTest : UnitSpec() {

    fun <T> EQ(): Eq<FluxKOf<T>> = object : Eq<FluxKOf<T>> {
        override fun eqv(a: FluxKOf<T>, b: FluxKOf<T>): Boolean =
                try {
                    a.value().blockFirst() == b.value().blockFirst()
                } catch (throwable: Throwable) {
                    val errA = try {
                        a.value().blockFirst()
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

        "instances can be resolved implicitly" {
            functor<ForFluxK>() shouldNotBe null
            applicative<ForFluxK>() shouldNotBe null
            monad<ForFluxK>() shouldNotBe null
            applicativeError<ForFluxK, Unit>() shouldNotBe null
            monadError<ForFluxK, Unit>() shouldNotBe null
            monadSuspend<ForFluxK>() shouldNotBe null
            async<ForFluxK>() shouldNotBe null
            effect<ForFluxK>() shouldNotBe null
            foldable<ForFluxK>() shouldNotBe null
            traverse<ForFluxK>() shouldNotBe null
        }

        testLaws(
                AsyncLaws.laws(FluxK.async(), EQ(), EQ()),
                FoldableLaws.laws(FluxK.foldable(), { FluxK.pure(it) }, Eq.any()),
                TraverseLaws.laws(FluxK.traverse(), FluxK.functor(), { FluxK.pure(it) }, EQ())
        )

        "Multi-thread Fluxes finish correctly" {
            val value: Flux<Int> = FluxK.monadErrorFlat().bindingCatch {
                val a = Flux.just(0).delayElements(Duration.ofSeconds(2)).k().bind()
                yields(a)
            }.value()

            value.test()
                    .expectNext(0)
                    .verifyComplete()
        }

        "Flux cancellation forces binding to cancel without completing too" {
            val value = FluxK.monadErrorFlat().bindingCatch {
                val a = Flux.just(0).delayElements(Duration.ofSeconds(3)).k().bind()
                a
            }.value()

            val test = value.doOnSubscribe { subscription -> Flux.just(0).delayElements(Duration.ofSeconds(1)).subscribe { subscription.cancel() } }.test()
            test
                    .expectNextCount(0)
                    .verifyComplete()
        }

    }

}