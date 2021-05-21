package arrow.optics

import arrow.core.getOrElse
import arrow.core.left
import arrow.core.right
import arrow.core.test.UnitSpec
import arrow.core.test.generators.functionAToB
import arrow.optics.test.laws.SetterLaws
import io.kotest.property.Arb
import io.kotest.property.checkAll

class SetterTest : UnitSpec() {

  init {

    testLaws(
      SetterLaws.laws(
        setter = Setter.id(),
        aGen = Gen.int(),
        bGen = Gen.int(),
        funcGen = Gen.functionAToB(Gen.int()),
      )
    )

    testLaws(
      SetterLaws.laws(
        setter = tokenSetter,
        aGen = genToken,
        bGen = Gen.string(),
        funcGen = Gen.functionAToB(Gen.string()),
      )
    )

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
      checkAll(genToken, Gen.string()) { token, value ->
        tokenSetter.modify(token) { value } == tokenSetter.lift { value }(token)
      }
    }
  }
}
