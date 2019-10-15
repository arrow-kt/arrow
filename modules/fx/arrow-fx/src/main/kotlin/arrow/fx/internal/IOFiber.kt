package arrow.fx.internal

import arrow.fx.ForIO
import arrow.fx.IO
import arrow.fx.IOConnection
import arrow.fx.IOPartialOf
import arrow.fx.typeclasses.Fiber
import arrow.fx.typeclasses.Fiber2
import arrow.fx.typeclasses.throwPolicy

internal fun <E, A> IOFiber(promise: UnsafePromise<E, A>, conn: IOConnection): Fiber2<ForIO, E, A> {
  val join: IO<E, A> = IO.Async(false) { conn2, cb ->
    conn2.push(IO(throwPolicy) { promise.remove(cb) })

    promise.get { a ->
      cb(a)
      conn2.pop()
      conn.pop()
    }
  }

  return Fiber2<ForIO, E, A>(join, conn.cancel())
}
