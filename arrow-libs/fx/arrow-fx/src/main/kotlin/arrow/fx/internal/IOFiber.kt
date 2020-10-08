package arrow.fx.internal

import arrow.fx.ForIO
import arrow.fx.IO
import arrow.fx.coroutines.SuspendConnection
import arrow.fx.typeclasses.Fiber

internal fun <A> IOFiber(promise: UnsafePromise<A>, conn: SuspendConnection): Fiber<ForIO, A> {
  val join: IO<A> = IO.cancellable { cb ->
    promise.get(cb)

    IO { promise.remove(cb) }
  }

  return Fiber(join, IO.effect { conn.cancel() })
}
