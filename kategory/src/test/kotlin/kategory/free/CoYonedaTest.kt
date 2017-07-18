package kategory

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.forAll
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class CoYonedaTest : UnitSpec() {

    init {
        "map should modify the content of any HK1" {
            forAll { x: Int ->
                val op = CoYoneda.apply(Id(x), { _ -> "" })
                val mapped = op.map { _ -> true }.lower(functor<Id.F>())
                val expected = Id(true)

                expected == mapped
            }
        }

        "instance map should be consistent with CoYonedaFunctor#map" {
            forAll { x: Int ->
                val op = CoYoneda.apply(Id(x), { _ -> "" })
                val mapped = op.map { _ -> true }.lower(functor<Id.F>())
                val expected = CoYoneda.functor<Id.F, Int>().map(op, { _ -> true}).ev().lower(functor<Id.F>())

                expected == mapped
            }
        }

        "toYoneda should convert to an equivalent Yoneda" {
            forAll { x: Int ->
                val op = CoYoneda.apply(Id(x), Int::toString)
                val toYoneda = op.toYoneda(functor<Id.F>()).lower().ev()
                val expected = Yoneda.apply(Id(x.toString())).lower().ev()

                expected == toYoneda
            }
        }
    }
}
