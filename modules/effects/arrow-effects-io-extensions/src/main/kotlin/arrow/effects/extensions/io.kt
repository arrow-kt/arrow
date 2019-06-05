package arrow.effects.extensions

import arrow.effects.ForIO
import arrow.effects.IO
import arrow.effects.extensions.io.concurrent.concurrent
import arrow.effects.extensions.io.dispatchers.dispatchers
import arrow.effects.fix
import arrow.effects.typeclasses.Concurrent
import arrow.effects.typeclasses.ConcurrentEffect
import arrow.effects.typeclasses.Dispatchers
import arrow.effects.typeclasses.Environment
import arrow.effects.Timer
import arrow.effects.typeclasses.ConcurrentSyntax
import arrow.extension
import kotlin.coroutines.CoroutineContext
import arrow.effects.IODispatchers as IOD

@extension
interface IODispatchers : Dispatchers<ForIO> {
  override fun default(): CoroutineContext =
    IOD.CommonPool
}

@extension
interface IOEnvironment : Environment<ForIO> {
  override fun dispatchers(): Dispatchers<ForIO> =
    IO.dispatchers()

  override fun handleAsyncError(e: Throwable): IO<Unit> =
    IO { println("Found uncaught async exception!"); e.printStackTrace() }
}

@extension
interface IODefaultConcurrent : Concurrent<ForIO>, IOConcurrent {

  override fun dispatchers(): Dispatchers<ForIO> =
    IO.dispatchers()
}

fun IO.Companion.timer(): Timer<ForIO> = Timer(IO.concurrent())

@extension
interface IODefaultConcurrentEffect : ConcurrentEffect<ForIO>, IOConcurrentEffect, IODefaultConcurrent

fun <A> IO.Companion.fx(c: suspend ConcurrentSyntax<ForIO>.() -> A): IO<A> =
  IO.concurrent().fxConcurrent(c).fix()
