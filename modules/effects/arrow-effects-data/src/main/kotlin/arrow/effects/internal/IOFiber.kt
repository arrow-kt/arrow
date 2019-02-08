package arrow.effects.internal

import arrow.effects.ForIO
import arrow.effects.IO
import arrow.effects.IOConnection
import arrow.effects.typeclasses.Fiber

internal fun <A> IOFiber(promise: UnsafePromise<A>, conn: IOConnection): Fiber<ForIO, A> {
  val join: IO<A> = IO.async { conn2, cb ->
    conn2.push(IO { promise.remove(cb) })
    conn.push(conn2.cancel())

    promise.get { a ->
      cb(a)
      conn2.pop()
      conn.pop()
    }
  }

  return Fiber(join, conn.cancel())
}