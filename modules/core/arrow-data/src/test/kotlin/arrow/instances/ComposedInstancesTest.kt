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

typealias OptionTNel = Kind<OptionTPartialOf<ForNonEmptyList>, Int>

@RunWith(KTestJUnitRunner::class)
class ComposedInstancesTest : UnitSpec() {
    val EQ_OPTION_NEL: Eq<NestedType<ForOption, ForNonEmptyList, Int>> = Eq { a, b ->
        a.unnest().fix() == b.unnest().fix()
    }

    val EQ_LK_OPTION: Eq<NestedType<ForListK, ForOption, Int>> = Eq { a, b ->
        a.unnest().fix() == b.unnest().fix()
    }

    val EQ_OPTIONT_ID_NEL: Eq<NestedType<OptionTPartialOf<ForId>, OptionTPartialOf<ForNonEmptyList>, Int>> =
            Eq { a, b ->
                a.unnest().value().value().fold(
                        { b.unnest().value().value().isEmpty() },
                        { optionA: OptionTNel ->
                            b.unnest().value().value().fix().fold(
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
            SemigroupKLaws.laws(ComposedSemigroupK<ForListK, ForOption>(ListK.semigroupK()), ComposedApplicative(ListK.applicative(), Option.applicative()), EQ_LK_OPTION),
            MonoidKLaws.laws(ComposedMonoidK<ForListK, ForOption>(ListK.monoidK()), ComposedApplicative(ListK.applicative(), Option.applicative()), EQ_LK_OPTION)
        )
    }
}
