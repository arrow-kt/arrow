package arrow

import arrow.core.Id
import arrow.core.Option
import arrow.core.Some
import arrow.free.Coyoneda
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldBe
import io.kotlintest.matchers.shouldNotBe
import io.kotlintest.properties.forAll
import org.junit.runner.RunWith
import arrow.test.UnitSpec
import arrow.test.laws.FunctorLaws
import arrow.typeclasses.Eq
import arrow.typeclasses.functor

@RunWith(KTestJUnitRunner::class)
class CoyonedaTest : UnitSpec() {
    val EQ: Eq<CoyonedaKind<IdHK, Int, Int>> = Eq { a, b ->
        a.ev().lower(Id.functor()) == b.ev().lower(Id.functor())
    }

    init {

        "instances can be resolved implicitly" {
            functor<CoyonedaKindPartial<IdHK, Int>>() shouldNotBe null
        }

        testLaws(FunctorLaws.laws(Coyoneda.functor(), { Coyoneda(Id(0), { it }) }, EQ))

        "map should be stack-safe" {
            val loops = 10000

            tailrec fun loop(n: Int, acc: Coyoneda<OptionHK, Int, Int>): Coyoneda<OptionHK, Int, Int> =
                    if (n <= 0) acc
                    else loop(n - 1, acc.map { it + 1 })

            val result = loop(loops, Coyoneda(Some(0), { it })).lower(Option.functor())
            val expected = Some(loops)

            expected shouldBe result
        }

        "toYoneda should convert to an equivalent Yoneda" {
            forAll { x: Int ->
                val op = Coyoneda(Id(x), Int::toString)
                val toYoneda = arrow.test.laws.ev()
                val expected = arrow.test.laws.ev()

                expected == toYoneda
            }
        }
    }
}
