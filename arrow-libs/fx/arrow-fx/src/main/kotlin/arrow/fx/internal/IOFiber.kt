package arrow.fx.internal

import arrow.fx.IO
import arrow.fx.IOConnection
import arrow.fx.IOPartialOf
import arrow.fx.typeclasses.Fiber

internal fun <E, A> IOFiber(promise: UnsafePromise<E, A>, conn: IOConnection): Fiber<IOPartialOf<E>, A> {
  val join: IO<E, A> = IO.cancellable { cb ->
    promise.get(cb)

    IO { promise.remove(cb) }
  }

  return Fiber(join, conn.cancel())
}
