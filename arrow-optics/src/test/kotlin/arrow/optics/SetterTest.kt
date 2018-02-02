package arrow.optics

import arrow.core.getOrElse
import arrow.data.k
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import arrow.typeclasses.Eq
import arrow.test.laws.SetterLaws
import arrow.test.UnitSpec
import arrow.test.generators.genFunctionAToB
import arrow.test.generators.genOption
import arrow.syntax.either.left
import arrow.syntax.either.right
import arrow.syntax.foldable.combineAll
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class SetterTest : UnitSpec() {

    init {

        testLaws(SetterLaws.laws(
                setter = Setter.id(),
                aGen = Gen.int(),
                bGen = Gen.int(),
                funcGen = genFunctionAToB(Gen.int()),
                EQA = Eq.any()
        ))

        testLaws(SetterLaws.laws(
                setter = tokenSetter,
                aGen = TokenGen,
                bGen = Gen.string(),
                funcGen = genFunctionAToB(Gen.string()),
                EQA = Eq.any()
        ))

        testLaws(SetterLaws.laws(
                setter = Setter.fromFunctor(),
                aGen = genOption(TokenGen),
                bGen = Gen.string(),
                funcGen = genFunctionAToB(Gen.string()),
                EQA = Eq.any()
        ))

        "asFold should behave as valid Fold: size" {
            forAll(SumGen) { sum: SumType ->
                sumPrism.asFold().size(sum) == sumPrism.getOption(sum).map { 1 }.getOrElse { 0 }
            }
        }

        "asFold should behave as valid Fold: nonEmpty" {
            forAll(SumGen) { sum: SumType ->
                sumPrism.asFold().nonEmpty(sum) == sumPrism.getOption(sum).nonEmpty()
            }
        }

        "asFold should behave as valid Fold: isEmpty" {
            forAll(SumGen) { sum: SumType ->
                sumPrism.asFold().isEmpty(sum) == sumPrism.getOption(sum).isEmpty()
            }
        }

        "asFold should behave as valid Fold: getAll" {
            forAll(SumGen) { sum: SumType ->
                sumPrism.asFold().getAll(sum) == sumPrism.getOption(sum).toList().k()
            }
        }

        "asFold should behave as valid Fold: combineAll" {
            forAll(SumGen) { sum: SumType ->
                sumPrism.asFold().combineAll(sum) == sumPrism.getOption(sum).combineAll()
            }
        }

        "asFold should behave as valid Fold: fold" {
            forAll(SumGen) { sum: SumType ->
                sumPrism.asFold().fold(sum) == sumPrism.getOption(sum).combineAll()
            }
        }

        "asFold should behave as valid Fold: headOption" {
            forAll(SumGen) { sum: SumType ->
                sumPrism.asFold().headOption(sum) == sumPrism.getOption(sum)
            }
        }

        "asFold should behave as valid Fold: lastOption" {
            forAll(SumGen) { sum: SumType ->
                sumPrism.asFold().lastOption(sum) == sumPrism.getOption(sum)
            }
        }

        "Joining two lenses together with same target should yield same result" {
            val userTokenStringSetter = userSetter compose tokenSetter
            val joinedSetter = tokenSetter.choice(userTokenStringSetter)
            val oldValue = "oldValue"
            val token = Token(oldValue)
            val user = User(token)

            forAll({ value: String ->
                joinedSetter.set(token.left(), value).swap().getOrElse { Token("Wrong value") }.value ==
                        joinedSetter.set(user.right(), value).getOrElse { User(Token("Wrong value")) }.token.value
            })
        }

        "Lifting a function should yield the same result as direct modify" {
            forAll(TokenGen, Gen.string(), { token, value ->
                tokenSetter.modify(token) { value } == tokenSetter.lift { value }(token)
            })
        }

    }

}
