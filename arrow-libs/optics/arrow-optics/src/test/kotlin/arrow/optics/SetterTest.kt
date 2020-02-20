package arrow.optics

import arrow.Kind
import arrow.core.ForOption
import arrow.core.Option
import arrow.core.left
import arrow.core.right
import arrow.core.toT
import arrow.core.extensions.option.functor.functor
import arrow.core.getOrElse
import arrow.mtl.State
import arrow.mtl.run
import arrow.optics.mtl.assign_
import arrow.optics.mtl.update_
import arrow.test.UnitSpec
import arrow.test.generators.functionAToB
import arrow.test.generators.option
import arrow.test.laws.SetterLaws
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

class SetterTest : UnitSpec() {

  init {

    testLaws(SetterLaws.laws(
      setter = Setter.id(),
      aGen = Gen.int(),
      bGen = Gen.int(),
      funcGen = Gen.functionAToB(Gen.int()),
      EQA = Eq.any()
    ))

    testLaws(SetterLaws.laws(
      setter = tokenSetter,
      aGen = genToken,
      bGen = Gen.string(),
      funcGen = Gen.functionAToB(Gen.string()),
      EQA = Eq.any()
    ))

    testLaws(SetterLaws.laws(
      setter = Setter.fromFunctor<ForOption, String, String>(Option.functor()),
      aGen = Gen.option(Gen.string()).map<Kind<ForOption, String>> { it },
      bGen = Gen.string(),
      funcGen = Gen.functionAToB(Gen.string()),
      EQA = Eq.any()
    ))

    "Joining two lenses together with same target should yield same result" {
      val userTokenStringSetter = userSetter compose tokenSetter
      val joinedSetter = tokenSetter.choice(userTokenStringSetter)
      val oldValue = "oldValue"
      val token = Token(oldValue)
      val user = User(token)

      forAll { value: String ->
        joinedSetter.set(token.left(), value).swap().getOrElse { Token("Wrong value") }.value ==
          joinedSetter.set(user.right(), value).getOrElse { User(Token("Wrong value")) }.token.value
      }
    }

    "Lifting a function should yield the same result as direct modify" {
      forAll(genToken, Gen.string()) { token, value ->
        tokenSetter.modify(token) { value } == tokenSetter.lift { value }(token)
      }
    }

    "update_ f should be as modify f within State and returning Unit" {
      forAll(genToken, Gen.functionAToB<String, String>(Gen.string())) { generatedToken, f ->
        tokenSetter.update_(f).run(generatedToken) ==
          State { token: Token ->
            tokenSetter.modify(token, f) toT Unit
          }.run(generatedToken)
      }
    }

    "assign_ f should be as modify f within State and returning Unit" {
      forAll(genToken, Gen.string()) { generatedToken, string ->
        tokenSetter.assign_(string).run(generatedToken) ==
          State { token: Token ->
            tokenSetter.set(token, string) toT Unit
          }.run(generatedToken)
      }
    }
  }
}
