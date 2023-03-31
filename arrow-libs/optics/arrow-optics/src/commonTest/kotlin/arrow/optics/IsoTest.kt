package arrow.optics

import arrow.core.Either
import arrow.optics.test.functionAToB
import arrow.optics.test.laws.IsoLaws
import arrow.optics.test.laws.LensLaws
import arrow.optics.test.laws.OptionalLaws
import arrow.optics.test.laws.PrismLaws
import arrow.optics.test.laws.TraversalLaws
import arrow.optics.test.laws.testLaws
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll

class IsoTest : StringSpec({

  val aIso: Iso<SumType.A, String> = Iso(
    get = { a: SumType.A -> a.string },
    reverseGet = SumType::A
  )

  testLaws(
    "Iso token - ",
    LensLaws(
      lens = Iso.token(),
      aGen = Arb.token(),
      bGen = Arb.string(),
      funcGen = Arb.functionAToB(Arb.string())
    ),

    PrismLaws(
      prism = aIso,
      aGen = Arb.sumTypeA(),
      bGen = Arb.string(),
      funcGen = Arb.functionAToB(Arb.string())
    ),

    TraversalLaws(
      traversal = Iso.token(),
      aGen = Arb.token(),
      bGen = Arb.string(),
      funcGen = Arb.functionAToB(Arb.string())
    ),

    OptionalLaws(
      optional = Iso.token(),
      aGen = Arb.token(),
      bGen = Arb.string(),
      funcGen = Arb.functionAToB(Arb.string())
    ),

    IsoLaws(
      iso = Iso.token(),
      aGen = Arb.token(),
      bGen = Arb.string(),
      funcGen = Arb.functionAToB(Arb.string())
    )
  )

  with(Iso.token()) {

    "asFold should behave as valid Fold: size" {
      checkAll(Arb.token()) { token ->
        size(token) shouldBe 1
      }
    }

    "asFold should behave as valid Fold: nonEmpty" {
      checkAll(Arb.token()) { token ->
        isNotEmpty(token) shouldBe true
      }
    }

    "asFold should behave as valid Fold: isEmpty" {
      checkAll(Arb.token()) { token ->
        !isEmpty(token) shouldBe true
      }
    }

    "asFold should behave as valid Fold: getAll" {
      checkAll(Arb.token()) { token ->
        getAll(token) shouldBe listOf(token.value)
      }
    }

    "asFold should behave as valid Fold: fold" {
      checkAll(Arb.token()) { token ->
        fold("", { x, y -> x + y }, token) shouldBe token.value
      }
    }

    "asFold should behave as valid Fold: headOption" {
      checkAll(Arb.token()) { token ->
        firstOrNull(token) shouldBe token.value
      }
    }

    "asFold should behave as valid Fold: lastOption" {
      checkAll(Arb.token()) { token ->
        lastOrNull(token) shouldBe token.value
      }
    }
  }

  "Lifting a function should yield the same result as not yielding" {
    checkAll(Arb.token(), Arb.string()) { token, value ->
      Iso.token().modify(token) { value } shouldBe Iso.token().lift { value }(token)
    }
  }

  "Creating a first pair with a type should result in the target to value" {
    val first = Iso.token().first<Int>()
    checkAll(Arb.token(), Arb.int()) { token: Token, int: Int ->
      first.get(token to int) shouldBe (token.value to int)
    }
  }

  "Creating a second pair with a type should result in the value to target" {
    val second = Iso.token().second<Int>()
    checkAll(Arb.int(), Arb.token()) { int: Int, token: Token ->
      second.get(int to token) shouldBe (int to token.value)
    }
  }

  "Creating a left with a type should result in a sum target to value" {
    val left = Iso.token().left<Int>()
    checkAll(Arb.token(), Arb.int()) { token: Token, int: Int ->
      left.get(Either.Left(token)) shouldBe Either.Left(token.value)
      left.get(Either.Right(int)) shouldBe Either.Right(int)
    }
  }

  "Creating a right with a type should result in a sum value to target" {
    val left = Iso.token().right<Int>()
    checkAll(Arb.token(), Arb.int()) { token: Token, int: Int ->
      left.get(Either.Left(int)) shouldBe Either.Left(int)
      left.get(Either.Right(token)) shouldBe Either.Right(token.value)
    }
  }

  "Finding a target using a predicate within a Iso should be wrapped in the correct option result" {
    checkAll(Arb.boolean()) { predicate: Boolean ->
      (Iso.token().findOrNull(Token("any value")) { predicate }?.let { true } ?: false) shouldBe predicate
    }
  }

  "Checking existence predicate over the target should result in same result as predicate" {
    checkAll(Arb.boolean()) { predicate: Boolean ->
      Iso.token().any(Token("any value")) { predicate } shouldBe predicate
    }
  }

  "Pairing two disjoint isos together" {
    val joinedIso = Iso.token() split Iso.user()

    checkAll(Arb.string()) { tokenValue: String ->
      val token = Token(tokenValue)
      val user = User(token)
      joinedIso.get(token to user) shouldBe (tokenValue to token)
    }
  }

  "Composing isos should result in an iso of the first iso's value with the second iso's target" {
    val composedIso = Iso.user() compose Iso.token()

    checkAll(Arb.string()) { tokenValue: String ->
      val token = Token(tokenValue)
      val user = User(token)
      composedIso.get(user) shouldBe tokenValue
    }
  }

})
