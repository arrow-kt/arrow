package arrow.optics

import arrow.core.getOrElse
import arrow.core.left
import arrow.core.right
import arrow.core.test.UnitSpec
import arrow.core.test.generators.functionAToB
import arrow.optics.test.laws.SetterLaws
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.string

class SetterTest : UnitSpec() {

  init {

    testLaws(
      "Setter identity - ",
      SetterLaws.laws(
        setter = Setter.id(),
        aGen = Arb.int(),
        bGen = Arb.int(),
        funcGen = Arb.functionAToB(Arb.int()),
      )
    )

    testLaws(
      "Setter token - ",
      SetterLaws.laws(
        setter = Setter.token(),
        aGen = Arb.token(),
        bGen = Arb.string(),
        funcGen = Arb.functionAToB(Arb.string()),
      )
    )

    "Joining two lenses together with same target should yield same result" {
      val userTokenStringSetter = Setter.user() compose Setter.token()
      val joinedSetter = Setter.token().choice(userTokenStringSetter)
      val oldValue = "oldValue"
      val token = Token(oldValue)
      val user = User(token)

      checkAll(Arb.string()) { value: String ->
        joinedSetter.set(token.left(), value).swap().getOrElse { Token("Wrong value") }.value shouldBe
          joinedSetter.set(user.right(), value).getOrElse { User(Token("Wrong value")) }.token.value
      }
    }

    "Lifting a function should yield the same result as direct modify" {
      checkAll(Arb.token(), Arb.string()) { token, value ->
        Setter.token().modify(token) { value } shouldBe Setter.token().lift { value }(token)
      }
    }
  }
}
