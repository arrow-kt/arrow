package arrow.fx.coroutines

import io.kotest.assertions.fail
import io.kotest.matchers.string.shouldStartWith
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class ParMapJvmTest {
  @Test fun parMapRunsOnProvidedContext() = runTest { // 100 is same default length as Arb.list
    checkAll(Arb.int(min = Int.MIN_VALUE, max = 100)) { i ->
      val res = resourceScope {
        (0 until i).parMap(single()) { Thread.currentThread().name }
      }
      res.forEach { it shouldStartWith "single" }
    }
  }

  @Test fun parMapConcurrency3RunsOnProvidedContext() = runTest {
    checkAll(Arb.int(min = Int.MIN_VALUE, max = 100)) { i ->
      val res = resourceScope {
        (0 until i).parMap(single(), concurrency = 3) {
          Thread.currentThread().name
        }
      }
      res.forEach { it shouldStartWith "single" }
    }
  }

  @Test fun parMapOrAccumulateCombineEmptyErrorRunsOnProvidedContext() = runTest { // 100 is same default length as Arb.list
    checkAll(Arb.int(min = Int.MIN_VALUE, max = 100)) { i ->
      val res = resourceScope {
        (0 until i).parMapOrAccumulate<Nothing, Int, String>(single(), combine = emptyError) { Thread.currentThread().name }
      }
      res.fold(
        { fail("Expected Right but found $res") },
        { l -> l.forEach { it shouldStartWith "single" } }
      )
    }
  }

  @Test fun parMapOrAccumulateCombineEmptyErrorConcurrency3RunsOnProvidedContext() = runTest { // 100 is same default length as Arb.list
    checkAll(Arb.int(min = Int.MIN_VALUE, max = 100)) { i ->
      val res = resourceScope {
        (0 until i).parMapOrAccumulate<Nothing, Int, String>(
          single(),
          combine = emptyError,
          concurrency = 3
        ) { Thread.currentThread().name }
      }
      res.fold(
        { fail("Expected Right but found $res") },
        { l -> l.forEach { it shouldStartWith "single" } }
      )
    }
  }

  @Test fun parMapOrAccumulateRunsOnProvidedContext() = runTest { // 100 is same default length as Arb.list
    checkAll(Arb.int(min = Int.MIN_VALUE, max = 100)) { i ->
      val res = resourceScope {
        (0 until i).parMapOrAccumulate<Nothing, Int, String>(single()) {
          Thread.currentThread().name
        }
      }
      res.fold(
        { fail("Expected Right but found $res") },
        { l -> l.forEach { it shouldStartWith "single" } }
      )
    }
  }

  @Test fun parMapOrAccumulateConcurrency3RunsOnProvidedContext() = runTest { // 100 is same default length as Arb.list
    checkAll(Arb.int(min = Int.MIN_VALUE, max = 100)) { i ->

      val res = resourceScope {
        (0 until i).parMapOrAccumulate<Nothing, Int, String>(single(), concurrency = 3) {
          Thread.currentThread().name
        }
      }
      res.fold(
        { fail("Expected Right but found $res") },
        { l -> l.forEach { it shouldStartWith "single" } }
      )
    }
  }
}

private val emptyError: (Nothing, Nothing) -> Nothing =
  { _, _ -> throw AssertionError("Should not be called") }

suspend fun ResourceScope.single() = singleThreadContext("single")
