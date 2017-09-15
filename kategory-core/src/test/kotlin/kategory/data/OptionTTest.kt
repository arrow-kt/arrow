package kategory

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.forAll
import io.kotlintest.matchers.shouldNotBe
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class OptionTTest : UnitSpec() {

    fun <A> EQ(): Eq<HK<OptionTKindPartial<A>, Int>> = Eq { a, b ->
        a.value() == b.value()
    }

    fun <A> EQ_NESTED(): Eq<HK<OptionTKindPartial<A>, HK<OptionTKindPartial<A>, Int>>> = Eq { a, b ->
        a.value() == b.value()
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
                EQ<IdHK>()))

        testLaws(MonoidKLaws.laws(
                OptionT.monoidK(Id.monad()),
                OptionT.applicative(Id.monad()),
                EQ<IdHK>()))

        testLaws(FunctorFilterLaws.laws(
                OptionT.functorFilter(),
                { OptionT(Id(it.some())) },
                EQ<IdHK>()))

        testLaws(TraverseFilterLaws.laws(
                OptionT.traverseFilter(),
                OptionT.applicative(Option.monad()),
                { OptionT(Option(it.some())) },
                EQ<OptionHK>(),
                EQ_NESTED<OptionHK>()))

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
