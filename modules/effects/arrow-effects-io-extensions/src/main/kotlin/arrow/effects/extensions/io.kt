package arrow.effects.extensions

import arrow.effects.*
import arrow.effects.extensions.io.dispatchers.dispatchers
import arrow.effects.typeclasses.Concurrent
import arrow.effects.typeclasses.ConcurrentEffect
import arrow.effects.typeclasses.Dispatchers
import arrow.effects.typeclasses.Environment
import arrow.extension
import kotlin.coroutines.CoroutineContext
import arrow.effects.IODispatchers as IOD
import arrow.effects.ap as ioAp
import arrow.effects.startF as ioStart

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

@extension
interface IODefaultConcurrentEffect : ConcurrentEffect<ForIO>, IOConcurrentEffect, IODefaultConcurrent
