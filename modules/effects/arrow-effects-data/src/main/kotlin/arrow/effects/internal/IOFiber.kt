package arrow.effects.internal

import arrow.core.Either
import arrow.effects.ForIO
import arrow.effects.IO
import arrow.effects.IOConnection
import arrow.effects.typeclasses.Fiber

@Suppress("FunctionName")
internal fun <A> IOFiber(promise: UnsafePromise<A>, conn: IOConnection): Fiber<ForIO, A> {
  val join: IO<A> = IO.async { conn2, cb ->
    val cb2: (Either<Throwable, A>) -> Unit = {
      cb(it)
      conn2.pop()
      conn.pop()
    }

    conn2.push(IO.Lazy { promise.remove(cb2) })
    conn.push(conn2.cancel())
    promise.get(cb2)
  }
  return Fiber(join, conn.cancel())
}
