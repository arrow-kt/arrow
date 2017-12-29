package arrow.instances

import io.kotlintest.KTestJUnitRunner
import arrow.*
import arrow.core.Id
import arrow.core.Option
import arrow.core.Some
import arrow.data.*
import org.junit.runner.RunWith
import arrow.test.UnitSpec
import arrow.test.laws.*

typealias OptionTNel = HK<OptionTKindPartial<NonEmptyListHK>, Int>

@RunWith(KTestJUnitRunner::class)
class ComposedInstancesTest : UnitSpec() {
    val EQ_OPTION_NEL: Eq<NestedType<OptionHK, NonEmptyListHK, Int>> = Eq { a, b ->
        a.unnest().ev() == b.unnest().ev()
    }

    val EQ_LKW_OPTION: Eq<NestedType<ListKWHK, OptionHK, Int>> = Eq { a, b ->
        a.unnest().ev() == b.unnest().ev()
    }

    val EQ_OPTIONT_ID_NEL: Eq<NestedType<OptionTKindPartial<IdHK>, OptionTKindPartial<NonEmptyListHK>, Int>> =
            Eq { a, b ->
                a.unnest().value().value().fold(
                        { b.unnest().value().value().isEmpty() },
                        { optionA: OptionTNel ->
                            b.unnest().value().value().ev().fold(
                                    { false },
                                    { it.value() == optionA.value() })
                        })
            }

    val cf: (Int) -> HK<Nested<OptionHK, NonEmptyListHK>, Int> = { Some(it.nel()).nest() }

    init {
        testLaws(
            FunctorLaws.laws(ComposedFunctor(Option.functor(), NonEmptyList.functor()), cf, EQ_OPTION_NEL),
            FunctorFilterLaws.laws(ComposedFunctorFilter(OptionT.functorFilter(Id.monad()), OptionT.functorFilter(NonEmptyList.monad())), { OptionT.pure(OptionT.pure(it, NonEmptyList.monad()), Id.monad()).nest() }, EQ_OPTIONT_ID_NEL),
            ApplicativeLaws.laws(ComposedApplicative(Option.applicative(), NonEmptyList.applicative()), EQ_OPTION_NEL),
            FoldableLaws.laws(ComposedFoldable(Option.foldable(), NonEmptyList.foldable()), cf, Eq.any()),
            TraverseLaws.laws(ComposedTraverse(Option.traverse(), NonEmptyList.traverse(), NonEmptyList.applicative()), ComposedFunctor.invoke(Option.functor(), NonEmptyList.functor()), cf, EQ_OPTION_NEL),
            SemigroupKLaws.laws(ComposedSemigroupK<ListKWHK, OptionHK>(ListKW.semigroupK()), ComposedApplicative(ListKW.applicative(), Option.applicative()), EQ_LKW_OPTION),
            MonoidKLaws.laws(ComposedMonoidK<ListKWHK, OptionHK>(ListKW.monoidK()), ComposedApplicative(ListKW.applicative(), Option.applicative()), EQ_LKW_OPTION)
        )
    }
}
