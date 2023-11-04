package arrow.collectors

import io.kotest.matchers.shouldBe
import kotlin.test.Test

class CollectorsTest {
  @Test
  fun lengthWorks() = runTestOverLists {
    it.collect(Collectors.length) shouldBe it.size
    it.parCollect(Collectors.length) shouldBe it.size
  }

  @Test
  fun sumWorks() = runTestOverLists {
    it.collect(Collectors.sum) shouldBe it.sum()
    it.parCollect(Collectors.sum) shouldBe it.sum()
  }

  @Test
  fun zipSumWorks1() = runTestOverLists {
    it.collect(zip(Collectors.sum, Collectors.sum, Int::plus)) shouldBe it.sum() * 2
    it.parCollect(zip(Collectors.sum, Collectors.sum, Int::plus)) shouldBe it.sum() * 2
  }

  @Test
  fun zipSumWorks2() = runTestOverLists {
    it.collect(zip(Collectors.sum, Collectors.sum, Int::minus)) shouldBe 0
    it.parCollect(zip(Collectors.sum, Collectors.sum, Int::minus)) shouldBe 0
  }

  @Test
  fun bestWorks() = runTestOverLists {
    it.collect(Collectors.bestBy { old, new -> new > old }) shouldBe it.maxOrNull()
    it.parCollect(Collectors.bestBy { old, new -> new > old }) shouldBe it.maxOrNull()
  }

  @Test
  fun listWorks() = runTestOverLists {
    it.collect(Collectors.list()) shouldBe it
    it.parCollect(Collectors.list()) shouldBe it
  }

  @Test
  fun setWorks() = runTestOverLists {
    it.collect(Collectors.set()) shouldBe it.toSet()
    it.parCollect(Collectors.set()) shouldBe it.toSet()
  }
}
