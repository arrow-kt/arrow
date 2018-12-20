import arrow.effects.*
import arrow.effects.deferredk.async.async
import arrow.effects.deferredk.bracket.uncancelable
import arrow.effects.deferredk.monad.flatMap
import arrow.effects.instances.io.async.async
import arrow.effects.instances.io.monad.flatMap
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.runBlocking

fun main(args: Array<String>) = runBlocking<Unit> {

  Promise.uncancelable<ForDeferredK, Unit>(DeferredK.async()).flatMap { latch ->
    DeferredK.async<Unit> { conn, _ ->
      conn.push(DeferredK { println("cancelling") })
    }.uncancelable()
      .runAsync {
        DeferredK { println("runAsync $it") }.flatMap {
          latch.complete(Unit)
        }
      }.flatMap { latch.get }
  }.fix().unsafeRunSync()

}