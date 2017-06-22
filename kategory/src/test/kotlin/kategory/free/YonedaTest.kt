package kategory

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.forAll
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class YonedaTest : UnitSpec() {

    init {
        "map should modify the content of any HK1" {
            forAll { x: Int ->
                val op = Yoneda.apply(Id(x))
                val mapped = op.map({ _ -> true }, functor<Id.F>()).lower()
                val expected = Id(true)

                expected == mapped
            }
        }

        "instance map should be consistent with YonedaFunctor#map" {
            forAll { x: Int ->
                val op = Yoneda.apply(Id(x))
                val mapped = op.map({ _ -> true }, functor()).lower()
                val expected = YonedaFunctor<Id.F>(functor()).map(op, { _ -> true}).ev().lower()

                expected == mapped
            }
        }

        "toCoYoneda should convert to an equivalent CoYoneda" {
            forAll { x: Int ->
                val op = Yoneda.apply(Id(x.toString()))
                val toYoneda = op.toCoYoneda().lower(functor<Id.F>()).ev()
                val expected = CoYoneda.apply(Id(x), Int::toString).lower(functor<Id.F>()).ev()

                expected == toYoneda
            }
        }
    }
}
