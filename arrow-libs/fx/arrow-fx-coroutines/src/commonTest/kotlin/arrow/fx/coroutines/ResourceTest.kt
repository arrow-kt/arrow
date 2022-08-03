package arrow.fx.coroutines

import arrow.core.Either
import arrow.core.continuations.either
import arrow.core.left
import arrow.fx.coroutines.continuations.ResourceScope
import arrow.fx.coroutines.continuations.resource
import io.kotest.assertions.fail
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.matchers.types.shouldBeTypeOf
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bool
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.negativeInt
import io.kotest.property.arbitrary.positiveInt
import io.kotest.property.arbitrary.string
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList

class ResourceTest : ArrowFxSpec(
  spec = {
    
    "Can consume resource" {
      checkAll(Arb.int()) { n ->
        val r = resource({ n }, { _, _ -> })
        r.use { it + 1 } shouldBe n + 1
      }
    }
    
    "flatMap resource is released first" {
      checkAll(Arb.positiveInt(), Arb.negativeInt()) { a, b ->
        val l = mutableListOf<Int>()
        fun r(n: Int) = resource({ n.also(l::add) }, { it, _ -> l.add(-it) })
        
        resource {
          val i = r(a).bind()
          r(i + b).bind()
        }.use { it + 1 } shouldBe (a + b) + 1
        
        l.shouldContainExactly(a, a + b, -a - b, -a)
      }
    }
    
    "value resource is released with Complete" {
      checkAll(Arb.int()) { n ->
        val p = CompletableDeferred<ExitCase>()
        resource({ n }, { _, ex -> require(p.complete(ex)) }).use { }
        
        p.await() shouldBe ExitCase.Completed
      }
    }
    
    "error resource finishes with error" {
      checkAll(Arb.throwable()) { e ->
        val p = CompletableDeferred<ExitCase>()
        val r = resource<Int>({ throw e }, { _, ex -> require(p.complete(ex)) })
        
        Either.catch {
          r.use { it + 1 }
        } should leftException(e)
      }
    }
    
    "never use can be cancelled with ExitCase.Completed" {
      checkAll(Arb.int()) { n ->
        val p = CompletableDeferred<ExitCase>()
        val start = CompletableDeferred<Unit>()
        val r = resource({ n }, { _, ex -> require(p.complete(ex)) })
        
        val f = async {
          r.use {
            require(start.complete(Unit))
            never<Int>()
          }
        }
        
        start.await()
        f.cancel()
        p.await().shouldBeInstanceOf<ExitCase.Cancelled>()
      }
    }
    
    "traverse: identity" {
      checkAll(
        Arb.list(Arb.int()),
        Arb.functionAToB<Int, String>(Arb.string())
      ) { list, f ->
        resource {
          list.map {
            resource { f(it) }.bind()
          }
        } resourceShouldBe resource { list.map(f) }
      }
    }
    
    "Resource can close from either" {
      val exit = CompletableDeferred<ExitCase>()
      either<String, Int> {
        resource {
          resource({ 1 }) { _, ex -> require(exit.complete(ex)) }
          shift<Int>("error")
        }.use { it }
      } shouldBe "error".left()
      exit.await().shouldBeTypeOf<ExitCase.Cancelled>()
    }
    
    val depth: Int = 100
    
    class CheckableAutoClose {
      var started = true
      fun close() {
        started = false
      }
    }
    
    "parZip - success" {
      suspend fun ResourceScope.closeable(): CheckableAutoClose =
        resource({ CheckableAutoClose() }) { a, _ -> a.close() }
      
      val all = resource {
        parZip({
          (1..depth).map { closeable() }
        }, {
          (1..depth).map { closeable() }
        }, { a, b -> a + b })
      }.use { all ->
        all.also { all.forEach { it.started shouldBe true } }
      }
      
      all.forEach { it.started shouldBe false }
    }
    
    fun generate(): Pair<List<CompletableDeferred<Int>>, Resource<Int>> {
      val promises = (1..depth).map { Pair(it, CompletableDeferred<Int>()) }
      val res = promises.fold(resource({ 0 }, { _, _ -> })) { acc, (i, promise) ->
        resource {
          val ii = acc.bind()
          resource({ ii + i }) { _, _ ->
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
          resource {
            parZip({
              resource.bind()
              throw RuntimeException()
            }, { }) { _, _ -> }
          }.use { fail("It should never reach here") }
        }
        
        (1..depth).zip(promises) { i, promise ->
          promise.await() shouldBe i
        }
      }
    }
    
    "parZip - deep finalizers are called when final one cancels" {
      io.kotest.property.checkAll(3, Arb.int(10..100)) {
        val cancel = CancellationException(null, null)
        val (promises, resource) = generate()
        shouldThrow<CancellationException> {
          resource {
            parZip({}, {
              resource.bind()
              throw cancel
            }) { _, _ -> }
          }.use { fail("It should never reach here") }
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
          val res = resource {
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
          }
          
          res.use { fail("It should never reach here") }
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
          resource {
            parZip({
              resource({ require(started.complete(Unit)); i }, { ii, ex ->
                require(released.complete(ii to ex))
              })
            }, {
              started.await()
              throw cancel
            }) { _, _ -> }
          }.use { fail("It should never reach here") }
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
          resource {
            parZip({
              started.await()
              throw cancel
            }, {
              resource({ require(started.complete(Unit)); i }, { ii, ex ->
                require(released.complete(ii to ex))
              })
            }) { _, _ -> }
          }.use { fail("It should never reach here") }
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
          resource {
            parZip({
              resource(
                { require(started.complete(Unit)); i },
                { ii, ex -> require(released.complete(ii to ex)) }
              )
            }, {
              started.await()
              throw throwable
            }) { _, _ -> }
          }.use { fail("It should never reach here") }
          
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
          resource {
            parZip({
              started.await()
              throw throwable
            }, {
              resource(
                { require(started.complete(Unit)); i },
                { ii, ex -> require(released.complete(ii to ex)) }
              )
            }) { _, _ -> }
          }.use { fail("It should never reach here") }
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
          resource {
            parZip({
              resource({ i }, { ii, ex -> require(released.complete(ii to ex)) })
            }, {
              resource({ }, { _, _ -> throw cancel })
            }) { _, _ -> }
          }.use { }
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
          resource {
            parZip({
              resource({ }, { _, _ -> throw cancel })
            }, {
              resource({ i }, { ii, ex -> require(released.complete(ii to ex)) })
            }) { _, _ -> }
          }.use { }
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
          resource {
            parZip({
              resource({ i }, { ii, ex -> require(released.complete(ii to ex)) })
            }, {
              resource({ }, { _, _ -> throw throwable })
            }) { _, _ -> }
          }.use { }
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
          resource {
            parZip({
              resource({ }, { _, _ -> throw throwable })
            }, {
              resource({ i }, { ii, ex -> require(released.complete(ii to ex)) })
            }) { _, _ -> }
          } use { }
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
          resource {
            parZip({
              resource({ a }) { aa, ex -> require(releasedA.complete(aa to ex)) }
            }, {
              resource({ b }) { bb, ex -> require(releasedB.complete(bb to ex)) }
            }) { _, _ -> }
          }.use { throw throwable }
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
          resource {
            parZip({
              resource({ a }) { aa, ex -> require(releasedA.complete(aa to ex)) }
            }, {
              resource({ b }) { bb, ex -> require(releasedB.complete(bb to ex)) }
            }) { _, _ -> }
          }.use { throw CancellationException("") }
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
  }
)

private suspend infix fun <T, U : T> Resource<T>.resourceShouldBe(expected: Resource<U?>): Unit =
  resource {
    bind() shouldBe expected.bind()
  }.use { }
