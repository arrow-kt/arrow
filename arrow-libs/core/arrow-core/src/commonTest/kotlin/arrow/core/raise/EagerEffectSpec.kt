package arrow.core.raise

import arrow.core.Either
import arrow.core.identity
import arrow.core.left
import arrow.core.right
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

@Suppress("UNREACHABLE_CODE", "UNUSED_EXPRESSION")
class EagerEffectSpec : StringSpec({
  "try/catch - can recover from raise" {
    checkAll(Arb.int(), Arb.string()) { i, s ->
      eagerEffect {
        try {
          raise(s)
        } catch (e: Throwable) {
          i
        }
      }.fold({ unreachable() }, ::identity) shouldBe i
    }
  }

  "try/catch - finally works" {
    checkAll(Arb.string(), Arb.int()) { s, i ->
      val promise = CompletableDeferred<Int>()
      eagerEffect {
        try {
          raise(s)
        } finally {
          require(promise.complete(i))
        }
      }
        .fold(::identity) { unreachable() } shouldBe s
      promise.await() shouldBe i
    }
  }

  "try/catch - First raise is ignored and second is returned" {
    checkAll(Arb.int(), Arb.string(), Arb.string()) { i, s, s2 ->
      eagerEffect<String, Int> {
        try {
          raise(s)
        } catch (e: Throwable) {
          i
        }
        raise(s2)
      }.fold(::identity) { unreachable() } shouldBe s2
    }
  }

  "recover - catch" {
    checkAll(Arb.int(), Arb.long()) { i, l ->
      eagerEffect<String, Int> {
        eagerEffect<Long, Int> {
          raise(l)
        } getOrElse  { ll ->
          ll shouldBe l
          i
        }
      }.fold({ unreachable() }, { it }) shouldBe i
    }
  }

  "recover - no catch" {
    checkAll(Arb.int(), Arb.long()) { i, l ->
      eagerEffect<String, Int> {
        eagerEffect<Long, Int> {
          i
        } getOrElse  { ll ->
          ll shouldBe l
          i + 1
        }
      }.fold({ unreachable() }, ::identity) shouldBe i
    }
  }

  "recover - raise from catch" {
    checkAll(Arb.long(), Arb.string()) { l, error ->
      eagerEffect {
        eagerEffect<Long, Int> {
          raise(l)
        } getOrElse  { ll ->
          ll shouldBe l
          raise(error)
        }
      }.fold(::identity) { unreachable() } shouldBe error
    }
  }

  "success" {
    eagerEffect<Nothing, Int> { 1 }
      .fold({ unreachable() }, ::identity) shouldBe 1
  }

  "short-circuit" {
    eagerEffect {
      raise("hello")
    }.fold(::identity) { unreachable() } shouldBe "hello"
  }

  "Rethrows exceptions" {
    val e = RuntimeException("test")
    Either.catch {
      eagerEffect<Nothing, Nothing> { throw e }
        .fold({ unreachable() }, { unreachable() })
    } shouldBe Either.Left(e)
  }

  "ensure null in eager either computation" {
    checkAll(Arb.boolean(), Arb.int(), Arb.string()) { predicate, success, raise ->
      either {
        ensure(predicate) { raise }
        success
      } shouldBe if (predicate) success.right() else raise.left()
    }
  }

  "ensureNotNull in eager either computation" {
    fun square(i: Int): Int = i * i

    checkAll(Arb.int().orNull(), Arb.string()) { i: Int?, raise: String ->
      val res = either {
        ensureNotNull(i) { raise }
        square(i) // Smart-cast by contract
      }
      val expected = i?.let(::square)?.right() ?: raise.left()
      res shouldBe expected
    }
  }

  "recover - happy path" {
    checkAll(Arb.string()) { str ->
      eagerEffect<Int, String> {
        str
      }.recover<Int, Nothing, String> { unreachable() }
        .fold({ unreachable() }, ::identity) shouldBe str
    }
  }

  "recover - error path and recover" {
    checkAll(Arb.int(), Arb.string()) { int, fallback ->
      eagerEffect<Int, String> {
        raise(int)
        unreachable()
      }.recover<Int, Nothing, String> { fallback }
        .fold({ unreachable() }, ::identity) shouldBe fallback
    }
  }

  "recover - error path and re-raise" {
    checkAll(Arb.int(), Arb.string()) { int, fallback ->
      eagerEffect<Int, Unit> {
        raise(int)
        unreachable()
      }.recover { raise(fallback) }
        .fold(::identity) { unreachable() } shouldBe fallback
    }
  }

  "recover - error path and throw" {
    checkAll(Arb.int(), Arb.string()) { int, msg ->
      shouldThrow<RuntimeException> {
        eagerEffect<Int, String> {
          raise(int)
          unreachable()
        }.recover<Int, Nothing, String> { throw RuntimeException(msg) }
          .fold({ unreachable() }, { unreachable() })
      }.message.shouldNotBeNull() shouldBe msg
    }
  }

  "catch - happy path" {
    checkAll(Arb.string()) { str ->
      eagerEffect<Int, String> {
        str
      }.catch { unreachable() }
        .fold({ unreachable() }, ::identity) shouldBe str
    }
  }

  "catch - error path and recover" {
    checkAll(Arb.string(), Arb.string()) { msg, fallback ->
      eagerEffect<Int, String> {
        throw RuntimeException(msg)
      }.catch { fallback }
        .fold({ unreachable() }, ::identity) shouldBe fallback
    }
  }

  "catch - error path and re-raise" {
    checkAll(Arb.string(), Arb.int()) { msg, fallback ->
      eagerEffect<Int, Unit> {
        throw RuntimeException(msg)
      }.catch { raise(fallback) }
        .fold(::identity) { unreachable() } shouldBe fallback
    }
  }

  "catch - error path and throw" {
    checkAll(Arb.string(), Arb.string()) { msg, msg2 ->
      shouldThrow<RuntimeException> {
        eagerEffect<Int, String> {
          throw RuntimeException(msg)
        }.catch { throw RuntimeException(msg2) }
          .fold({ unreachable() }, { unreachable() })
      }.message.shouldNotBeNull() shouldBe msg2
    }
  }

  "catch - reified exception and recover" {
    eagerEffect<Nothing, Int> {
      throw ArithmeticException()
    }.catch { _: ArithmeticException -> 1 }
      .fold({ unreachable() }, ::identity) shouldBe 1
  }

  "catch - reified exception and raise" {
    eagerEffect<String, Int> {
      throw ArithmeticException("Boom!")
    }.catch { e: ArithmeticException -> raise(e.message.shouldNotBeNull()) }
      .fold(::identity) { unreachable() } shouldBe "Boom!"
  }

  "catch - reified exception and no match" {
    shouldThrow<RuntimeException> {
      eagerEffect<Nothing, Int> {
        throw RuntimeException("Boom!")
      }.catch { _: ArithmeticException -> 1 }
        .fold({ unreachable() }, { unreachable() })
    }.message shouldBe "Boom!"
  }
})
