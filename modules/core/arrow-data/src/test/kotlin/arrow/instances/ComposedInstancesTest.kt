package arrow.instances

import arrow.Kind
import arrow.core.*
import arrow.data.*
import arrow.mtl.instances.ComposedFunctorFilter
import arrow.test.UnitSpec
import arrow.test.laws.*
import arrow.typeclasses.*
import io.kotlintest.KTestJUnitRunner
import org.junit.runner.RunWith

typealias OptionTNel = Kind<OptionTKindPartial<ForNonEmptyList>, Int>

@RunWith(KTestJUnitRunner::class)
class ComposedInstancesTest : UnitSpec() {
    val EQ_OPTION_NEL: Eq<NestedType<ForOption, ForNonEmptyList, Int>> = Eq { a, b ->
        a.unnest().reify() == b.unnest().reify()
    }

    val EQ_LKW_OPTION: Eq<NestedType<ForListKW, ForOption, Int>> = Eq { a, b ->
        a.unnest().reify() == b.unnest().reify()
    }

    val EQ_OPTIONT_ID_NEL: Eq<NestedType<OptionTKindPartial<ForId>, OptionTKindPartial<ForNonEmptyList>, Int>> =
            Eq { a, b ->
                a.unnest().value().value().fold(
                        { b.unnest().value().value().isEmpty() },
                        { optionA: OptionTNel ->
                            b.unnest().value().value().reify().fold(
                                    { false },
                                    { it.value() == optionA.value() })
                        })
            }

    val cf: (Int) -> Kind<Nested<ForOption, ForNonEmptyList>, Int> = { Some(it.nel()).nest() }

    init {
        testLaws(
            FunctorLaws.laws(ComposedFunctor(Option.functor(), NonEmptyList.functor()), cf, EQ_OPTION_NEL),
            FunctorFilterLaws.laws(ComposedFunctorFilter(OptionT.functorFilter(Id.monad()), OptionT.functorFilter(NonEmptyList.monad())), { OptionT.pure(OptionT.pure(it, NonEmptyList.monad()), Id.monad()).nest() }, EQ_OPTIONT_ID_NEL),
            ApplicativeLaws.laws(ComposedApplicative(Option.applicative(), NonEmptyList.applicative()), EQ_OPTION_NEL),
            FoldableLaws.laws(ComposedFoldable(Option.foldable(), NonEmptyList.foldable()), cf, Eq.any()),
            TraverseLaws.laws(ComposedTraverse(Option.traverse(), NonEmptyList.traverse(), NonEmptyList.applicative()), ComposedFunctor.invoke(Option.functor(), NonEmptyList.functor()), cf, EQ_OPTION_NEL),
            SemigroupKLaws.laws(ComposedSemigroupK<ForListKW, ForOption>(ListKW.semigroupK()), ComposedApplicative(ListKW.applicative(), Option.applicative()), EQ_LKW_OPTION),
            MonoidKLaws.laws(ComposedMonoidK<ForListKW, ForOption>(ListKW.monoidK()), ComposedApplicative(ListKW.applicative(), Option.applicative()), EQ_LKW_OPTION)
        )
    }
}
