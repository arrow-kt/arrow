package arrow.fx.coroutines

import arrow.core.Either
import arrow.core.continuations.either
import arrow.core.left
import io.kotest.assertions.fail
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.matchers.types.shouldBeTypeOf
import io.kotest.property.Arb
import io.kotest.property.Exhaustive
import io.kotest.property.arbitrary.bool
import io.kotest.property.arbitrary.element
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.negativeInt
import io.kotest.property.arbitrary.orNull
import io.kotest.property.arbitrary.positiveInt
import io.kotest.property.checkAll
import io.kotest.property.arbitrary.string
import io.kotest.property.exhaustive.collection
import io.kotest.property.exhaustive.of
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlin.random.Random

class ResourceTest : StringSpec({
  
  "acquire - success - identity" {
    checkAll(Arb.int()) { n ->
      resourceScope {
        install({ n }) { _, _ -> } shouldBe n
      }
    }
  }
  
  "respect FIFO order installed release functions" {
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
  
  "value resource is released with Complete" {
    checkAll(Arb.int()) { n ->
      val p = CompletableDeferred<ExitCase>()
      resourceScope {
        install({ n }) { _, ex -> require(p.complete(ex)) }
      }
      p.await() shouldBe ExitCase.Completed
    }
  }
  
  "error resource finishes with error" {
    checkAll(Arb.throwable()) { e ->
      val p = CompletableDeferred<ExitCase>()
      suspend fun ResourceScope.failingScope(): Nothing =
        install({ throw e }, { _, ex -> require(p.complete(ex)) })
      
      Either.catch {
        resourceScope { failingScope() }
      } should leftException(e)
    }
  }
  
  "never use can be cancelled with ExitCase.Completed" {
    checkAll(Arb.int()) { n ->
      val p = CompletableDeferred<ExitCase>()
      val start = CompletableDeferred<Unit>()
      suspend fun ResourceScope.n(): Int = install({ n }, { _, ex -> require(p.complete(ex)) })
      
      val f = async {
        resourceScope {
          n()
          require(start.complete(Unit))
          never<Int>()
        }
      }
      
      start.await()
      f.cancel()
      p.await().shouldBeInstanceOf<ExitCase.Cancelled>()
    }
  }
  
  "Map + bind (traverse)" {
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
  
  "Resource can close from either" {
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
  
  val depth: Int = 10
  
  class CheckableAutoClose {
    var started = true
    fun close() {
      started = false
    }
  }
  
  "parZip - success" {
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
  
  fun generate(): Pair<List<CompletableDeferred<Int>>, Resource<Int>> {
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
  
  "parZip - deep finalizers are called when final one blows" {
    io.kotest.property.checkAll(3, Arb.int(10..100)) {
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
  
  "parZip - deep finalizers are called when final one cancels" {
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
  "parZip - Deep finalizers get called on left or right cancellation" {
    checkAll(Arb.bool()) { isLeft ->
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
  
  "parZip - Right CancellationException on acquire" {
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
  
  "parZip - Left CancellationException on acquire" {
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
  
  "parZip - Right error on acquire" {
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
  
  "parZip - Left error on acquire" {
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
  
  "parZip - Right CancellationException on release" {
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
  
  "parZip - Left CancellationException on release" {
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
  
  "parZip - Right error on release" {
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
  
  "parZip - Left error on release" {
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
  
  "parZip - error in use" {
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
  
  "parZip - cancellation in use" {
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
  
  "resource.asFlow()" {
    checkAll(Arb.int()) { n ->
      val released = CompletableDeferred<ExitCase>()
      val r = resource({ n }, { _, ex -> require(released.complete(ex)) })
      
      r.asFlow().map { it + 1 }.toList() shouldBe listOf(n + 1)
      
      released.await() shouldBe ExitCase.Completed
    }
  }
  
  "resource.asFlow() - failed" {
    checkAll(Arb.int(), Arb.throwable()) { n, throwable ->
      val released = CompletableDeferred<ExitCase>()
      val r = resource({ n }, { _, ex -> require(released.complete(ex)) })
      
      shouldThrow<Throwable> {
        r.asFlow().collect { throw throwable }
      } shouldBe throwable
      
      released.await().shouldBeTypeOf<ExitCase.Failure>().failure shouldBe throwable
    }
  }
  
  "resource.asFlow() - cancelled" {
    checkAll(Arb.int()) { n ->
      val released = CompletableDeferred<ExitCase>()
      val r = resource({ n }, { _, ex -> require(released.complete(ex)) })
      
      shouldThrow<CancellationException> {
        r.asFlow().collect { throw CancellationException("") }
      }
      
      released.await().shouldBeTypeOf<ExitCase.Cancelled>()
    }
  }
  
  fun Exhaustive.Companion.exitCase(): Exhaustive<ExitCase> =
    Exhaustive.of(
      ExitCase.Completed,
      ExitCase.Failure(Exception()),
      ExitCase.Cancelled(CancellationException(null, null))
    )
  
  "allocated" {
    checkAll(
      Exhaustive.exitCase(),
      Arb.int(),
      Arb.string().map(::RuntimeException).orNull()
    ) { exitCase, seed, exception ->
      val released = CompletableDeferred<ExitCase>()
      val (allocate, release) = resource({ seed }) { _, exitCase -> released.complete(exitCase) }
        .allocated()
      
      try {
        allocate shouldBe seed
        exception?.let { throw it }
      } finally {
        release(exitCase)
      }
      released.await() shouldBe exitCase
    }
  }
})
