package kategory

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldBe
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class KleisliTest : UnitSpec() {
    init {

        val me = Kleisli.monadError<Try.F, Int, Throwable>(Try)

        testLaws(MonadErrorLaws.laws(me, object : Eq<KleisliTKind<Try.F, Int, Int>> {
            override fun eqv(a: KleisliTKind<Try.F, Int, Int>, b: KleisliTKind<Try.F, Int, Int>): Boolean =
                a.ev().run(1) == b.ev().run(1)

        }))

        "andThen should continue sequence" {
            val kleisli: Kleisli<Id.F, Int, Int> = Kleisli({ a: Int -> Id(a) })

            kleisli.andThen(Id(3)).run(0).ev().value shouldBe 3

            kleisli.andThen { b -> Id(b + 1) }.run(0).ev().value shouldBe 1
        }
    }
}
