package arrow.fx.coroutines

import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.string
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.completeWith
import kotlin.coroutines.Continuation
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.intrinsics.startCoroutineUninterceptedOrReturn

class PredefTest : ArrowFxSpec(
  spec = {
    
    "suspended always suspends" {
      checkAll(Arb.int()) { i ->
        val promise = CompletableDeferred<Int>()
        
        val x = i.suspended()
          .startCoroutineUninterceptedOrReturn(
            Continuation(EmptyCoroutineContext) {
              promise.completeWith(it)
            }
          )
        
        x shouldBe COROUTINE_SUSPENDED
        promise.await() shouldBe i
      }
    }
    
    "raise" {
      checkAll(Arb.string(), Arb.string()) { a, b ->
        val t0 = Thread.currentThread().name
        
        resourceScope {
          val ui = singleThreadContext(a)
          val io = singleThreadContext(b)
          t0 shouldBe Thread.currentThread().name
          
          ui.shift()
          Thread.currentThread().name shouldBe a
          
          io.shift()
          Thread.currentThread().name shouldBe b
        }
      }
    }
  }
)
