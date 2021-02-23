package arrow.fx.coroutines

import arrow.core.Either
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.string

class ResourceTest : ArrowFxSpec(
  spec = {

    "Can consume resource" {
      checkAll(Arb.int()) { n ->
        val r = Resource({ n }, { _ -> Unit })

        r.use { it + 1 } shouldBe n + 1
      }
    }

    "value resource is released with Complete" {
      checkAll(Arb.int()) { n ->
        val p = Promise<ExitCase>()
        Resource({ n }, { _, ex -> p.complete(ex) })
          .use { Unit }

        p.get() shouldBe ExitCase.Completed
      }
    }

    "error resource finishes with error" {
      checkAll(Arb.throwable()) { e ->
        val p = Promise<ExitCase>()
        val r = Resource<Int>({ throw e }, { _, ex -> p.complete(ex) })

        Either.catch {
          r.use { it + 1 }
        } should leftException(e)
      }
    }

    "never use can be cancelled with ExitCase.Completed" {
      checkAll(Arb.int()) { n ->
        val p = Promise<ExitCase>()
        val start = Promise<Unit>()
        val r = Resource({ n }, { _, ex -> p.complete(ex) })

        val f = ForkAndForget {
          r.use {
            start.complete(Unit)
            never<Int>()
          }
        }

        start.get()
        f.cancel()
        p.get().shouldBeInstanceOf<ExitCase.Cancelled>()
      }
    }

    "traverseFilterResource: identity" {
      checkAll(
        Arb.list(Arb.int()),
        Arb.functionAToB<Int, String?>(Arb.nullable(Arb.string()))
      ) { list, f ->
        list.traverseFilterResource { Resource.just(f(it)) } resourceShouldBe Resource.just(list.mapNotNull(f))
      }
    }

    "flatTraverseFilterResource: identity" {
      checkAll(
        Arb.list(Arb.int()),
        Arb.functionAToB<Int, List<String?>>(Arb.list(Arb.nullable(Arb.string())))
      ) { list, f ->
        list.flatTraverseFilterResource { Resource.just(f(it)) } resourceShouldBe Resource.just(
          list.flatMap(f).filterNotNull()
        )
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

    "flatTraverseResource: map + flatSequence == flatTraverse" {
      checkAll(
        Arb.list(Arb.int()),
        Arb.list(Arb.string()).map { { _: Int -> Resource.just(it) } }
      ) { list, f ->
        list.flatTraverseResource(f) resourceShouldBe list.map(f).flatSequence()
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

    "traverseResource: sequentialComposition" {
      checkAll(
        Arb.list(Arb.int()),
        Arb.functionAToB<Int, String>(Arb.string()),
        Arb.functionAToB<String, Long>(Arb.long())
      ) { list, f, g ->
        list.traverseResource { Resource.just(f(it)) }
          .map { it.map(g) } resourceShouldBe list.traverseFilterResource { Resource.just(g(f(it))) }
      }
    }

    "traverseResource: leftToRight" {
      checkAll(Arb.list(Arb.int())) { list ->
        val mutable = mutableListOf<Int>()
        list.traverseResource { mutable.add(it); Resource.just(Unit) }
        mutable.toList() shouldBe list
      }
    }
  }
)

@Suppress("UNCHECKED_CAST")
private suspend infix fun <T, U : T> Resource<T>.resourceShouldBe(expected: Resource<U?>): Unit =
  zip(expected).use { (a, b) -> a shouldBe b }
