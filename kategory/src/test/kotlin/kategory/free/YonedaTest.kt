package kategory

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.forAll
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class YonedaTest : UnitSpec() {

    val F = Yoneda.functor(Id)

    val EQ = object : Eq<YonedaKind<IdHK, Int>> {
        override fun eqv(a: YonedaKind<IdHK, Int>, b: YonedaKind<IdHK, Int>): Boolean =
                a.ev().lower() == b.ev().lower()
    }

    init {
        testLaws(FunctorLaws.laws(F, { Yoneda.apply(kategory.Id(it)) }, EQ))

        "map should modify the content of any HK1" {
            forAll { x: Int ->
                val op = Yoneda.apply(Id(x))
                val mapped = op.map({ _ -> true }, Id).lower()
                val expected = Id(true)

                expected == mapped
            }
        }

        "instance map should be consistent with YonedaFunctor#map" {
            forAll { x: Int ->
                val op = Yoneda.apply(Id(x))
                val mapped = op.map({ _ -> true }, Id).lower()
                val expected = Yoneda.functor(Id).map(op, { _ -> true }).ev().lower()

                expected == mapped
            }
        }

        "toCoYoneda should convert to an equivalent CoYoneda" {
            forAll { x: Int ->
                val op = Yoneda.apply(Id(x.toString()))
                val toYoneda = op.toCoYoneda().lower(Id).ev()
                val expected = CoYoneda.apply(Id(x), Int::toString).lower(Id).ev()

                expected == toYoneda
            }
        }
    }
}
