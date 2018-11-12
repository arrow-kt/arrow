package arrow.effects

import arrow.effects.deferredk.monad.monad
import arrow.effects.typeclasses.Fiber
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.Dispatchers.Unconfined

val UI = Unconfined

fun <A> DeferredKOf<A>.startF(ctx: CoroutineContext = Dispatchers.Default): DeferredK<Fiber<ForDeferredK, A>> = this.fix().scope.run {
    val start = asyncK(ctx = ctx, start = CoroutineStart.DEFAULT) {
      this@startF.await()
    }

    DeferredK.just(Fiber(start, asyncK { start.cancel() }))
  }

fun DeferredK.Companion.sleep(milis: Long, scope: CoroutineScope = GlobalScope): DeferredK<Unit> =
  scope.asyncK {
    delay(milis)
  }

fun <A, B, C> parMap(first: DeferredK<A>,
                     second: DeferredK<B>,
                     f: (A, B) -> C): DeferredK<C> =
  DeferredK.monad().binding {
    val fiberOne: Fiber<ForDeferredK, A> = first.startF(IO).bind()
    val fiberTwo: Fiber<ForDeferredK, B> = second.startF(IO).bind()

    val one: A = fiberOne.join.bind()
    val two: B = fiberTwo.join.bind()
    f(one, two)
  }.fix()

val first = DeferredK.sleep(5000).map {
  println("Hi I am first")
  1
}

val second = DeferredK.sleep(3000).map {
  println("Hi I am second")
  2
}
