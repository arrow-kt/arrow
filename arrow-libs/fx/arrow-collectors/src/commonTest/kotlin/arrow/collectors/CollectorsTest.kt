package arrow.collectors

import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.PropertyContext
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.checkAll
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class CollectorsTest {
  fun runTestOverLists(
    block: suspend PropertyContext.(List<Int>) -> Unit
  ): TestResult = runTest {
    checkAll(Arb.list(Arb.int()), block)
  }

  @Test
  fun lengthWorks() = runTestOverLists {
    it.collect(Collectors.length) shouldBe it.size
  }

  @Test
  fun sumWorks() = runTestOverLists {
    it.collect(Collectors.sum) shouldBe it.sum()
  }

  @Test
  fun bestWorks() = runTestOverLists {
    it.collect(Collectors.bestBy { old, new -> new > old }) shouldBe it.maxOrNull()
  }
}
