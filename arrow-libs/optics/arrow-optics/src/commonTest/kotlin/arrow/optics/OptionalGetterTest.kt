package arrow.optics

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.checkAll

class OptionalGetterTest : StringSpec() {

  init {
    "get should return value if predicate is true and null if otherwise" {
      checkAll(Arb.int(), Arb.boolean()) { int, predicate ->
        OptionalGetter.filter<Int> { predicate }.getOrNull(int) shouldBe (if (predicate) int else null)
      }
    }

    "getAll should return the old list if predicate is true" {
      checkAll(Arb.list(Arb.int()), Arb.boolean()) { list, predicate ->
        (Fold.list<Int>() compose OptionalGetter.filter { predicate }).getAll(list) shouldBe (if (predicate) list else emptyList())
      }
    }
  }
}
