package arrow.fx.coroutines

import arrow.core.Either
import io.kotest.assertions.fail
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.matchers.types.shouldBeTypeOf
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.string
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async

class ResourceTest : ArrowFxSpec(
  spec = {

    "Can consume resource" {
      checkAll(Arb.int()) { n ->
        val r = Resource({ n }, { _, _ -> Unit })

        r.use { it + 1 } shouldBe n + 1
      }
    }

    "value resource is released with Complete" {
      checkAll(Arb.int()) { n ->
        val p = CompletableDeferred<ExitCase>()
        Resource({ n }, { _, ex -> p.complete(ex) })
          .use { Unit }

        p.await() shouldBe ExitCase.Completed
      }
    }

    "error resource finishes with error" {
      checkAll(Arb.throwable()) { e ->
        val p = CompletableDeferred<ExitCase>()
        val r = Resource<Int>({ throw e }, { _, ex -> p.complete(ex) })

        Either.catch {
          r.use { it + 1 }
        } should leftException(e)
      }
    }

    "never use can be cancelled with ExitCase.Completed" {
      checkAll(Arb.int()) { n ->
        val p = CompletableDeferred<ExitCase>()
        val start = CompletableDeferred<Unit>()
        val r = Resource({ n }, { _, ex -> p.complete(ex) })

        val f = async {
          r.use {
            start.complete(Unit)
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
        list.traverseResource { Resource.just(f(it)) } resourceShouldBe Resource.just(list.map(f))
      }
    }

    "traverseResource: map + sequence == traverse" {
      checkAll(
        Arb.list(Arb.int()),
        Arb.string().map { { _: Int -> Resource.just(it) } }
      ) { list, f ->
        list.traverseResource(f) resourceShouldBe list.map(f).sequence()
      }
    }
    "traverseResource: parallelComposition" {
      checkAll(
        Arb.list(Arb.int()),
        Arb.functionAToB<Int, String>(Arb.string()),
        Arb.functionAToB<Int, String>(Arb.string())
      ) { list, f, g ->

        val ff = list.traverseResource { Resource.just(f(it)) }
        val gg = list.traverseResource { Resource.just(g(it)) }

        val result = ff.zip(gg).map { (a, b) ->
          a.zip(b)
        }

        list.traverseResource { Resource.just(f(it) to g(it)) } resourceShouldBe result
      }
    }

    "traverseResource: leftToRight" {
      checkAll(Arb.list(Arb.int())) { list ->
        val mutable = mutableListOf<Int>()
        list.traverseResource { mutable.add(it); Resource.just(Unit) }
        mutable.toList() shouldBe list
      }
    }

    "parZip - Right CancellationException on acquire" {
      checkAll(Arb.int()) { i ->
        val cancel = CancellationException(null)
        val released = CompletableDeferred<Pair<Int, ExitCase>>()

        shouldThrow<CancellationException> {
          Resource({ i }, { ii, ex ->
            released.complete(ii to ex)
          }).parZip(Resource({ throw cancel }) { _, _ -> }) { _, _ -> }
            .use { fail("It should never reach here") }
        }

        val (ii, ex) = released.await()
        ii shouldBe i
        ex.shouldBeTypeOf<ExitCase.Cancelled>()
      }
    }

    "parZip - Left CancellationException on acquire" {
      checkAll(Arb.int()) { i ->
        val cancel = CancellationException(null)
        val released = CompletableDeferred<Pair<Int, ExitCase>>()

        shouldThrow<CancellationException> {
          Resource({ throw cancel }) { _, _ -> }
            .parZip(Resource({ i }, { ii, ex ->
              released.complete(ii to ex)
            })) { _, _ -> }
            .use { fail("It should never reach here") }
        }

        val (ii, ex) = released.await()
        ii shouldBe i
        ex.shouldBeTypeOf<ExitCase.Cancelled>()
      }
    }

    "parZip - Right error on acquire" {
      checkAll(Arb.int(), Arb.throwable()) { i, throwable ->
        val released = CompletableDeferred<Pair<Int, ExitCase>>()

        shouldThrow<Throwable> {
          Resource({ i }, { ii, ex -> released.complete(ii to ex) })
            .parZip(
              Resource({ throw throwable }) { _, _ -> }
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

        shouldThrow<Throwable> {
          Resource({ throw throwable }) { _, _ -> }
            .parZip(
              Resource({ i }, { ii, ex -> released.complete(ii to ex) })
            ) { _, _ -> }
            .use { fail("It should never reach here") }
        } shouldBe throwable

        val (ii, ex) = released.await()
        ii shouldBe i
        ex.shouldBeTypeOf<ExitCase.Failure>()
      }
    }

    "parZip - Right CancellationException on release" {
      checkAll(Arb.int()) { i ->
        val cancel = CancellationException(null)
        val released = CompletableDeferred<Pair<Int, ExitCase>>()

        shouldThrow<CancellationException> {
          Resource({ i }, { ii, ex -> released.complete(ii to ex) })
            .parZip(
              Resource({ }) { _, _ -> throw cancel }
            ) { _, _ -> }
            .use { }
        }

        val (ii, ex) = released.await()
        ii shouldBe i
        ex.shouldBeTypeOf<ExitCase.Completed>()
      }
    }

    "parZip - Left CancellationException on release" {
      checkAll(Arb.int()) { i ->
        val cancel = CancellationException(null)
        val released = CompletableDeferred<Pair<Int, ExitCase>>()

        shouldThrow<CancellationException> {
          Resource({ }) { _, _ -> throw cancel }
            .parZip(
              Resource({ i }, { ii, ex -> released.complete(ii to ex) })
            ) { _, _ -> }
            .use { /*fail("It should never reach here")*/ }
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
          Resource({ i }, { ii, ex -> released.complete(ii to ex) })
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

        shouldThrow<Throwable> {
          Resource({ }) { _, _ -> throw throwable }
            .parZip(
              Resource({ i }, { ii, ex -> released.complete(ii to ex) })
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

        shouldThrow<Throwable> {
          Resource({ a }) { aa, ex -> releasedA.complete(aa to ex) }
            .parZip(
              Resource({ b }) { bb, ex -> releasedB.complete(bb to ex) }
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
            r.set("$b")
            modifyGate.complete(0)
          }) { _, _ -> }) { _a, _b -> _a to _b }
          .use {
            r.get() shouldBe "$b$a"
          }
      }
    }
  }
)

@Suppress("UNCHECKED_CAST")
private suspend infix fun <T, U : T> Resource<T>.resourceShouldBe(expected: Resource<U?>): Unit =
  zip(expected).use { (a, b) -> a shouldBe b }
