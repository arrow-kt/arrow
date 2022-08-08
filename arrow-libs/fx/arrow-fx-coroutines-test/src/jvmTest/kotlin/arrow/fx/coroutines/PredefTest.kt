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

    "shift" {
      checkAll(Arb.string(), Arb.string()) { a, b ->
        val t0 = threadName.invoke()

        resource {
          Pair(singleThreadContext(a), singleThreadContext(b))
        }.use { (ui, io) ->
            t0 shouldBe threadName.invoke()

            ui.shift()
            threadName.invoke() shouldBe a

            io.shift()
            threadName.invoke() shouldBe b
          }
      }
    }
  }
)
