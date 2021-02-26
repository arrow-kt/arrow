package arrow.optics

import arrow.core.Left
import arrow.core.ListK
import arrow.core.Option
import arrow.core.Right
import arrow.core.extensions.listk.eq.eq
import arrow.core.extensions.option.eq.eq
import arrow.core.int
import arrow.core.string
import arrow.core.test.UnitSpec
import arrow.core.test.generators.functionAToB
import arrow.optics.test.laws.LensLaws
import arrow.optics.test.laws.OptionalLaws
import arrow.optics.test.laws.SetterLaws
import arrow.optics.test.laws.TraversalLaws
import arrow.typeclasses.Eq
import arrow.typeclasses.Monoid
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

class LensTest : UnitSpec() {

  init {
    testLaws(
      LensLaws.laws(
        lens = tokenLens,
        aGen = genToken,
        bGen = Gen.string(),
        funcGen = Gen.functionAToB(Gen.string()),
        EQA = Eq.any(),
        EQB = Eq.any(),
        MB = Monoid.string()
      ),

      TraversalLaws.laws(
        traversal = tokenLens,
        aGen = genToken,
        bGen = Gen.string(),
        funcGen = Gen.functionAToB(Gen.string()),
        EQA = Eq.any(),
        EQOptionB = Option.eq(Eq.any()),
        EQListB = ListK.eq(Eq.any())
      ),

      OptionalLaws.laws(
        optional = tokenLens,
        aGen = genToken,
        bGen = Gen.string(),
        funcGen = Gen.functionAToB(Gen.string()),
        EQA = Eq.any(),
        EQOptionB = Option.eq(Eq.any())
      ),

      SetterLaws.laws(
        setter = tokenLens,
        aGen = genToken,
        bGen = Gen.string(),
        funcGen = Gen.functionAToB(Gen.string()),
        EQA = Token.eq()
      )
    )

    testLaws(
      LensLaws.laws(
        lens = Lens.id(),
        aGen = Gen.int(),
        bGen = Gen.int(),
        funcGen = Gen.functionAToB(Gen.int()),
        EQA = Eq.any(),
        EQB = Eq.any(),
        MB = Monoid.int()
      )
    )

    "asFold should behave as valid Fold: size" {
      forAll(genToken) { token ->
        tokenLens.size(token) == 1
      }
    }

    "asFold should behave as valid Fold: nonEmpty" {
      forAll(genToken) { token ->
        tokenLens.isNotEmpty(token)
      }
    }

    "asFold should behave as valid Fold: isEmpty" {
      forAll(genToken) { token ->
        !tokenLens.isEmpty(token)
      }
    }

    "asFold should behave as valid Fold: getAll" {
      forAll(genToken) { token ->
        tokenLens.getAll(token) == listOf(token.value)
      }
    }

    "asFold should behave as valid Fold: combineAll" {
      forAll(genToken) { token ->
        tokenLens.combineAll(Monoid.string(), token) == token.value
      }
    }

    "asFold should behave as valid Fold: fold" {
      forAll(genToken) { token ->
        tokenLens.fold(Monoid.string(), token) == token.value
      }
    }

    "asFold should behave as valid Fold: headOption" {
      forAll(genToken) { token ->
        tokenLens.firstOrNull(token) == token.value
      }
    }

    "asFold should behave as valid Fold: lastOption" {
      forAll(genToken) { token ->
        tokenLens.lastOrNull(token) == token.value
      }
    }

    "asGetter should behave as valid Getter: get" {
      forAll(genToken) { token ->
        tokenLens.get(token) == tokenGetter.get(token)
      }
    }

    "asGetter should behave as valid Getter: find" {
      forAll(genToken, Gen.functionAToB<String, Boolean>(Gen.bool())) { token, p ->
        tokenLens.findOrNull(token, p) == tokenGetter.findOrNull(token, p)
      }
    }

    "asGetter should behave as valid Getter: exist" {
      forAll(genToken, Gen.functionAToB<String, Boolean>(Gen.bool())) { token, p ->
        tokenLens.any(token, p) == tokenGetter.any(token, p)
      }
    }

    "Lifting a function should yield the same result as not yielding" {
      forAll(genToken, Gen.string()) { token, value ->
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
      forAll(genToken, genUser) { token: Token, user: User ->
        spiltLens.get(token to user) == token.value to user.token
      }
    }

    "Creating a first pair with a type should result in the target to value" {
      val first = tokenLens.first<Int>()
      forAll(genToken, Gen.int()) { token: Token, int: Int ->
        first.get(token to int) == token.value to int
      }
    }

    "Creating a second pair with a type should result in the value target" {
      val second = tokenLens.second<Int>()
      forAll(Gen.int(), genToken) { int: Int, token: Token ->
        second.get(int to token) == int to token.value
      }
    }
  }
}
