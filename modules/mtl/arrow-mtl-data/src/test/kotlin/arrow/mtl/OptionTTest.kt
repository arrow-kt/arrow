package arrow.mtl

import arrow.Kind
import arrow.core.Const
import arrow.core.Either
import arrow.core.ForId
import arrow.core.ForNonEmptyList
import arrow.core.Id
import arrow.core.NonEmptyList
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.extensions.const.divisible.divisible
import arrow.core.extensions.const.eqK.eqK
import arrow.core.extensions.either.eq.eq
import arrow.core.extensions.eq
import arrow.core.extensions.id.monad.monad
import arrow.core.extensions.monoid
import arrow.core.extensions.nonemptylist.eq.eq
import arrow.core.extensions.nonemptylist.monad.monad
import arrow.core.extensions.option.eq.eq
import arrow.core.extensions.option.eqK.eqK
import arrow.core.extensions.option.monad.monad
import arrow.core.extensions.option.traverseFilter.traverseFilter
import arrow.core.fix
import arrow.core.toT
import arrow.core.value
import arrow.fx.ForIO
import arrow.fx.IO
import arrow.fx.extensions.io.async.async
import arrow.fx.fix
import arrow.fx.mtl.optiont.async.async
import arrow.fx.typeclasses.Duration
import arrow.fx.typeclasses.seconds
import arrow.mtl.extensions.ComposedFunctorFilter
import arrow.mtl.extensions.optiont.applicative.applicative
import arrow.mtl.extensions.optiont.divisible.divisible
import arrow.mtl.extensions.optiont.eqK.eqK
import arrow.mtl.extensions.optiont.functorFilter.functorFilter
import arrow.mtl.extensions.optiont.monoidK.monoidK
import arrow.mtl.extensions.optiont.semigroupK.semigroupK
import arrow.mtl.extensions.optiont.traverseFilter.traverseFilter
import arrow.mtl.typeclasses.Nested
import arrow.mtl.typeclasses.nest
import arrow.mtl.typeclasses.unnest
import arrow.test.UnitSpec
import arrow.test.generators.GenK
import arrow.test.generators.genK
import arrow.test.generators.option
import arrow.test.laws.AsyncLaws
import arrow.test.laws.DivisibleLaws
import arrow.test.laws.FunctorFilterLaws
import arrow.test.laws.MonoidKLaws
import arrow.test.laws.SemigroupKLaws
import arrow.test.laws.TraverseFilterLaws
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import arrow.typeclasses.Monad
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

typealias OptionTNel = Kind<OptionTPartialOf<ForNonEmptyList>, Int>

class OptionTTest : UnitSpec() {

  val NELM: Monad<ForNonEmptyList> = NonEmptyList.monad()

  val optiontEQK = OptionT.eqK(IO.eqK())

  init {

    val nestedEQK = object : EqK<Nested<OptionTPartialOf<ForId>, OptionTPartialOf<ForNonEmptyList>>> {
      override fun <A> Kind<Nested<OptionTPartialOf<ForId>, OptionTPartialOf<ForNonEmptyList>>, A>.eqK(other: Kind<Nested<OptionTPartialOf<ForId>, OptionTPartialOf<ForNonEmptyList>>, A>, EQ: Eq<A>): Boolean =
        (this.unnest().fix() toT other.unnest().fix()).let { (a, b) ->

          a.value().value().fix().fold(
            { b.value().value().isEmpty() },

            { optionA ->
              b.value().value().fix().fold(
                { false },

                { some ->
                  NonEmptyList.eq(Option.eq(EQ)).run { some.value().fix().eqv(optionA.fix().value().fix()) }
                }
              )
            }
          )
        }
    }

    fun nestedGenk() = object : GenK<Nested<OptionTPartialOf<ForId>, OptionTPartialOf<ForNonEmptyList>>> {
      override fun <A> genK(gen: Gen<A>): Gen<Kind<Nested<OptionTPartialOf<ForId>, OptionTPartialOf<ForNonEmptyList>>, A>> =
        gen.map {
          OptionT.just(Id.monad(), OptionT.just(NonEmptyList.monad(), it)).nest()
        }
    }

    testLaws(
      AsyncLaws.laws(OptionT.async(IO.async()), optiontEQK),

      SemigroupKLaws.laws(
        OptionT.semigroupK(Option.monad()),
        OptionT.genk(Option.genK()),
        OptionT.eqK(Option.eqK())),

      FunctorFilterLaws.laws(
        ComposedFunctorFilter(OptionT.functorFilter(Id.monad()),
          OptionT.functorFilter(NonEmptyList.monad())),
        nestedGenk(),
        nestedEQK),

      MonoidKLaws.laws(
        OptionT.monoidK(Option.monad()),
        OptionT.genk(Option.genK()),
        OptionT.eqK(Option.eqK())),

      FunctorFilterLaws.laws(
        OptionT.functorFilter(Option.monad()),
        OptionT.genk(Option.genK()),
        OptionT.eqK(Option.eqK())),

      TraverseFilterLaws.laws(
        OptionT.traverseFilter(Option.traverseFilter()),
        OptionT.applicative(Option.monad()),
        OptionT.genk(Option.genK()),
        OptionT.eqK(Option.eqK())
      ),

      DivisibleLaws.laws(
        OptionT.divisible(
          Const.divisible(Int.monoid())
        ),
        OptionT.genk(Const.genK(Gen.int())),
        OptionT.eqK(Const.eqK(Int.eq()))
      )
    )

    "toLeft for Some should build a correct EitherT"
    {
      forAll { a: Int, b: String ->
        OptionT
          .fromOption(NELM, Some(a))
          .toLeft(NELM) { b } == EitherT.left<ForNonEmptyList, Int, String>(NELM, a)
      }
    }

    "toLeft for None should build a correct EitherT"
    {
      forAll { b: String ->
        OptionT.fromOption<ForNonEmptyList, Int>(NELM, None).toLeft(NELM) { b } == EitherT.right<ForNonEmptyList, Int, String>(NELM, b)
      }
    }

    "toRight for Some should build a correct EitherT"
    {
      forAll { a: Int, b: String ->
        OptionT
          .fromOption(NELM, Some(b))
          .toRight(NELM) { a } == EitherT.right<ForNonEmptyList, Int, String>(NELM, b)
      }
    }

    "toRight for None should build a correct EitherT"
    {
      forAll { a: Int ->
        OptionT.fromOption<ForNonEmptyList, String>(NELM, None).toRight(NELM) { a } == EitherT.left<ForNonEmptyList, Int, String>(NELM, a)
      }
    }
  }
}

fun <F> OptionT.Companion.genk(genkF: GenK<F>) = object : GenK<Kind<ForOptionT, F>> {
  override fun <A> genK(gen: Gen<A>): Gen<Kind<Kind<ForOptionT, F>, A>> = genkF.genK(Gen.option(gen)).map {
    OptionT(it)
  }
}

fun IO.Companion.eqK(timeout: Duration = 60.seconds) = object : EqK<ForIO> {
  override fun <A> Kind<ForIO, A>.eqK(other: Kind<ForIO, A>, EQ: Eq<A>): Boolean =
    (this.fix() to other.fix()).let {
      val ls = it.first.attempt().unsafeRunTimed(timeout)
      val rs = it.second.attempt().unsafeRunTimed(timeout)

      Option.eq(Either.eq(Eq.any(), EQ)).run {
        ls.eqv(rs)
      }
    }
}
