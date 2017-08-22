package kategory

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldBe
import io.kotlintest.properties.forAll
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class CoyonedaTest : UnitSpec() {
    val F = Coyoneda.functor<IdHK, Int>()

    val EQ = object : Eq<CoyonedaKind<IdHK, Int, Int>> {
        override fun eqv(a: CoyonedaKind<IdHK, Int, Int>, b: CoyonedaKind<IdHK, Int, Int>): Boolean =
                a.ev().lower(Id) == a.ev().lower(Id)

    }

    init {

        testLaws(FunctorLaws.laws(Coyoneda.functor(), { Coyoneda.apply(Id(0), { it }) }, EQ))

        "map should modify the content of any HK1" {
            forAll { x: Int ->
                val op = Coyoneda.apply(Id(x), { _ -> "" })
                val mapped = op.map { _ -> true }.lower(Id)
                val expected = Id(true)

                expected == mapped
            }
        }

        "instance map should be consistent with CoyonedaFunctor#map" {
            forAll { x: Int ->
                val op = Coyoneda.apply(Id(x), { _ -> "" })
                val mapped = op.map { _ -> true }.lower(Id)
                val expected = Coyoneda.functor<IdHK, Int>().map(op, { _ -> true }).ev().lower(Id)

                expected == mapped
            }
        }

        "map should retain function application ordering" {
            forAll { x: Int ->
                val op = Coyoneda.apply(Id(x), { it })
                val mapped = op.map { it + 1 }.map { it * 3 }.lower(Id).ev()
                val expected = Id((x + 1) * 3)

                expected == mapped
            }
        }

        "map should be stack-safe" {
            val loops = 10000

            tailrec fun loop(n: Int, acc: Coyoneda<OptionHK, Int, Int>): Coyoneda<OptionHK, Int, Int> =
                    if (n <= 0) acc
                    else loop(n - 1, acc.map { it + 1 })

            val result = loop(loops, Coyoneda.apply(Option.Some(0), { it })).lower(Option.functor())
            val expected = Option.Some(loops)

            expected shouldBe result
        }

        "toYoneda should convert to an equivalent Yoneda" {
            forAll { x: Int ->
                val op = Coyoneda.apply(Id(x), Int::toString)
                val toYoneda = op.toYoneda(Id).lower().ev()
                val expected = Yoneda.apply(Id(x.toString())).lower().ev()

                expected == toYoneda
            }
        }
    }
}
