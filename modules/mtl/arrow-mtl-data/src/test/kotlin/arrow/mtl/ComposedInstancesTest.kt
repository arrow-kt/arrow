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
import arrow.core.Tuple2
import arrow.core.extensions.function1.contravariant.contravariant
import arrow.core.extensions.listk.eqK.eqK
import arrow.core.extensions.listk.monoidK.monoidK
import arrow.core.extensions.listk.semigroupK.semigroupK
import arrow.core.extensions.nonemptylist.applicative.applicative
import arrow.core.extensions.nonemptylist.eqK.eqK
import arrow.core.extensions.nonemptylist.foldable.foldable
import arrow.core.extensions.nonemptylist.functor.functor
import arrow.core.extensions.nonemptylist.traverse.traverse
import arrow.core.extensions.option.applicative.applicative
import arrow.core.extensions.option.eqK.eqK
import arrow.core.extensions.option.foldable.foldable
import arrow.core.extensions.option.functor.functor
import arrow.core.extensions.option.traverse.traverse
import arrow.core.extensions.tuple2.bifunctor.bifunctor
import arrow.core.fix
import arrow.core.invoke
import arrow.core.k
import arrow.mtl.extensions.nested
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
import arrow.test.UnitSpec
import arrow.test.generators.GenK
import arrow.test.generators.functionAToB
import arrow.test.generators.genK
import arrow.test.generators.nested
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

    val GENK_LK_OPTION = ListK.genK().nested(Option.genK())

    val EQK_LK_OPTION = ListK.eqK().nested(Option.eqK())

    fun EQK_FN1() = object : EqK<Conested<ForFunction1, Int>> {
      override fun <A> Kind<Conested<ForFunction1, Int>, A>.eqK(other: Kind<Conested<ForFunction1, Int>, A>, EQ: Eq<A>): Boolean =
        (this.counnest() to other.counnest()).let {
          val ls = it.first.fix().invoke(1)
          val rs = it.first.fix().invoke(1)

          ls == rs
        }
    }

    val EQK_OPTION_FN1: EqK<Nested<ForOption, Conested<ForFunction1, Int>>> = Option.eqK().nested(EQK_FN1())

    fun <A> GENK_OPTION_FN1(genA: Gen<A>): GenK<Nested<ForOption, Conested<ForFunction1, A>>> =
      object : GenK<Nested<ForOption, Conested<ForFunction1, A>>> {
        override fun <B> genK(gen: Gen<B>): Gen<Kind<Nested<ForOption, Conested<ForFunction1, A>>, B>> =
          Gen.functionAToB<B, A>(genA).map { it.k().conest() }.orNull().map {
            Option.fromNullable(it).nest()
          }
      }

    val EQ_TUPLE2: Eq<Kind2<Nested<ForTuple2, ForTuple2>, Int, Int>> = Eq { a, b ->
      a.biunnest().fix() == b.biunnest().fix()
    }

    val EQK_OPTION_NEL: EqK<Nested<ForOption, ForNonEmptyList>> =
      Option.eqK().nested(NonEmptyList.eqK())

    val GENK_OPTION_NEL: GenK<Nested<ForOption, ForNonEmptyList>> =
      Option.genK().nested(NonEmptyList.genK())

    val bifunctorCf: (Int) -> Kind2<Nested<ForTuple2, ForTuple2>, Int, Int> = { Tuple2(Tuple2(it, it), Tuple2(it, it)).binest() }

    testLaws(
      InvariantLaws.laws(ComposedInvariantCovariant(Option.functor(), NonEmptyList.functor()), GENK_OPTION_NEL, EQK_OPTION_NEL)
    )

    testLaws(
      InvariantLaws.laws(ComposedInvariantContravariant(Option.functor(), Function1.contravariant<Int>()), GENK_OPTION_FN1(Gen.int()), EQK_OPTION_FN1)
    )

    testLaws(
      FunctorLaws.laws(ComposedFunctor(Option.functor(), NonEmptyList.functor()), GENK_OPTION_NEL, EQK_OPTION_NEL),
      ApplicativeLaws.laws(ComposedApplicative(Option.applicative(), NonEmptyList.applicative()), GENK_OPTION_NEL, EQK_OPTION_NEL),
      FoldableLaws.laws(ComposedFoldable(Option.foldable(), NonEmptyList.foldable()), GENK_OPTION_NEL),
      TraverseLaws.laws(ComposedTraverse(Option.traverse(), NonEmptyList.traverse()), GENK_OPTION_NEL, EQK_OPTION_NEL),
      SemigroupKLaws.laws(ComposedSemigroupK<ForListK, ForOption>(ListK.semigroupK()), GENK_LK_OPTION, EQK_LK_OPTION),
      MonoidKLaws.laws(ComposedMonoidK<ForListK, ForOption>(ListK.monoidK()), GENK_LK_OPTION, EQK_LK_OPTION),
      BifunctorLaws.laws(ComposedBifunctor(Tuple2.bifunctor(), Tuple2.bifunctor()), bifunctorCf, EQ_TUPLE2)
    )
  }
}
