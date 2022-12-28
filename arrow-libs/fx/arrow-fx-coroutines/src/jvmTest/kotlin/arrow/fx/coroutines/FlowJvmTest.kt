package arrow.fx.coroutines

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.toSet
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.flow.flowOn

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@ExperimentalTime
class FlowJvmTest : StringSpec({
  "parMap - single thread - identity" {
    single.use { ctx ->
      checkAll(Arb.flow(Arb.int())) { flow ->
        flow.parMap { it }.flowOn(ctx)
          .toList() shouldBe flow.toList()
      }
    }
  }
  
  "parMap - flowOn" {
    single.use { ctx ->
      checkAll(Arb.flow(Arb.int())) { flow ->
        flow.parMap { Thread.currentThread().name }.flowOn(ctx)
          .toList().forEach {
            it shouldContain singleThreadName
          }
      }
    }
  }
  
  "parMapUnordered - single thread - identity" {
    single.use { ctx ->
      checkAll(Arb.flow(Arb.int())) { flow ->
        flow.parMapUnordered { it }.flowOn(ctx)
          .toSet() shouldBe flow.toSet()
      }
    }
  }
  
  "parMapUnordered - flowOn" {
    single.use { ctx ->
      checkAll(Arb.flow(Arb.int())) { flow ->
        flow.parMap { Thread.currentThread().name }.flowOn(ctx)
          .toList().forEach {
            it shouldContain singleThreadName
          }
      }
    }
  }
})
