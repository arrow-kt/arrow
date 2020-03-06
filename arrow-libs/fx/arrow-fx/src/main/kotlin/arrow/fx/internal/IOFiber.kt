package arrow.fx.internal

import arrow.fx.ForIO
import arrow.fx.IO
import arrow.fx.IOConnection
import arrow.fx.typeclasses.Fiber

internal fun <A> IOFiber(promise: UnsafePromise<A>, conn: IOConnection): Fiber<ForIO, A> {
  val join: IO<A> = IO.cancellable { cb ->
    promise.get(cb)

    IO { promise.remove(cb) }
  }

  return Fiber(join, conn.cancel())
}
