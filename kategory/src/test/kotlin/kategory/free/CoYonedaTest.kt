package kategory

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldBe
import io.kotlintest.properties.forAll
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class CoYonedaTest : UnitSpec() {
    val F = CoYoneda.functor<Id.F, Int>()

    val AP: Applicative<CoYonedaF<Id.F, Int>> = object : Applicative<CoYonedaF<Id.F, Int>> {
        override fun <A> pure(a: A): CoYonedaKind<Id.F, Int, A> =
                CoYoneda.apply(Id(0), { a })

        override fun <A, B> map(fa: CoYonedaKind<Id.F, Int, A>, f: (A) -> B): CoYonedaKind<Id.F, Int, B> =
               F.map(fa, f)

        override fun <A, B> ap(fa: CoYonedaKind<Id.F, Int, A>, ff: CoYonedaKind<Id.F, Int, (A) -> B>): CoYonedaKind<Id.F, Int, B> =
                throw IllegalStateException("Operation not allowed")
    }

    val EQ = object : Eq<CoYonedaKind<Id.F, Int, Int>> {
        override fun eqv(a: CoYonedaKind<Id.F, Int, Int>, b: CoYonedaKind<Id.F, Int, Int>): Boolean =
                a.ev().lower(Id) == a.ev().lower(Id)

    }

    init {

        testLaws(FunctorLaws.laws(AP, EQ))

        "map should modify the content of any HK1" {
            forAll { x: Int ->
                val op = CoYoneda.apply(Id(x), { _ -> "" })
                val mapped = op.map { _ -> true }.lower(Id)
                val expected = Id(true)

                expected == mapped
            }
        }

        "instance map should be consistent with CoYonedaFunctor#map" {
            forAll { x: Int ->
                val op = CoYoneda.apply(Id(x), { _ -> "" })
                val mapped = op.map { _ -> true }.lower(Id)
                val expected = CoYoneda.functor<Id.F, Int>().map(op, { _ -> true }).ev().lower(Id)

                expected == mapped
            }
        }

        "map should retain function application ordering" {
            forAll { x: Int ->
                val op = CoYoneda.apply(Id(x), { it })
                val mapped = op.map { it + 1 }.map { it * 3 }.lower(Id).ev()
                val expected = Id((x + 1) * 3)

                expected == mapped
            }
        }

        "map should be stack-safe" {
            val loops = 5000

            fun loop(n: Int, acc: CoYoneda<Option.F, Int, Int>): CoYoneda<Option.F, Int, Int> =
                    if (n <= 0) acc
                    else loop(n - 1, acc.map { it + 1 })

            val result = loop(loops, CoYoneda.apply(Option.Some(0), { it })).lower(Option)
            val expected = Option.Some(loops)

            expected shouldBe result
        }

        "toYoneda should convert to an equivalent Yoneda" {
            forAll { x: Int ->
                val op = CoYoneda.apply(Id(x), Int::toString)
                val toYoneda = op.toYoneda(Id).lower().ev()
                val expected = Yoneda.apply(Id(x.toString())).lower().ev()

                expected == toYoneda
            }
        }
    }
}
