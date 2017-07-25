package kategory

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.forAll
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class CoproductTest : UnitSpec() {
    init {

        val coproductIdIdApplicative = object : Applicative<CoproductFG<Id.F, Id.F>> {
            val coproductIdIdFunctor = Coproduct.functor<Id.F, Id.F>()

            override fun <A> pure(a: A): HK<CoproductFG<Id.F, Id.F>, A> =
                    Coproduct(Either.Right(Id(a)))

            override fun <A, B> ap(fa: HK<CoproductFG<Id.F, Id.F>, A>, ff: HK<CoproductFG<Id.F, Id.F>, (A) -> B>): HK<CoproductFG<Id.F, Id.F>, B> =
                    throw IllegalStateException("This method should not be called")

            override fun <A, B> map(fa: HK<CoproductFG<Id.F, Id.F>, A>, f: (A) -> B): HK<CoproductFG<Id.F, Id.F>, B> =
                    coproductIdIdFunctor.map(fa, f)
        }

        testLaws(TraverseLaws.laws(Coproduct.traverse<Id.F, Id.F>(), coproductIdIdApplicative, { Coproduct(Either.Right(Id(it))) }, object : Eq<HK3<Coproduct.F, Id.F, Id.F, Int>>{
            override fun eqv(a: CoproductKind<Id.F, Id.F, Int>, b: CoproductKind<Id.F, Id.F, Int>): Boolean =
                    a.ev().extract() == b.ev().extract()
        } ))

        "CoproductComonad should comprehend with cobind" {
            forAll { num: Int ->
                val cobinding = CoproductComonad.any().cobinding {
                    val a = !Coproduct(Either.Right(Coproduct(Either.Right(Id(num.toString())))), Id, Coproduct.comonad<Id.F, Id.F>())
                    val parseA = Integer.parseInt(a)
                    val b = Coproduct<NonEmptyList.F, NonEmptyList.F, Int>(Either.Left(NonEmptyList.of(parseA * 2, parseA * 100))).extract()
                    extract { Coproduct<Id.F, Id.F, Int>(Either.Left(Id(b * 3))) }
                }
                cobinding == num * 6
            }
        }
    }
}
