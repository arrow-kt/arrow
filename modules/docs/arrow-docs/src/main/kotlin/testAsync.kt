import arrow.Kind
import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import arrow.effects.*
import arrow.effects.deferredk.async.async
import arrow.effects.deferredk.effect.effect
import arrow.effects.deferredk.monad.flatMap
import arrow.effects.deferredk.monad.map
import arrow.effects.instances.io.async.async
import arrow.effects.instances.io.effect.effect
import arrow.effects.instances.io.monad.flatMap
import arrow.effects.typeclasses.Effect
import arrow.test.generators.genThrowable
import io.kotlintest.matchers.shouldBe
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.lang.RuntimeException

fun main(args: Array<String>) = runBlocking<Unit> {

  IO.async<Unit> { conn, cb ->
    conn.push(IO { println("I got cancelled by IO") })
    cb(Right(Unit))
  }.flatMap {
    IO.async<Unit> { conn, cb ->
      conn.cancel().fix().unsafeRunSync()
    }
  }.runAsyncCancellable { IO { println(it) } }
    .unsafeRunSync()

//  DeferredK.async<Unit> { conn, cb ->
//    conn.push(DeferredK { println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>  I got cancelled by DeferredK") })
//    cb(Right(Unit))
//  }.flatMap {
    DeferredK.async<Unit> { conn, cb ->
      conn.push(DeferredK { println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@   But I did get called!") })
//      conn.cancel().unsafeRunSync()
//    }
  }.runAsyncCancellable { DeferredK { println(it) } }
    .unsafeRunSync()
    .invoke()

  //  IO.async<Unit> { conn, cb ->
//    conn.push(IO { println("Hello from IO cancel stack") })
//    conn.cancel().fix().unsafeRunAsync {  }
//  }.runAsync { IO {  println(it) } }

//  DeferredK.just(1)
//    .runAsync { throw RuntimeException("Nested Boom!") }
//    .unsafeRunAsync {  }


  println("runAsync")
  println("######################################################################")

  IO.effect().runAsyncShouldAlwaysExecuteWhenRun { it.fix().unsafeRunSync() }
    .also { println("IO.effect().runAsyncShouldAlwaysExecuteWhenRun") }
  IO.effect().runAsyncShouldRunWhenRaiseError2 { it.fix().unsafeRunSync() }
    .also { println("IO.effect().runAsyncShouldRunWhenRaiseError2") }

//  DeferredK.effect().runAsyncShouldAlwaysExecuteWhenRun { it.fix().unsafeRunSync() }
//    .also { println("DeferredK.effect().runAsyncShouldAlwaysExecuteWhenRun") }
//  DeferredK.effect().runAsyncShouldRunWhenRaiseError2 { it.fix().unsafeRunSync() }
//    .also { println("DeferredK.effect().runAsyncShouldRunWhenRaiseError2") }

  DeferredK.raiseError<Unit>(RuntimeException("DeferredK Boom!"))
    .runAsync { DeferredK { println("runAsync $it") } }
    .unsafeRunAsync { println("unsafeRunAsync $it") }

  DeferredK.raiseError<Unit>(RuntimeException("DeferredK Boom!"))
    .runAsync { DeferredK { it.fold({ e -> throw e }, { Unit }) } }
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

  IO.async<Unit> { conn, cb ->
    conn.push(IO { println("Hello from IO runAsyncCancellable cancel stack") })
  }.runAsyncCancellable { IO { println("IO.runAsyncCancellable $it") } }
    .unsafeRunSync()
    .invoke()

  DeferredK.async<Unit> { conn, cb ->
    conn.push(DeferredK { println("Hello from DeferredK runAsyncCancellable cancel stack") })
  }
    .runAsyncCancellable { DeferredK { println("DeferredK.runAsyncCancellable $it") } }
    .unsafeRunSync()
    .invoke()

//  Promise.uncancelable<ForIO, Unit>(IO.async()).flatMap { latch ->
//    IO.async<Unit> { conn, cb ->
//      conn.push(latch.complete(Unit))
//    }.unsafeRunAsyncCancellable { }
//      .invoke()
//
//    latch.get
//  }.unsafeRunSync()
//
//  Promise.uncancelable<ForDeferredK, Unit>(DeferredK.async()).flatMap { latch ->
//    DeferredK.async<Unit> { conn, cb ->
//      conn.push(latch.complete(Unit))
//    }.unsafeRunAsyncCancellable { }
//      .invoke()
//
//    latch.get
//  }.unsafeRunSync()

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

fun <F> Effect<F>.runAsyncShouldRunWhenRaiseError2(run: (Kind<F, Boolean>) -> Boolean): Unit {
  forAll(genThrowable()) { t ->
    run(Promise.uncancelable<F, Either<Throwable, Unit>>(this).flatMap { latch ->
      run(raiseError<Unit>(t)
        .runAsync { latch.complete(it) }.map { true })

      latch.get.map { either -> either == Left(t) }
    })
  }
}

fun <F> Effect<F>.runAsyncShouldAlwaysExecuteWhenRun(run: (Kind<F, Boolean>) -> Boolean): Unit {
  forAll(Gen.int()) { i ->
    run(Promise.uncancelable<F, Int>(this).flatMap { latch ->
      run(just(i)
        .runAsync {
          it.fold(latch::error, latch::complete)
        }.map { true })
      latch.get.map { ii -> i == ii }
    })
  }
}

//IO.async<Unit> { conn, cb ->
//  conn.push(IO { println("Hello from IO cancel stack") })
//}.unsafeRunAsyncCancellable { }
//.invoke()

fun <F> Effect<F>.asyncShouldCancelTokenOnAsync(): Unit {

}

