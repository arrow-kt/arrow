package arrow.core

import arrow.core.test.laws.MonoidLaws
import arrow.core.test.testLaws
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.checkAll
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class ListKTest {

  @Test fun monoidLaws() =
    testLaws(MonoidLaws("List", emptyList(), List<Int>::plus, Arb.list(Arb.int())))

  @Test fun mapNotNullOk() = runTest {
      checkAll(Arb.list(Arb.int())) { listk ->
        listk.mapNotNull {
          when (it % 2 == 0) {
            true -> it.toString()
            else -> null
          }
        } shouldBe listk.toList().filter { it % 2 == 0 }.map { it.toString() }
      }
    }

}
