package arrow.core.continuations

import arrow.core.Either
import arrow.core.identity
import arrow.core.left
import arrow.core.right
import io.kotest.assertions.fail
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.orNull
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class EagerEffectSpec : StringSpec({
  "try/catch - can recover from shift" {
    checkAll(Arb.int(), Arb.string()) { i, s ->
      eagerEffect<String, Int> {
        try {
          shift(s)
        } catch (e: Throwable) {
          i
        }
      }
        .fold({ fail("Should never come here") }, ::identity) shouldBe i
    }
  }

  "try/catch - can recover from shift suspended" {
    checkAll(Arb.int(), Arb.string()) { i, s ->
      eagerEffect<String, Int> {
        try {
          shift(s)
        } catch (e: Throwable) {
          i
        }
      }.fold({ fail("Should never come here") }, ::identity) shouldBe i
    }
  }

  "try/catch - finally works" {
    checkAll(Arb.string(), Arb.int()) { s, i ->
      val promise = CompletableDeferred<Int>()
      eagerEffect<String, Int> {
        try {
          shift(s)
        } finally {
          require(promise.complete(i))
        }
      }
        .fold(::identity) { fail("Should never come here") } shouldBe s
      promise.await() shouldBe i
    }
  }

  "try/catch - finally works suspended" {
    checkAll(Arb.string(), Arb.int()) { s, i ->
      val promise = CompletableDeferred<Int>()
      eagerEffect<String, Int> {
        try {
          shift(s)
        } finally {
          require(promise.complete(i))
        }
      }
        .fold(::identity) { fail("Should never come here") } shouldBe s
      promise.await() shouldBe i
    }
  }

  "try/catch - First shift is ignored and second is returned" {
    checkAll(Arb.int(), Arb.string(), Arb.string()) { i, s, s2 ->
      eagerEffect<String, Int> {
        val x: Int =
          try {
            shift(s)
          } catch (e: Throwable) {
            i
          }
        shift(s2)
      }
        .fold(::identity) { fail("Should never come here") } shouldBe s2
    }
  }

  suspend fun test() {
    val leakedAsync =
      coroutineScope<suspend () -> Deferred<Unit>> {
        suspend {
          async { println("I am never going to run, until I get called invoked from outside") }
        }
      }
    leakedAsync.invoke().await()
  }

  "try/catch - First shift is ignored and second is returned suspended" {
    checkAll(Arb.int(), Arb.string(), Arb.string()) { i, s, s2 ->
      eagerEffect<String, Int> {
        val x: Int =
          try {
            shift(s)
          } catch (e: Throwable) {
            i
          }
        shift(s2)
      }
        .fold(::identity) { fail("Should never come here") } shouldBe s2
    }
  }

  "immediate values" { eagerEffect<Nothing, Int> { 1 }.value() shouldBe 1 }

  "suspended value" { eagerEffect<Nothing, Int> { 1 }.value() shouldBe 1 }

  "immediate short-circuit" {
    eagerEffect<String, Nothing> { shift("hello") }.runCont() shouldBe "hello"
  }

  "suspended short-circuit" {
    eagerEffect<String, Nothing> { shift("hello") }.runCont() shouldBe "hello"
  }

  "Rethrows immediate exceptions" {
    val e = RuntimeException("test")
    Either.catch { eagerEffect<Nothing, Nothing> { throw e }.runCont() } shouldBe Either.Left(e)
  }

  "Rethrows suspended exceptions" {
    val e = RuntimeException("test")
    Either.catch { eagerEffect<Nothing, Nothing> { throw e }.runCont() } shouldBe Either.Left(e)
  }

  // Fails https://github.com/Kotlin/kotlinx.coroutines/issues/3005
  "ensure null in either computation" {
    checkAll(Arb.boolean(), Arb.int(), Arb.string()) { predicate, success, shift ->
      either<String, Int> {
        ensure(predicate) { shift }
        success
      } shouldBe if (predicate) success.right() else shift.left()
    }
  }

  // Fails https://github.com/Kotlin/kotlinx.coroutines/issues/3005
  "ensureNotNull in either computation" {
    fun square(i: Int): Int = i * i

    checkAll(Arb.int().orNull(), Arb.string()) { i: Int?, shift: String ->
      val res =
        either<String, Int> {
          val ii = i
          ensureNotNull(ii) { shift }
          square(ii) // Smart-cast by contract
        }
      val expected = i?.let(::square)?.right() ?: shift.left()
      res shouldBe expected
    }
  }
})
