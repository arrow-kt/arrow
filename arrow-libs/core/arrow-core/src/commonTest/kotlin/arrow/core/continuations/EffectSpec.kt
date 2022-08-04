package arrow.core.continuations

import arrow.core.Either
import arrow.core.identity
import arrow.core.left
import arrow.core.right
import io.kotest.assertions.fail
import io.kotest.common.runBlocking
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.int
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
    "try/catch - can recover from shift" {
      checkAll(Arb.int(), Arb.string()) { i, s ->
        effect<String, Int> {
          try {
            shift(s)
          } catch (e: Throwable) {
            i
          }
        }.fold({ fail("Should never come here") }, ::identity) shouldBe i
      }
    }

    "try/catch - can recover from shift suspended" {
      checkAll(Arb.int(), Arb.string()) { i, s ->
        effect<String, Int> {
          try {
            shift(s.suspend())
          } catch (e: Throwable) {
            i
          }
        }.fold({ fail("Should never come here") }, ::identity) shouldBe i
      }
    }

    "try/catch - finally works" {
      checkAll(Arb.string(), Arb.int()) { s, i ->
        val promise = CompletableDeferred<Int>()
        effect<String, Int> {
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
        effect<String, Int> {
          try {
            shift(s.suspend())
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
        effect<String, Int> {
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

    "try/catch - First shift is ignored and second is returned suspended" {
      checkAll(Arb.int(), Arb.string(), Arb.string()) { i, s, s2 ->
        effect<String, Int> {
          val x: Int =
            try {
              shift(s.suspend())
            } catch (e: Throwable) {
              i
            }
          shift(s2.suspend())
        }
          .fold(::identity) { fail("Should never come here") } shouldBe s2
      }
    }

    "attempt - catch" {
      checkAll(Arb.int(), Arb.long()) { i, l ->
        effect<String, Int> {
          effect<Long, Int> {
            shift(l)
          } catch { ll ->
            ll shouldBe l
            i
          }
        }.runCont() shouldBe i
      }
    }

    "attempt - no catch" {
      checkAll(Arb.int(), Arb.long()) { i, l ->
        effect<String, Int> {
          effect<Long, Int> {
            i
          } catch { ll ->
            ll shouldBe l
            i + 1
          }
        }.runCont() shouldBe i
      }
    }

    "eagerEffect can be consumed within an Effect computation" {
      checkAll(Arb.int(), Arb.int()) { a, b ->
        val eager: EagerEffect<String, Int> =
          eagerEffect { a }

        effect<String, Int> {
          val aa = eager.bind()
          aa + b.suspend()
        }.runCont() shouldBe (a + b)
      }
    }

    "eagerEffect shift short-circuits effect computation" {
      checkAll(Arb.string(), Arb.int()) { a, b ->
        val eager: EagerEffect<String, Int> =
          eagerEffect { shift(a) }

        effect<String, Int> {
          val aa = eager.bind()
          aa + b.suspend()
        }.runCont() shouldBe a
      }
    }

    "immediate values" { effect<Nothing, Int> { 1 }.value() shouldBe 1 }

    "suspended value" { effect<Nothing, Int> { 1.suspend() }.value() shouldBe 1 }

    "immediate short-circuit" {
      effect<String, Nothing> { shift("hello") }.runCont() shouldBe "hello"
    }

    "suspended short-circuit" {
      effect<String, Nothing> { shift("hello".suspend()) }.runCont() shouldBe "hello"
    }

    "Rethrows immediate exceptions" {
      val e = RuntimeException("test")
      Either.catch { effect<Nothing, Nothing> { throw e }.runCont() } shouldBe Either.Left(e)
    }

    "Rethrows suspended exceptions" {
      val e = RuntimeException("test")
      Either.catch { effect<Nothing, Nothing> { e.suspend() }.runCont() } shouldBe Either.Left(e)
    }

    "Can short-circuit immediately from nested blocks" {
      effect<String, Int> {
        effect<Nothing, Long> { shift("test") }.runCont()
        fail("Should never reach this point")
      }
        .runCont() shouldBe "test"
    }

    "Can short-circuit suspended from nested blocks" {
      effect<String, Int> {
        effect<Nothing, Long> { shift("test".suspend()) }.runCont()
        fail("Should never reach this point")
      }
        .runCont() shouldBe "test"
    }

    "Can short-circuit immediately after suspending from nested blocks" {
      effect<String, Int> {
        effect<Nothing, Long> {
          1L.suspend()
          shift("test".suspend())
        }
          .runCont()
        fail("Should never reach this point")
      }
        .runCont() shouldBe "test"
    }

    "ensure null in either computation" {
      checkAll(Arb.boolean(), Arb.int(), Arb.string()) { predicate, success, shift ->
        either<String, Int> {
          ensure(predicate) { shift }
          success
        } shouldBe if (predicate) success.right() else shift.left()
      }
    }

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

    "low-level use-case: distinguish between concurrency error and shift exception" {
      val effect = effect<String, Int> { shift("Shift") }
      val e = RuntimeException("test")
      Either.catch {
        effect<String, Int> {
          try {
            effect.bind()
          } catch (eagerShiftError: Eager) {
            fail("Should never come here")
          } catch (shiftError: Suspend) {
            e.suspend()
          } catch (otherError: Throwable) {
            fail("Should never come here")
          }
        }.runCont()
      } shouldBe Either.Left(e)
    }

    "low-level use-case: eager shift exception within effect computations doesn't change shift exception" {
      val effect = eagerEffect<String, Int> { shift("Shift") }
      val e = RuntimeException("test")
      Either.catch {
        effect<String, Int> {
          try {
            effect.bind()
          } catch (eagerShiftError: Eager) {
            fail("Should never come here")
          } catch (shiftError: Suspend) {
            e.suspend()
          } catch (otherError: Throwable) {
            fail("Should never come here")
          }
        }.runCont()
      } shouldBe Either.Left(e)
    }
    
    "#2760 - dispatching in nested Effect blocks does not make the nested Continuation to hang" {
      checkAll(Arb.string()) { msg ->
        fun failure(): Effect<Failure, String> = effect {
          withContext(Dispatchers.Default) {}
          shift(Failure(msg))
        }
        
        effect<Failure, Int> {
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
          shift(error)
        }
      
        val newError: Effect<List<Char>, Int> =
          failed.handleErrorWith { str ->
            effect { shift(str.reversed().toList()) }
          }
      
        newError.toEither() shouldBe Either.Left(error.reversed().toList())
      }
    }
    
    "#2779 - bind nested in fold does not make nested Continuations hang" {
      checkAll(Arb.string()) { error ->
        val failed: Effect<String, Int> = effect {
          withContext(Dispatchers.Default) {}
          shift(error)
        }
      
        val newError: Effect<List<Char>, Int> =
          effect {
            failed.fold({ r ->
              effect<List<Char>, Int> {
                shift(r.reversed().toList())
              }.bind()
            }, ::identity)
          }
      
        newError.toEither() shouldBe Either.Left(error.reversed().toList())
      }
    }
  })

private data class Failure(val msg: String)

suspend fun currentContext(): CoroutineContext = kotlin.coroutines.coroutineContext

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
