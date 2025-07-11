package arrow.core.raise

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.NonEmptySet
import arrow.core.identity
import arrow.core.left
import arrow.core.right
import arrow.core.shouldBeTypeOf
import arrow.core.shouldThrow
import arrow.core.test.either
import arrow.core.test.nonEmptyList
import arrow.core.test.nonEmptySet
import arrow.core.toNonEmptyListOrNull
import io.kotest.assertions.fail
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldStartWith
import io.kotest.property.Arb
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.flatMap
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.orNull
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.intrinsics.intercepted
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import kotlin.coroutines.startCoroutine
import kotlin.test.Test
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext

@Suppress("UNREACHABLE_CODE")
class EffectSpec {
  @OptIn(DelicateRaiseApi::class)
  @Test fun tryCatchCanRecoverFromRaise() = runTest {
    checkAll(Arb.int().suspend(), Arb.string().suspend()) { i, s ->
      effect {
        try {
          raise(s())
        } catch (_: RaiseCancellationException) {
          i()
        }
      }.getOrElse { unreachable() } shouldBe i()
    }
  }

  @Test fun tryCatchFinallyWorks() = runTest {
    checkAll(Arb.string().suspend(), Arb.int().suspend()) { s, i ->
      val promise = CompletableDeferred<Int>()
      effect {
        try {
          raise(s().suspend())
        } finally {
          require(promise.complete(i()))
        }
      }.fold(::identity) { unreachable() } shouldBe s()
      promise.await() shouldBe i()
    }
  }

  @OptIn(DelicateRaiseApi::class)
  @Test fun tryCatchFirstRaiseIsIgnoredAndSecondIsReturned() = runTest {
    checkAll(Arb.int().suspend(), Arb.string().suspend(), Arb.string().suspend()) { i, s, s2 ->
      effect<String, Int> {
        try {
          raise(s())
        } catch (_: RaiseCancellationException) {
          i()
        }
        raise(s2())
      }
        .fold(::identity) { unreachable() } shouldBe s2()
    }
  }

  @Test fun recoverRaise() = runTest {
    checkAll(Arb.int().suspend(), Arb.long().suspend()) { i, l ->
      effect<String, Int> {
        effect<Long, Int> {
          raise(l())
        } getOrElse { ll ->
          ll shouldBe l()
          i()
        }
      }.getOrElse { unreachable() } shouldBe i()
    }
  }

  @Test fun recoverRaiseAndTransformError() = runTest {
    checkAll(
      Arb.long().suspend(),
      Arb.string().suspend()
    ) { l, s ->
      effect {
        effect<Long, Int> {
          raise(l())
        } getOrElse { ll ->
          ll shouldBe l()
          raise(s())
        }
      }.fold(::identity) { unreachable() } shouldBe s()
    }
  }

  @Test fun recoverSuccess() = runTest {
    checkAll(Arb.int().suspend()) { i ->
      effect<String, Int> {
        effect<Long, Int> { i() } getOrElse { unreachable() }
      }.getOrElse { unreachable() } shouldBe i()
    }
  }

  @Test fun recoverCatchRaiseAndRecover() = runTest {
    checkAll(Arb.int().suspend(), Arb.long().suspend()) { i, l ->
      effect<String, Int> {
        effect<Long, Int> {
          raise(l())
        } getOrElse { ll ->
          ll shouldBe l()
          i()
        }
      }.getOrElse { unreachable() } shouldBe i()
    }
  }

  @Test fun recoverCatchRaiseAndTransformError() = runTest {
    checkAll(Arb.long().suspend(), Arb.string().suspend()) { l, s ->
      effect {
        effect<Long, Int> {
          raise(l())
        } getOrElse { ll ->
          ll shouldBe l()
          raise(s())
        }
      }.fold(::identity) { unreachable() } shouldBe s()
    }
  }

  val boom = RuntimeException("boom")

  @Test fun recoverCatchThrowAndRecover() = runTest {
    checkAll(Arb.int().suspend()) { i ->
      effect<String, Int> {
        effect<Long, Int> {
          throw boom
        }.catch { e ->
          e shouldBe boom
          i()
        }.getOrElse { unreachable() }
      }.getOrElse { unreachable() } shouldBe i()
    }
  }

