package arrow

import arrow.core.Id
import arrow.data.Kleisli
import arrow.data.Try
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldNotBe
import org.junit.runner.RunWith
import arrow.test.UnitSpec
import arrow.test.laws.MonadErrorLaws

@RunWith(KTestJUnitRunner::class)
class KleisliTest : UnitSpec() {
    private fun <A> EQ(): Eq<KleisliKind<TryHK, Int, A>> = Eq { a, b ->
        a.ev().run(1) == b.ev().run(1)
    }

    init {

        "instances can be resolved implicitly" {
            functor<KleisliKindPartial<IdHK, Int>>() shouldNotBe null
            applicative<KleisliKindPartial<IdHK, Int>>() shouldNotBe null
            monad<KleisliKindPartial<IdHK, Int>>() shouldNotBe null
            monadReader<KleisliKindPartial<IdHK, Int>, Int>() shouldNotBe null
            monadError<KleisliKindPartial<EitherHK, Int>, Throwable>() shouldNotBe null
        }

        testLaws(MonadErrorLaws.laws(Kleisli.monadError<TryHK, Int, Throwable>(Try.monadError()), EQ(), EQ()))

        "andThen should continue sequence" {
            val kleisli: Kleisli<IdHK, Int, Int> = Kleisli({ a: Int -> Id(a) })

            arrow.test.laws.ev().value shouldBe 3

            arrow.test.laws.ev().value shouldBe 1
        }
    }
}
