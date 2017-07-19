package kategory

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.forAll
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class CoproductTest : UnitSpec() {
    init {

        "CoproductComonad should comprehend with cobind" {
            forAll { num: Int ->
                val cobinding = CoproductComonad.any().cobinding {
                    val a = !Coproduct(Either.Right(Coproduct(Either.Right(Id(num.toString())))), Id, Coproduct.comonad<Id.F, Id.F>())
                    val parseA = Integer.parseInt(a)
                    val b = Coproduct<NonEmptyList.F, NonEmptyList.F, Int>(Either.Left(NonEmptyList.of(parseA * 2, parseA * 100))).extract()
                    val c = extract { Coproduct<Id.F, Id.F, Int>(Either.Left(Id(b * 3))) }
                    yields(c)
                }

                Option(1).map { it + 2 } == Option(3)
            }
        }
    }
}
