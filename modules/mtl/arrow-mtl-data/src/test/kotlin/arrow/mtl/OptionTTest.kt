package arrow.mtl

import arrow.Kind
import arrow.core.Const
import arrow.core.ForConst
import arrow.core.ForId
import arrow.core.ForNonEmptyList
import arrow.core.ForOption
import arrow.core.Id
import arrow.core.NonEmptyList
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.const
import arrow.core.extensions.const.divisible.divisible
import arrow.core.extensions.id.monad.monad
import arrow.core.extensions.monoid
import arrow.core.extensions.nonemptylist.eq.eq
import arrow.core.extensions.nonemptylist.monad.monad
import arrow.core.extensions.option.eq.eq
import arrow.core.extensions.option.monad.monad
import arrow.core.extensions.option.traverseFilter.traverseFilter
import arrow.core.fix
import arrow.core.toT
import arrow.core.value
import arrow.fx.ForIO
import arrow.fx.IO
import arrow.fx.extensions.io.applicativeError.attempt
import arrow.fx.extensions.io.async.async
import arrow.fx.mtl.optiont.async.async
import arrow.fx.typeclasses.seconds
import arrow.mtl.extensions.ComposedFunctorFilter
import arrow.mtl.extensions.optiont.applicative.applicative
import arrow.mtl.extensions.optiont.divisible.divisible
import arrow.mtl.extensions.optiont.functorFilter.functorFilter
import arrow.mtl.extensions.optiont.monoidK.monoidK
import arrow.mtl.extensions.optiont.semigroupK.semigroupK
import arrow.mtl.extensions.optiont.traverseFilter.traverseFilter
import arrow.mtl.typeclasses.Nested
import arrow.mtl.typeclasses.nest
import arrow.mtl.typeclasses.unnest
import arrow.test.UnitSpec
import arrow.test.generators.intSmall
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

  fun <A> EQK() = object : EqK<OptionTPartialOf<A>> {
    override fun <B> Kind<OptionTPartialOf<A>, B>.eqK(other: Kind<OptionTPartialOf<A>, B>, EQ: Eq<B>): Boolean =
      (this.fix() to other.fix()).let {
        it.first == it.second
      }
  }

  val NELM: Monad<ForNonEmptyList> = NonEmptyList.monad()

  fun <A> IOEQ(): Eq<Kind<OptionTPartialOf<ForIO>, A>> = Eq { a, b ->
    a.value().attempt().unsafeRunTimed(60.seconds) == b.value().attempt().unsafeRunTimed(60.seconds)
  }

  fun ioEQK() = object : EqK<OptionTPartialOf<ForIO>> {
    override fun <A> Kind<OptionTPartialOf<ForIO>, A>.eqK(other: Kind<OptionTPartialOf<ForIO>, A>, EQ: Eq<A>): Boolean =
      (this.fix() to other.fix()).let {
        IOEQ<A>().run {
          it.first.eqv(it.second)
        }
      }
  }

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

    val cf: (Int) -> OptionT<Kind<ForConst, Int>, Int> = { OptionT(it.const()) }
    val g = Gen.int().map(cf) as Gen<Kind<Kind<ForOptionT, Kind<ForConst, Int>>, Int>>

    testLaws(
      AsyncLaws.laws(OptionT.async(IO.async()), ioEQK()),

      SemigroupKLaws.laws(
        OptionT.semigroupK(Option.monad()),
        Gen.int().map
        { OptionT.applicative(Option.monad()).just(it) } as Gen<Kind<OptionTPartialOf<ForOption>, Int>>,
        EQK()),

      FunctorFilterLaws.laws(
        ComposedFunctorFilter(OptionT.functorFilter(Id.monad()),
          OptionT.functorFilter(NonEmptyList.monad())),
        Gen.int().map
        { OptionT.just(Id.monad(), OptionT.just(NonEmptyList.monad(), it)).nest() },
        nestedEQK),

      MonoidKLaws.laws(
        OptionT.monoidK(Option.monad()),
        OptionT.applicative(Option.monad()),
        EQK()),

      FunctorFilterLaws.laws(
        OptionT.functorFilter(Option.monad()),
        Gen.int().map
        { OptionT(Some(Some(it))) } as Gen<Kind<OptionTPartialOf<ForOption>, Int>>,
        EQK()),

      TraverseFilterLaws.laws(
        OptionT.traverseFilter(Option.traverseFilter()),
        OptionT.applicative(Option.monad()),
        Gen.intSmall().map
        { OptionT(Some(Some(it))) } as Gen<Kind<OptionTPartialOf<ForOption>, Int>>,
        EQK()
      ),

      DivisibleLaws.laws(
        OptionT.divisible(
          Const.divisible(Int.monoid())
        ),
        g,
        EQK()
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
