package arrow.optics

import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.core.test.UnitSpec
import arrow.typeclasses.Monoid
import io.kotest.property.Arb
import io.kotest.property.checkAll

class GetterTest : UnitSpec() {

  init {

    val userGetter = userIso
    val length = Getter<String, Int> { it.length }
    val upper = Getter<String, String> { it.toUpperCase() }

    with(tokenGetter) {

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

    with(tokenGetter) {

      "Getting the target should always yield the exact result" {
        forAll { value: String ->
          get(Token(value)) == value
        }
      }

      "Finding a target using a predicate within a Getter should be wrapped in the correct option result" {
        forAll { value: String, predicate: Boolean ->
          findOrNull(Token(value)) { predicate }?.let { true } ?: false == predicate
        }
      }

      "Checking existence of a target should always result in the same result as predicate" {
        forAll { value: String, predicate: Boolean ->
          any(Token(value)) { predicate } == predicate
        }
      }
    }

    "Zipping two lenses should yield a tuple of the targets" {
      forAll { value: String ->
        length.zip(upper).get(value) == value.length to value.toUpperCase()
      }
    }

    "Joining two getters together with same target should yield same result" {
      val userTokenStringGetter = userGetter compose tokenGetter
      val joinedGetter = tokenGetter.choice(userTokenStringGetter)

      forAll { tokenValue: String ->
        val token = Token(tokenValue)
        val user = User(token)
        joinedGetter.get(Left(token)) == joinedGetter.get(Right(user))
      }
    }

    "Pairing two disjoint getters should yield a pair of their results" {
      val splitGetter: Getter<Pair<Token, User>, Pair<String, Token>> = tokenGetter.split(userGetter)
      checkAll(genToken, genUser) { token: Token, user: User ->
        splitGetter.get(token to user) == token.value to user.token
      }
    }

    "Creating a first pair with a type should result in the target to value" {
      val first = tokenGetter.first<Int>()
      checkAll(genToken, Gen.int()) { token: Token, int: Int ->
        first.get(token to int) == token.value to int
      }
    }

    "Creating a second pair with a type should result in the value target" {
      val first = tokenGetter.second<Int>()
      checkAll(Gen.int(), genToken) { int: Int, token: Token ->
        first.get(int to token) == int to token.value
      }
    }
  }
}
