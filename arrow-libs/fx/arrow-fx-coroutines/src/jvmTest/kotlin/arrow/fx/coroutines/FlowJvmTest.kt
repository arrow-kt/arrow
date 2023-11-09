package arrow.fx.coroutines

import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import kotlin.test.Test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.toSet
import kotlinx.coroutines.flow.flowOn

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class FlowJvmTest {
  @Test fun parMapSingleThreadIdentity() = runTestUsingDefaultDispatcher {
    resourceScope {
      val ctx = singleThreadContext("single")
      checkAll(Arb.flow(Arb.int())) { flow ->
        flow.parMap { it }.flowOn(ctx)
          .toList() shouldBe flow.toList()
      }
    }
  }

  @Test fun parMapflowOn() = runTestUsingDefaultDispatcher {
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
  
  @Test fun parMapUnorderedSingleThreadIdentity() = runTestUsingDefaultDispatcher {
    resourceScope {
      val ctx = singleThreadContext("single")
      checkAll(Arb.flow(Arb.int())) { flow ->
        flow.parMapUnordered { it }.flowOn(ctx)
          .toSet() shouldBe flow.toSet()
      }
    }
  }
  
  @Test fun parMapUnorderedFlowOn() = runTestUsingDefaultDispatcher {
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
}
