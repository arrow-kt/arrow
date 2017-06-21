package kategory

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldBe
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class KleisliTest : UnitSpec() {
    init {
        "andThen should continue sequence" {
            val kleisli: Kleisli<Id.F, Int, Int> = Kleisli({ a: Int -> Id(a) })

            kleisli.andThen(Id(3)).run(0).ev().value shouldBe 3

            kleisli.andThen { b -> Id(b + 1) }.run(0).ev().value shouldBe 1
        }
    }
}
