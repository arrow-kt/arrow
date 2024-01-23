package arrow.fx.coroutines

import arrow.core.Either
import arrow.core.continuations.AtomicRef
import arrow.core.continuations.update
import arrow.core.identity
import arrow.core.left
import io.kotest.assertions.fail
import io.kotest.core.spec.style.StringSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.collections.shouldContainExactly
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
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlin.random.Random

@OptIn(ExperimentalCoroutinesApi::class)
class ResourceTest : StringSpec({

    "Can consume resource" {
      checkAll(Arb.int()) { n ->
        val r = resource({ n }, { _, _ -> })
        r.use { it + 1 } shouldBe n + 1
      }
    }

    "flatMap resource is released first" {
      checkAll(Arb.positiveInt(), Arb.negativeInt()) { a, b ->
        val l = AtomicRef<List<Int>>(mutableListOf())
        fun r(n: Int) = resource(
          {
            l.update { it + n }
            n
          },
          { x, _ -> l.update { it + (-x) } }
        )

        r(a).flatMap { r(it + b) }
          .use { it + 1 } shouldBe (a + b) + 1

        l.get().shouldContainExactly(a, a + b, -a - b, -a)
      }
    }

    "value resource is released with Complete" {
      checkAll(Arb.int()) { n ->
        val p = CompletableDeferred<ExitCase>()
        resource({ n }, { _, ex -> require(p.complete(ex)) })
          .use { Unit }

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
            awaitCancellation()
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
      arrow.core.computations.either {
        arrow.fx.coroutines.continuations.resource {
          resource({ 1 }) { _, ex -> require(exit.complete(ex)) }.bind()
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
      val started: AtomicRef<Boolean> = AtomicRef(true)
      fun close() {
        started.update { _ -> false }
      }
    }

    fun closeable(): Resource<CheckableAutoClose> =
      resource({ CheckableAutoClose() }) { a, _ -> a.close() }

    "parZip - success" {
      val all = (1..depth).traverse { closeable() }.parZip(
        (1..depth).traverse { closeable() }
      ) { a, b -> a + b }.use { all ->
        all.also { all.forEach { it.started.get() shouldBe true } }
      }
      all.forEach { it.started.get() shouldBe false }
    }

    fun generate(): Pair<List<CompletableDeferred<Int>>, Resource<Int>> {
      val promises = (1..depth).map { Pair(it, CompletableDeferred<Int>()) }
      val res = promises.fold(resource({ 0 }) { _, _ -> }) { acc, (i, p) ->
        resource {
          acc.bind() + install({ i }) { ii, _ -> p.complete(ii) }
        }
      }
      return Pair(promises.map { it.second }, res)
    }

    "parZip - deep finalizers are called when final one blows" {
      checkAll(3, Arb.int(10..100)) {
        val (promises, resource) = generate()
        assertThrowable {
          resource.flatMap {
            resource({ throw RuntimeException() }) { _, _ -> }
          }.parZip(resource({ }) { _, _ -> }) { _, _ -> }
            .use { fail("It should never reach here") }
        }.shouldBeTypeOf<RuntimeException>()

        (1..depth).zip(promises) { i, promise ->
          promise.await() shouldBe i
        }
      }
    }

    "parZip - deep finalizers are called when final one cancels" {
      checkAll(3, Arb.int(10..100)) {
        val cancel = CancellationException(null, null)
        val (promises, resource) = generate()
        assertThrowable {
          resource.flatMap {
            resource({ throw cancel }) { _, _ -> }
          }.parZip(resource({ }) { _, _ -> }) { _, _ -> }
            .use { fail("It should never reach here") }
        }.shouldBeTypeOf<CancellationException>()

        (1..depth).zip(promises) { i, promise ->
          promise.await() shouldBe i
        }
      }
    }

    // Test multiple release triggers on acquire fail.
    "parZip - Deep finalizers get called on left or right cancellation" {
      checkAll(Arb.boolean()) { isLeft ->
        val cancel = CancellationException(null, null)
        val (promises, resource) = generate()
        val latch = CompletableDeferred<Int>()
        assertThrowable {
          val res = if (isLeft) resource({
            latch.await() shouldBe (1..depth).sum()
            throw cancel
          }) { _, _ -> }.parZip(
            resource.flatMap {
              resource({ require(latch.complete(it)) }) { _, _ -> }
            }
          ) { _, _ -> }
          else resource.flatMap {
            resource({ require(latch.complete(it)) }) { _, _ -> }
          }.parZip(
            resource({
              latch.await() shouldBe (1..depth).sum()
              throw cancel
            }) { _, _ -> }
          ) { _, _ -> }

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
          resource({ require(started.complete(Unit)); i }, { ii, ex ->
            require(released.complete(ii to ex))
          }).parZip(resource({ started.await(); throw cancel }) { _, _ -> }) { _, _ -> }
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
          resource({
            started.await()
            throw cancel
          }) { _, _ -> }
            .parZip(
              resource({ require(started.complete(Unit)); i }, { ii, ex ->
                require(released.complete(ii to ex))
              })
            ) { _, _ -> }
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
          resource(
            { require(started.complete(Unit)); i },
            { ii, ex -> require(released.complete(ii to ex)) }
          ).parZip(
            resource({ started.await(); throw throwable }) { _, _ -> }
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
          resource({
            started.await()
            throw throwable
          }) { _, _ -> }
            .parZip(
              resource(
                { require(started.complete(Unit)); i },
                { ii, ex -> require(released.complete(ii to ex)) }
              )
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
        val cancel = CancellationException(null, null)
        val released = CompletableDeferred<Pair<Int, ExitCase>>()

        assertThrowable {
          resource({ i }, { ii, ex -> require(released.complete(ii to ex)) })
            .parZip(
              resource({ }) { _, _ -> throw cancel }
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
          resource({ }) { _, _ -> throw cancel }
            .parZip(
              resource({ i }, { ii, ex -> require(released.complete(ii to ex)) })
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
          resource({ i }, { ii, ex -> require(released.complete(ii to ex)) })
            .parZip(
              resource({ }) { _, _ -> throw throwable }
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
          resource({ }) { _, _ -> throw throwable }
            .parZip(
              resource({ i }, { ii, ex -> require(released.complete(ii to ex)) })
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
          resource({ a }) { aa, ex -> require(releasedA.complete(aa to ex)) }
            .parZip(
              resource({ b }) { bb, ex -> require(releasedB.complete(bb to ex)) }
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

        resource({
          modifyGate.await()
          r.update { i -> "$i$a" }
        }) { _, _ -> }
          .parZip(
            resource({
              r.set("$b")
              require(modifyGate.complete(0))
            }) { _, _ -> }
          ) { _a, _b -> _a to _b }
          .use {
            r.get() shouldBe "$b$a"
          }
      }
    }

    "resource.asFlow()" {
      checkAll(Arb.int()) { n ->
        val r = resource({ n }, { _, _ -> Unit })

        r.asFlow().map { it + 1 }.toList() shouldBe listOf(n + 1)
      }
    }

    suspend fun checkAllocated(mkResource: (() -> Int, (Int, ExitCase) -> Unit) -> Resource<Int>) {
      listOf(
        ExitCase.Completed,
        ExitCase.Failure(Exception()),
        ExitCase.Cancelled(CancellationException(null, null))
      ).forAll { exit ->
        val released = CompletableDeferred<Int>()
        val seed = Random.nextInt()

        val (allocate, release) = mkResource({ seed }) { i, _ -> released.complete(i) }.allocated()

        release(allocate(), exit)

        released.getCompleted() shouldBe seed
      }
    }

    "allocated - Allocate" {
      checkAllocated { allocate, release ->
        resource(allocate, release)
      }
    }

    "allocated - Defer" {
      checkAllocated { allocate, release ->
        Resource.defer { resource(allocate, release) }
      }
    }

    "allocated - Bind" {
      checkAllocated { allocate, release ->
        Resource.Bind(resource(allocate, release)) { Resource.just(it) }
      }
    }

    "allocated - Dsl" {
      checkAllocated { allocate, close ->
        arrow.fx.coroutines.continuations.resource { allocate() } releaseCase (close)
      }
    }
  }
)

private suspend infix fun <T, U : T> Resource<T>.resourceShouldBe(expected: Resource<U?>): Unit =
  zip(expected).use { (a, b) -> a shouldBe b }
