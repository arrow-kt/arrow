package arrow.optics

import arrow.core.Option
import arrow.core.Some
import arrow.core.getOrElse
import arrow.core.identity
import arrow.data.ListK
import arrow.data.k
import arrow.instances.monoid
import arrow.instances.listk.eq.eq
import arrow.instances.option.eq.eq
import arrow.test.UnitSpec
import arrow.test.generators.genEither
import arrow.test.generators.genFunctionAToB
import arrow.test.generators.genTuple
import arrow.test.laws.OptionalLaws
import arrow.test.laws.PrismLaws
import arrow.test.laws.SetterLaws
import arrow.test.laws.TraversalLaws
import arrow.typeclasses.Eq
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class PrismTest : UnitSpec() {

  init {
    testLaws(
      PrismLaws.laws(
        prism = sumPrism,
        aGen = SumGen,
        bGen = Gen.string(),
        funcGen = genFunctionAToB(Gen.string()),
        EQA = Eq.any(),
        EQOptionB = Eq.any()
      ),

      SetterLaws.laws(
        setter = sumPrism.asSetter(),
        aGen = SumGen,
        bGen = Gen.string(),
        funcGen = genFunctionAToB(Gen.string()),
        EQA = Eq.any()
      ),

      TraversalLaws.laws(
        traversal = sumPrism.asTraversal(),
        aGen = SumGen,
        bGen = Gen.string(),
        funcGen = genFunctionAToB(Gen.string()),
        EQA = Eq.any(),
        EQOptionB = Option.eq(Eq.any()),
        EQListB = ListK.eq(Eq.any())
      ),

      OptionalLaws.laws(
        optional = sumPrism.asOptional(),
        aGen = SumGen,
        bGen = Gen.string(),
        funcGen = genFunctionAToB(Gen.string()),
        EQA = Eq.any(),
        EQOptionB = Option.eq(Eq.any())
      )
    )

    testLaws(PrismLaws.laws(
      prism = sumPrism.first(),
      aGen = genTuple(SumGen, Gen.int()),
      bGen = genTuple(Gen.string(), Gen.int()),
      funcGen = genFunctionAToB(genTuple(Gen.string(), Gen.int())),
      EQA = Eq.any(),
      EQOptionB = Eq.any()
    ))

    testLaws(PrismLaws.laws(
      prism = sumPrism.second(),
      aGen = genTuple(Gen.int(), SumGen),
      bGen = genTuple(Gen.int(), Gen.string()),
      funcGen = genFunctionAToB(genTuple(Gen.int(), Gen.string())),
      EQA = Eq.any(),
      EQOptionB = Eq.any()
    ))

    testLaws(PrismLaws.laws(
      prism = sumPrism.right<SumType, SumType, String, String, Int>(),
      aGen = genEither(Gen.int(), SumGen),
      bGen = genEither(Gen.int(), Gen.string()),
      funcGen = genFunctionAToB(genEither(Gen.int(), Gen.string())),
      EQA = Eq.any(),
      EQOptionB = Eq.any()
    ))

    testLaws(PrismLaws.laws(
      prism = sumPrism.left<SumType, SumType, String, String, Int>(),
      aGen = genEither(SumGen, Gen.int()),
      bGen = genEither(Gen.string(), Gen.int()),
      funcGen = genFunctionAToB(genEither(Gen.string(), Gen.int())),
      EQA = Eq.any(),
      EQOptionB = Eq.any()
    ))

    testLaws(PrismLaws.laws(
      prism = Prism.id(),
      aGen = genEither(Gen.int(), Gen.int()),
      bGen = genEither(Gen.int(), Gen.int()),
      funcGen = genFunctionAToB(genEither(Gen.int(), Gen.int())),
      EQA = Eq.any(),
      EQOptionB = Eq.any()
    ))

    with(sumPrism.asFold()) {

      "asFold should behave as valid Fold: size" {
        forAll(SumGen) { sum: SumType ->
          size(sum) == sumPrism.getOption(sum).map { 1 }.getOrElse { 0 }
        }
      }

      "asFold should behave as valid Fold: nonEmpty" {
        forAll(SumGen) { sum: SumType ->
          nonEmpty(sum) == sumPrism.getOption(sum).nonEmpty()
        }
      }

      "asFold should behave as valid Fold: isEmpty" {
        forAll(SumGen) { sum: SumType ->
          isEmpty(sum) == sumPrism.getOption(sum).isEmpty()
        }
      }

      "asFold should behave as valid Fold: getAll" {
        forAll(SumGen) { sum: SumType ->
          getAll(sum) == sumPrism.getOption(sum).toList().k()
        }
      }

      "asFold should behave as valid Fold: combineAll" {
        forAll(SumGen) { sum: SumType ->
          combineAll(String.monoid(), sum) ==
            sumPrism.getOption(sum).fold({ String.monoid().empty() }, ::identity)
        }
      }

      "asFold should behave as valid Fold: fold" {
        forAll(SumGen) { sum: SumType ->
          fold(String.monoid(), sum) ==
            sumPrism.getOption(sum).fold({ String.monoid().empty() }, ::identity)
        }
      }

      "asFold should behave as valid Fold: headOption" {
        forAll(SumGen) { sum: SumType ->
          headOption(sum) == sumPrism.getOption(sum)
        }
      }

      "asFold should behave as valid Fold: lastOption" {
        forAll(SumGen) { sum: SumType ->
          lastOption(sum) == sumPrism.getOption(sum)
        }
      }
    }

    "Joining two prisms together with same target should yield same result" {
      forAll(SumGen) { a ->
        (sumPrism compose stringPrism).getOption(a) == sumPrism.getOption(a).flatMap(stringPrism::getOption) &&
          (sumPrism + stringPrism).getOption(a) == (sumPrism compose stringPrism).getOption(a)
      }
    }

    "Checking if a prism exists with a target" {
      forAll(SumGen, SumGen, Gen.bool()) { a, other, bool ->
        Prism.only(a, object : Eq<SumType> {
          override fun SumType.eqv(b: SumType): Boolean = bool
        }).isEmpty(other) == bool
      }
    }

    "Checking if there is no target" {
      forAll(SumGen) { sum ->
        sumPrism.isEmpty(sum) == sum !is SumType.A
      }
    }

    "Checking if a target exists" {
      forAll(SumGen) { sum ->
        sumPrism.nonEmpty(sum) == sum is SumType.A
      }
    }

    "Setting a target on a prism should set the correct target"{
      forAll(AGen, Gen.string()) { a, string ->
        sumPrism.setOption(a, string) == Some(a.copy(string = string))
      }
    }

    "Finding a target using a predicate within a Lens should be wrapped in the correct option result" {
      forAll(SumGen, Gen.bool()) { sum, predicate ->
        sumPrism.find(sum) { predicate }.fold({ false }, { true }) == (predicate && sum is SumType.A)
      }
    }

    "Checking existence predicate over the target should result in same result as predicate" {
      forAll(SumGen, Gen.bool()) { sum, predicate ->
        sumPrism.exist(sum) { predicate } == (predicate && sum is SumType.A)
      }
    }

    "Checking satisfaction of predicate over the target should result in opposite result as predicate" {
      forAll(SumGen, Gen.bool()) { sum, predicate ->
        sumPrism.all(sum) { predicate } == (predicate || sum is SumType.B)
      }
    }

  }

}
