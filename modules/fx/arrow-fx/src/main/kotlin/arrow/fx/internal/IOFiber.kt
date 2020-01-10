package arrow.fx.internal

import arrow.fx.IO
import arrow.fx.IOConnection
import arrow.fx.IOPartialOf
import arrow.fx.typeclasses.Fiber

internal fun <E, A> IOFiber(promise: UnsafePromise<E, A>, conn: IOConnection): Fiber<IOPartialOf<E>, A> {
  val join: IO<E, A> = IO.Async { conn2, cb ->
    conn2.push(IO { promise.remove(cb) })

    promise.get { a ->
      cb(a)
      conn2.pop()
      conn.pop()
    }
  }

  return Fiber(join, conn.cancel())
}
