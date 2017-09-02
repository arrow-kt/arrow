package kategory

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldBe
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class KleisliTest : UnitSpec() {
    init {

        val me = Kleisli.monadError<TryHK, Int, Throwable>(Try.monadError())

        testLaws(MonadErrorLaws.laws(me, object : Eq<KleisliKind<TryHK, Int, Int>> {
            override fun eqv(a: KleisliKind<TryHK, Int, Int>, b: KleisliKind<TryHK, Int, Int>): Boolean =
                a.ev().run(1) == b.ev().run(1)

        }))

        "andThen should continue sequence" {
            val kleisli: Kleisli<IdHK, Int, Int> = Kleisli({ a: Int -> Id(a) })

            kleisli.andThen(Id(3)).run(0).ev().value shouldBe 3

            kleisli.andThen { b -> Id(b + 1) }.run(0).ev().value shouldBe 1
        }
    }
}
