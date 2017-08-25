package kategory

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.forAll
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class YonedaTest : UnitSpec() {

    val F = Yoneda.functor(Id.functor())

    val EQ = object : Eq<YonedaKind<IdHK, Int>> {
        override fun eqv(a: YonedaKind<IdHK, Int>, b: YonedaKind<IdHK, Int>): Boolean =
                a.ev().lower() == b.ev().lower()
    }

    init {
        testLaws(FunctorLaws.laws(F, { Yoneda.apply(kategory.Id(it)) }, EQ))

        "toCoyoneda should convert to an equivalent Coyoneda" {
            forAll { x: Int ->
                val op = Yoneda.apply(Id(x.toString()))
                val toYoneda = op.toCoyoneda().lower(Id.functor()).ev()
                val expected = Coyoneda.apply(Id(x), Int::toString).lower(Id.functor()).ev()

                expected == toYoneda
            }
        }
    }
}
