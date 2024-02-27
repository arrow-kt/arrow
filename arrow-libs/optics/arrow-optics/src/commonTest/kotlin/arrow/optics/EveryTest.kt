package arrow.optics

import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.orNull
import io.kotest.property.checkAll
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class EveryTest {

  @Test
  fun sizeOk() = runTest {
    checkAll(Arb.list(Arb.int())) { ints ->
      Every.list<Int>().size(ints) shouldBe ints.size
    }
  }

  @Test
  fun nonEmptyOk() = runTest {
    checkAll(Arb.list(Arb.int())) { ints ->
      Every.list<Int>().isNotEmpty(ints) shouldBe ints.isNotEmpty()
    }
  }

  @Test
  fun isEmptyOk() = runTest {
    checkAll(Arb.list(Arb.int())) { ints ->
      Every.list<Int>().isEmpty(ints) shouldBe ints.isEmpty()
    }
  }

  @Test
  fun getAllOk() = runTest {
    checkAll(Arb.list(Arb.int())) { ints ->
      Every.list<Int>().getAll(ints) shouldBe ints
    }
  }

  @Test
  fun foldOk() = runTest {
    checkAll(Arb.list(Arb.int())) { ints ->
      Every.list<Int>().fold(0, { x, y -> x + y }, ints) shouldBe ints.sum()
    }
  }

  @Test
  fun firstOrNullOk() = runTest {
    checkAll(Arb.list(Arb.int().orNull())) { ints ->
      Every.list<Int?>().firstOrNull(ints) shouldBe ints.firstOrNull()
    }
  }

  @Test
  fun lastOrNullOk() = runTest {
    checkAll(Arb.list(Arb.int().orNull())) { ints ->
      Every.list<Int?>().lastOrNull(ints) shouldBe ints.lastOrNull()
    }
  }

  @Test
  fun firstOrNullPredicateOk() = runTest {
    checkAll(Arb.list(Arb.int(-100..100).orNull())) { ints ->
      val predicate = { i: Int? -> i?.let { it > 10 } ?: false }
      Every.list<Int?>().findOrNull(ints, predicate) shouldBe ints.firstOrNull(predicate)
    }
  }

}
