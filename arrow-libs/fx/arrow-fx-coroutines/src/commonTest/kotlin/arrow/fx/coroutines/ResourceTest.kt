package arrow.fx.coroutines

import arrow.core.Either
import arrow.core.left
import arrow.core.raise.either
import arrow.fx.coroutines.ExitCase.Companion.ExitCase
import io.kotest.assertions.fail
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.matchers.types.shouldBeTypeOf
import io.kotest.property.Arb
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.negativeInt
import io.kotest.property.arbitrary.positiveInt
import io.kotest.property.checkAll
import io.kotest.property.arbitrary.string
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class ResourceTest {

  @Test
  fun acquireSuccessIdentity() = runTest {
    checkAll(Arb.int()) { n ->
      resourceScope {
        install({ n }) { _, _ -> } shouldBe n
      }
    }
  }

  @Test
  fun respectFIFOOrderInstalledFunction() = runTest {
    checkAll(Arb.positiveInt(), Arb.negativeInt()) { a, b ->
      val order = mutableListOf<Int>()

      suspend fun ResourceScope.scoped(n: Int): Int =
        install({ n.also(order::add) }, { it, _ -> order.add(-it) })

      resourceScope {
        val x = scoped(a)
        val y = scoped(x + b)
        y + 1 shouldBe (a + b) + 1
      }

      order.shouldContainExactly(a, a + b, -a - b, -a)
    }
  }

  @Test
  fun resourceReleasedWithComplete() = runTest {
    checkAll(Arb.int()) { n ->
      val p = CompletableDeferred<ExitCase>()
      resourceScope {
        install({ n }) { _, ex -> require(p.complete(ex)) }
      }
      p.await() shouldBe ExitCase.Completed
    }
  }

  @Test
  fun errorFinishesWithError() = runTest {
    checkAll(Arb.throwable()) { e ->
      val p = CompletableDeferred<ExitCase>()
      suspend fun ResourceScope.failingScope(): Nothing =
        install({ throw e }, { _, ex -> require(p.complete(ex)) })

      Either.catch {
        resourceScope { failingScope() }
      } should leftException(e)
    }
  }

  @Test
  fun neverCancelled() = runTest {
    checkAll(Arb.int()) { n ->
      val p = CompletableDeferred<ExitCase>()
      val start = CompletableDeferred<Unit>()
      suspend fun ResourceScope.n(): Int = install({ n }, { _, ex -> require(p.complete(ex)) })

      val f = async {
        resourceScope {
          n()
          require(start.complete(Unit))
          awaitCancellation()
        }
      }

      start.await()
      f.cancel()
      p.await().shouldBeInstanceOf<ExitCase.Cancelled>()
    }
  }

  @Test
  fun mapBind() = runTest {
    checkAll(
      Arb.list(Arb.int()),
      Arb.functionAToB<Int, String>(Arb.string())
    ) { list, f ->
      resourceScope {
        list.map {
          resource { f(it) }.bind()
        }
      } shouldBe list.map(f)
    }
  }

  @Test
  fun resourceCloseFromEither() = runTest {
    val exit = CompletableDeferred<ExitCase>()
    either<String, Int> {
      resourceScope {
        install({ 1 }) { _, ex ->
          require(exit.complete(ex))
        }
        raise("error")
      }
    } shouldBe "error".left()
    exit.await().shouldBeTypeOf<ExitCase.Cancelled>()
  }

  private val depth = 10

  class CheckableAutoClose {
    var started = true
    fun close() {
      started = false
    }
  }

  @Test
  fun parZipSuccess() = runTestUsingDefaultDispatcher {
    suspend fun ResourceScope.closeable(): CheckableAutoClose =
      install({ CheckableAutoClose() }) { a: CheckableAutoClose, _: ExitCase -> a.close() }

    resourceScope {
      parZip({
        (1..depth).map { closeable() }
      }, {
        (1..depth).map { closeable() }
      }, { a, b -> a + b })
    }
  }

  private fun generate(): Pair<List<CompletableDeferred<Int>>, Resource<Int>> {
    val promises = (1..depth).map { Pair(it, CompletableDeferred<Int>()) }
    val res = promises.fold(resource({ 0 }, { _, _ -> })) { acc, (i, promise) ->
      resource {
        val ii = acc.bind()
        install({ ii + i }) { _, _ ->
          require(promise.complete(i))
        }
      }
    }
    return Pair(promises.map { it.second }, res)
  }

  @Test
  fun parZipFinalizersBlow() = runTestUsingDefaultDispatcher {
    checkAll(3, Arb.int(10..100)) {
      val (promises, resource) = generate()
      shouldThrow<RuntimeException> {
        resourceScope {
          parZip({
            resource.bind()
            throw RuntimeException()
          }, { }) { _, _ -> }
          fail("It should never reach here")
        }
      }

      (1..depth).zip(promises) { i, promise ->
        promise.await() shouldBe i
      }
    }
  }

  @Test
  fun parZipFinalizersCancel() = runTestUsingDefaultDispatcher {
    checkAll(3, Arb.int(10..100)) {
      val cancel = CancellationException(null, null)
      val (promises, resource) = generate()
      shouldThrow<CancellationException> {
        resourceScope {
          parZip({}, {
            resource.bind()
            throw cancel
          }) { _, _ -> }
          fail("It should never reach here")
        }
      }

      (1..depth).zip(promises) { i, promise ->
        promise.await() shouldBe i
      }
    }
  }

  // Test multiple release triggers on acquire fail.
  @Test
  fun parZipFinalizersLeftOrRightCancellation() = runTestUsingDefaultDispatcher {
    checkAll(Arb.boolean()) { isLeft ->
      val cancel = CancellationException(null, null)
      val (promises, resource) = generate()
      val latch = CompletableDeferred<Int>()
      shouldThrow<CancellationException> {
        resourceScope {
          if (isLeft) {
            parZip({
              latch.await() shouldBe (1..depth).sum()
              throw cancel
            }, {
              val i = resource.bind()
              require(latch.complete(i))
            }) { _, _ -> }
          } else {
            parZip({
              val i = resource.bind()
              require(latch.complete(i))
            }, {
              latch.await() shouldBe (1..depth).sum()
              throw cancel
            }) { _, _ -> }
          }
          fail("It should never reach here")
        }
      }

      (1..depth).zip(promises) { i, promise ->
        promise.await() shouldBe i
      }
    }
  }

  @Test
  fun parZipRightCancellationExceptionOnAcquire() = runTestUsingDefaultDispatcher {
    checkAll(Arb.int()) { i ->
      val cancel = CancellationException(null, null)
      val released = CompletableDeferred<Pair<Int, ExitCase>>()
      val started = CompletableDeferred<Unit>()
      shouldThrow<CancellationException> {
        resourceScope {
          parZip({
            install({
              require(started.complete(Unit))
              i
            }, { ii: Int, ex: ExitCase ->
              require(released.complete(ii to ex))
            })
          }, {
            started.await()
            throw cancel
          }) { _, _ -> }
          fail("It should never reach here")
        }
      }

      val (ii, ex) = released.await()
      ii shouldBe i
      ex.shouldBeTypeOf<ExitCase.Cancelled>()
    }
  }

  @Test
  fun parZipLeftCancellationExceptionOnAcquire() = runTestUsingDefaultDispatcher {
    checkAll(Arb.int()) { i ->
      val cancel = CancellationException(null, null)
      val released = CompletableDeferred<Pair<Int, ExitCase>>()
      val started = CompletableDeferred<Unit>()

      shouldThrow<CancellationException> {
        resourceScope {
          parZip({
            started.await()
            throw cancel
          }, {
            install({
              require(started.complete(Unit))
              i
            }, { ii: Int, ex: ExitCase ->
              require(released.complete(ii to ex))
            })
          }) { _, _ -> }
          fail("It should never reach here")
        }
      }

      val (ii, ex) = released.await()
      ii shouldBe i
      ex.shouldBeTypeOf<ExitCase.Cancelled>()
    }
  }

  @Test
  fun parZipRightErrorOnAcquire() = runTestUsingDefaultDispatcher {
    checkAll(Arb.int(), Arb.throwable()) { i, throwable ->
      val released = CompletableDeferred<Pair<Int, ExitCase>>()
      val started = CompletableDeferred<Unit>()
      shouldThrow<Throwable> {
        resourceScope {
          parZip({
            install({
              require(started.complete(Unit))
              i
            }, { ii: Int, ex: ExitCase -> require(released.complete(ii to ex)) }
            )
          }, {
            started.await()
            throw throwable
          }) { _, _ -> }
          fail("It should never reach here")
        }

      } shouldBe throwable

      val (ii, ex) = released.await()
      ii shouldBe i
      ex.shouldBeTypeOf<ExitCase.Failure>()
    }
  }

  @Test
  fun parZipLeftErrorOnAcquire() = runTestUsingDefaultDispatcher {
    checkAll(Arb.int(), Arb.throwable()) { i, throwable ->
      val released = CompletableDeferred<Pair<Int, ExitCase>>()
      val started = CompletableDeferred<Unit>()
      shouldThrow<Throwable> {
        resourceScope {
          parZip({
            started.await()
            throw throwable
          }, {
            install({
              require(started.complete(Unit))
              i
            }, { ii: Int, ex: ExitCase -> require(released.complete(ii to ex)) }
            )
          }) { _, _ -> }
          fail("It should never reach here")
        }
      } shouldBe throwable

      val (ii, ex) = released.await()
      ii shouldBe i
      ex.shouldBeTypeOf<ExitCase.Failure>()
    }
  }

  @Test
  fun parZipRightCancellationExceptionOnRelease() = runTestUsingDefaultDispatcher {
    checkAll(Arb.int()) { i ->
      val cancel = CancellationException(null, null)
      val released = CompletableDeferred<Pair<Int, ExitCase>>()

      shouldThrow<CancellationException> {
        resourceScope {
          parZip({
            install({ i }, { ii: Int, ex: ExitCase -> require(released.complete(ii to ex)) })
          }, {
            install({ }, { _: Unit, _: ExitCase -> throw cancel })
          }) { _, _ -> }
        }
      }

      val (ii, ex) = released.await()
      ii shouldBe i
      ex.shouldBeTypeOf<ExitCase.Completed>()
    }
  }

  @Test
  fun parZipLeftCancellationExceptionOnRelease() = runTestUsingDefaultDispatcher {
    checkAll(Arb.int()) { i ->
      val cancel = CancellationException(null, null)
      val released = CompletableDeferred<Pair<Int, ExitCase>>()

      shouldThrow<CancellationException> {
        resourceScope {
          parZip({
            install({ }, { _: Unit, _: ExitCase -> throw cancel })
          }, {
            install({ i }, { ii: Int, ex: ExitCase -> require(released.complete(ii to ex)) })
          }) { _, _ -> }
        }
      }

      val (ii, ex) = released.await()
      ii shouldBe i
      ex.shouldBeTypeOf<ExitCase.Completed>()
    }
  }

  @Test
  fun parZipRightErrorOnRelease() = runTestUsingDefaultDispatcher {
    checkAll(Arb.int(), Arb.throwable()) { i, throwable ->
      val released = CompletableDeferred<Pair<Int, ExitCase>>()

      shouldThrow<Throwable> {
        resourceScope {
          parZip({
            install({ i }, { ii: Int, ex: ExitCase -> require(released.complete(ii to ex)) })
          }, {
            install({ }, { _: Unit, _: ExitCase -> throw throwable })
          }) { _, _ -> }
        }
      } shouldBe throwable

      val (ii, ex) = released.await()
      ii shouldBe i
      ex.shouldBeTypeOf<ExitCase.Completed>()
    }
  }

  @Test
  fun parZipLeftErrorOnRelease() = runTestUsingDefaultDispatcher {
    checkAll(Arb.int(), Arb.throwable()) { i, throwable ->
      val released = CompletableDeferred<Pair<Int, ExitCase>>()

      shouldThrow<Throwable> {
        resourceScope {
          parZip({
            install({ }, { _: Unit, _: ExitCase -> throw throwable })
          }, {
            install({ i }, { ii: Int, ex: ExitCase -> require(released.complete(ii to ex)) })
          }) { _, _ -> }
        }
      } shouldBe throwable

      val (ii, ex) = released.await()
      ii shouldBe i
      ex.shouldBeTypeOf<ExitCase.Completed>()
    }
  }

  @Test
  fun parZipErrorInUse() = runTestUsingDefaultDispatcher {
    checkAll(Arb.int(), Arb.int(), Arb.throwable()) { a, b, throwable ->
      val releasedA = CompletableDeferred<Pair<Int, ExitCase>>()
      val releasedB = CompletableDeferred<Pair<Int, ExitCase>>()

      shouldThrow<Throwable> {
        resourceScope {
          parZip({
            install({ a }) { aa: Int, ex: ExitCase -> require(releasedA.complete(aa to ex)) }
          }, {
            install({ b }) { bb: Int, ex: ExitCase -> require(releasedB.complete(bb to ex)) }
          }) { _, _ -> }
          throw throwable
        }
      } shouldBe throwable

      val (aa, exA) = releasedA.await()
      aa shouldBe a
      exA.shouldBeTypeOf<ExitCase.Failure>()

      val (bb, exB) = releasedB.await()
      bb shouldBe b
      exB.shouldBeTypeOf<ExitCase.Failure>()
    }
  }

  @Test
  fun parZipCancellationInUse() = runTestUsingDefaultDispatcher {
    checkAll(Arb.int(), Arb.int()) { a, b ->
      val releasedA = CompletableDeferred<Pair<Int, ExitCase>>()
      val releasedB = CompletableDeferred<Pair<Int, ExitCase>>()

      shouldThrow<CancellationException> {
        resourceScope {
          parZip({
            install({ a }) { aa: Int, ex: ExitCase -> require(releasedA.complete(aa to ex)) }
          }, {
            install({ b }) { bb: Int, ex: ExitCase -> require(releasedB.complete(bb to ex)) }
          }) { _, _ -> }
          throw CancellationException("")
        }
      }

      val (aa, exA) = releasedA.await()
      aa shouldBe a
      exA.shouldBeTypeOf<ExitCase.Cancelled>()

      val (bb, exB) = releasedB.await()
      bb shouldBe b
      exB.shouldBeTypeOf<ExitCase.Cancelled>()
    }
  }

  @Test
  fun resourceAsFlow() = runTest {
    checkAll(Arb.int()) { n ->
      val released = CompletableDeferred<ExitCase>()
      val r = resource({ n }, { _, ex -> require(released.complete(ex)) })

      r.asFlow().map { it + 1 }.toList() shouldBe listOf(n + 1)

      released.await() shouldBe ExitCase.Completed
    }
  }

  @Test
  fun resourceAsFlowFail() = runTest {
    checkAll(Arb.int(), Arb.throwable()) { n, throwable ->
      val released = CompletableDeferred<ExitCase>()
      val r = resource({ n }, { _, ex -> require(released.complete(ex)) })

      shouldThrow<Throwable> {
        r.asFlow().collect { throw throwable }
      } shouldBe throwable

      released.await().shouldBeTypeOf<ExitCase.Failure>().failure shouldBe throwable
    }
  }

  @Test
  fun resourceAsFlowCancel() = runTest {
    checkAll(Arb.int()) { n ->
      val released = CompletableDeferred<ExitCase>()
      val r = resource({ n }, { _, ex -> require(released.complete(ex)) })

      shouldThrow<CancellationException> {
        r.asFlow().collect { throw CancellationException("") }
      }

      released.await().shouldBeTypeOf<ExitCase.Cancelled>()
    }
  }

  @OptIn(DelicateCoroutinesApi::class)
  @Test
  fun allocatedWorks() = runTest {
    checkAll(Arb.int()) { seed ->
      val released = CompletableDeferred<ExitCase>()
      val (allocate, release) = resource({ seed }) { _, exitCase -> released.complete(exitCase) }
        .allocated()

      allocate shouldBe seed
      release(ExitCase.Completed)
      released.await() shouldBe ExitCase.Completed
    }
  }

  @OptIn(DelicateCoroutinesApi::class)
  @Test
  fun allocatedSupressedException() = runTest {
    checkAll(
      Arb.int(),
      Arb.string().map(::RuntimeException),
      Arb.string().map(::IllegalStateException)
    ) { seed, original, suppressed ->
      val released = CompletableDeferred<ExitCase>()
      val (allocate, release) =
        resource({ seed }) { _, exitCase ->
          released.complete(exitCase)
          throw suppressed
        }.allocated()

      val exception = shouldThrow<RuntimeException> {
        try {
          allocate shouldBe seed
          throw original
        } catch (e: Throwable) {
          release(ExitCase(e))
        }
      }

      exception shouldBe original
      exception.suppressedExceptions.firstOrNull().shouldNotBeNull() shouldBe suppressed
      released.await().shouldBeTypeOf<ExitCase.Failure>()
    }
  }

  @OptIn(DelicateCoroutinesApi::class)
  @Test
  fun allocatedCancellationException() = runTest {
    checkAll(
      Arb.int(),
      Arb.string().map { CancellationException(it, null) },
      Arb.string().map(::IllegalStateException)
    ) { seed, cancellation, suppressed ->
      val released = CompletableDeferred<ExitCase>()
      val (allocate, release) =
        resource({ seed }) { _, exitCase ->
          released.complete(exitCase)
          throw suppressed
        }.allocated()

      val exception = shouldThrow<CancellationException> {
        try {
          allocate shouldBe seed
          throw cancellation
        } catch (e: Throwable) {
          release(ExitCase(e))
        }
      }

      exception shouldBe cancellation
      exception.suppressedExceptions.firstOrNull().shouldNotBeNull() shouldBe suppressed
      released.await().shouldBeTypeOf<ExitCase.Cancelled>()
    }
  }
}
