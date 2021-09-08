package generic

import arrow.continuations.Reset
import arrow.continuations.generic.RestrictedScope
import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.test.UnitSpec
import io.kotest.matchers.shouldBe

abstract class ContTestSuite : UnitSpec() {
  abstract suspend fun <A> runScope(func: (suspend RestrictedScope<A>.() -> A)): A

  abstract fun capabilities(): Set<ScopeCapabilities>

  init {
    "yield a list (also verifies stacksafety)" {
      runScope<List<Int>> {
        suspend fun <A> RestrictedScope<List<A>>.yield(a: A): Unit = shift { k -> listOf(a) + k(Unit) }
        for (i in 0..10_000) yield(i)
        emptyList()
      } shouldBe (0..10_000).toList()
    }
    "short circuit" {
      runScope<Either<String, Int>> {
        val no: Int = shift { Left("No thank you") }
        throw IllegalStateException("This should not be executed")
      } shouldBe Left("No thank you")
    }

    // some examples from http://homes.sice.indiana.edu/ccshan/recur/recur.pdf
    if (capabilities().contains(ScopeCapabilities.MultiShot)) {
      "multshot nondet" {
        runScope<List<Pair<Int, Int>>> {
          val i: Int = shift { k -> k(10) + k(20) }
          val j: Int = shift { k -> k(15) + k(25) }
          listOf(i to j)
        } shouldBe listOf(10 to 15, 10 to 25, 20 to 15, 20 to 25)
      }
      "multishot more than twice" {
        runScope<List<Triple<Int, Int, Int>>> {
          val i: Int = shift { k -> k(10) + k(20) }
          val j: Int = shift { k -> k(15) + k(25) }
          val k: Int = shift { k -> k(17) + k(27) }
          listOf(Triple(i, j, k))
        } shouldBe listOf(10, 20).flatMap { i -> listOf(15, 25).flatMap { j -> listOf(17, 27).map { k -> Triple(i, j, k) } } }
      }
      "multishot more than twice and with more multishot invocations" {
        runScope<List<Triple<Int, Int, Int>>> {
          val i: Int = shift { k -> k(10) + k(20) + k(30) + k(40) + k(50) }
          val j: Int = shift { k -> k(15) + k(25) + k(35) + k(45) + k(55) }
          val k: Int = shift { k -> k(17) + k(27) + k(37) + k(47) + k(57) }
          listOf(Triple(i, j, k))
        } shouldBe
          listOf(10, 20, 30, 40, 50)
            .flatMap { i ->
              listOf(15, 25, 35, 45, 55)
                .flatMap { j ->
                  listOf(17, 27, 37, 47, 57)
                    .map { k -> Triple(i, j, k) }
                }
            }
      }
      "multishot is stacksafe regardless of stack size" {
        runScope<Int> {
          // bring 10k elements on the stack
          var sum = 0
          for (i0 in 1..10_000) sum += shift<Int> { it(i0) }

          // run the continuation from here 10k times and sum the results
          // This is about as bad as a scenario as it gets :)
          val j: Int = shift {
            var sum2 = 0
            for (i0 in 1..10_000) sum2 += it(i0)
            sum2
          }

          sum + j
        }
      }
    }
  }
}

sealed class ScopeCapabilities {
  object MultiShot : ScopeCapabilities()
}

class SingleShotContTestSuite : ContTestSuite() {
  override suspend fun <A> runScope(func: (suspend RestrictedScope<A>.() -> A)): A =
    Reset.restricted { func(this) }

  override fun capabilities(): Set<ScopeCapabilities> = emptySet()
}

// class MultiShotContTestSuite : ContTestSuite() {
//   override suspend fun <A> runScope(func: (suspend RestrictedScope<A>.() -> A)): A =
//     MultiShotDelimContScope.reset { func(this) }
//
//   override fun capabilities(): Set<ScopeCapabilities> = setOf(ScopeCapabilities.MultiShot)
// }
