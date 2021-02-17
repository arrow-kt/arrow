package arrow.optics

import arrow.core.Either
import arrow.core.Option
import arrow.core.extensions.monoid
import arrow.core.extensions.listk.eq.eq
import arrow.core.extensions.option.eq.eq
import arrow.core.ListK
import arrow.core.string
import arrow.core.test.UnitSpec
import arrow.core.test.generators.functionAToB
import arrow.optics.test.laws.IsoLaws
import arrow.optics.test.laws.LensLaws
import arrow.optics.test.laws.OptionalLaws
import arrow.optics.test.laws.PrismLaws
import arrow.optics.test.laws.SetterLaws
import arrow.optics.test.laws.TraversalLaws
import arrow.typeclasses.Eq
import arrow.typeclasses.Monoid
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

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
        funcGen = Gen.functionAToB(Gen.string()),
        EQA = Token.eq(),
        EQB = Eq.any(),
        MB = String.monoid()
      ),

      PrismLaws.laws(
        prism = aIso,
        aGen = genSumTypeA,
        bGen = Gen.string(),
        funcGen = Gen.functionAToB(Gen.string()),
        EQA = Eq.any()
      ),

      TraversalLaws.laws(
        traversal = tokenIso,
        aGen = genToken,
        bGen = Gen.string(),
        funcGen = Gen.functionAToB(Gen.string()),
        EQA = Eq.any(),
        EQOptionB = Option.eq(Eq.any()),
        EQListB = ListK.eq(Eq.any())
      ),

      OptionalLaws.laws(
        optional = tokenIso,
        aGen = genToken,
        bGen = Gen.string(),
        funcGen = Gen.functionAToB(Gen.string()),
        EQA = Eq.any(),
        EQOptionB = Option.eq(Eq.any())
      ),

      SetterLaws.laws(
        setter = tokenIso,
        aGen = genToken,
        bGen = Gen.string(),
        funcGen = Gen.functionAToB(Gen.string()),
        EQA = Token.eq()
      ),

      IsoLaws.laws(
        iso = tokenIso,
        aGen = genToken,
        bGen = Gen.string(),
        funcGen = Gen.functionAToB(Gen.string()),
        EQA = Token.eq(),
        EQB = Eq.any(),
        bMonoid = String.monoid()
      )
    )

    with(tokenIso) {

      "asFold should behave as valid Fold: size" {
        forAll(genToken) { token ->
          size(token) == 1
        }
      }

      "asFold should behave as valid Fold: nonEmpty" {
        forAll(genToken) { token ->
          isNotEmpty(token)
        }
      }

      "asFold should behave as valid Fold: isEmpty" {
        forAll(genToken) { token ->
          !isEmpty(token)
        }
      }

      "asFold should behave as valid Fold: getAll" {
        forAll(genToken) { token ->
          getAll(token) == listOf(token.value)
        }
      }

      "asFold should behave as valid Fold: combineAll" {
        forAll(genToken) { token ->
          combineAll(Monoid.string(), token) == token.value
        }
      }

      "asFold should behave as valid Fold: fold" {
        forAll(genToken) { token ->
          fold(Monoid.string(), token) == token.value
        }
      }

      "asFold should behave as valid Fold: headOption" {
        forAll(genToken) { token ->
          firstOrNull(token) == token.value
        }
      }

      "asFold should behave as valid Fold: lastOption" {
        forAll(genToken) { token ->
          lastOrNull(token) == token.value
        }
      }
    }

    with(tokenIso) {

      "asGetter should behave as valid Getter: get" {
        forAll(genToken) { token ->
          get(token) == tokenGetter.get(token)
        }
      }

      "asGetter should behave as valid Getter: find" {
        forAll(genToken, Gen.functionAToB<String, Boolean>(Gen.bool())) { token, p ->
          findOrNull(token, p) == tokenGetter.findOrNull(token, p)
        }
      }

      "asGetter should behave as valid Getter: exist" {
        forAll(genToken, Gen.functionAToB<String, Boolean>(Gen.bool())) { token, p ->
          any(token, p) == tokenGetter.any(token, p)
        }
      }
    }

    "Lifting a function should yield the same result as not yielding" {
      forAll(genToken, Gen.string()) { token, value ->
        tokenIso.modify(token) { value } == tokenIso.lift { value }(token)
      }
    }

    "Creating a first pair with a type should result in the target to value" {
      val first = tokenIso.first<Int>()
      forAll(genToken, Gen.int()) { token: Token, int: Int ->
        first.get(token to int) == token.value to int
      }
    }

    "Creating a second pair with a type should result in the value to target" {
      val second = tokenIso.second<Int>()
      forAll(Gen.int(), genToken) { int: Int, token: Token ->
        second.get(int to token) == int to token.value
      }
    }

    "Creating a left with a type should result in a sum target to value" {
      val left = tokenIso.left<Int>()
      forAll(genToken, Gen.int()) { token: Token, int: Int ->
        left.get(Either.Left(token)) == Either.Left(token.value) &&
          left.get(Either.Right(int)) == Either.Right(int)
      }
    }

    "Creating a right with a type should result in a sum value to target" {
      val left = tokenIso.right<Int>()
      forAll(genToken, Gen.int()) { token: Token, int: Int ->
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
