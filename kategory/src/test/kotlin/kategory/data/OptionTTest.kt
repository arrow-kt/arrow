package kategory

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.forAll
import kategory.laws.FunctorFilterLaws
import kategory.laws.TraverseFilterLaws
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class OptionTTest : UnitSpec() {
    init {

        val OptionTFIdEq = object : Eq<HK<OptionTKindPartial<IdHK>, Int>> {
            override fun eqv(a: HK<OptionTKindPartial<IdHK>, Int>, b: HK<OptionTKindPartial<IdHK>, Int>): Boolean =
                    a.ev().value == b.ev().value
        }

        testLaws(MonadLaws.laws(OptionT.monad(NonEmptyList.monad()), Eq.any()))
        testLaws(SemigroupKLaws.laws(
                OptionT.semigroupK(Id.monad()),
                OptionT.applicative(Id.monad()),
                OptionTFIdEq))

        testLaws(MonoidKLaws.laws(
                OptionT.monoidK(Id.applicative()),
                OptionT.applicative(Id.applicative()),
                OptionTFIdEq))

        testLaws(FunctorFilterLaws.laws(
                OptionT.functorFilter(),
                { OptionT(Id(it.some())) },
                OptionTFIdEq))

        testLaws(TraverseFilterLaws.laws(
                OptionT.traverseFilter(),
                OptionT.applicative(),
                { OptionT(Option(it.some())) },
                Eq.any()))

        "toLeft for Some should build a correct EitherT" {
            forAll { a: Int, b: String ->
                OptionT.fromOption<NonEmptyListHK, Int>(Option.Some(a)).toLeft { b } == EitherT.left<NonEmptyListHK, Int, String>(a)
            }
        }

        "toLeft for None should build a correct EitherT" {
            forAll { a: Int, b: String ->
                OptionT.fromOption<NonEmptyListHK, Int>(Option.None).toLeft { b } == EitherT.right<NonEmptyListHK, Int, String>(b)
            }
        }

        "toRight for Some should build a correct EitherT" {
            forAll { a: Int, b: String ->
                OptionT.fromOption<NonEmptyListHK, String>(Option.Some(b)).toRight { a } == EitherT.right<NonEmptyListHK, Int, String>(b)
            }
        }

        "toRight for None should build a correct EitherT" {
            forAll { a: Int, b: String ->
                OptionT.fromOption<NonEmptyListHK, String>(Option.None).toRight { a } == EitherT.left<NonEmptyListHK, Int, String>(a)
            }
        }

    }
}
