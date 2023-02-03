package arrow.fx.resilience

import arrow.fx.coroutines.parMap
import arrow.fx.coroutines.parZip
import io.kotest.assertions.fail
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.checkAll
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.channels.Channel

@Suppress("unused")
class SagaSpec : StringSpec({
  "Saga returns action result" {
    checkAll(Arb.int()) { i ->
      val saga = saga({ i }) { fail("Doesn't run") }
      saga.transact() shouldBeExactly i
    }
  }

  class SagaFailed : RuntimeException()

  "Saga runs compensation if throw in builder & rethrows exception" {
    checkAll(Arb.int()) { i ->
      val compensation = CompletableDeferred<Int>()
      val saga = saga {
        saga({ i }) { compensation.complete(it) }
        throw SagaFailed()
      }
      shouldThrow<SagaFailed> { saga.transact() }
      compensation.await() shouldBeExactly i
    }
  }

  "Saga runs compensation if throw in saga & rethrows exception" {
    checkAll(Arb.int()) { i ->
      val compensation = CompletableDeferred<Int>()
      val saga = saga {
        saga({ i }) { compensation.complete(it) }
        saga({ throw SagaFailed() }) { fail("Doesn't run") }
      }
      shouldThrow<SagaFailed> { saga.transact() }
      compensation.await() shouldBeExactly i
    }
  }

  "Saga runs compensation in order & rethrows exception" {
    checkAll(Arb.int(), Arb.int()) { a, b ->
      val compensations = Channel<Int>(2)
      val saga = saga {
        saga({ a }) { compensations.send(it) }
        saga({ b }) { compensations.send(it) }
        saga({ throw SagaFailed() }) { fail("Doesn't run") }
      }
      shouldThrow<SagaFailed> { saga.transact() }
      compensations.receive() shouldBeExactly b
      compensations.receive() shouldBeExactly a
      compensations.close()
    }
  }

  "Sage composes compensation errors" {
    checkAll(Arb.int()) { a ->
      val compensationA = CompletableDeferred<Int>()
      val original = SagaFailed()
      val compensation = SagaFailed()
      val saga = saga {
        saga({ a }) { compensationA.complete(it) }
        saga({}) { throw compensation }
        saga({ throw original }) { fail("Doesn't run") }
      }
      val res = shouldThrow<SagaFailed> { saga.transact() }
      res shouldBe original
      res.suppressedExceptions[0] shouldBe compensation
      compensationA.await() shouldBeExactly a
    }
  }

  "Sage composes compensation errors when thrown in block" {
    checkAll(Arb.int()) { a ->
      val compensationA = CompletableDeferred<Int>()
      val original = SagaFailed()
      val compensation = SagaFailed()
      val saga = saga {
        saga({ a }) { compensationA.complete(it) }
        saga({}) { throw compensation }
        throw original
      }
      val res = shouldThrow<SagaFailed> { saga.transact() }
      res shouldBe original
      res.suppressedExceptions[0] shouldBe compensation
      compensationA.await() shouldBeExactly a
    }
  }

  "Saga can traverse" {
    checkAll(Arb.list(Arb.int())) { iis ->
      saga { iis.map { saga({ it }) { fail("Doesn't run") } } }.transact() shouldBe iis
    }
  }

  "Saga can parTraverse" {
    checkAll(Arb.list(Arb.int())) { iis ->
      saga { iis.parMap { saga({ it }) { fail("Doesn't run") } } }.transact() shouldBe iis
    }
  }

  "parZip runs left compensation" {
    checkAll(Arb.int()) { a ->
      val compensationA = CompletableDeferred<Int>()
      val latch = CompletableDeferred<Unit>()
      val saga = saga {
        parZip(
          {
            saga({
              latch.complete(Unit)
              a
            }) { compensationA.complete(it) }
          },
          {
            saga({
              latch.await()
              throw SagaFailed()
            }) { fail("Doesn't run") }
          }
        ) { _, _ -> }
      }
      shouldThrow<SagaFailed> { saga.transact() }
      compensationA.await() shouldBeExactly a
    }
  }

  "parZip runs right compensation" {
    checkAll(Arb.int()) { a ->
      val compensationB = CompletableDeferred<Int>()
      val latch = CompletableDeferred<Unit>()
      val saga = saga {
        parZip(
          {
            saga({
              latch.await()
              throw SagaFailed()
            }) { fail("Doesn't run") }
          },
          {
            saga({
              latch.complete(Unit)
              a
            }) { compensationB.complete(it) }
          }
        ) { _, _ -> }
      }
      shouldThrow<SagaFailed> { saga.transact() }
      compensationB.await() shouldBeExactly a
    }
  }
})
