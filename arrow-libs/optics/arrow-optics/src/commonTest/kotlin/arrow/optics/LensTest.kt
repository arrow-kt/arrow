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
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.string

class LensTest : UnitSpec() {

  init {
    testLaws(
      "TokenLens - ",
      LensLaws.laws(
        lens = Lens.token(),
        aGen = Arb.token(),
        bGen = Arb.string(),
        funcGen = Arb.functionAToB(Arb.string())
      ),

      TraversalLaws.laws(
        traversal = Lens.token(),
        aGen = Arb.token(),
        bGen = Arb.string(),
        funcGen = Arb.functionAToB(Arb.string()),
      ),

      OptionalLaws.laws(
        optional = Lens.token(),
        aGen = Arb.token(),
        bGen = Arb.string(),
        funcGen = Arb.functionAToB(Arb.string()),
      ),

      SetterLaws.laws(
        setter = Lens.token(),
        aGen = Arb.token(),
        bGen = Arb.string(),
        funcGen = Arb.functionAToB(Arb.string()),
      )
    )

    testLaws(
      "Identity Lens - ",
      LensLaws.laws(
        lens = Lens.id(),
        aGen = Arb.int(),
        bGen = Arb.int(),
        funcGen = Arb.functionAToB(Arb.int()),
      )
    )

    "asFold should behave as valid Fold: size" {
      checkAll(Arb.token()) { token ->
        Lens.token().size(token) shouldBe 1
      }
    }

    "asFold should behave as valid Fold: nonEmpty" {
      checkAll(Arb.token()) { token ->
        Lens.token().isNotEmpty(token) shouldBe true
      }
    }

    "asFold should behave as valid Fold: isEmpty" {
      checkAll(Arb.token()) { token ->
        !Lens.token().isEmpty(token) shouldBe true
      }
    }

    "asFold should behave as valid Fold: getAll" {
      checkAll(Arb.token()) { token ->
        Lens.token().getAll(token) shouldBe listOf(token.value)
      }
    }

    "asFold should behave as valid Fold: combineAll" {
      checkAll(Arb.token()) { token ->
        Lens.token().combineAll(Monoid.string(), token) shouldBe token.value
      }
    }

    "asFold should behave as valid Fold: fold" {
      checkAll(Arb.token()) { token ->
        Lens.token().fold(Monoid.string(), token) shouldBe token.value
      }
    }

    "asFold should behave as valid Fold: headOption" {
      checkAll(Arb.token()) { token ->
        Lens.token().firstOrNull(token) shouldBe token.value
      }
    }

    "asFold should behave as valid Fold: lastOption" {
      checkAll(Arb.token()) { token ->
        Lens.token().lastOrNull(token) shouldBe token.value
      }
    }

    "asGetter should behave as valid Getter: get" {
      checkAll(Arb.token()) { token ->
        Lens.token().get(token) shouldBe Getter.token().get(token)
      }
    }

    "asGetter should behave as valid Getter: find" {
      checkAll(Arb.token(), Arb.functionAToB<String, Boolean>(Arb.boolean())) { token, p ->
        Lens.token().findOrNull(token, p) shouldBe Getter.token().findOrNull(token, p)
      }
    }

    "asGetter should behave as valid Getter: exist" {
      checkAll(Arb.token(), Arb.functionAToB<String, Boolean>(Arb.boolean())) { token, p ->
        Lens.token().any(token, p) shouldBe Getter.token().any(token, p)
      }
    }

    "Lifting a function should yield the same result as not yielding" {
      checkAll(Arb.token(), Arb.string()) { token, value ->
        Lens.token().set(token, value) shouldBe Lens.token().lift { value }(token)
      }
    }

    "Finding a target using a predicate within a Lens should be wrapped in the correct option result" {
      checkAll(Arb.boolean()) { predicate: Boolean ->
        Lens.token().findOrNull(Token("any value")) { predicate }?.let { true } ?: false shouldBe predicate
      }
    }

    "Checking existence predicate over the target should result in same result as predicate" {
      checkAll(Arb.boolean()) { predicate: Boolean ->
        Lens.token().any(Token("any value")) { predicate } shouldBe predicate
      }
    }

    "Joining two lenses together with same target should yield same result" {
      val userTokenStringLens = Lens.user() compose Lens.token()
      val joinedLens = Lens.token() choice userTokenStringLens

      checkAll(Arb.string()) { tokenValue: String ->
        val token = Token(tokenValue)
        val user = User(token)
        joinedLens.get(Left(token)) shouldBe joinedLens.get(Right(user))
      }
    }

    "Pairing two disjoint lenses should yield a pair of their results" {
      val spiltLens: Lens<Pair<Token, User>, Pair<String, Token>> = Lens.token() split Lens.user()
      checkAll(Arb.token(), Arb.user()) { token: Token, user: User ->
        spiltLens.get(token to user) shouldBe (token.value to user.token)
      }
    }

    "Creating a first pair with a type should result in the target to value" {
      val first = Lens.token().first<Int>()
      checkAll(Arb.token(), Arb.int()) { token: Token, int: Int ->
        first.get(token to int) shouldBe (token.value to int)
      }
    }

    "Creating a second pair with a type should result in the value target" {
      val second = Lens.token().second<Int>()
      checkAll(Arb.int(), Arb.token()) { int: Int, token: Token ->
        second.get(int to token) shouldBe (int to token.value)
      }
    }
  }
}
