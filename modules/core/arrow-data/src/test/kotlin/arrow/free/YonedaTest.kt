package arrow.free

import arrow.core.ForId
import arrow.core.Id
import arrow.*.extract
import arrow.core.functor
import arrow.test.UnitSpec
import arrow.test.laws.FunctorLaws
import arrow.typeclasses.Eq
import arrow.typeclasses.functor
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldNotBe
import io.kotlintest.properties.forAll
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class YonedaTest : UnitSpec() {

    val F = Yoneda.functor<ForId>()

    val EQ = Eq<YonedaOf<ForId, Int>> { a, b ->
        a.extract().lower() == b.extract().lower()
    }

    init {

        "instances can be resolved implicitly" {
            functor<YonedaPartialOf<ForId>>() shouldNotBe null
        }

        testLaws(FunctorLaws.laws(F, { Yoneda(Id(it)) }, EQ))

        "toCoyoneda should convert to an equivalent Coyoneda" {
            forAll { x: Int ->
                val op = Yoneda(Id(x.toString()))
                val toYoneda = op.toCoyoneda().lower(Id.functor()).extract()
                val expected = Coyoneda(Id(x), Int::toString).lower(Id.functor()).extract()

                expected == toYoneda
            }
        }
    }
}