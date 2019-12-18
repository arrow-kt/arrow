package arrow.fx.internal

import arrow.fx.BIO
import arrow.fx.ForIO
import arrow.fx.IO
import arrow.fx.IOConnection
import arrow.fx.typeclasses.Fiber

internal fun <A> IOFiber(promise: UnsafePromise<A>, conn: IOConnection): Fiber<ForIO, A> {
  val join: IO<A> = BIO.Async { conn2, cb ->
    conn2.push(IO { promise.remove(cb) })

    promise.get { a ->
      cb(a)
      conn2.pop()
      conn.pop()
    }
  }

  return Fiber(join, conn.cancel())
}
