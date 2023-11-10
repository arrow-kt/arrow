package arrow.optics

import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.optics.test.functionAToB
import arrow.optics.test.laws.LensLaws
import arrow.optics.test.laws.OptionalLaws
import arrow.optics.test.laws.TraversalLaws
import arrow.optics.test.laws.testLaws
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class LensTest {

  @Test
  fun laws() =
    testLaws(
      LensLaws(
        lens = Lens.token(),
        aGen = Arb.token(),
        bGen = Arb.string(),
        funcGen = Arb.functionAToB(Arb.string())
      ),

      TraversalLaws(
        traversal = Lens.token(),
        aGen = Arb.token(),
        bGen = Arb.string(),
        funcGen = Arb.functionAToB(Arb.string()),
      ),

      OptionalLaws(
        optional = Lens.token(),
        aGen = Arb.token(),
        bGen = Arb.string(),
        funcGen = Arb.functionAToB(Arb.string()),
      )
    )

  @Test
  fun identityLens() =
    testLaws(
      LensLaws(
        lens = Lens.id(),
        aGen = Arb.int(),
        bGen = Arb.int(),
        funcGen = Arb.functionAToB(Arb.int()),
      )
    )

  @Test
  fun sizeOk() = runTest {
    checkAll(Arb.token()) { token ->
      Lens.token().size(token) shouldBe 1
    }
  }

  @Test
  fun isNonEmptyOk() = runTest {
    checkAll(Arb.token()) { token ->
      Lens.token().isNotEmpty(token) shouldBe true
    }
  }

  @Test
  fun isEmptyOk() = runTest {
    checkAll(Arb.token()) { token ->
      !Lens.token().isEmpty(token) shouldBe true
    }
  }

  @Test
  fun getAllOk() = runTest {
    checkAll(Arb.token()) { token ->
      Lens.token().getAll(token) shouldBe listOf(token.value)
    }
  }

  @Test
  fun foldOk() = runTest {
    checkAll(Arb.token()) { token ->
      Lens.token().fold("", { x, y -> x + y }, token) shouldBe token.value
    }
  }

  @Test
  fun firstOrNullOk() = runTest {
    checkAll(Arb.token()) { token ->
      Lens.token().firstOrNull(token) shouldBe token.value
    }
  }

  @Test
  fun lastOrNullOk() = runTest {
    checkAll(Arb.token()) { token ->
      Lens.token().lastOrNull(token) shouldBe token.value
    }
  }

  @Test
  fun setOrLift() = runTest {
    checkAll(Arb.token(), Arb.string()) { token, value ->
      Lens.token().set(token, value) shouldBe Lens.token().lift { value }(token)
    }
  }

  @Test
  fun findOrNullPredicate() = runTest {
    checkAll(Arb.boolean()) { predicate: Boolean ->
      (Lens.token().findOrNull(Token("any value")) { predicate }?.let { true } ?: false) shouldBe predicate
    }
  }

  @Test
  fun anyOk() = runTest {
    checkAll(Arb.boolean()) { predicate: Boolean ->
      Lens.token().any(Token("any value")) { predicate } shouldBe predicate
    }
  }

  @Test
  fun joinOk() = runTest {
    val userTokenStringLens = Lens.user() compose Lens.token()
    val joinedLens = Lens.token() choice userTokenStringLens

    checkAll(Arb.string()) { tokenValue: String ->
      val token = Token(tokenValue)
      val user = User(token)
      joinedLens.get(Left(token)) shouldBe joinedLens.get(Right(user))
    }
  }

  @Test
  fun splitOk() = runTest {
    val spiltLens: Lens<Pair<Token, User>, Pair<String, Token>> = Lens.token() split Lens.user()
    checkAll(Arb.token(), Arb.user()) { token: Token, user: User ->
      spiltLens.get(token to user) shouldBe (token.value to user.token)
    }
  }

  @Test
  fun firstOk() = runTest {
    val first = Lens.token().first<Int>()
    checkAll(Arb.token(), Arb.int()) { token: Token, int: Int ->
      first.get(token to int) shouldBe (token.value to int)
    }
  }

  @Test
  fun secondOk() = runTest {
    val second = Lens.token().second<Int>()
    checkAll(Arb.int(), Arb.token()) { int: Int, token: Token ->
      second.get(int to token) shouldBe (int to token.value)
    }
  }

}
