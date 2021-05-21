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
import io.kotest.property.Arb
import io.kotest.property.checkAll

class IsoTest : UnitSpec() {

  init {

    val aIso: Iso<SumType.A, String> = Iso(
      get = { a: SumType.A -> a.string },
      reverseGet = SumType::A
    )

    testLaws(
      LensLaws.laws(
        lens = tokenIso,
        aGen = genToken,
        bGen = Gen.string(),
        funcGen = Gen.functionAToB(Gen.string())
      ),

      PrismLaws.laws(
        prism = aIso,
        aGen = genSumTypeA,
        bGen = Gen.string(),
        funcGen = Gen.functionAToB(Gen.string())
      ),

      TraversalLaws.laws(
        traversal = tokenIso,
        aGen = genToken,
        bGen = Gen.string(),
        funcGen = Gen.functionAToB(Gen.string())
      ),

      OptionalLaws.laws(
        optional = tokenIso,
        aGen = genToken,
        bGen = Gen.string(),
        funcGen = Gen.functionAToB(Gen.string())
      ),

      SetterLaws.laws(
        setter = tokenIso,
        aGen = genToken,
        bGen = Gen.string(),
        funcGen = Gen.functionAToB(Gen.string())
      ),

      IsoLaws.laws(
        iso = tokenIso,
        aGen = genToken,
        bGen = Gen.string(),
        funcGen = Gen.functionAToB(Gen.string())
      )
    )

    with(tokenIso) {

      "asFold should behave as valid Fold: size" {
        checkAll(genToken) { token ->
          size(token) == 1
        }
      }

      "asFold should behave as valid Fold: nonEmpty" {
        checkAll(genToken) { token ->
          isNotEmpty(token)
        }
      }

      "asFold should behave as valid Fold: isEmpty" {
        checkAll(genToken) { token ->
          !isEmpty(token)
        }
      }

      "asFold should behave as valid Fold: getAll" {
        checkAll(genToken) { token ->
          getAll(token) == listOf(token.value)
        }
      }

      "asFold should behave as valid Fold: combineAll" {
        checkAll(genToken) { token ->
          combineAll(Monoid.string(), token) == token.value
        }
      }

      "asFold should behave as valid Fold: fold" {
        checkAll(genToken) { token ->
          fold(Monoid.string(), token) == token.value
        }
      }

      "asFold should behave as valid Fold: headOption" {
        checkAll(genToken) { token ->
          firstOrNull(token) == token.value
        }
      }

      "asFold should behave as valid Fold: lastOption" {
        checkAll(genToken) { token ->
          lastOrNull(token) == token.value
        }
      }
    }

    with(tokenIso) {

      "asGetter should behave as valid Getter: get" {
        checkAll(genToken) { token ->
          get(token) == tokenGetter.get(token)
        }
      }

      "asGetter should behave as valid Getter: find" {
        checkAll(genToken, Gen.functionAToB<String, Boolean>(Gen.bool())) { token, p ->
          findOrNull(token, p) == tokenGetter.findOrNull(token, p)
        }
      }

      "asGetter should behave as valid Getter: exist" {
        checkAll(genToken, Gen.functionAToB<String, Boolean>(Gen.bool())) { token, p ->
          any(token, p) == tokenGetter.any(token, p)
        }
      }
    }

    "Lifting a function should yield the same result as not yielding" {
      checkAll(genToken, Gen.string()) { token, value ->
        tokenIso.modify(token) { value } == tokenIso.lift { value }(token)
      }
    }

    "Creating a first pair with a type should result in the target to value" {
      val first = tokenIso.first<Int>()
      checkAll(genToken, Gen.int()) { token: Token, int: Int ->
        first.get(token to int) == token.value to int
      }
    }

    "Creating a second pair with a type should result in the value to target" {
      val second = tokenIso.second<Int>()
      checkAll(Gen.int(), genToken) { int: Int, token: Token ->
        second.get(int to token) == int to token.value
      }
    }

    "Creating a left with a type should result in a sum target to value" {
      val left = tokenIso.left<Int>()
      checkAll(genToken, Gen.int()) { token: Token, int: Int ->
        left.get(Either.Left(token)) == Either.Left(token.value) &&
          left.get(Either.Right(int)) == Either.Right(int)
      }
    }

    "Creating a right with a type should result in a sum value to target" {
      val left = tokenIso.right<Int>()
      checkAll(genToken, Gen.int()) { token: Token, int: Int ->
        left.get(Either.Left(int)) == Either.Left(int) &&
          left.get(Either.Right(token)) == Either.Right(token.value)
      }
    }

    "Finding a target using a predicate within a Iso should be wrapped in the correct option result" {
      forAll { predicate: Boolean ->
        tokenIso.findOrNull(Token("any value")) { predicate }?.let { true } ?: false == predicate
      }
    }

    "Checking existence predicate over the target should result in same result as predicate" {
      forAll { predicate: Boolean ->
        tokenIso.any(Token("any value")) { predicate } == predicate
      }
    }

    "Pairing two disjoint isos together" {
      val joinedIso = tokenIso split userIso

      forAll { tokenValue: String ->
        val token = Token(tokenValue)
        val user = User(token)
        joinedIso.get(token to user) == tokenValue to token
      }
    }

    "Composing isos should result in an iso of the first iso's value with the second iso's target" {
      val composedIso = userIso compose tokenIso

      forAll { tokenValue: String ->
        val token = Token(tokenValue)
        val user = User(token)
        composedIso.get(user) == tokenValue
      }
    }
  }
}