  @Test fun recoverCatchThrowAndTransformError() = runTest {
    checkAll(Arb.string().suspend()) { s ->
      effect {
        effect<Long, Int> {
          throw boom
        }.catch { e ->
          e shouldBe boom
          raise(s())
        }.getOrElse { unreachable() }
      }.fold(::identity) { unreachable() } shouldBe s()
    }
  }

  @Test fun recoverCatchRaiseAndThrow() = runTest {
    checkAll(Arb.long().suspend()) { l ->
      effect<String, Int> {
        effect<Long, Int> {
          raise(l())
        }.recover { ll ->
          ll shouldBe l()
          throw boom
          raise("failure")
        }.getOrElse { unreachable() }
      }.fold(::identity, { unreachable() }) { unreachable() } shouldBe boom
    }
  }

  @Test fun recoverCatchThrowAndThrow() = runTest {
    val boom2 = ArithmeticException("boom2")
    effect<String, Int> {
      effect<Long, Int> {
        throw boom
      }.catch { e ->
        e shouldBe boom
        throw boom2
      }.getOrElse { unreachable() }
    }.fold(::identity, { unreachable() }) { unreachable() } shouldBe boom2
  }

  @Test fun recoverCatchSuccess() = runTest {
    checkAll(Arb.int().suspend()) { i ->
      effect<String, Int> {
        effect<Long, Int> { i() }
          .catch { unreachable() }.getOrElse { unreachable() }
      }.getOrElse { unreachable() } shouldBe i()
    }
  }

  @Test fun catchThrowAndThrow() = runTest {
    val boom2 = ArithmeticException("boom2")
    effect {
      effect<String, Int> {
        throw boom
      }.catch { e ->
        e shouldBe boom
        throw boom2
      }.bind()
    }.fold(::identity, { unreachable() }) { unreachable() } shouldBe boom2
  }

  @Test fun catchThrowAndTransformError() = runTest {
    checkAll(Arb.string().suspend()) { s ->
      effect {
        effect<String, Int> {
          throw boom
        }.catch { e ->
          e shouldBe boom
          raise(s())
        }.bind()
      }.fold(::identity) { unreachable() } shouldBe s()
    }
  }

  @Test fun catchThrowAndRecover() = runTest {
    checkAll(Arb.int().suspend()) { i ->
      effect {
        effect<String, Int> {
          throw boom
        }.catch { e ->
          e shouldBe boom
          i()
        }.bind()
      }.getOrElse { unreachable() } shouldBe i()
    }
  }

  @Test fun eagerEffectCanBeConsumedWithinAnEffectComputation() = runTest {
    checkAll(Arb.int(), Arb.int().suspend()) { a, b ->
      val eager: EagerEffect<String, Int> =
        eagerEffect { a }

      effect {
        val bb = b()
        val aa = eager()
        aa + bb
      }.fold(::identity, ::identity) shouldBe (a + b())
    }
  }

  @Test fun eagerEffectRaiseShortCircuitsEffectComputation() = runTest {
    checkAll(Arb.string(), Arb.int().suspend()) { a, b ->
      val eager: EagerEffect<String, Int> =
        eagerEffect { raise(a) }

      effect {
        val bb = b()
        val aa = eager()
        aa + bb
      }.fold(::identity, ::identity) shouldBe a
    }
  }

  @Test fun eagerEffectCanBeConsumedWithinAnEffectComputationWithBind() = runTest {
    checkAll(Arb.int(), Arb.int().suspend()) { a, b ->
      val eager: EagerEffect<String, Int> =
        eagerEffect { a }

      effect {
        val bb = b()
        val aa = eager.bind()
        aa + bb
      }.fold(::identity, ::identity) shouldBe (a + b())
    }
  }

  @Test fun eagerEffectRaiseShortCircuitsEffectComputationWithBind() = runTest {
    checkAll(Arb.string(), Arb.int().suspend()) { a, b ->
      val eager: EagerEffect<String, Int> =
        eagerEffect { raise(a) }

      effect {
        val bb = b()
        val aa = eager.bind()
        aa + bb
      }.fold(::identity, ::identity) shouldBe a
    }
  }

  @Test fun success() = runTest {
    checkAll(Arb.int().suspend()) { i ->
      effect<Nothing, Int> { i() }
        .getOrElse { unreachable() } shouldBe i()
    }
  }

  @Test fun shortCircuit() = runTest {
    checkAll(Arb.string().suspend()) { msg ->
      effect {
        raise(msg())
      }.fold(::identity) { unreachable() } shouldBe msg()
    }
  }

