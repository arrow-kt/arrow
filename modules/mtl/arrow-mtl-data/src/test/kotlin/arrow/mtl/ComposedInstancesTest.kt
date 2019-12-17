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
import arrow.core.extensions.nonemptylist.eq.eq
import arrow.core.extensions.nonemptylist.foldable.foldable
import arrow.core.extensions.nonemptylist.functor.functor
import arrow.core.extensions.nonemptylist.traverse.traverse
import arrow.core.extensions.option.applicative.applicative
import arrow.core.extensions.option.eq.eq
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
import arrow.mtl.typeclasses.binest
import arrow.mtl.typeclasses.biunnest
import arrow.mtl.typeclasses.nest
import arrow.mtl.typeclasses.unnest
import arrow.test.UnitSpec
import arrow.test.generators.GenK
import arrow.test.generators.intSmall
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
import arrow.typeclasses.EqK
import arrow.typeclasses.conest
import arrow.typeclasses.counnest
import io.kotlintest.properties.Gen

class ComposedInstancesTest : UnitSpec() {
  init {

    val EQK_LK_OPTION = object : EqK<Nested<ForListK, ForOption>> {
      override fun <A> Kind<Nested<ForListK, ForOption>, A>.eqK(other: Kind<Nested<ForListK, ForOption>, A>, EQ: Eq<A>): Boolean =
        this.unnest().fix() == other.unnest().fix()
    }

    val EQK_OPTION_FN1 = object : EqK<Nested<ForOption, Conested<ForFunction1, Int>>> {
      override fun <A> Kind<Nested<ForOption, Conested<ForFunction1, Int>>, A>.eqK(other: Kind<Nested<ForOption, Conested<ForFunction1, Int>>, A>, EQ: Eq<A>): Boolean {
        return this.unnest().fix().fold(
          { other.unnest().fix().isEmpty() },
          { fnA ->
            other.unnest().fix().fold(
              { false },
              { it.counnest().invoke(1) == fnA.counnest().invoke(1) }
            )
          }
        )
      }
    }

    val EQ_TUPLE2: Eq<Kind2<Nested<ForTuple2, ForTuple2>, Int, Int>> = Eq { a, b ->
      a.biunnest().fix() == b.biunnest().fix()
    }

    val EQK_OPTION_NEL = object : EqK<Nested<ForOption, ForNonEmptyList>> {
      override fun <A> Kind<Nested<ForOption, ForNonEmptyList>, A>.eqK(other: Kind<Nested<ForOption, ForNonEmptyList>, A>, EQ: Eq<A>): Boolean {
        val ls = this.unnest().fix().map { it.fix() }
        val rs = other.unnest().fix().map { it.fix() }

        return Option.eq(NonEmptyList.eq(EQ)).run {
          ls.eqv(rs)
        }
      }
    }

    val cf: (Int) -> Kind<Nested<ForOption, ForNonEmptyList>, Int> = { Some(it.nel()).nest() }

    val G = Gen.intSmall().map(cf)

    val GK = object : GenK<Nested<ForOption, ForNonEmptyList>> {
      override fun <A> genK(gen: Gen<A>): Gen<Kind<Nested<ForOption, ForNonEmptyList>, A>> = gen.map { Some(it.nel()).nest() }
    }

    val cf2: (Int) -> Kind<Nested<ForOption, Conested<ForFunction1, Int>>, Int> = { x: Int ->
      Some({ y: Int -> x + y }.k().conest()).nest()
    }

    val bifunctorCf: (Int) -> Kind2<Nested<ForTuple2, ForTuple2>, Int, Int> = { Tuple2(Tuple2(it, it), Tuple2(it, it)).binest() }

    testLaws(
      InvariantLaws.laws(ComposedInvariantCovariant(Option.functor(), NonEmptyList.functor()), Gen.int().map(cf), EQK_OPTION_NEL)
    )

    testLaws(
      InvariantLaws.laws(ComposedInvariantContravariant(Option.functor(), Function1.contravariant<Int>()), Gen.int().map(cf2), EQK_OPTION_FN1)
    )

    testLaws(
      FunctorLaws.laws(ComposedFunctor(Option.functor(), NonEmptyList.functor()), Gen.int().map(cf), EQK_OPTION_NEL),
      ApplicativeLaws.laws(ComposedApplicative(Option.applicative(), NonEmptyList.applicative()), EQK_OPTION_NEL),
      FoldableLaws.laws(ComposedFoldable(Option.foldable(), NonEmptyList.foldable()), GK),
      TraverseLaws.laws(ComposedTraverse(Option.traverse(), NonEmptyList.traverse()), ComposedFunctor.invoke(Option.functor(), NonEmptyList.functor()), G, EQK_OPTION_NEL),
      SemigroupKLaws.laws(ComposedSemigroupK<ForListK, ForOption>(ListK.semigroupK()), Gen.int().map { ComposedApplicative(ListK.applicative(), Option.applicative()).just(it) }, EQK_LK_OPTION),
      MonoidKLaws.laws(ComposedMonoidK<ForListK, ForOption>(ListK.monoidK()), ComposedApplicative(ListK.applicative(), Option.applicative()), EQK_LK_OPTION),
      BifunctorLaws.laws(ComposedBifunctor(Tuple2.bifunctor(), Tuple2.bifunctor()), bifunctorCf, EQ_TUPLE2)
    )
  }
}
