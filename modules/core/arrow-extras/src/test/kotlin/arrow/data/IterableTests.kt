package arrow.data

import arrow.core.case
import arrow.core.then
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
        case({ n: Int -> n % 2 == 0 } then { (it * 2).toString() })
      ) shouldBe listOf("4", "8")
    }

  }
}