  @Test fun rethrowsExceptions() = runTest {
    checkAll(Arb.string().suspend()) { msg ->
      shouldThrow<RuntimeException> {
        effect<String, Int> {
          throw RuntimeException(msg())
        }.toEither()
      }.message shouldBe msg()
    }
  }

  @Test fun canShortCircuitFromNestedBlocks() = runTest {
    checkAll(Arb.string().suspend()) { msg ->
      effect<String, Int> {
        effect<Nothing, Long> { raise(msg()) }.getOrElse { unreachable() }
        fail("Should never reach this point")
      }
        .fold(::identity, ::identity) shouldBe msg()
    }
  }

  @Test fun canShortCircuitImmediatelyAfterSuspendingFromNestedBlocks() = runTest {
    checkAll(Arb.string().suspend()) { msg ->
      effect<String, Int> {
        effect<Nothing, Long> {
          1L.suspend()
          raise(msg())
        }.getOrElse { unreachable() }
        fail("Should never reach this point")
      }.fold(::identity, ::identity) shouldBe msg()
    }
  }

  @Test fun ensureNullInEitherComputation() = runTest {
    checkAll(
      Arb.boolean().suspend(),
      Arb.int().suspend(),
      Arb.string().suspend()
    ) { predicate, success, raise ->
      either {
        ensure(predicate()) { raise() }
        success()
      } shouldBe if (predicate()) success().right() else raise().left()
    }
  }

  @Test fun ensureNotNullInEitherComputation() = runTest {
    fun square(i: Int): Int = i * i

    checkAll(Arb.int().orNull().suspend(), Arb.string().suspend()) { i, raise ->
      val res =
        either {
          val ii = i()
          ensureNotNull(ii) { raise() }
          square(ii) // Smart-cast by contract
        }
      val expected = i()?.let(::square)?.right() ?: raise().left()
      res shouldBe expected
    }
  }

  @Test fun issue2760DispatchingInNestedEffectBlocksDoesNotMakeTheNestedContinuationToHang() = runTest {
    checkAll(Arb.string()) { msg ->
      fun failure(): Effect<Failure, String> = effect {
        withContext(Dispatchers.Default) {}
        raise(Failure(msg))
      }

      effect {
        failure().bind()
        1
      }.fold(
        recover = { it },
        transform = { fail("Should never come here") },
      ) shouldBe Failure(msg)
    }
  }

  @Test fun issue2779HandleErrorWithDoesNotMakeNestedEffectHang() = runTest {
    checkAll(Arb.string()) { error ->
      val failed: Effect<String, Int> = effect {
        withContext(Dispatchers.Default) {}
        raise(error)
      }

      val newError: Effect<List<Char>, Int> =
        failed.recover { str ->
          raise(str.reversed().toList())
        }

      newError.toEither() shouldBe Either.Left(error.reversed().toList())
    }
  }

  @Test fun issue2779BindNestedInFoldDoesNotMakeNestedEffectHang() = runTest {
    checkAll(Arb.string()) { error ->
      val failed: Effect<String, Int> = effect {
        withContext(Dispatchers.Default) {}
        raise(error)
      }

      val newError: Effect<List<Char>, Int> =
        effect {
          failed.fold({ r ->
            effect<List<Char>, Int> {
              raise(r.reversed().toList())
            }.bind()
          }, ::identity)
        }

      newError.toEither() shouldBe Either.Left(error.reversed().toList())
    }
  }

  @Test fun canHandleThrownExceptions() = runTest {
    checkAll(Arb.string().suspend(), Arb.string().suspend()) { msg, fallback ->
      effect<Int, String> {
        throw RuntimeException(msg())
      }.fold(
        { fallback() },
        ::identity,
        ::identity
      ) shouldBe fallback()
    }
  }

  @Test fun canRaiseFromThrownExceptions() = runTest {
    checkAll(Arb.string().suspend(), Arb.string().suspend()) { msg, fallback ->
      effect {
        effect<Int, String> {
          throw RuntimeException(msg())
        }.fold(
          { raise(fallback()) },
          ::identity,
          { it.length }
        )
      }.fold(::identity, ::identity) shouldBe fallback()
    }
  }

