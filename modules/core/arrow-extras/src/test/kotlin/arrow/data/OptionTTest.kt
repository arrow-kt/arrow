package arrow.data

import arrow.Kind
import arrow.core.*
import arrow.core.extensions.const.divisible.divisible
import arrow.core.extensions.monoid
import arrow.effects.ForIO
import arrow.effects.IO
import arrow.effects.extensions.io.applicativeError.attempt
import arrow.effects.extensions.io.async.async
import arrow.effects.extensions.optiont.async.async
import arrow.effects.typeclasses.seconds
import arrow.data.extensions.nonemptylist.monad.monad
import arrow.core.extensions.option.monad.monad
import arrow.data.extensions.optiont.applicative.applicative
import arrow.data.extensions.optiont.divisible.divisible
import arrow.data.extensions.optiont.monoidK.monoidK
import arrow.data.extensions.optiont.semigroupK.semigroupK
import arrow.mtl.extensions.option.traverseFilter.traverseFilter
import arrow.mtl.extensions.optiont.functorFilter.functorFilter
import arrow.mtl.extensions.optiont.traverseFilter.traverseFilter
import arrow.test.UnitSpec
import arrow.test.laws.*
import arrow.typeclasses.*
import io.kotlintest.properties.forAll
import io.kotlintest.runner.junit4.KotlinTestRunner
import org.junit.runner.RunWith

@RunWith(KotlinTestRunner::class)
class OptionTTest : UnitSpec() {

  fun <A> EQ(): Eq<Kind<OptionTPartialOf<A>, Int>> = Eq { a, b ->
    a.value() == b.value()
  }

  fun <A> EQ_NESTED(): Eq<Kind<OptionTPartialOf<A>, Kind<OptionTPartialOf<A>, Int>>> = Eq { a, b ->
    a.value() == b.value()
  }

  private fun IOEitherEQ(): Eq<Kind<OptionTPartialOf<ForIO>, Either<Throwable, Int>>> = Eq { a, b ->
    a.value().attempt().unsafeRunSync() == b.value().attempt().unsafeRunSync()
  }

  val NELM: Monad<ForNonEmptyList> = NonEmptyList.monad()

  fun <A> IOEQ(): Eq<Kind<OptionTPartialOf<ForIO>, A>> = Eq { a, b ->
    a.value().attempt().unsafeRunTimed(60.seconds) == b.value().attempt().unsafeRunTimed(60.seconds)
  }

  init {

    testLaws(
      AsyncLaws.laws(OptionT.async(IO.async()), IOEQ(), IOEitherEQ()),

      SemigroupKLaws.laws(
        OptionT.semigroupK(Option.monad()),
        OptionT.applicative(Option.monad()),
        EQ()),

      MonoidKLaws.laws(
        OptionT.monoidK(Option.monad()),
        OptionT.applicative(Option.monad()),
        EQ()),

      FunctorFilterLaws.laws(
        OptionT.functorFilter(Option.monad()),
        { OptionT(Some(Some(it))) },
        EQ()),

      TraverseFilterLaws.laws(
        OptionT.traverseFilter(Option.traverseFilter()),
        OptionT.applicative(Option.monad()),
        { OptionT(Some(Some(it))) },
        EQ(),
        EQ_NESTED()),

      DivisibleLaws.laws(
        OptionT.divisible(
          Const.divisible(Int.monoid())
        ),
        { OptionT(it.const()) },
        Eq { a, b -> a.value().value() == b.value().value() }
      )
    )

    "toLeft for Some should build a correct EitherT" {
      forAll { a: Int, b: String ->
        OptionT
          .fromOption(NELM, Some(a))
          .toLeft(NELM) { b } == EitherT.left<ForNonEmptyList, Int, String>(NELM, a)
      }
    }

    "toLeft for None should build a correct EitherT" {
      forAll { b: String ->
        OptionT.fromOption<ForNonEmptyList, Int>(NELM, None).toLeft(NELM) { b } == EitherT.right<ForNonEmptyList, Int, String>(NELM, b)
      }
    }

    "toRight for Some should build a correct EitherT" {
      forAll { a: Int, b: String ->
        OptionT
          .fromOption(NELM, Some(b))
          .toRight(NELM) { a } == EitherT.right<ForNonEmptyList, Int, String>(NELM, b)
      }
    }

    "toRight for None should build a correct EitherT" {
      forAll { a: Int ->
        OptionT.fromOption<ForNonEmptyList, String>(NELM, None).toRight(NELM) { a } == EitherT.left<ForNonEmptyList, Int, String>(NELM, a)
      }
    }

  }
}
