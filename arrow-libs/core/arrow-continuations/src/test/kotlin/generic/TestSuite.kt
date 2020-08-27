package generic

import arrow.continuations.generic.DelimitedScope
import arrow.continuations.generic.MultiShotDelimContScope
import arrow.continuations.generic.DelimContScope
import arrow.continuations.generic.NestedDelimContScope
import arrow.core.Either
import arrow.core.Left
import arrow.core.Tuple2
import arrow.core.Tuple3
import arrow.core.test.UnitSpec
import arrow.core.toT
import io.kotlintest.shouldBe

abstract class ContTestSuite : UnitSpec() {
  abstract fun <A> runScope(func: (suspend DelimitedScope<A>.() -> A)): A

  abstract fun capabilities(): Set<ScopeCapabilities>

  init {
    "yield a list (also verifies stacksafety)" {
      runScope<List<Int>> {
        suspend fun <A> DelimitedScope<List<A>>.yield(a: A): Unit = shift { k -> listOf(a) + k(Unit) }
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
    "shiftCPS supports multishot regardless of scope" {
      runScope<Int> {
        shiftCPS<Int, Int>({ it(1) + it(2) }) { i -> i + 1 }
        throw IllegalStateException("This is unreachable")
      } shouldBe 5
    }
    "reset" {
      runScope<Int> {
        reset {
          shift { it(1) }
        }
      } shouldBe 1
    }
    if (capabilities().contains(ScopeCapabilities.MultiShot)) {
      // This comes from http://homes.sice.indiana.edu/ccshan/recur/recur.pdf and shows how reset/shift should behave
      "multishot reset/shift" {
        runScope<List<Char>> {
          listOf('a') + reset<List<Char>> {
            listOf('b') + shift<List<Char>> { f -> listOf('1') + f(f(listOf('c'))) }
          }
        } shouldBe listOf('a', '1', 'b', 'b', 'c')
        runScope<List<Char>> {
          listOf('a') + reset<List<Char>> {
            shiftCPS<List<Char>, List<Char>>({ f -> listOf('1') + f(f(listOf('c'))) }) { xs: List<Char> ->
              listOf('b') + xs
            }
          }
        } shouldBe listOf('a', '1', 'b', 'b', 'c')
      }
      // This also comes from http://homes.sice.indiana.edu/ccshan/recur/recur.pdf and shows that shift surrounds the
      //  captured continuation and the function receiving it with reset. This is done implicitly in our implementation
      "shift and control distinction" {
        runScope<String> {
          reset {
            suspend fun y() = shift<String> { f -> "a" + f("") }
            shift<String> { y() }
          }
        } shouldBe "a"
        // TODO this is not very accurate, probably not correct either
        runScope<String> {
          shiftCPS<String, String>({ it("") }, { s: String -> shift { f -> "a" + f("") } })
        } shouldBe "a"
      }
      "multshot nondet" {
        runScope<List<Tuple2<Int, Int>>> {
          val i: Int = shift { k -> k(10) + k(20) }
          val j: Int = shift { k -> k(15) + k(25) }
          listOf(i toT j)
        } shouldBe listOf(10 toT 15, 10 toT 25, 20 toT 15, 20 toT 25)
      }
      "multishot more than twice" {
        runScope<List<Tuple3<Int, Int, Int>>> {
          val i: Int = shift { k -> k(10) + k(20) }
          val j: Int = shift { k -> k(15) + k(25) }
          val k: Int = shift { k -> k(17) + k(27) }
          listOf(Tuple3(i, j, k))
        } shouldBe listOf(10, 20).flatMap { i -> listOf(15, 25).flatMap { j -> listOf(17, 27).map { k -> Tuple3(i, j, k) } } }
      }
      "multishot more than twice and with more multishot invocations" {
        runScope<List<Tuple3<Int, Int, Int>>> {
          val i: Int = shift { k -> k(10) + k(20) + k(30) + k(40) + k(50) }
          val j: Int = shift { k -> k(15) + k(25) + k(35) + k(45) + k(55) }
          val k: Int = shift { k -> k(17) + k(27) + k(37) + k(47) + k(57) }
          listOf(Tuple3(i, j, k))
        } shouldBe
          listOf(10, 20, 30, 40, 50)
            .flatMap { i -> listOf(15, 25, 35, 45, 55)
              .flatMap { j -> listOf(17, 27, 37, 47, 57)
                .map { k -> Tuple3(i, j, k) }
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
    if (capabilities().contains(ScopeCapabilities.NestedScopes)) {
      "nested reset calling between scopes" {
        runScope {
          val a: Int = shift { it(5) }
          a + reset<Int> fst@{
            val i: Int = shift { it(10) }
            reset<Int> snd@{
              val j: Int = shift { it(20) }
              val k: Int = this@fst.shift { it(30) }
              i + j + k
            }
          } shouldBe 65
        }
      }
      "nested reset calling between a lot of scopes" {
        runScope fst@{
          val a: Int = shift { it(5) }
          a + reset<Int> snd@{
            val i: Int = shift { it(10) }
            reset<Int> third@{
              val j: Int = shift { it(20) }
              val k: Int = this@fst.shift<Int> { it(30) } + this@snd.shift<Int> { it(40) }
              reset fourth@{
                val p: Int = shift { it(20) }
                val k2: Int = this@fst.shift<Int> { it(30) } + this@snd.shift<Int> { it(40) }
                val t: Int = this@third.shift { it(5) }
                i + j + k + p + k2 + t
              }
            }
          } shouldBe 200
        }
      }
      "nested reset calling between scopes with short circuit" {
        runScope {
          val a: Int = shift { it(5) }
          a + reset<Int> fst@{
            val i: Int = shift { it(10) }
            reset<Int> snd@{
              val j: Int = shift { it(20) }
              val k: Int = this@fst.shift { 5 }
              i + j + k
            }
          } shouldBe 10
        }
      }
      "nested reset calling between a lot of scopes and short circuit" {
        runScope fst@{
          val a: Int = shift { it(5) }
          a + reset<Int> snd@{
            val i: Int = shift { it(10) }
            reset<Int> third@{
              val j: Int = shift { it(20) }
              val k: Int = this@fst.shift<Int> { it(30) } + this@snd.shift<Int> { it(40) }
              reset fourth@{
                val p: Int = shift { it(20) }
                val k2: Int = this@fst.shift<Int> { it(30) } + this@snd.shift<Int> { it(40) }
                val t: Int = this@third.shift { 5 }
                i + j + k + p + k2 + t
              }
            }
          } shouldBe 10
        }
      }
    }
  }
}

sealed class ScopeCapabilities {
  object MultiShot : ScopeCapabilities()
  object NestedScopes : ScopeCapabilities()
}

class SingleShotContTestSuite : ContTestSuite() {
  override fun <A> runScope(func: (suspend DelimitedScope<A>.() -> A)): A =
    DelimContScope.reset(func)

  override fun capabilities(): Set<ScopeCapabilities> = emptySet()
}

class MultiShotContTestSuite : ContTestSuite() {
  override fun <A> runScope(func: (suspend DelimitedScope<A>.() -> A)): A =
    MultiShotDelimContScope.reset(func)

  override fun capabilities(): Set<ScopeCapabilities> = setOf(ScopeCapabilities.MultiShot)
}

class NestedContTestSuite : ContTestSuite() {
  override fun <A> runScope(func: suspend DelimitedScope<A>.() -> A): A =
    NestedDelimContScope.reset(func)

  override fun capabilities(): Set<ScopeCapabilities> = setOf(ScopeCapabilities.NestedScopes)
}
