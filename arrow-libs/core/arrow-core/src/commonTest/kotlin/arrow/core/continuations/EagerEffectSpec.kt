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
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.orNull
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import kotlinx.coroutines.CompletableDeferred

class EagerEffectSpec : StringSpec({
  "try/catch - can recover from shift" {
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
      }.fold(::identity) { fail("Should never come here") } shouldBe s2
    }
  }

  "attempt - catch" {
    checkAll(Arb.int(), Arb.long()) { i, l ->
      eagerEffect<String, Int> {
        catch({
          shift(l)
        }) { ll ->
          ll shouldBe l
          i
        }
      }.runCont() shouldBe i
    }
  }

  "attempt - no catch" {
    checkAll(Arb.int(), Arb.long()) { i, l ->
      eagerEffect {
        catch<String, Long, Int>({
          i
        }) { ll ->
          ll shouldBe l
          i + 1
        }
      }.runCont() shouldBe i
    }
  }

  "immediate values" { eagerEffect<Nothing, Int> { 1 }.toEither().orNull() shouldBe 1 }

  "immediate short-circuit" { eagerEffect<String, Nothing> { shift("hello") }.runCont() shouldBe "hello" }

  "Rethrows immediate exceptions" {
    val e = RuntimeException("test")
    Either.catch { eagerEffect<Nothing, Nothing> { throw e }.runCont() } shouldBe Either.Left(e)
  }

  "ensure null in eager either computation" {
    checkAll(Arb.boolean(), Arb.int(), Arb.string()) { predicate, success, shift ->
      either<String, Int> {
        ensure(predicate) { shift }
        success
      } shouldBe if (predicate) success.right() else shift.left()
    }
  }

  "ensureNotNull in eager either computation" {
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

  "low-level use-case: distinguish between concurrency error and shift exception" {
    val effect = eagerEffect<String, Int> { shift("Shift") }
    val e = RuntimeException("test")
    Either.catch {
      eagerEffect<String, Int> {
        try {
          effect.bind()
        } catch (eagerShiftError: ShiftCancellationException) {
          throw e
        } catch (otherError: Throwable) {
          fail("Should never come here")
        }
      }.runCont()
    } shouldBe Either.Left(e)
  }
})
