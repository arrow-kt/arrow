package arrow.fx.coroutines

import arrow.atomic.Atomic
import arrow.atomic.update
import arrow.core.Either
import arrow.core.identity
import arrow.core.left
import io.kotest.assertions.fail
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.matchers.types.shouldBeTypeOf
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bool
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.map
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
        val r = Resource({ n }, { _, _ -> Unit })

        r.use { it + 1 } shouldBe n + 1
      }
    }

    "flatMap resource is released first" {
      checkAll(Arb.positiveInt(), Arb.negativeInt()) { a, b ->
        val l = mutableListOf<Int>()
        fun r(n: Int) = Resource({ n.also(l::add) }, { it, _ -> l.add(-it) })

        r(a).flatMap { r(it + b) }
          .use { it + 1 } shouldBe (a + b) + 1

        l.shouldContainExactly(a, a + b, -a - b, -a)
      }
    }

    "value resource is released with Complete" {
      checkAll(Arb.int()) { n ->
        val p = CompletableDeferred<ExitCase>()
        Resource({ n }, { _, ex -> require(p.complete(ex)) })
          .use { Unit }

        p.await() shouldBe ExitCase.Completed
      }
    }

    "error resource finishes with error" {
      checkAll(Arb.throwable()) { e ->
        val p = CompletableDeferred<ExitCase>()
        val r = Resource<Int>({ throw e }, { _, ex -> require(p.complete(ex)) })

        Either.catch {
          r.use { it + 1 }
        } should leftException(e)
      }
    }

    "never use can be cancelled with ExitCase.Completed" {
      checkAll(Arb.int()) { n ->
        val p = CompletableDeferred<ExitCase>()
        val start = CompletableDeferred<Unit>()
        val r = Resource({ n }, { _, ex -> require(p.complete(ex)) })

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

    "traverseResource: identity" {
      checkAll(
        Arb.list(Arb.int()),
        Arb.functionAToB<Int, String>(Arb.string())
      ) { list, f ->
        list.traverse { Resource.just(f(it)) } resourceShouldBe Resource.just(list.map(f))
      }
    }

    "traverse: map + sequence == traverse" {
      checkAll(
        Arb.list(Arb.int()),
        Arb.string().map { { _: Int -> Resource.just(it) } }
      ) { list, f ->
        list.traverse(f) resourceShouldBe list.map(f).sequence()
      }
    }
    "traverse: parallelComposition" {
      checkAll(
        Arb.list(Arb.int()),
        Arb.functionAToB<Int, String>(Arb.string()),
        Arb.functionAToB<Int, String>(Arb.string())
      ) { list, f, g ->

        val ff = list.traverse { Resource.just(f(it)) }
        val gg = list.traverse { Resource.just(g(it)) }

        val result = ff.zip(gg).map { (a, b) ->
          a.zip(b)
        }

        list.traverse { Resource.just(f(it) to g(it)) } resourceShouldBe result
      }
    }

    "traverse: leftToRight" {
      checkAll(Arb.list(Arb.int())) { list ->
        list.traverse { Resource.just(it) }
          .use(::identity) shouldBe list
      }
    }

    "Resource can close from either" {
      val exit = CompletableDeferred<ExitCase>()
      arrow.core.computations.either<String, Int> {
        arrow.fx.coroutines.continuations.resource<Int> {
          Resource({ 1 }) { _, ex -> require(exit.complete(ex)) }.bind()
          "error".left().bind()
          1
        }.use { it }
      } shouldBe "error".left()
      // Should be ExitCase.Cancelled but still Failure due to ShortCircuit
      // Effect<R, A> will fix this issue by properly shifting and cancelling
      exit.await().shouldBeTypeOf<ExitCase.Failure>()
    }

    val depth: Int = 100

    class CheckableAutoClose {
      var started = true
      fun close() {
        started = false
      }
    }

    fun closeable(): Resource<CheckableAutoClose> =
      Resource({ CheckableAutoClose() }) { a, _ -> a.close() }

    "parZip - success" {
      val all = (1..depth).traverse { closeable() }.parZip(
        (1..depth).traverse { closeable() }
      ) { a, b -> a + b }.use { all ->
        all.also { all.forEach { it.started shouldBe true } }
      }
      all.forEach { it.started shouldBe false }
    }

    fun generate(): Pair<List<CompletableDeferred<Int>>, Resource<Int>> {
      val promises = (1..depth).map { Pair(it, CompletableDeferred<Int>()) }
      val res = promises.fold(Resource({ 0 }, { _, _ -> })) { acc, (i, promise) ->
        acc.flatMap { ii: Int ->
          Resource({ ii + i }) { _, _ ->
            require(promise.complete(i))
          }
        }
      }
      return Pair(promises.map { it.second }, res)
    }

    "parZip - deep finalizers are called when final one blows" {
      io.kotest.property.checkAll(3, Arb.int(10..100)) {
        val (promises, resource) = generate()
        assertThrowable {
          resource.flatMap {
            Resource({ throw RuntimeException() }) { _, _ -> }
          }.parZip(Resource({ }) { _, _ -> }) { _, _ -> }
            .use { fail("It should never reach here") }
        }.shouldBeTypeOf<RuntimeException>()

        (1..depth).zip(promises) { i, promise ->
          promise.await() shouldBe i
        }
      }
    }

    "parZip - deep finalizers are called when final one cancels" {
      io.kotest.property.checkAll(3, Arb.int(10..100)) {
        val cancel = CancellationException(null, null)
        val (promises, resource) = generate()
        assertThrowable {
          resource.flatMap {
            Resource({ throw cancel }) { _, _ -> }
          }.parZip(Resource({ }) { _, _ -> }) { _, _ -> }
            .use { fail("It should never reach here") }
        }.shouldBeTypeOf<CancellationException>()

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
        assertThrowable {
          val res = if (isLeft) Resource({
            latch.await() shouldBe (1..depth).sum()
            throw cancel
          }) { _, _ -> }.parZip(resource.flatMap {
            Resource({ require(latch.complete(it)) }) { _, _ -> }
          }) { _, _ -> }
          else resource.flatMap {
            Resource({ require(latch.complete(it)) }) { _, _ -> }
          }.parZip(Resource({
            latch.await() shouldBe (1..depth).sum()
            throw cancel
          }) { _, _ -> }) { _, _ -> }

          res.use { fail("It should never reach here") }
        }.shouldBeTypeOf<CancellationException>()

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
        assertThrowable {
          Resource({ require(started.complete(Unit)); i }, { ii, ex ->
            require(released.complete(ii to ex))
          }).parZip(Resource({ started.await(); throw cancel }) { _, _ -> }) { _, _ -> }
            .use { fail("It should never reach here") }
        }.shouldBeTypeOf<CancellationException>()

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

        assertThrowable {
          Resource({
            started.await()
            throw cancel
          }) { _, _ -> }
            .parZip(Resource({ require(started.complete(Unit)); i }, { ii, ex ->
              require(released.complete(ii to ex))
            })) { _, _ -> }
            .use { fail("It should never reach here") }
        }.shouldBeTypeOf<CancellationException>()

        val (ii, ex) = released.await()
        ii shouldBe i
        ex.shouldBeTypeOf<ExitCase.Cancelled>()
      }
    }

    "parZip - Right error on acquire" {
      checkAll(Arb.int(), Arb.throwable()) { i, throwable ->
        val released = CompletableDeferred<Pair<Int, ExitCase>>()
        val started = CompletableDeferred<Unit>()
        assertThrowable {
          Resource(
            { require(started.complete(Unit)); i },
            { ii, ex -> require(released.complete(ii to ex)) }
          ).parZip(
            Resource({ started.await(); throw throwable }) { _, _ -> }
          ) { _, _ -> }
            .use { fail("It should never reach here") }
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
        assertThrowable {
          Resource({
            started.await()
            throw throwable
          }) { _, _ -> }
            .parZip(
              Resource(
                { require(started.complete(Unit)); i },
                { ii, ex -> require(released.complete(ii to ex)) }
              )) { _, _ -> }
            .use { fail("It should never reach here") }
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

        assertThrowable {
          Resource({ i }, { ii, ex -> require(released.complete(ii to ex)) })
            .parZip(
              Resource({ }) { _, _ -> throw cancel }
            ) { _, _ -> }
            .use { }
        }.shouldBeTypeOf<CancellationException>()

        val (ii, ex) = released.await()
        ii shouldBe i
        ex.shouldBeTypeOf<ExitCase.Completed>()
      }
    }

    "parZip - Left CancellationException on release" {
      checkAll(Arb.int()) { i ->
        val cancel = CancellationException(null, null)
        val released = CompletableDeferred<Pair<Int, ExitCase>>()

        assertThrowable {
          Resource({ }) { _, _ -> throw cancel }
            .parZip(
              Resource({ i }, { ii, ex -> require(released.complete(ii to ex)) })
            ) { _, _ -> }
            .use { /*fail("It should never reach here")*/ }
        }.shouldBeTypeOf<CancellationException>()

        val (ii, ex) = released.await()
        ii shouldBe i
        ex.shouldBeTypeOf<ExitCase.Completed>()
      }
    }

    "parZip - Right error on release" {
      checkAll(Arb.int(), Arb.throwable()) { i, throwable ->
        val released = CompletableDeferred<Pair<Int, ExitCase>>()

        assertThrowable {
          Resource({ i }, { ii, ex -> require(released.complete(ii to ex)) })
            .parZip(
              Resource({ }) { _, _ -> throw throwable }
            ) { _, _ -> }
            .use { }
        } shouldBe throwable

        val (ii, ex) = released.await()
        ii shouldBe i
        ex.shouldBeTypeOf<ExitCase.Completed>()
      }
    }

    "parZip - Left error on release" {
      checkAll(Arb.int(), Arb.throwable()) { i, throwable ->
        val released = CompletableDeferred<Pair<Int, ExitCase>>()

        assertThrowable {
          Resource({ }) { _, _ -> throw throwable }
            .parZip(
              Resource({ i }, { ii, ex -> require(released.complete(ii to ex)) })
            ) { _, _ -> }
            .use { }
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

        assertThrowable {
          Resource({ a }) { aa, ex -> require(releasedA.complete(aa to ex)) }
            .parZip(
              Resource({ b }) { bb, ex -> require(releasedB.complete(bb to ex)) }
            ) { _, _ -> }
            .use { throw throwable }
        } shouldBe throwable

        val (aa, exA) = releasedA.await()
        aa shouldBe a
        exA.shouldBeTypeOf<ExitCase.Failure>()

        val (bb, exB) = releasedB.await()
        bb shouldBe b
        exB.shouldBeTypeOf<ExitCase.Failure>()
      }
    }

    "parZip - runs in parallel" {
      checkAll(Arb.int(), Arb.int()) { a, b ->
        val r = Atomic("")
        val modifyGate = CompletableDeferred<Int>()

        Resource({
          modifyGate.await()
          r.update { i -> "$i$a" }
        }) { _, _ -> }
          .parZip(Resource({
            r.value = "$b"
            require(modifyGate.complete(0))
          }) { _, _ -> }) { _a, _b -> _a to _b }
          .use {
            r.value shouldBe "$b$a"
          }
      }
    }

    "resource.asFlow()" {
      checkAll(Arb.int()) { n ->
        val r = Resource({ n }, { _, _ -> Unit })

        r.asFlow().map { it + 1 }.toList() shouldBe listOf(n + 1)
      }
    }
  }
)

@Suppress("UNCHECKED_CAST")
private suspend infix fun <T, U : T> Resource<T>.resourceShouldBe(expected: Resource<U?>): Unit =
  zip(expected).use { (a, b) -> a shouldBe b }
