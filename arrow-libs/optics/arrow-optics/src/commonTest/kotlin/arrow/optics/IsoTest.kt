package arrow.optics

import arrow.core.Either
import arrow.optics.test.functionAToB
import arrow.optics.test.laws.IsoLaws
import arrow.optics.test.laws.LensLaws
import arrow.optics.test.laws.OptionalLaws
import arrow.optics.test.laws.PrismLaws
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

class IsoTest {

  private val aIso: Iso<SumType.A, String> = Iso(
    get = { a: SumType.A -> a.string },
    reverseGet = SumType::A
  )

  @Test
  fun laws() =
    testLaws(
      LensLaws(
        lens = Iso.token(),
        aGen = Arb.token(),
        bGen = Arb.string(),
        funcGen = Arb.functionAToB(Arb.string())
      ),

      PrismLaws(
        prism = aIso,
        aGen = Arb.sumTypeA(),
        bGen = Arb.string(),
        funcGen = Arb.functionAToB(Arb.string())
      ),

      TraversalLaws(
        traversal = Iso.token(),
        aGen = Arb.token(),
        bGen = Arb.string(),
        funcGen = Arb.functionAToB(Arb.string())
      ),

      OptionalLaws(
        optional = Iso.token(),
        aGen = Arb.token(),
        bGen = Arb.string(),
        funcGen = Arb.functionAToB(Arb.string())
      ),

      IsoLaws(
        iso = Iso.token(),
        aGen = Arb.token(),
        bGen = Arb.string(),
        funcGen = Arb.functionAToB(Arb.string())
      )
    )


  @Test
  fun sizeOk() = runTest {
    checkAll(Arb.token()) { token ->
      Iso.token().size(token) shouldBe 1
    }
  }

  @Test
  fun isNotEmptyOk() = runTest {
    checkAll(Arb.token()) { token ->
      Iso.token().isNotEmpty(token) shouldBe true
    }
  }

  @Test
  fun isEmptyOk() = runTest {
    checkAll(Arb.token()) { token ->
      !Iso.token().isEmpty(token) shouldBe true
    }
  }

  @Test
  fun getAllOk() = runTest {
    checkAll(Arb.token()) { token ->
      Iso.token().getAll(token) shouldBe listOf(token.value)
    }
  }

  @Test
  fun foldOk() = runTest {
    checkAll(Arb.token()) { token ->
      Iso.token().fold("", { x, y -> x + y }, token) shouldBe token.value
    }
  }

  @Test
  fun firstOrNullOk() = runTest {
    checkAll(Arb.token()) { token ->
      Iso.token().firstOrNull(token) shouldBe token.value
    }
  }

  @Test
  fun lastOrNullOk() = runTest {
    checkAll(Arb.token()) { token ->
      Iso.token().lastOrNull(token) shouldBe token.value
    }
  }

  @Test
  fun modifyOk() = runTest {
    checkAll(Arb.token(), Arb.string()) { token, value ->
      Iso.token().modify(token) { value } shouldBe Iso.token().lift { value }(token)
    }
  }

  @Test
  fun firstOk() = runTest {
    val first = Iso.token().first<Int>()
    checkAll(Arb.token(), Arb.int()) { token: Token, int: Int ->
      first.get(token to int) shouldBe (token.value to int)
    }
  }

  @Test
  fun secondOk() = runTest {
    val second = Iso.token().second<Int>()
    checkAll(Arb.int(), Arb.token()) { int: Int, token: Token ->
      second.get(int to token) shouldBe (int to token.value)
    }
  }

  @Test
  fun leftOk() = runTest {
    val left = Iso.token().left<Int>()
    checkAll(Arb.token(), Arb.int()) { token: Token, int: Int ->
      left.get(Either.Left(token)) shouldBe Either.Left(token.value)
      left.get(Either.Right(int)) shouldBe Either.Right(int)
    }
  }

  @Test
  fun rightOk() = runTest {
    val left = Iso.token().right<Int>()
    checkAll(Arb.token(), Arb.int()) { token: Token, int: Int ->
      left.get(Either.Left(int)) shouldBe Either.Left(int)
      left.get(Either.Right(token)) shouldBe Either.Right(token.value)
    }
  }

  @Test
  fun firstOrNullPredicateOk() = runTest {
    checkAll(Arb.boolean()) { predicate: Boolean ->
      (Iso.token().findOrNull(Token("any value")) { predicate }?.let { true } ?: false) shouldBe predicate
    }
  }

  @Test
  fun anyOk() = runTest {
    checkAll(Arb.boolean()) { predicate: Boolean ->
      Iso.token().any(Token("any value")) { predicate } shouldBe predicate
    }
  }

  @Test
  fun splitOk() = runTest {
    val joinedIso = Iso.token() split Iso.user()

    checkAll(Arb.string()) { tokenValue: String ->
      val token = Token(tokenValue)
      val user = User(token)
      joinedIso.get(token to user) shouldBe (tokenValue to token)
    }
  }

  @Test
  fun composeOk() = runTest {
    val composedIso = Iso.user() compose Iso.token()

    checkAll(Arb.string()) { tokenValue: String ->
      val token = Token(tokenValue)
      val user = User(token)
      composedIso.get(user) shouldBe tokenValue
    }
  }

}