  @Test fun canThrowFromThrownExceptions() = runTest {
    checkAll(Arb.string().suspend(), Arb.string().suspend()) { msg, fallback ->
      shouldThrow<IllegalStateException> {
        effect<Int, String> {
          throw RuntimeException(msg())
        }.fold(
          { throw IllegalStateException(fallback()) },
          ::identity,
          { it.length }
        )
      }.message shouldBe fallback()
    }
  }

  @Test fun recoverHappyPath() = runTest {
    checkAll(Arb.string().suspend()) { str ->
      effect<Int, String> {
        str()
      }.recover<Int, Nothing, String> { fail("It should never catch a success value") }
        .getOrElse { unreachable() } shouldBe str()
    }
  }

  @Test fun recoverErrorPathAndRecover() = runTest {
    checkAll(Arb.int().suspend(), Arb.string().suspend()) { int, fallback ->
      effect<Int, String> {
        raise(int())
        unreachable()
      }.recover<Int, Nothing, String> { fallback() }
        .getOrElse { unreachable() } shouldBe fallback()
    }
  }

  @Test fun recoverErrorPathAndReRaise() = runTest {
    checkAll(Arb.int().suspend(), Arb.string().suspend()) { int, fallback ->
      effect<Int, Unit> {
        raise(int())
        unreachable()
      }.recover { raise(fallback()) }
        .fold(::identity, ::identity) shouldBe fallback()
    }
  }

  @Test fun recoverErrorPathAndThrow() = runTest {
    checkAll(Arb.int().suspend(), Arb.string().suspend()) { int, msg ->
      shouldThrow<RuntimeException> {
        effect<Int, String> {
          raise(int())
          unreachable()
        }.recover<Int, Nothing, String> { throw RuntimeException(msg()) }
          .getOrElse { unreachable() }
      }.message.shouldNotBeNull() shouldBe msg()
    }
  }

  @Test fun catchHappyPath() = runTest {
    checkAll(Arb.string().suspend()) { str ->
      effect<Int, String> {
        str()
      }.catch { unreachable() }
        .getOrElse { unreachable() } shouldBe str()
    }
  }

  @Test fun catchErrorPathAndRecover() = runTest {
    checkAll(Arb.string().suspend(), Arb.string().suspend()) { msg, fallback ->
      effect<Int, String> {
        throw RuntimeException(msg())
      }.catch { fallback() }
        .fold({ unreachable() }, { unreachable() }, ::identity) shouldBe fallback()
    }
  }

  @Test fun catchErrorPathAndReRaise() = runTest {
    checkAll(Arb.string().suspend(), Arb.int().suspend()) { msg, fallback ->
      effect<Int, Unit> {
        throw RuntimeException(msg())
      }.catch { raise(fallback()) }
        .fold({ unreachable() }, ::identity) { unreachable() } shouldBe fallback()
    }
  }

  @Test fun catchErrorPathAndThrow() = runTest {
    checkAll(Arb.string().suspend(), Arb.string().suspend()) { msg, msg2 ->
      effect<Int, String> {
        throw RuntimeException(msg())
      }.catch { throw IllegalStateException(msg2()) }
        .fold(::identity, { unreachable() }, { unreachable() })
        .shouldBeTypeOf<IllegalStateException>()
        .message shouldBe msg2()
    }
  }

  @Test fun catchReifiedErrorPathAndRecover() = runTest {
    checkAll(Arb.string().suspend(), Arb.string().suspend()) { msg, fallback ->
      effect<Int, String> {
        throw ArithmeticException(msg())
      }.catch { e: ArithmeticException ->
        e.message shouldBe msg()
        fallback()
      }.fold({ unreachable() }, { unreachable() }, ::identity) shouldBe fallback()
    }
  }

  @Test fun catchReifiedErrorPathAndRaise() = runTest {
    checkAll(Arb.string().suspend(), Arb.int().suspend()) { msg, error ->
      effect<Int, String> {
        throw ArithmeticException(msg())
      }.catch { e: ArithmeticException ->
        e.message shouldBe msg()
        raise(error())
      }.fold({ unreachable() }, ::identity) { unreachable() } shouldBe error()
    }
  }

  @Test fun catchReifiedErrorPathAndNoMatch() = runTest {
    checkAll(Arb.string().suspend()) { msg ->
      effect<Int, String> {
        throw RuntimeException(msg())
      }.catch { _: ArithmeticException ->
        unreachable()
      }.fold(
        ::identity,
        { unreachable() }
      ) { unreachable() }
        .shouldBeTypeOf<RuntimeException>()
        .message shouldBe msg()
    }
  }

