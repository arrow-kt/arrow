package kategory.instances

import io.kotlintest.KTestJUnitRunner
import kategory.*
import kategory.laws.FunctorFilterLaws
import org.junit.runner.RunWith

typealias OptionTNel = HK<OptionTKindPartial<NonEmptyListHK>, Int>

@RunWith(KTestJUnitRunner::class)
class ComposedInstancesTest : UnitSpec() {
    val EQ: Eq<HK<ComposedType<OptionHK, NonEmptyListHK>, Int>> = object : Eq<HK<ComposedType<OptionHK, NonEmptyListHK>, Int>> {
        override fun eqv(a: HK<ComposedType<OptionHK, NonEmptyListHK>, Int>, b: HK<ComposedType<OptionHK, NonEmptyListHK>, Int>): Boolean =
                a.lower().ev() == b.lower().ev()
    }

    val EQ_OPTIONT_ID_NEL: Eq<HK<ComposedType<OptionTKindPartial<IdHK>, OptionTKindPartial<NonEmptyListHK>>, Int>> =
            object : Eq<HK<ComposedType<OptionTKindPartial<IdHK>, OptionTKindPartial<NonEmptyListHK>>, Int>> {
                override fun eqv(a: HK<ComposedType<OptionTKindPartial<IdHK>, OptionTKindPartial<NonEmptyListHK>>, Int>, b: HK<ComposedType<OptionTKindPartial<IdHK>, OptionTKindPartial<NonEmptyListHK>>, Int>): Boolean =
                        a.lower().value().value().fold(
                                { b.lower().value().value().isEmpty },
                                { optionA: OptionTNel -> b.lower().value().value().ev().fold({ false }, { it.ev() == optionA.ev() }) })
            }

    val cf: (Int) -> HK<ComposedType<OptionHK, NonEmptyListHK>, Int> = { it.nel().some().lift() }

    val CAP = ComposedApplicative(Option.applicative(), NonEmptyList.applicative())

    init {
        testLaws(FunctorLaws.laws(ComposedFunctor(Option.functor(), NonEmptyList.functor()), cf, EQ))
        testLaws(FunctorFilterLaws.laws(ComposedFunctorFilter(OptionT.functorFilter(Id.monad()), OptionT.functorFilter(NonEmptyList.monad())), { OptionT.pure(OptionT.pure(it, NonEmptyList.monad()), Id.monad()).lift() }, EQ_OPTIONT_ID_NEL))
        testLaws(ApplicativeLaws.laws(CAP, EQ))
        testLaws(FoldableLaws.laws(ComposedFoldable(Option.foldable(), NonEmptyList.foldable()), cf, Eq.any()))
        testLaws(TraverseLaws.laws(ComposedTraverse(Option.traverse(), NonEmptyList.traverse(), NonEmptyList.applicative()), ComposedFunctor.invoke(Option.functor(), NonEmptyList.functor()), cf, EQ))
        testLaws(SemigroupKLaws.laws(ComposedSemigroupK<OptionHK, NonEmptyListHK>(Option.semigroupK()), CAP, EQ))
        testLaws(MonoidKLaws.laws(ComposedMonoidK<OptionHK, NonEmptyListHK>(Option.monoidK()), CAP, EQ))
    }
}