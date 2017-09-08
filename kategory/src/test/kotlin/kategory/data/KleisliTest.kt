package kategory

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldBe
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class KleisliTest : UnitSpec() {
    private fun <A> EQ(): Eq<KleisliKind<TryHK, Int, A>> = Eq { a, b ->
        a.ev().run(1) == b.ev().run(1)
    }

    init {
        testLaws(MonadErrorLaws.laws(Kleisli.monadError<TryHK, Int, Throwable>(Try.monadError()), EQ(), EQ()))

        "andThen should continue sequence" {
            val kleisli: Kleisli<IdHK, Int, Int> = Kleisli({ a: Int -> Id(a) })

            kleisli.andThen(Id(3)).run(0).ev().value shouldBe 3

            kleisli.andThen { b -> Id(b + 1) }.run(0).ev().value shouldBe 1
        }
    }
}