  @Test fun catchSuccess() = runTest {
    checkAll(Arb.string().suspend()) { msg ->
      effect<Int, String> {
        msg()
      }.catch()
        .getOrElse { unreachable() } shouldBe Result.success(msg())
    }
  }

  @Test fun catchException() = runTest {
    checkAll(Arb.string().suspend()) { msg ->
      effect<Int, String> {
        throw RuntimeException(msg())
      }.catch()
        .fold({ unreachable() }, { unreachable() }, ::identity)
        .exceptionOrNull()
        .shouldNotBeNull()
        .message shouldBe msg()
    }
  }

  @Test fun accumulateReturnsEveryError() = runTest {
    checkAll(Arb.list(Arb.int(), range = 2..20)) { errors ->
      either<NonEmptyList<Int>, List<String>> {
        mapOrAccumulate(errors) { raise(it) }
      } shouldBe errors.toNonEmptyListOrNull()!!.left()
    }
  }

  @Test fun accumulateReturnsNoError() = runTest {
    checkAll(Arb.list(Arb.int(), range = 0..20)) { elements ->
      either<NonEmptyList<Int>, List<Int>> {
        mapOrAccumulate(elements) { it }
      } shouldBe elements.right()
    }
  }

  @Test fun nonEmptyListMapOrAccumulateReturnsEveryError() = runTest {
    checkAll(Arb.nonEmptyList(Arb.int(), range = 2..20)) { errors ->
      either<NonEmptyList<Int>, NonEmptyList<String>> {
        mapOrAccumulate(errors) { raise(it) }
      } shouldBe errors.toNonEmptyListOrNull()!!.left()
    }
  }

  @Test fun nonEmptyListMapOrAccumulateReturnsNoError() = runTest {
    checkAll(Arb.nonEmptyList(Arb.int(), range = 0..20)) { elements ->
      either<NonEmptyList<Int>, NonEmptyList<Int>> {
        mapOrAccumulate(elements) { it }
      } shouldBe elements.right()
    }
  }

  @Test fun nonEmptySetMapOrAccumulateReturnsEveryError() = runTest {
    checkAll(Arb.nonEmptySet(Arb.int(), range = 2..20)) { errors ->
      either<NonEmptyList<Int>, NonEmptySet<String>> {
        mapOrAccumulate(errors) { raise(it) }
      } shouldBe errors.toNonEmptyListOrNull()!!.left()
    }
  }

  @Test fun nonEmptySetMapOrAccumulateReturnsNoError() = runTest {
    checkAll(Arb.nonEmptySet(Arb.int(), range = 0..20)) { elements ->
      either<NonEmptyList<Int>, NonEmptySet<Int>> {
        mapOrAccumulate(elements) { it }
      } shouldBe elements.right()
    }
  }

  @Test fun bindAllFailsOnFirstError() = runTest {
    checkAll(Arb.list(Arb.either(Arb.int(), Arb.int()), range = 0..20)) { eithers ->
      val expected = eithers.firstOrNull { it.isLeft() } ?: eithers.mapNotNull { it.getOrNull() }.right()
      either {
        eithers.bindAll()
      } shouldBe expected
    }
  }

  @Test fun accumulateBindAll() = runTest {
    checkAll(Arb.list(Arb.either(Arb.int(), Arb.int()), range = 0..20)) { eithers ->
      val expected =
        eithers.mapNotNull { it.leftOrNull() }.toNonEmptyListOrNull()?.left() ?: eithers.mapNotNull { it.getOrNull() }.right()

      either {
        zipOrAccumulate(
          { eithers.bindAll() },
          { emptyList<Int>() }
        ) { a, b -> a + b }
      } shouldBe expected
    }
  }

  @Test fun nonEmptyListBindAllFailsOnFirstError() = runTest {
    checkAll(Arb.nonEmptyList(Arb.either(Arb.int(), Arb.int()), range = 0..20)) { eithers ->
      val expected = eithers.firstOrNull { it.isLeft() } ?: eithers.mapNotNull { it.getOrNull() }.right()
      either {
        eithers.bindAll()
      } shouldBe expected
    }
  }

