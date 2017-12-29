package arrow

import arrow.core.Id
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.data.EitherT
import arrow.data.NonEmptyList
import arrow.data.OptionT
import arrow.data.value
import arrow.instances.monad
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.forAll
import io.kotlintest.matchers.shouldNotBe
import org.junit.runner.RunWith
import arrow.test.UnitSpec
import arrow.test.laws.*
import arrow.typeclasses.*

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

        testLaws(
            MonadLaws.laws(OptionT.monad(NonEmptyList.monad()), Eq.any()),
            SemigroupKLaws.laws(
                OptionT.semigroupK(Id.monad()),
                OptionT.applicative(Id.monad()),
                EQ()),

            MonoidKLaws.laws(
                OptionT.monoidK(Id.monad()),
                OptionT.applicative(Id.monad()),
                EQ()),

            FunctorFilterLaws.laws(
                OptionT.functorFilter(),
                { OptionT(Id(Some(it))) },
                EQ()),

            TraverseFilterLaws.laws(
                OptionT.traverseFilter(),
                OptionT.applicative(Option.monad()),
                { OptionT(Option(Some(it))) },
                EQ(),
                EQ_NESTED())
        )

        "toLeft for Some should build a correct EitherT" {
            forAll { a: Int, b: String ->
                OptionT.fromOption<NonEmptyListHK, Int>(Some(a)).toLeft({ b }, NELM) == EitherT.left<NonEmptyListHK, Int, String>(a, applicative())
            }
        }

        "toLeft for None should build a correct EitherT" {
            forAll { a: Int, b: String ->
                OptionT.fromOption<NonEmptyListHK, Int>(None).toLeft({ b }, NELM) == EitherT.right<NonEmptyListHK, Int, String>(b, applicative())
            }
        }

        "toRight for Some should build a correct EitherT" {
            forAll { a: Int, b: String ->
                OptionT.fromOption<NonEmptyListHK, String>(Some(b)).toRight({ a }, NELM) == EitherT.right<NonEmptyListHK, Int, String>(b, applicative())
            }
        }

        "toRight for None should build a correct EitherT" {
            forAll { a: Int, b: String ->
                OptionT.fromOption<NonEmptyListHK, String>(None).toRight({ a }, NELM) == EitherT.left<NonEmptyListHK, Int, String>(a, applicative())
            }
        }

    }
}
