package arrow.core

import arrow.syntax.collections.collect
import arrow.test.UnitSpec
import io.kotlintest.runner.junit4.KotlinTestRunner
import io.kotlintest.shouldBe
import org.junit.runner.RunWith

@RunWith(KotlinTestRunner::class)
class IterableTests : UnitSpec() {

  init {

    "Iterable.collect can filter and transform" {
      listOf(1, 2, 3, 4, 5).collect(
        { i -> if (i % 2 == 0) Some((i * 2).toString()) else None }
      ) shouldBe listOf("4", "8")
    }
  }
}
