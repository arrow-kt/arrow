package arrow.optics

import arrow.core.Either
import arrow.core.Option
import arrow.core.Some
import arrow.core.extensions.monoid
import arrow.core.extensions.listk.eq.eq
import arrow.core.extensions.option.eq.eq
import arrow.core.extensions.option.functor.functor
import arrow.core.toT
import arrow.core.ListK
import arrow.core.k
import arrow.test.UnitSpec
import arrow.test.generators.functionAToB
import arrow.test.laws.IsoLaws
import arrow.test.laws.LensLaws
import arrow.test.laws.OptionalLaws
import arrow.test.laws.PrismLaws
import arrow.test.laws.SetterLaws
import arrow.test.laws.TraversalLaws
import arrow.typeclasses.Eq
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
        lens = tokenIso.asLens(),
        aGen = genToken,
        bGen = Gen.string(),
        funcGen = Gen.functionAToB(Gen.string()),
        EQA = Token.eq(),
        EQB = Eq.any(),
        MB = String.monoid()
      ),

      PrismLaws.laws(
        prism = aIso.asPrism(),
        aGen = genSumTypeA,
        bGen = Gen.string(),
        funcGen = Gen.functionAToB(Gen.string()),
        EQA = Eq.any(),
        EQOptionB = Eq.any()
      ),

      TraversalLaws.laws(
        traversal = tokenIso.asTraversal(),
        aGen = genToken,
        bGen = Gen.string(),
        funcGen = Gen.functionAToB(Gen.string()),
        EQA = Eq.any(),
        EQOptionB = Option.eq(Eq.any()),
        EQListB = ListK.eq(Eq.any())
      ),

      OptionalLaws.laws(
        optional = tokenIso.asOptional(),
        aGen = genToken,
        bGen = Gen.string(),
        funcGen = Gen.functionAToB(Gen.string()),
        EQA = Eq.any(),
        EQOptionB = Option.eq(Eq.any())
      ),

      SetterLaws.laws(
        setter = tokenIso.asSetter(),
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

    with(tokenIso.asFold()) {

      "asFold should behave as valid Fold: size" {
        forAll(genToken) { token ->
          size(token) == 1
        }
      }

      "asFold should behave as valid Fold: nonEmpty" {
        forAll(genToken) { token ->
          nonEmpty(token)
        }
      }

      "asFold should behave as valid Fold: isEmpty" {
        forAll(genToken) { token ->
          !isEmpty(token)
        }
      }

      "asFold should behave as valid Fold: getAll" {
        forAll(genToken) { token ->
          getAll(token) == listOf(token.value).k()
        }
      }

      "asFold should behave as valid Fold: combineAll" {
        forAll(genToken) { token ->
          combineAll(String.monoid(), token) == token.value
        }
      }

      "asFold should behave as valid Fold: fold" {
        forAll(genToken) { token ->
          fold(String.monoid(), token) == token.value
        }
      }

      "asFold should behave as valid Fold: headOption" {
        forAll(genToken) { token ->
          headOption(token) == Some(token.value)
        }
      }

      "asFold should behave as valid Fold: lastOption" {
        forAll(genToken) { token ->
          lastOption(token) == Some(token.value)
        }
      }
    }

    with(tokenIso.asGetter()) {

      "asGetter should behave as valid Getter: get" {
        forAll(genToken) { token ->
          get(token) == tokenGetter.get(token)
        }
      }

      "asGetter should behave as valid Getter: find" {
        forAll(genToken, Gen.functionAToB<String, Boolean>(Gen.bool())) { token, p ->
          find(token, p) == tokenGetter.find(token, p)
        }
      }

      "asGetter should behave as valid Getter: exist" {
        forAll(genToken, Gen.functionAToB<String, Boolean>(Gen.bool())) { token, p ->
          exist(token, p) == tokenGetter.exist(token, p)
        }
      }
    }

    "Lifting a function should yield the same result as not yielding" {
      forAll(genToken, Gen.string()) { token, value ->
        tokenIso.modify(token) { value } == tokenIso.lift { value }(token)
      }
    }

    "Lifting a function as a functor should yield the same result as not yielding" {
      forAll(genToken, Gen.string()) { token, value ->
        tokenIso.modifyF(Option.functor(), token) { Some(value) } == tokenIso.liftF(Option.functor()) { Some(value) }(
          token
        )
      }
    }

    "Creating a first pair with a type should result in the target to value" {
      val first = tokenIso.first<Int>()
      forAll(genToken, Gen.int()) { token: Token, int: Int ->
        first.get(token toT int) == token.value toT int
      }
    }

    "Creating a second pair with a type should result in the value to target" {
      val second = tokenIso.second<Int>()
      forAll(Gen.int(), genToken) { int: Int, token: Token ->
        second.get(int toT token) == int toT token.value
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
        tokenIso.find(Token("any value")) { predicate }.fold({ false }, { true }) == predicate
      }
    }

    "Checking existence predicate over the target should result in same result as predicate" {
      forAll { predicate: Boolean ->
        tokenIso.exist(Token("any value")) { predicate } == predicate
      }
    }

    "Pairing two disjoint isos together" {
      val joinedIso = tokenIso split userIso

      forAll { tokenValue: String ->
        val token = Token(tokenValue)
        val user = User(token)
        joinedIso.get(token toT user) == tokenValue toT token
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
