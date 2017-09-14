package kategory

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.forAll
import io.kotlintest.matchers.shouldNotBe
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class OptionTTest : UnitSpec() {

    val EQ_ID: Eq<HK<OptionTKindPartial<IdHK>, Int>> = Eq { a, b ->
        a.value() == b.value()
    }

    val EQ_OPTION = object : Eq<HK<OptionTKindPartial<OptionHK>, Int>> {
        override fun eqv(a: HK<OptionTKindPartial<OptionHK>, Int>, b: HK<OptionTKindPartial<OptionHK>, Int>): Boolean =
                a.ev().value == b.ev().value
    }

    val EQ_NESTED_OPTION = object : Eq<HK<OptionTKindPartial<OptionHK>, HK<OptionTKindPartial<OptionHK>, Int>>> {
        override fun eqv(a: HK<OptionTKindPartial<OptionHK>, HK<OptionTKindPartial<OptionHK>, Int>>, b: HK<OptionTKindPartial<OptionHK>, HK<OptionTKindPartial<OptionHK>, Int>>): Boolean =
                a.ev().value == b.ev().value
    }

    val NELM: Monad<NonEmptyListHK> = monad<NonEmptyListHK>()

    init {

        "instances can be resolved implicitly" {
            functor<OptionTKindPartial<NonEmptyListHK>>() shouldNotBe null
            applicative<OptionTKindPartial<NonEmptyListHK>>() shouldNotBe null
            monad<OptionTKindPartial<NonEmptyListHK>>() shouldNotBe null
            foldable<OptionTKindPartial<NonEmptyListHK>>() shouldNotBe null
            traverse<OptionTKindPartial<NonEmptyListHK>>() shouldNotBe null
            semigroupK<OptionTKindPartial<ListKWHK>>() shouldNotBe null
            monoidK<OptionTKindPartial<ListKWHK>>() shouldNotBe null
            functorFilter<OptionTKindPartial<ListKWHK>>() shouldNotBe null
            traverseFilter<OptionTKindPartial<OptionHK>>() shouldNotBe null
        }

        testLaws(MonadLaws.laws(OptionT.monad(NonEmptyList.monad()), Eq.any()))
        testLaws(SemigroupKLaws.laws(
                OptionT.semigroupK(Id.monad()),
                OptionT.applicative(Id.monad()),
                EQ_ID))

        testLaws(MonoidKLaws.laws(
                OptionT.monoidK(Id.monad()),
                OptionT.applicative(Id.monad()),
                EQ_ID))

        testLaws(FunctorFilterLaws.laws(
                OptionT.functorFilter(),
                { OptionT(Id(it.some())) },
                EQ_ID))

        testLaws(TraverseFilterLaws.laws(
                OptionT.traverseFilter(),
                OptionT.applicative(Option.monad()),
                { OptionT(Option(it.some())) },
                EQ_OPTION,
                EQ_NESTED_OPTION))

        "toLeft for Some should build a correct EitherT" {
            forAll { a: Int, b: String ->
                OptionT.fromOption<NonEmptyListHK, Int>(Option.Some(a)).toLeft({ b }, NELM) == EitherT.left<NonEmptyListHK, Int, String>(a, applicative())
            }
        }

        "toLeft for None should build a correct EitherT" {
            forAll { a: Int, b: String ->
                OptionT.fromOption<NonEmptyListHK, Int>(Option.None).toLeft({ b }, NELM) == EitherT.right<NonEmptyListHK, Int, String>(b, applicative())
            }
        }

        "toRight for Some should build a correct EitherT" {
            forAll { a: Int, b: String ->
                OptionT.fromOption<NonEmptyListHK, String>(Option.Some(b)).toRight({ a }, NELM) == EitherT.right<NonEmptyListHK, Int, String>(b, applicative())
            }
        }

        "toRight for None should build a correct EitherT" {
            forAll { a: Int, b: String ->
                OptionT.fromOption<NonEmptyListHK, String>(Option.None).toRight({ a }, NELM) == EitherT.left<NonEmptyListHK, Int, String>(a, applicative())
            }
        }

    }
}
