package arrow.instances

import arrow.Kind
import arrow.Kind2
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
  init {
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

    val EQ_OPTION_FN1: Eq<NestedType<ForOption, Conested<ForFunction1, Int>, Int>> = Eq { a, b ->
      a.unnest().fix() == b.unnest().fix()
    }

    val EQ_TUPLE2: Eq<Kind2<Nested<ForTuple2, ForTuple2>, Int, Int>> = Eq { a, b ->
      a.biunnest().fix() == b.biunnest().fix()
    }

    val cf: (Int) -> Kind<Nested<ForOption, ForNonEmptyList>, Int> = { Some(it.nel()).nest() }

    val cf2: (Int) -> Kind<Nested<ForOption, Conested<ForFunction1, Int>>, Int> = { x: Int ->
      Some({ y: Int -> x + y }.k().conest()).nest()
    }

    val bifunctorCf: (Int) -> Kind2<Nested<ForTuple2, ForTuple2>, Int, Int> = { Tuple2(Tuple2(it, it), Tuple2(it, it)).binest() }

    testLaws(
      InvariantLaws.laws(ComposedInvariant(Option.functor(), NonEmptyList.functor()), cf, EQ_OPTION_NEL),
      InvariantLaws.laws(ComposedInvariantCovariant(Option.functor(), NonEmptyList.functor()), cf, EQ_OPTION_NEL),
      InvariantLaws.laws(ComposedInvariantContravariant(Option.functor(), Function1.contravariant<Int>()), cf2, EQ_OPTION_FN1),
      FunctorLaws.laws(ComposedFunctor(Option.functor(), NonEmptyList.functor()), cf, EQ_OPTION_NEL),
      FunctorFilterLaws.laws(ComposedFunctorFilter(OptionT.functorFilter(Id.monad()), OptionT.functorFilter(NonEmptyList.monad())), { OptionT.just(Id.monad(), OptionT.just(NonEmptyList.monad(), it)).nest() }, EQ_OPTIONT_ID_NEL),
      ApplicativeLaws.laws(ComposedApplicative(Option.applicative(), NonEmptyList.applicative()), EQ_OPTION_NEL),
      FoldableLaws.laws(ComposedFoldable(Option.foldable(), NonEmptyList.foldable()), cf, Eq.any()),
      TraverseLaws.laws(ComposedTraverse(Option.traverse(), NonEmptyList.traverse(), NonEmptyList.applicative()), ComposedFunctor.invoke(Option.functor(), NonEmptyList.functor()), cf, EQ_OPTION_NEL),
      SemigroupKLaws.laws(ComposedSemigroupK<ForListK, ForOption>(ListK.semigroupK()), ComposedApplicative(ListK.applicative(), Option.applicative()), EQ_LK_OPTION),
      MonoidKLaws.laws(ComposedMonoidK<ForListK, ForOption>(ListK.monoidK()), ComposedApplicative(ListK.applicative(), Option.applicative()), EQ_LK_OPTION),
      BifunctorLaws.laws(ComposedBifunctor(Tuple2.bifunctor(), Tuple2.bifunctor()), bifunctorCf, EQ_TUPLE2)
    )
  }
}
