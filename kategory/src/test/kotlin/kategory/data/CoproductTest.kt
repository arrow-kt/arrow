package kategory

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.forAll
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class CoproductTest : UnitSpec() {
    init {

        val coproductIdIdApplicative = object : Applicative<CoproductKindPartial<IdHK, IdHK>> {
            val coproductIdIdFunctor = Coproduct.functor<IdHK, IdHK>()

            override fun <A> pure(a: A): HK<CoproductKindPartial<IdHK, IdHK>, A> =
                    Coproduct(Either.Right(Id(a)))

            override fun <A, B> ap(fa: HK<CoproductKindPartial<IdHK, IdHK>, A>, ff: HK<CoproductKindPartial<IdHK, IdHK>, (A) -> B>): HK<CoproductKindPartial<IdHK, IdHK>, B> =
                    throw IllegalStateException("This method should not be called")

            override fun <A, B> map(fa: HK<CoproductKindPartial<IdHK, IdHK>, A>, f: (A) -> B): HK<CoproductKindPartial<IdHK, IdHK>, B> =
                    coproductIdIdFunctor.map(fa, f)
        }

        testLaws(TraverseLaws.laws(Coproduct.traverse<IdHK, IdHK>(), coproductIdIdApplicative, { Coproduct(Either.Right(Id(it))) }, object : Eq<HK3<CoproductHK, IdHK, IdHK, Int>>{
            override fun eqv(a: CoproductKind<IdHK, IdHK, Int>, b: CoproductKind<IdHK, IdHK, Int>): Boolean =
                    a.ev().extract() == b.ev().extract()
        } ))

        "CoproductComonad should comprehend with cobind" {
            forAll { num: Int ->
                val cobinding = CoproductComonad.any().cobinding {
                    val a = !Coproduct(Either.Right(Coproduct(Either.Right(Id(num.toString())))), Id, Coproduct.comonad<IdHK, IdHK>())
                    val parseA = Integer.parseInt(a)
                    val b = Coproduct<NonEmptyListHK, NonEmptyListHK, Int>(Either.Left(NonEmptyList.of(parseA * 2, parseA * 100))).extract()
                    extract { Coproduct<IdHK, IdHK, Int>(Either.Left(Id(b * 3))) }
                }
                cobinding == num * 6
            }
        }
    }
}
