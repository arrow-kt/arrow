package arrow.collectors

import io.kotest.matchers.shouldBe
import kotlin.test.Test

class CollectorsTest {
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
