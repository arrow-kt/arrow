package arrow.core.raise

import arrow.core.Either
import arrow.core.identity
import arrow.core.left
import arrow.core.right
import io.kotest.assertions.fail
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldStartWith
import io.kotest.matchers.types.shouldBeTypeOf
import io.kotest.property.Arb
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.orNull
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import kotlin.test.Test
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.test.runTest

@Suppress("UNREACHABLE_CODE", "UNUSED_EXPRESSION")
class EagerEffectSpec {
  @Test fun tryCatchCanRecoverFromRaise() = runTest {
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

  @Test fun tryCatchFinallyWorks() = runTest {
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

  @Test fun tryCatchFirstRaiseIsIgnoredAndSecondIsReturned() = runTest {
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

  @Test fun recoverCatch() = runTest {
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

  @Test fun recoverNoCatch() = runTest {
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

  @Test fun recoverRaiseFromCatch() = runTest {
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

  @Test fun success() = runTest {
    eagerEffect<Nothing, Int> { 1 }
      .fold({ unreachable() }, ::identity) shouldBe 1
  }

  @Test fun shortCircuit() = runTest {
    eagerEffect {
      raise("hello")
    }.fold(::identity) { unreachable() } shouldBe "hello"
  }

  @Test fun rethrowsExceptions() = runTest {
    val e = RuntimeException("test")
    Either.catch {
      eagerEffect<Nothing, Nothing> { throw e }
        .fold({ unreachable() }, { unreachable() })
    } shouldBe Either.Left(e)
  }

  @Test fun ensureNullInEagerEitherComputation() = runTest {
    checkAll(Arb.boolean(), Arb.int(), Arb.string()) { predicate, success, raise ->
      either {
        ensure(predicate) { raise }
        success
      } shouldBe if (predicate) success.right() else raise.left()
    }
  }

  @Test fun ensureNotNullInEagerEitherComputation() = runTest {
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

  @Test fun recoverHappyPath() = runTest {
    checkAll(Arb.string()) { str ->
      eagerEffect<Int, String> {
        str
      }.recover<Int, Nothing, String> { unreachable() }
        .fold({ unreachable() }, ::identity) shouldBe str
    }
  }

  @Test fun recoverErrorPathAndRecover() = runTest {
    checkAll(Arb.int(), Arb.string()) { int, fallback ->
      eagerEffect<Int, String> {
        raise(int)
        unreachable()
      }.recover<Int, Nothing, String> { fallback }
        .fold({ unreachable() }, ::identity) shouldBe fallback
    }
  }

  @Test fun recoverErrorPathAndReRaise() = runTest {
    checkAll(Arb.int(), Arb.string()) { int, fallback ->
      eagerEffect<Int, Unit> {
        raise(int)
        unreachable()
      }.recover { raise(fallback) }
        .fold(::identity) { unreachable() } shouldBe fallback
    }
  }

  @Test fun recoverErrorPathAndThrow() = runTest {
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

  @Test fun catchHappyPath() = runTest {
    checkAll(Arb.string()) { str ->
      eagerEffect<Int, String> {
        str
      }.catch { unreachable() }
        .fold({ unreachable() }, ::identity) shouldBe str
    }
  }

  @Test fun catchErrorPathAndRecover() = runTest {
    checkAll(Arb.string(), Arb.string()) { msg, fallback ->
      eagerEffect<Int, String> {
        throw RuntimeException(msg)
      }.catch { fallback }
        .fold({ unreachable() }, ::identity) shouldBe fallback
    }
  }

  @Test fun catchErrorPathAndReRaise() = runTest {
    checkAll(Arb.string(), Arb.int()) { msg, fallback ->
      eagerEffect<Int, Unit> {
        throw RuntimeException(msg)
      }.catch { raise(fallback) }
        .fold(::identity) { unreachable() } shouldBe fallback
    }
  }

  @Test fun catchErrorPathAndThrow() = runTest {
    checkAll(Arb.string(), Arb.string()) { msg, msg2 ->
      shouldThrow<RuntimeException> {
        eagerEffect<Int, String> {
          throw RuntimeException(msg)
        }.catch { throw RuntimeException(msg2) }
          .fold({ unreachable() }, { unreachable() })
      }.message.shouldNotBeNull() shouldBe msg2
    }
  }

  @Test fun catchReifiedExceptionAndRecover() = runTest {
    eagerEffect<Nothing, Int> {
      throw ArithmeticException()
    }.catch { _: ArithmeticException -> 1 }
      .fold({ unreachable() }, ::identity) shouldBe 1
  }

  @Test fun catchReifiedExceptionAndRaise() = runTest {
    eagerEffect<String, Int> {
      throw ArithmeticException("Boom!")
    }.catch { e: ArithmeticException -> raise(e.message.shouldNotBeNull()) }
      .fold(::identity) { unreachable() } shouldBe "Boom!"
  }

  @Test fun catchReifiedExceptionAndNoMatch() = runTest {
    shouldThrow<RuntimeException> {
      eagerEffect<Nothing, Int> {
        throw RuntimeException("Boom!")
      }.catch { _: ArithmeticException -> 1 }
        .fold({ unreachable() }, { unreachable() })
    }.message shouldBe "Boom!"
  }

  @Test fun shiftLeakedResultsInRaiseLeakException() = runTest {
    eagerEffect {
      suspend { raise("failure") }
    }.fold(
      {
        it.message shouldStartWith "raise or bind was called outside of its DSL scope"
      },
      { unreachable() }) { f -> f() }
  }

  @Test fun shiftLeakedResultsInRaiseLeakExceptionWithException() = runTest {
    shouldThrow<IllegalStateException> {
      val leak = CompletableDeferred<suspend () -> Unit>()
      eagerEffect {
        leak.complete { raise("failure") }
        throw RuntimeException("Boom")
      }.fold(
        {
          it.shouldBeTypeOf<RuntimeException>().message shouldBe "Boom"
          leak.await().invoke()
        },
        { fail("Cannot be here") }
      ) { fail("Cannot be here") }
    }.message shouldStartWith "raise or bind was called outside of its DSL scope"
  }

  @Test fun shiftLeakedResultsInRaiseLeakExceptionAfterRaise() = runTest {
    shouldThrow<IllegalStateException> {
      val leak = CompletableDeferred<suspend () -> Unit>()
      eagerEffect {
        leak.complete { raise("failure") }
        raise("Boom!")
      }.fold(
        { unreachable() },
        {
          it shouldBe "Boom!"
          leak.await().invoke()
        }) { fail("Cannot be here") }
    }.message shouldStartWith "raise or bind was called outside of its DSL scope"
  }

  @Test fun mapErrorRaiseAndTransformError() = runTest {
    checkAll(Arb.long(), Arb.string()) { l, s ->
      (eagerEffect<Long, Int> {
        raise(l)
      } mapError { ll ->
        ll shouldBe l
        s
      }).fold(::identity) { unreachable() } shouldBe s
    }
  }

  @Test fun mapErrorSuccess() = runTest {
    checkAll(Arb.int()) { i ->
      (eagerEffect<Long, Int> { i } mapError { unreachable() })
        .get() shouldBe i
    }
  }
}
