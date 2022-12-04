package arrow.fx.coroutines

import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.toSet
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.flow.flowOn

@ExperimentalTime
class FlowJvmTest : ArrowFxSpec(spec = {
  "parMap - single thread - identity" {
    resourceScope {
      val ctx = singleThreadContext("single")
      checkAll(Arb.flow(Arb.int())) { flow ->
        flow.parMap { it }.flowOn(ctx)
          .toList() shouldBe flow.toList()
      }
    }
  }
  
  "parMap - flowOn" {
    resourceScope {
      val ctx = singleThreadContext("single")
      checkAll(Arb.flow(Arb.int())) { flow ->
        flow.parMap { Thread.currentThread().name }.flowOn(ctx)
          .toList().forEach {
            it shouldContain "single"
          }
      }
    }
  }
  
  "parMapUnordered - single thread - identity" {
    resourceScope {
      val ctx = singleThreadContext("single")
      checkAll(Arb.flow(Arb.int())) { flow ->
        flow.parMapUnordered { it }.flowOn(ctx)
          .toSet() shouldBe flow.toSet()
      }
    }
  }
  
  "parMapUnordered - flowOn" {
    resourceScope {
      val ctx = singleThreadContext("single")
      checkAll(Arb.flow(Arb.int())) { flow ->
        flow.parMap { Thread.currentThread().name }.flowOn(ctx)
          .toList().forEach {
            it shouldContain "single"
          }
      }
    }
  }
})
