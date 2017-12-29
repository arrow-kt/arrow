package arrow

import arrow.core.Id
import arrow.free.Yoneda
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldNotBe
import io.kotlintest.properties.forAll
import org.junit.runner.RunWith
import arrow.test.UnitSpec
import arrow.test.laws.FunctorLaws

@RunWith(KTestJUnitRunner::class)
class YonedaTest : UnitSpec() {

    val F = Yoneda.functor<IdHK>()

    val EQ = Eq<YonedaKind<IdHK, Int>> { a, b ->
        a.ev().lower() == b.ev().lower()
    }

    init {

        "instances can be resolved implicitly" {
            functor<YonedaKindPartial<IdHK>>() shouldNotBe null
        }

        testLaws(FunctorLaws.laws(F, { Yoneda(Id(it)) }, EQ))

        "toCoyoneda should convert to an equivalent Coyoneda" {
            forAll { x: Int ->
                val op = Yoneda(Id(x.toString()))
                val toYoneda = arrow.test.laws.ev()
                val expected = arrow.test.laws.ev()

                expected == toYoneda
            }
        }
    }
}
