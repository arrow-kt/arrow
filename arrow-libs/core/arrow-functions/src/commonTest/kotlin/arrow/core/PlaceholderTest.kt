package arrow.core

import arrow.core.partial.*
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class PlaceholderTest {

  fun arb(): Arb<String> = Arb.string(1)

  fun p2(x: String, y: String) = x + y
  fun p3(x: String, y: String, z: String) = x + y + z

  @Test
  fun partially1of2() = runTest {
    checkAll(arb(), arb()) { a1, a2 ->
      val q2 = (::p2)(a1, `__`)
      q2(a2) shouldBe p2(a1, a2)
      (::p2)(a1, `__`)(a2) shouldBe p2(a1, a2)

      val f: (String, String) -> String = { p1: String, p2: String -> p1 + p2 }
      f(a1, `__`)(a2) shouldBe f(a1, a2)
    }
  }

  @Test
  fun partially1of3() = runTest {
    checkAll(arb(), arb(), arb()) { a1, a2, a3 ->
      val q3 = (::p3)(a1, `__`, `__`)
      q3(a2, a3) shouldBe p3(a1, a2, a3)
      (::p3)(a1, `__`, `__`)(a2, a3) shouldBe p3(a1, a2, a3)

      val f: (String, String, String) -> String = { p1: String, p2: String, p3: String -> p1 + p2 + p3 }
      f(a1, `__`, `__`)(a2, a3) shouldBe f(a1, a2, a3)
    }
  }

  @Test
  fun partially2of3() = runTest {
    checkAll(arb(), arb(), arb()) { a1, a2, a3 ->
      val q3 = (::p3)(a1, a2, `__`)
      q3(a3) shouldBe p3(a1, a2, a3)
      (::p3)(a1, a2, `__`)(a3) shouldBe p3(a1, a2, a3)

      val f: (String, String, String) -> String = { p1: String, p2: String, p3: String -> p1 + p2 + p3 }
      f(a1, a2, `__`)(a3) shouldBe f(a1, a2, a3)
    }
  }
}
