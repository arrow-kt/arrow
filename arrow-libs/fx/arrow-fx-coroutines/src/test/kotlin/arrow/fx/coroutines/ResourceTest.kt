package arrow.fx.coroutines

import arrow.core.Either
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll

class ResourceTest : ArrowFxSpec(spec = {

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
      } shouldBe Either.Left(e)
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
      p.get() shouldBe ExitCase.Cancelled
    }
  }
})