  @Test fun nonEmptyListBindAllAccumulateErrors() = runTest {
    checkAll(Arb.nonEmptyList(Arb.either(Arb.int(), Arb.int()), range = 0..20)) { eithers ->
      val expected =
        eithers.mapNotNull { it.leftOrNull() }.toNonEmptyListOrNull()?.left() ?: eithers.mapNotNull { it.getOrNull() }.right()

      either {
        zipOrAccumulate(
          { eithers.bindAll() },
          { emptyList<Int>() }
        ) { a, b -> a + b }
      } shouldBe expected
    }
  }

  @Test fun nonEmptySetBindAllFailsOnFirstError() = runTest {
    checkAll(Arb.nonEmptySet(Arb.either(Arb.int(), Arb.int()), range = 0..20)) { eithers ->
      val expected = eithers.firstOrNull { it.isLeft() } ?: eithers.mapNotNull { it.getOrNull() }.toSet().right()
      either {
        eithers.bindAll()
      } shouldBe expected
    }
  }

  @Test fun nonEmptySetBindAllAccumulateErrors() = runTest {
    checkAll(Arb.nonEmptySet(Arb.either(Arb.int(), Arb.int()), range = 0..20)) { eithers ->
      val expected =
        eithers.mapNotNull { it.leftOrNull() }.toNonEmptyListOrNull()?.left() ?: eithers.mapNotNull { it.getOrNull() }.toSet().right()

      either {
        zipOrAccumulate(
          { eithers.bindAll() },
          { emptySet<Int>() }
        ) { a, b -> a + b }
      } shouldBe expected
    }
  }

  /*
  @Test fun shiftLeakedResultsInRaiseLeakException() = runTest {
    effect {
      suspend { raise("failure") }
    }.fold(
      {
        it.message shouldStartWith "raise or bind was called outside of its DSL scope"
      },
      { unreachable() }) { f -> f() }
  }
  */

  @Test fun shiftLeakedResultsInRaiseLeakExceptionWithException() = runTest {
    shouldThrow<IllegalStateException> {
      val leak = CompletableDeferred<suspend () -> Unit>()
      effect {
        leak.complete { raise("failure") }
        throw RuntimeException("Boom")
      }.fold(
        {
          it.shouldBeTypeOf<RuntimeException>().message shouldBe "Boom"
          leak.await().invoke()
        },
        { fail("Cannot be here") }
      ) { fail("Cannot be here") }
    }.message shouldStartWith "'raise' or 'bind' was leaked"
  }

  @Test fun shiftLeakedResultsInRaiseLeakExceptionAfterRaise() = runTest {
    shouldThrow<IllegalStateException> {
      val leak = CompletableDeferred<suspend () -> Unit>()
      effect {
        leak.complete { raise("failure") }
        raise("Boom!")
      }.fold(
        { unreachable() },
        {
          it shouldBe "Boom!"
          leak.await().invoke()
        }) { fail("Cannot be here") }
    }.message shouldStartWith "'raise' or 'bind' was leaked"
  }

  @Test fun mapErrorRaiseAndTransformError() = runTest {
    checkAll(
      Arb.long().suspend(),
      Arb.string().suspend()
    ) { l, s ->
      (effect<Long, Int> {
        raise(l())
      } mapError { ll ->
        ll shouldBe l()
        s()
      }).fold(::identity) { unreachable() } shouldBe s()
    }
  }

  @Test fun mapErrorSuccess() = runTest {
    checkAll(Arb.int().suspend()) { i ->
      (effect<Long, Int> { i() } mapError { unreachable() })
        .get() shouldBe i()
    }
  }
}

private data class Failure(val msg: String)

// Turn `A` into `suspend () -> A` which tests both the `immediate` and `COROUTINE_SUSPENDED` path.
private fun <A> Arb<A>.suspend(): Arb<suspend () -> A> =
  flatMap { a ->
    arbitrary(listOf(
      { a },
      suspend { a.suspend() }
    )) { suspend { a.suspend() } }
  }

internal suspend fun Throwable.suspend(): Nothing = suspendCoroutineUninterceptedOrReturn { cont ->
  suspend { throw this }
    .startCoroutine(Continuation(Dispatchers.Default) { cont.intercepted().resumeWith(it) })

  COROUTINE_SUSPENDED
}

internal suspend fun <A> A.suspend(): A = suspendCoroutineUninterceptedOrReturn { cont ->
  suspend { this }
    .startCoroutine(Continuation(Dispatchers.Default) { cont.intercepted().resumeWith(it) })

  COROUTINE_SUSPENDED
}

internal fun unreachable(): Nothing =
  fail("It should never reach this point")
