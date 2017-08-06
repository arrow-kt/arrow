package kategory

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.forAll
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class YonedaTest : UnitSpec() {

    val F = Yoneda.functor(Id)

    val AP = object : Applicative<YonedaF<Id.F>> {
        override fun <A> pure(a: A): HK<YonedaF<Id.F>, A> =
                Yoneda.apply(Id(a))

        override fun <A, B> map(fa: HK<YonedaF<Id.F>, A>, f: (A) -> B): HK<YonedaF<Id.F>, B> =
                F.map(fa, f)

        override fun <A, B> ap(fa: HK<YonedaF<Id.F>, A>, ff: HK<YonedaF<Id.F>, (A) -> B>): HK<YonedaF<Id.F>, B> =
                throw IllegalStateException("Operation not allowed")
    }

    val EQ = object : Eq<YonedaKind<Id.F, Int>> {
        override fun eqv(a: YonedaKind<Id.F, Int>, b: YonedaKind<Id.F, Int>): Boolean =
                a.ev().lower() == b.ev().lower()
    }

    init {
        testLaws(FunctorLaws.laws(AP, EQ))

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
