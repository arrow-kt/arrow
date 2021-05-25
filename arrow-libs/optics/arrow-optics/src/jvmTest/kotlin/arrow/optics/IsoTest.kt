package arrow.optics

import arrow.core.Either
import arrow.core.test.UnitSpec
import arrow.core.test.generators.functionAToB
import arrow.optics.test.laws.IsoLaws
import arrow.optics.test.laws.LensLaws
import arrow.optics.test.laws.OptionalLaws
import arrow.optics.test.laws.PrismLaws
import arrow.optics.test.laws.SetterLaws
import arrow.optics.test.laws.TraversalLaws
import arrow.typeclasses.Monoid
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bool
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll

class IsoTest : UnitSpec() {

  init {

    val aIso: Iso<SumType.A, String> = Iso(
      get = { a: SumType.A -> a.string },
      reverseGet = SumType::A
    )

    testLaws(
      "Iso token - ",
      LensLaws.laws(
        lens = tokenIso,
        aGen = genToken,
        bGen = Arb.string(),
        funcGen = Arb.functionAToB(Arb.string())
      ),

      PrismLaws.laws(
        prism = aIso,
        aGen = genSumTypeA,
        bGen = Arb.string(),
        funcGen = Arb.functionAToB(Arb.string())
      ),

      TraversalLaws.laws(
        traversal = tokenIso,
        aGen = genToken,
        bGen = Arb.string(),
        funcGen = Arb.functionAToB(Arb.string())
      ),

      OptionalLaws.laws(
        optional = tokenIso,
        aGen = genToken,
        bGen = Arb.string(),
        funcGen = Arb.functionAToB(Arb.string())
      ),

      SetterLaws.laws(
        setter = tokenIso,
        aGen = genToken,
        bGen = Arb.string(),
        funcGen = Arb.functionAToB(Arb.string())
      ),

      IsoLaws.laws(
        iso = tokenIso,
        aGen = genToken,
        bGen = Arb.string(),
        funcGen = Arb.functionAToB(Arb.string())
      )
    )

    with(tokenIso) {

      "asFold should behave as valid Fold: size" {
        checkAll(genToken) { token ->
          size(token) shouldBe 1
        }
      }

      "asFold should behave as valid Fold: nonEmpty" {
        checkAll(genToken) { token ->
          isNotEmpty(token) shouldBe true
        }
      }

      "asFold should behave as valid Fold: isEmpty" {
        checkAll(genToken) { token ->
          !isEmpty(token) shouldBe true
        }
      }

      "asFold should behave as valid Fold: getAll" {
        checkAll(genToken) { token ->
          getAll(token) shouldBe listOf(token.value)
        }
      }

      "asFold should behave as valid Fold: combineAll" {
        checkAll(genToken) { token ->
          combineAll(Monoid.string(), token) shouldBe token.value
        }
      }

      "asFold should behave as valid Fold: fold" {
        checkAll(genToken) { token ->
          fold(Monoid.string(), token) shouldBe token.value
        }
      }

      "asFold should behave as valid Fold: headOption" {
        checkAll(genToken) { token ->
          firstOrNull(token) shouldBe token.value
        }
      }

      "asFold should behave as valid Fold: lastOption" {
        checkAll(genToken) { token ->
          lastOrNull(token) shouldBe token.value
        }
      }
    }

    with(tokenIso) {

      "asGetter should behave as valid Getter: get" {
        checkAll(genToken) { token ->
          get(token) shouldBe tokenGetter.get(token)
        }
      }

      "asGetter should behave as valid Getter: find" {
        checkAll(genToken, Arb.functionAToB<String, Boolean>(Arb.bool())) { token, p ->
          findOrNull(token, p) shouldBe tokenGetter.findOrNull(token, p)
        }
      }

      "asGetter should behave as valid Getter: exist" {
        checkAll(genToken, Arb.functionAToB<String, Boolean>(Arb.bool())) { token, p ->
          any(token, p) shouldBe tokenGetter.any(token, p)
        }
      }
    }

    "Lifting a function should yield the same result as not yielding" {
      checkAll(genToken, Arb.string()) { token, value ->
        tokenIso.modify(token) { value } shouldBe tokenIso.lift { value }(token)
      }
    }

    "Creating a first pair with a type should result in the target to value" {
      val first = tokenIso.first<Int>()
      checkAll(genToken, Arb.int()) { token: Token, int: Int ->
        first.get(token to int) shouldBe (token.value to int)
      }
    }

    "Creating a second pair with a type should result in the value to target" {
      val second = tokenIso.second<Int>()
      checkAll(Arb.int(), genToken) { int: Int, token: Token ->
        second.get(int to token) shouldBe (int to token.value)
      }
    }

    "Creating a left with a type should result in a sum target to value" {
      val left = tokenIso.left<Int>()
      checkAll(genToken, Arb.int()) { token: Token, int: Int ->
        left.get(Either.Left(token)) shouldBe Either.Left(token.value)
        left.get(Either.Right(int)) shouldBe Either.Right(int)
      }
    }

    "Creating a right with a type should result in a sum value to target" {
      val left = tokenIso.right<Int>()
      checkAll(genToken, Arb.int()) { token: Token, int: Int ->
        left.get(Either.Left(int)) shouldBe Either.Left(int)
        left.get(Either.Right(token)) shouldBe Either.Right(token.value)
      }
    }

    "Finding a target using a predicate within a Iso should be wrapped in the correct option result" {
      checkAll { predicate: Boolean ->
        tokenIso.findOrNull(Token("any value")) { predicate }?.let { true } ?: false shouldBe predicate
      }
    }

    "Checking existence predicate over the target should result in same result as predicate" {
      checkAll { predicate: Boolean ->
        tokenIso.any(Token("any value")) { predicate } shouldBe predicate
      }
    }

    "Pairing two disjoint isos together" {
      val joinedIso = tokenIso split userIso

      checkAll { tokenValue: String ->
        val token = Token(tokenValue)
        val user = User(token)
        joinedIso.get(token to user) shouldBe (tokenValue to token)
      }
    }

    "Composing isos should result in an iso of the first iso's value with the second iso's target" {
      val composedIso = userIso compose tokenIso

      checkAll { tokenValue: String ->
        val token = Token(tokenValue)
        val user = User(token)
        composedIso.get(user) shouldBe tokenValue
      }
    }
  }
}
