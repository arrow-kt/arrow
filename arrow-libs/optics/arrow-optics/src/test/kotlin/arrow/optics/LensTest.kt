package arrow.optics

import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.core.test.UnitSpec
import arrow.core.test.generators.functionAToB
import arrow.optics.test.laws.LensLaws
import arrow.optics.test.laws.OptionalLaws
import arrow.optics.test.laws.SetterLaws
import arrow.optics.test.laws.TraversalLaws
import arrow.typeclasses.Monoid
import io.kotest.property.Arb
import io.kotest.property.checkAll

class LensTest : UnitSpec() {

  init {
    testLaws(
      LensLaws.laws(
        lens = tokenLens,
        aGen = genToken,
        bGen = Gen.string(),
        funcGen = Gen.functionAToB(Gen.string())
      ),

      TraversalLaws.laws(
        traversal = tokenLens,
        aGen = genToken,
        bGen = Gen.string(),
        funcGen = Gen.functionAToB(Gen.string()),
      ),

      OptionalLaws.laws(
        optional = tokenLens,
        aGen = genToken,
        bGen = Gen.string(),
        funcGen = Gen.functionAToB(Gen.string()),
      ),

      SetterLaws.laws(
        setter = tokenLens,
        aGen = genToken,
        bGen = Gen.string(),
        funcGen = Gen.functionAToB(Gen.string()),
      )
    )

    testLaws(
      LensLaws.laws(
        lens = Lens.id(),
        aGen = Gen.int(),
        bGen = Gen.int(),
        funcGen = Gen.functionAToB(Gen.int()),
      )
    )

    "asFold should behave as valid Fold: size" {
      checkAll(genToken) { token ->
        tokenLens.size(token) == 1
      }
    }

    "asFold should behave as valid Fold: nonEmpty" {
      checkAll(genToken) { token ->
        tokenLens.isNotEmpty(token)
      }
    }

    "asFold should behave as valid Fold: isEmpty" {
      checkAll(genToken) { token ->
        !tokenLens.isEmpty(token)
      }
    }

    "asFold should behave as valid Fold: getAll" {
      checkAll(genToken) { token ->
        tokenLens.getAll(token) == listOf(token.value)
      }
    }

    "asFold should behave as valid Fold: combineAll" {
      checkAll(genToken) { token ->
        tokenLens.combineAll(Monoid.string(), token) == token.value
      }
    }

    "asFold should behave as valid Fold: fold" {
      checkAll(genToken) { token ->
        tokenLens.fold(Monoid.string(), token) == token.value
      }
    }

    "asFold should behave as valid Fold: headOption" {
      checkAll(genToken) { token ->
        tokenLens.firstOrNull(token) == token.value
      }
    }

    "asFold should behave as valid Fold: lastOption" {
      checkAll(genToken) { token ->
        tokenLens.lastOrNull(token) == token.value
      }
    }

    "asGetter should behave as valid Getter: get" {
      checkAll(genToken) { token ->
        tokenLens.get(token) == tokenGetter.get(token)
      }
    }

    "asGetter should behave as valid Getter: find" {
      checkAll(genToken, Gen.functionAToB<String, Boolean>(Gen.bool())) { token, p ->
        tokenLens.findOrNull(token, p) == tokenGetter.findOrNull(token, p)
      }
    }

    "asGetter should behave as valid Getter: exist" {
      checkAll(genToken, Gen.functionAToB<String, Boolean>(Gen.bool())) { token, p ->
        tokenLens.any(token, p) == tokenGetter.any(token, p)
      }
    }

    "Lifting a function should yield the same result as not yielding" {
      checkAll(genToken, Gen.string()) { token, value ->
        tokenLens.set(token, value) == tokenLens.lift { value }(token)
      }
    }

    "Finding a target using a predicate within a Lens should be wrapped in the correct option result" {
      forAll { predicate: Boolean ->
        tokenLens.findOrNull(Token("any value")) { predicate }?.let { true } ?: false == predicate
      }
    }

    "Checking existence predicate over the target should result in same result as predicate" {
      forAll { predicate: Boolean ->
        tokenLens.any(Token("any value")) { predicate } == predicate
      }
    }

    "Joining two lenses together with same target should yield same result" {
      val userTokenStringLens = userLens compose tokenLens
      val joinedLens = tokenLens choice userTokenStringLens

      forAll { tokenValue: String ->
        val token = Token(tokenValue)
        val user = User(token)
        joinedLens.get(Left(token)) == joinedLens.get(Right(user))
      }
    }

    "Pairing two disjoint lenses should yield a pair of their results" {
      val spiltLens: Lens<Pair<Token, User>, Pair<String, Token>> = tokenLens split userLens
      checkAll(genToken, genUser) { token: Token, user: User ->
        spiltLens.get(token to user) == token.value to user.token
      }
    }

    "Creating a first pair with a type should result in the target to value" {
      val first = tokenLens.first<Int>()
      checkAll(genToken, Gen.int()) { token: Token, int: Int ->
        first.get(token to int) == token.value to int
      }
    }

    "Creating a second pair with a type should result in the value target" {
      val second = tokenLens.second<Int>()
      checkAll(Gen.int(), genToken) { int: Int, token: Token ->
        second.get(int to token) == int to token.value
      }
    }
  }
}
