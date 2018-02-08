package arrow.free

import arrow.core.*
import arrow.test.UnitSpec
import arrow.test.laws.FunctorLaws
import arrow.typeclasses.Eq
import arrow.typeclasses.functor
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldBe
import io.kotlintest.matchers.shouldNotBe
import io.kotlintest.properties.forAll
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class CoyonedaTest : UnitSpec() {
    val EQ: Eq<CoyonedaOf<ForId, Int, Int>> = Eq { a, b ->
        a.extract().lower(Id.functor()) == b.extract().lower(Id.functor())
    }

    init {

        "instances can be resolved implicitly" {
            functor<CoyonedaPartialOf<ForId, Int>>() shouldNotBe null
        }

        testLaws(FunctorLaws.laws(Coyoneda.functor(), { Coyoneda(Id(0), { it }) }, EQ))

        "map should be stack-safe" {
            val loops = 10000

            tailrec fun loop(n: Int, acc: Coyoneda<ForOption, Int, Int>): Coyoneda<ForOption, Int, Int> =
                    if (n <= 0) acc
                    else loop(n - 1, acc.map { it + 1 })

            val result = loop(loops, Coyoneda(Some(0), { it })).lower(Option.functor())
            val expected = Some(loops)

            expected shouldBe result
        }

        "toYoneda should convert to an equivalent Yoneda" {
            forAll { x: Int ->
                val op = Coyoneda(Id(x), Int::toString)
                val toYoneda = op.toYoneda(Id.functor()).lower().extract()
                val expected = Yoneda(Id(x.toString())).lower().extract()

                expected == toYoneda
            }
        }
    }
}
