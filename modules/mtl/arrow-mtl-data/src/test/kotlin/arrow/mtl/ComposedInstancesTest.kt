package arrow.mtl

import arrow.Kind
import arrow.Kind2
import arrow.core.ForFunction1
import arrow.core.ForListK
import arrow.core.ForNonEmptyList
import arrow.core.ForOption
import arrow.core.ForTuple2
import arrow.core.Function1
import arrow.core.ListK
import arrow.core.NonEmptyList
import arrow.core.Option
import arrow.core.Some
import arrow.core.Tuple2
import arrow.core.extensions.function1.contravariant.contravariant
import arrow.core.extensions.listk.applicative.applicative
import arrow.core.extensions.listk.monoidK.monoidK
import arrow.core.extensions.listk.semigroupK.semigroupK
import arrow.core.extensions.nonemptylist.applicative.applicative
import arrow.core.extensions.nonemptylist.foldable.foldable
import arrow.core.extensions.nonemptylist.functor.functor
import arrow.core.extensions.nonemptylist.traverse.traverse
import arrow.core.extensions.option.applicative.applicative
import arrow.core.extensions.option.foldable.foldable
import arrow.core.extensions.option.functor.functor
import arrow.core.extensions.option.traverse.traverse
import arrow.core.extensions.tuple2.bifunctor.bifunctor
import arrow.core.fix
import arrow.core.invoke
import arrow.core.k
import arrow.core.nel
import arrow.mtl.typeclasses.ComposedApplicative
import arrow.mtl.typeclasses.ComposedBifunctor
import arrow.mtl.typeclasses.ComposedFoldable
import arrow.mtl.typeclasses.ComposedFunctor
import arrow.mtl.typeclasses.ComposedInvariantContravariant
import arrow.mtl.typeclasses.ComposedInvariantCovariant
import arrow.mtl.typeclasses.ComposedMonoidK
import arrow.mtl.typeclasses.ComposedSemigroupK
import arrow.mtl.typeclasses.ComposedTraverse
import arrow.mtl.typeclasses.Nested
import arrow.mtl.typeclasses.NestedType
import arrow.mtl.typeclasses.binest
import arrow.mtl.typeclasses.biunnest
import arrow.mtl.typeclasses.nest
import arrow.mtl.typeclasses.unnest
import arrow.test.UnitSpec
import arrow.test.laws.ApplicativeLaws
import arrow.test.laws.BifunctorLaws
import arrow.test.laws.FoldableLaws
import arrow.test.laws.FunctorLaws
import arrow.test.laws.InvariantLaws
import arrow.test.laws.MonoidKLaws
import arrow.test.laws.SemigroupKLaws
import arrow.test.laws.TraverseLaws
import arrow.typeclasses.Conested
import arrow.typeclasses.Eq
import arrow.typeclasses.conest
import arrow.typeclasses.counnest

class ComposedInstancesTest : UnitSpec() {
  init {
    val EQ_OPTION_NEL: Eq<NestedType<ForOption, ForNonEmptyList, Int>> = Eq { a, b ->
      a.unnest().fix() == b.unnest().fix()
    }

    val EQ_LK_OPTION: Eq<NestedType<ForListK, ForOption, Int>> = Eq { a, b ->
      a.unnest().fix() == b.unnest().fix()
    }

    val EQ_OPTION_FN1: Eq<NestedType<ForOption, Conested<ForFunction1, Int>, Int>> = Eq { a, b ->
      a.unnest().fix().fold(
        { b.unnest().fix().isEmpty() },
        { fnA ->
          b.unnest().fix().fold(
            { false },
            { it.counnest().invoke(1) == fnA.counnest().invoke(1) }
          )
        }
      )
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
      InvariantLaws.laws(ComposedInvariantCovariant(Option.functor(), NonEmptyList.functor()), cf, EQ_OPTION_NEL)
    )

    testLaws(
      InvariantLaws.laws(ComposedInvariantContravariant(Option.functor(), Function1.contravariant<Int>()), cf2, EQ_OPTION_FN1)
    )

    testLaws(
      FunctorLaws.laws(ComposedFunctor(Option.functor(), NonEmptyList.functor()), cf, EQ_OPTION_NEL),
      ApplicativeLaws.laws(ComposedApplicative(Option.applicative(), NonEmptyList.applicative()), EQ_OPTION_NEL),
      FoldableLaws.laws(ComposedFoldable(Option.foldable(), NonEmptyList.foldable()), cf, Eq.any()),
      TraverseLaws.laws(ComposedTraverse(Option.traverse(), NonEmptyList.traverse()), ComposedFunctor.invoke(Option.functor(), NonEmptyList.functor()), cf, EQ_OPTION_NEL),
      SemigroupKLaws.laws(ComposedSemigroupK<ForListK, ForOption>(ListK.semigroupK()), ComposedApplicative(ListK.applicative(), Option.applicative()), EQ_LK_OPTION),
      MonoidKLaws.laws(ComposedMonoidK<ForListK, ForOption>(ListK.monoidK()), ComposedApplicative(ListK.applicative(), Option.applicative()), EQ_LK_OPTION),
      BifunctorLaws.laws(ComposedBifunctor(Tuple2.bifunctor(), Tuple2.bifunctor()), bifunctorCf, EQ_TUPLE2)
    )
  }
}
