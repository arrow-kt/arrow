import arrow.core.Right
import arrow.effects.*
import arrow.effects.deferredk.async.async
import arrow.effects.deferredk.monad.flatMap
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.lang.RuntimeException

fun main(args: Array<String>) = runBlocking<Unit> {

  //  IO.async<Unit> { conn, cb ->
//    conn.push(IO { println("Hello from IO cancel stack") })
//    conn.cancel().fix().unsafeRunAsync {  }
//  }.runAsync { IO {  println(it) } }

//  DeferredK.just(1)
//    .runAsync { throw RuntimeException("Nested Boom!") }
//    .unsafeRunAsync {  }

  println("runAsync")
  println("######################################################################")

  IO.raiseError<Unit>(RuntimeException("IO Boom!"))
    .runAsync { IO { println("runAsync $it") } }
    .unsafeRunAsync { println("unsafeRunAsync $it") }

  DeferredK.raiseError<Unit>(RuntimeException("DeferredK Boom!"))
    .runAsync { DeferredK { println("runAsync $it") } }
    .unsafeRunAsync { println("unsafeRunAsync $it") }

  println("######################################################################")

//  Promise.uncancelable<ForDeferredK, Unit>(DeferredK.async()).flatMap { latch ->
//    DeferredK.async<Unit> { conn, _ ->
//      conn.push(latch.complete(Unit))
//    }
//      .unsafeRunAsyncCancellable {  }
//      .invoke()
//
//    latch.get
//  }.fix().unsafeRunSync()

  IO.async<Unit> { conn, cb ->
    conn.push(IO { println("Hello from IO cancel stack") })
  }.unsafeRunAsyncCancellable { }
    .invoke()

  DeferredK.async<Unit> { conn, cb ->
    conn.push(DeferredK { println("Hello from DeferredK cancel stack") })
  }.unsafeRunAsyncCancellable { }
    .invoke()

//  d.start()
//  d.cancelAndJoin()

//
//  def
////    .runAsync { DeferredK { println(it) } }
//    .unsafeRunAsyncCancellable { }
////    .invoke()
//
  delay(5000)

}