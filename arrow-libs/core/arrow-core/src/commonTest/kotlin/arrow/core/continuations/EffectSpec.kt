package arrow.core.continuations

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.identity
import arrow.core.left
import arrow.core.right
import io.kotest.assertions.fail
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
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
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.intrinsics.intercepted
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import kotlin.coroutines.startCoroutine
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EffectSpec :
  StringSpec({
    "try/catch - can recover from raise" {
      checkAll(Arb.int().suspend(), Arb.string().suspend()) { i, s ->
        effect {
          try {
            raise(s())
          } catch (e: Throwable) {
            i()
          }
        }.fold({ fail("Should never come here") }, ::identity) shouldBe i()
      }
    }

    "try/catch - finally works" {
      checkAll(Arb.string().suspend(), Arb.int().suspend()) { s, i ->
        val promise = CompletableDeferred<Int>()
        effect {
          try {
            raise(s().suspend())
          } finally {
            require(promise.complete(i()))
          }
        }
          .fold(::identity) { fail("Should never come here") } shouldBe s()
        promise.await() shouldBe i()
      }
    }

    "try/catch - First raise is ignored and second is returned" {
      checkAll(Arb.int().suspend(), Arb.string().suspend(), Arb.string().suspend()) { i, s, s2 ->
        effect<String, Int> {
          val x: Int = try {
            raise(s())
          } catch (e: Throwable) {
            i()
          }
          raise(s2())
        }
          .fold(::identity) { fail("Should never come here") } shouldBe s2()
      }
    }

    "attempt - catch" {
      checkAll(Arb.int().suspend(), Arb.long().suspend()) { i, l ->
        effect<String, Int> {
          effect<Long, Int> {
            raise(l())
          } recover { ll ->
            ll shouldBe l()
            i()
          }
        }.runCont() shouldBe i()
      }
    }

    "attempt - no catch" {
      checkAll(Arb.int().suspend(), Arb.long().suspend()) { i, l ->
        effect<String, Int> {
          effect<Long, Int> {
            i()
          } recover { ll ->
            ll shouldBe l()
            i() + 1
          }
        }.runCont() shouldBe i()
      }
    }

    "eagerEffect can be consumed within an Effect computation" {
      checkAll(Arb.int(), Arb.int().suspend()) { a, b ->
        val eager: EagerEffect<String, Int> =
          eagerEffect { a }

        effect {
          val bb = b()
          val aa = eager()
          aa + bb
        }.runCont() shouldBe (a + b())
      }
    }

    "eagerEffect raise short-circuits effect computation" {
      checkAll(Arb.string(), Arb.int().suspend()) { a, b ->
        val eager: EagerEffect<String, Int> =
          eagerEffect { raise(a) }

        effect {
          val bb = b()
          val aa = eager()
          aa + bb
        }.runCont() shouldBe a
      }
    }

    "success" {
      checkAll(Arb.int().suspend()) { i ->
        effect<Nothing, Int> { i() }.value() shouldBe i()
      }
    }

    "short-circuit" {
      checkAll(Arb.string().suspend()) { msg ->
        effect {
          raise(msg())
        }.runCont() shouldBe msg()
      }
    }

    "Rethrows exceptions" {
      checkAll(Arb.string().suspend()) { msg ->
        shouldThrow<RuntimeException> {
          effect<String, Int> {
            throw RuntimeException(msg())
          }.toEither()
        }.message shouldBe msg()
      }
    }

    "Can short-circuit from nested blocks" {
      checkAll(Arb.string().suspend()) { msg ->
        effect<String, Int> {
          effect<Nothing, Long> { raise(msg()) }.runCont()
          fail("Should never reach this point")
        }
          .runCont() shouldBe msg()
      }
    }

    "Can short-circuit immediately after suspending from nested blocks" {
      checkAll(Arb.string().suspend()) { msg ->
        effect<String, Int> {
          effect<Nothing, Long> {
            1L.suspend()
            raise(msg())
          }.runCont()
          fail("Should never reach this point")
        }.runCont() shouldBe msg()
      }
    }

    "ensure null in either computation" {
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

    "ensureNotNull in either computation" {
      fun square(i: Int): Int = i * i

      checkAll(Arb.int().orNull().suspend(), Arb.string().suspend()) { i, raise->
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

    "#2760 - dispatching in nested Effect blocks does not make the nested Continuation to hang" {
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

    "#2779 - handleErrorWith does not make nested Continuations hang" {
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

    "#2779 - bind nested in fold does not make nested Continuations hang" {
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

    "Can handle thrown exceptions" {
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

    "Can raise from thrown exceptions" {
      checkAll(Arb.string().suspend(), Arb.string().suspend()) { msg, fallback ->
        effect {
          effect<Int, String> {
            throw RuntimeException(msg())
          }.fold(
            { raise(fallback()) },
            ::identity,
            { it.length }
          )
        }.runCont() shouldBe fallback()
      }
    }

    "Can throw from thrown exceptions" {
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

    "catch - happy path" {
      checkAll(Arb.string().suspend()) { str ->
        effect<Int, String> {
          str()
        }.recover<Int, Nothing, String> { fail("It should never catch a success value") }
          .runCont() shouldBe str()
      }
    }

    @Suppress("UNREACHABLE_CODE")
    "catch - error path and recover" {
      checkAll(Arb.int().suspend(), Arb.string().suspend()) { int, fallback ->
        effect<Int, String> {
          raise(int())
          fail("It should never reach this point")
        }.recover<Int, Nothing, String> { fallback() }
          .runCont() shouldBe fallback()
      }
    }

    @Suppress("UNREACHABLE_CODE")
    "catch - error path and re-raise" {
      checkAll(Arb.int().suspend(), Arb.string().suspend()) { int, fallback ->
        effect<Int, Unit> {
          raise(int())
          fail("It should never reach this point")
        }.recover { raise(fallback()) }
          .runCont() shouldBe fallback()
      }
    }

    @Suppress("UNREACHABLE_CODE")
    "catch - error path and throw" {
      checkAll(Arb.int().suspend(), Arb.string().suspend()) { int, msg ->
        shouldThrow<RuntimeException> {
          effect<Int, String> {
            raise(int())
            fail("It should never reach this point")
          }.recover<Int, Nothing, String> { throw RuntimeException(msg()) }
            .runCont()
        }.message.shouldNotBeNull() shouldBe msg()
      }
    }

    "attempt - happy path" {
      checkAll(Arb.string().suspend()) { str ->
        effect<Int, String> {
          str()
        }.catch { fail("It should never catch a success value") }
          .runCont() shouldBe str()
      }
    }

    "attempt - error path and recover" {
      checkAll(Arb.string().suspend(), Arb.string().suspend()) { msg, fallback ->
        effect<Int, String> {
          throw RuntimeException(msg())
        }.catch { fallback() }
          .runCont() shouldBe fallback()
      }
    }

    "attempt - error path and re-raise" {
      checkAll(Arb.string().suspend(), Arb.int().suspend()) { msg, fallback ->
        effect<Int, Unit> {
          throw RuntimeException(msg())
        }.catch { raise(fallback()) }
          .runCont() shouldBe fallback()
      }
    }

    "attempt - error path and throw" {
      checkAll(Arb.string().suspend(), Arb.string().suspend()) { msg, msg2 ->
        shouldThrow<RuntimeException> {
          effect<Int, String> {
            throw RuntimeException(msg())
          }.catch { throw RuntimeException(msg2()) }
            .runCont()
        }.message.shouldNotBeNull() shouldBe msg2()
      }
    }

    "accumulate, returns every error" {
      checkAll(Arb.list(Arb.int(), range = 2 .. 100)) { errors ->
        either<NonEmptyList<Int>, List<String>> {
          mapOrAccumulate(errors) { raise(it) }
        } shouldBe NonEmptyList.fromListUnsafe(errors).left()
      }
    }

    "accumulate, returns no error" {
      checkAll(Arb.list(Arb.string())) { elements ->
        either<NonEmptyList<Int>, List<String>> {
          mapOrAccumulate(elements) { it }
        } shouldBe elements.right()
      }
    }
  })

private data class Failure(val msg: String)

suspend fun currentContext(): CoroutineContext = kotlin.coroutines.coroutineContext

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
