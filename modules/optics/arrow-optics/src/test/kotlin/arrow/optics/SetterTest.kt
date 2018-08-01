package arrow.optics

import arrow.core.*
import arrow.test.UnitSpec
import arrow.test.generators.genFunctionAToB
import arrow.test.generators.genOption
import arrow.test.laws.SetterLaws
import arrow.typeclasses.Eq
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
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
      setter = Setter.fromFunctor<ForOption, String, String>(Option.functor()),
      aGen = genOption(Gen.string()),
      bGen = Gen.string(),
      funcGen = genFunctionAToB(Gen.string()),
      EQA = Eq.any()
    ))

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
