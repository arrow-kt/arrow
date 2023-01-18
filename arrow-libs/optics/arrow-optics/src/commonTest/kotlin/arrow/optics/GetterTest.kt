package arrow.optics

import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.typeclasses.Monoid
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.string
import io.kotest.property.arbitrary.boolean
import io.kotest.property.checkAll

class GetterTest : StringSpec({

    "asFold should behave as valid Fold: size" {
      checkAll(Arb.token()) { token ->
        Getter.token().size(token) shouldBe 1
      }
    }

    "asFold should behave as valid Fold: nonEmpty" {
      checkAll(Arb.token()) { token ->
        Getter.token().isNotEmpty(token) shouldBe true
      }
    }

    "asFold should behave as valid Fold: isEmpty" {
      checkAll(Arb.token()) { token ->
        !Getter.token().isEmpty(token) shouldBe true
      }
    }

    "asFold should behave as valid Fold: getAll" {
      checkAll(Arb.token()) { token ->
        Getter.token().getAll(token) shouldBe listOf(token.value)
      }
    }

    "asFold should behave as valid Fold: combineAll" {
      checkAll(Arb.token()) { token ->
        Getter.token().fold(Monoid.string(), token) shouldBe token.value
      }
    }

    "asFold should behave as valid Fold: fold" {
      checkAll(Arb.token()) { token ->
        Getter.token().fold(Monoid.string(), token) shouldBe token.value
      }
    }

    "asFold should behave as valid Fold: headOption" {
      checkAll(Arb.token()) { token ->
        Getter.token().firstOrNull(token) shouldBe token.value
      }
    }

    "asFold should behave as valid Fold: lastOption" {
      checkAll(Arb.token()) { token ->
        Getter.token().lastOrNull(token) shouldBe token.value
      }
    }

    "Getting the target should always yield the exact result" {
      checkAll(Arb.string()) { value: String ->
        Getter.token().get(Token(value)) shouldBe value
      }
    }

    "Finding a target using a predicate within a Getter should be wrapped in the correct option result" {
      checkAll(Arb.string(), Arb.boolean()) { value: String, predicate: Boolean ->
        (Getter.token().findOrNull(Token(value)) { predicate }?.let { true } ?: false) shouldBe predicate
      }
    }

    "Checking existence of a target should always result in the same result as predicate" {
      checkAll(Arb.string(), Arb.boolean()) { value: String, predicate: Boolean ->
        Getter.token().any(Token(value)) { predicate } shouldBe predicate
      }
    }

    "Zipping two lenses should yield a tuple of the targets" {
      checkAll(Arb.string()) { value: String ->
        Getter<String, Int> { it.length }.zip { it.uppercase() }
          .get(value) shouldBe (value.length to value.uppercase())
      }
    }

    "Joining two getters together with same target should yield same result" {
      checkAll(Arb.string()) { tokenValue: String ->
        val userTokenStringGetter = Iso.user() compose Getter.token()
        val joinedGetter = Getter.token().choice(userTokenStringGetter)
        val token = Token(tokenValue)
        val user = User(token)
        joinedGetter.get(Left(token)) shouldBe joinedGetter.get(Right(user))
      }
    }

    "Pairing two disjoint getters should yield a pair of their results" {
      val splitGetter: Getter<Pair<Token, User>, Pair<String, Token>> = Getter.token().split(Iso.user())
      checkAll(Arb.token(), Arb.user()) { token: Token, user: User ->
        splitGetter.get(token to user) shouldBe (token.value to user.token)
      }
    }

    "Creating a first pair with a type should result in the target to value" {
      val first = Getter.token().first<Int>()
      checkAll(Arb.token(), Arb.int()) { token: Token, int: Int ->
        first.get(token to int) shouldBe (token.value to int)
      }
    }

    "Creating a second pair with a type should result in the value target" {
      checkAll(Arb.int(), Arb.token()) { int: Int, token: Token ->
        val first = Getter.token().second<Int>()
        first.get(int to token) shouldBe (int to token.value)
      }
    }

})
