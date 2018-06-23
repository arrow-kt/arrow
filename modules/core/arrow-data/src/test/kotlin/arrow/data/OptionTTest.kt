package arrow.data

import arrow.Kind
import arrow.core.*
import arrow.mtl.instances.ForOptionT
import arrow.test.UnitSpec
import arrow.test.laws.*
import arrow.typeclasses.Eq
import arrow.typeclasses.Monad
import io.kotlintest.runner.junit4.KotlinTestRunner
import io.kotlintest.properties.forAll
import org.junit.runner.RunWith

@RunWith(KotlinTestRunner::class)
class OptionTTest : UnitSpec() {

  fun <A> EQ(): Eq<Kind<OptionTPartialOf<A>, Int>> = Eq { a, b ->
    a.value() == b.value()
  }

  fun <A> EQ_NESTED(): Eq<Kind<OptionTPartialOf<A>, Kind<OptionTPartialOf<A>, Int>>> = Eq { a, b ->
    a.value() == b.value()
  }

  val NELM: Monad<ForNonEmptyList> = NonEmptyList.monad()

  init {

    ForOptionT(Option.monad(), Option.traverseFilter()) extensions {

      testLaws(
        MonadLaws.laws(this, Eq.any()),
        SemigroupKLaws.laws(
          this,
          this,
          EQ()),

        MonoidKLaws.laws(
          this,
          this,
          EQ()),

        FunctorFilterLaws.laws(
          this,
          { OptionT(Some(Some(it))) },
          EQ()),

        TraverseFilterLaws.laws(
          this,
          this,
          { OptionT(Some(Some(it))) },
          EQ(),
          EQ_NESTED())
      )

    }

    "toLeft for Some should build a correct EitherT" {
      forAll { a: Int, b: String ->
        OptionT.fromOption(NELM, Some(a)).toLeft(NELM, { b }) == EitherT.left<ForNonEmptyList, Int, String>(NELM, a)
      }
    }

    "toLeft for None should build a correct EitherT" {
      forAll { a: Int, b: String ->
        OptionT.fromOption<ForNonEmptyList, Int>(NELM, None).toLeft(NELM, { b }) == EitherT.right<ForNonEmptyList, Int, String>(this.NELM, b)
      }
    }

    "toRight for Some should build a correct EitherT" {
      forAll { a: Int, b: String ->
        OptionT.fromOption(NELM, Some(b)).toRight(NELM, { a }) == EitherT.right<ForNonEmptyList, Int, String>(NELM, b)
      }
    }

    "toRight for None should build a correct EitherT" {
      forAll { a: Int, b: String ->
        OptionT.fromOption<ForNonEmptyList, String>(NELM, None).toRight(NELM, { a }) == EitherT.left<ForNonEmptyList, Int, String>(this.NELM, a)
      }
    }

  }
}
