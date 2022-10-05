package arrow.core.continuations

import arrow.core.Either
import arrow.core.identity
import arrow.core.left
import arrow.core.right
import io.kotest.assertions.fail
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.nulls.shouldNotBeNull
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
      eagerEffect {
        try {
          raise(s)
        } catch (e: Throwable) {
          i
        }
      }.fold({ fail("Should never come here") }, ::identity) shouldBe i
    }
  }
  
  "try/catch - finally works" {
    checkAll(Arb.string(), Arb.int()) { s, i ->
      val promise = CompletableDeferred<Int>()
      eagerEffect {
        try {
          raise<Int>(s)
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
            raise(s)
          } catch (e: Throwable) {
            i
          }
        raise(s2)
      }.fold(::identity) { fail("Should never come here") } shouldBe s2
    }
  }
  
  "attempt - catch" {
    checkAll(Arb.int(), Arb.long()) { i, l ->
      eagerEffect<String, Int> {
        eagerEffect<Long, Int> {
          raise(l)
        } recover { ll ->
          ll shouldBe l
          i
        }
      }.runCont() shouldBe i
    }
  }
  
  "attempt - no catch" {
    checkAll(Arb.int(), Arb.long()) { i, l ->
      eagerEffect<String, Int> {
        eagerEffect<Long, Int> {
          i
        } recover { ll ->
          ll shouldBe l
          i + 1
        }
      }.runCont() shouldBe i
    }
  }
  
  "attempt - shift from catch" {
    checkAll(Arb.int(), Arb.long(), Arb.string()) { i, l, error ->
      eagerEffect {
        eagerEffect<Long, Int> {
          raise(l)
        } recover { ll ->
          ll shouldBe l
          raise(error)
        }
      }.runCont() shouldBe error
    }
  }
  
  "values" { eagerEffect<Nothing, Int> { 1 }.toEither().orNull() shouldBe 1 }
  
  "short-circuit" { eagerEffect<String, Nothing> { raise("hello") }.runCont() shouldBe "hello" }
  
  "Rethrows exceptions" {
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
  
  "catch - happy path" {
    checkAll(Arb.string()) { str ->
      eagerEffect<Int, String> {
        str
      }.recover<Int, Nothing, String> { fail("It should never catch a success value") }
        .runCont() shouldBe str
    }
  }
  
  "catch - error path and recover" {
    checkAll(Arb.int(), Arb.string()) { int, fallback ->
      eagerEffect<Int, String> {
        raise<String>(int)
        fail("It should never reach this point")
      }.recover<Int, Nothing, String> { fallback }
        .runCont() shouldBe fallback
    }
  }
  
  "catch - error path and re-shift" {
    checkAll(Arb.int(), Arb.string()) { int, fallback ->
      eagerEffect<Int, Unit> {
        raise<String>(int)
        fail("It should never reach this point")
      }.recover { raise(fallback) }
        .runCont() shouldBe fallback
    }
  }
  
  "catch - error path and throw" {
    checkAll(Arb.int(), Arb.string()) { int, msg ->
      shouldThrow<RuntimeException> {
        eagerEffect<Int, String> {
          raise<String>(int)
          fail("It should never reach this point")
        }.recover<Int, Nothing, String> { throw RuntimeException(msg) }
          .runCont()
      }.message.shouldNotBeNull() shouldBe msg
    }
  }
  
  "attempt - happy path" {
    checkAll(Arb.string()) { str ->
      eagerEffect<Int, String> {
        str
      }.catch { fail("It should never catch a success value") }
        .runCont() shouldBe str
    }
  }
  
  "attempt - error path and recover" {
    checkAll(Arb.string(), Arb.string()) { msg, fallback ->
      eagerEffect<Int, String> {
        throw RuntimeException(msg)
      }.catch { fallback }
        .runCont() shouldBe fallback
    }
  }
  
  "attempt - error path and re-shift" {
    checkAll(Arb.string(), Arb.int()) { msg, fallback ->
      eagerEffect<Int, Unit> {
        throw RuntimeException(msg)
      }.catch { raise(fallback) }
        .runCont() shouldBe fallback
    }
  }
  
  "attempt - error path and throw" {
    checkAll(Arb.string(), Arb.string()) { msg, msg2 ->
      shouldThrow<RuntimeException> {
        eagerEffect<Int, String> {
          throw RuntimeException(msg)
        }.catch { throw RuntimeException(msg2) }
          .runCont()
      }.message.shouldNotBeNull() shouldBe msg2
    }
  }
})
