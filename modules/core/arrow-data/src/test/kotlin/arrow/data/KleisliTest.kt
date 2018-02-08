package arrow.data

import arrow.core.*
import arrow.mtl.monadReader
import arrow.test.UnitSpec
import arrow.test.laws.MonadErrorLaws
import arrow.typeclasses.*
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldBe
import io.kotlintest.matchers.shouldNotBe
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class KleisliTest : UnitSpec() {
    private fun <A> EQ(): Eq<KleisliOf<ForTry, Int, A>> = Eq { a, b ->
        a.reify().run(1) == b.reify().run(1)
    }

    init {

        "instances can be resolved implicitly" {
            functor<KleisliPartialOf<ForId, Int>>() shouldNotBe null
            applicative<KleisliPartialOf<ForId, Int>>() shouldNotBe null
            monad<KleisliPartialOf<ForId, Int>>() shouldNotBe null
            monadReader<KleisliPartialOf<ForId, Int>, Int>() shouldNotBe null
            applicativeError<KleisliPartialOf<ForEither, Int>, Throwable>() shouldNotBe null
            monadError<KleisliPartialOf<ForEither, Int>, Throwable>() shouldNotBe null
        }

        testLaws(MonadErrorLaws.laws(Kleisli.monadError<ForTry, Int, Throwable>(Try.monadError()), EQ(), EQ()))

        "andThen should continue sequence" {
            val kleisli: Kleisli<ForId, Int, Int> = Kleisli({ a: Int -> Id(a) })

            kleisli.andThen(Id(3), Id.monad()).run(0).reify().value shouldBe 3

            kleisli.andThen({ b -> Id(b + 1) }, Id.monad()).run(0).reify().value shouldBe 1
        }
    }
}
