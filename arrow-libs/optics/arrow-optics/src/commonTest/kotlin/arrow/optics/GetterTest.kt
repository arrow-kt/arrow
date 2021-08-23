package arrow.optics

import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.core.test.UnitSpec
import arrow.typeclasses.Monoid
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.string
import io.kotest.property.arbitrary.boolean

class GetterTest : UnitSpec() {

  init {

    val userGetter = userIso
    val length = Getter<String, Int> { it.length }
    val upper = Getter<String, String> { it.uppercase() }

//    "asFold should behave as valid Fold: size" {
//      checkAll(genToken) { token ->
//        tokenGetter.size(token) shouldBe 1
//      }
//    }

//    "asFold should behave as valid Fold: nonEmpty" {
//      checkAll(genToken) { token ->
//        tokenGetter.isNotEmpty(token) shouldBe true
//      }
//    }
//
//    "asFold should behave as valid Fold: isEmpty" {
//      checkAll(genToken) { token ->
//        !tokenGetter.isEmpty(token) shouldBe true
//      }
//    }
//
//    "asFold should behave as valid Fold: getAll" {
//      checkAll(genToken) { token ->
//        tokenGetter.getAll(token) shouldBe listOf(token.value)
//      }
//    }
//
//    "asFold should behave as valid Fold: combineAll" {
//      checkAll(genToken) { token ->
//        tokenGetter.combineAll(Monoid.string(), token) shouldBe token.value
//      }
//    }
//
//    "asFold should behave as valid Fold: fold" {
//      checkAll(genToken) { token ->
//        tokenGetter.fold(Monoid.string(), token) shouldBe token.value
//      }
//    }
//
//    "asFold should behave as valid Fold: headOption" {
//      checkAll(genToken) { token ->
//        tokenGetter.firstOrNull(token) shouldBe token.value
//      }
//    }
//
//    "asFold should behave as valid Fold: lastOption" {
//      checkAll(genToken) { token ->
//        tokenGetter.lastOrNull(token) shouldBe token.value
//      }
//    }
//
//    "Getting the target should always yield the exact result" {
//      checkAll(Arb.string()) { value: String ->
//        tokenGetter.get(Token(value)) shouldBe value
//      }
//    }
//
//    "Finding a target using a predicate within a Getter should be wrapped in the correct option result" {
//      checkAll(Arb.string(), Arb.boolean()) { value: String, predicate: Boolean ->
//        tokenGetter.findOrNull(Token(value)) { predicate }?.let { true } ?: false shouldBe predicate
//      }
//    }
//
//    "Checking existence of a target should always result in the same result as predicate" {
//      checkAll(Arb.string(), Arb.boolean()) { value: String, predicate: Boolean ->
//        tokenGetter.any(Token(value)) { predicate } shouldBe predicate
//      }
//    }
//
//    "Zipping two lenses should yield a tuple of the targets" {
//      checkAll(Arb.string()) { value: String ->
//        length.zip(upper).get(value) shouldBe (value.length to value.toUpperCase())
//      }
//    }
//
//    "Joining two getters together with same target should yield same result" {
//      val userTokenStringGetter = userGetter compose tokenGetter
//      val joinedGetter = tokenGetter.choice(userTokenStringGetter)
//
//      checkAll(Arb.string()) { tokenValue: String ->
//        val token = Token(tokenValue)
//        val user = User(token)
//        joinedGetter.get(Left(token)) shouldBe joinedGetter.get(Right(user))
//      }
//    }
//
//    "Pairing two disjoint getters should yield a pair of their results" {
//      val splitGetter: Getter<Pair<Token, User>, Pair<String, Token>> = tokenGetter.split(userGetter)
//      checkAll(genToken, genUser) { token: Token, user: User ->
//        splitGetter.get(token to user) shouldBe (token.value to user.token)
//      }
//    }
//
//    "Creating a first pair with a type should result in the target to value" {
//      val first = tokenGetter.first<Int>()
//      checkAll(genToken, Arb.int()) { token: Token, int: Int ->
//        first.get(token to int) shouldBe (token.value to int)
//      }
//    }
//
//    "Creating a second pair with a type should result in the value target" {
//      val first = tokenGetter.second<Int>()
//      checkAll(Arb.int(), genToken) { int: Int, token: Token ->
//        first.get(int to token) shouldBe (int to token.value)
//      }
//    }
  }
}
