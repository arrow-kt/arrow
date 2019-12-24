package arrow.fx

import arrow.core.Option
import arrow.core.Tuple3
import arrow.core.internal.AtomicBooleanW
import arrow.core.internal.AtomicRefW
import arrow.core.extensions.option.applicative.applicative
import arrow.core.extensions.option.applicativeError.handleError
import arrow.core.nonFatalOrThrow
import arrow.core.none
import arrow.core.some
import arrow.fx.internal.IOForkedStart
import arrow.fx.internal.Platform
import kotlin.coroutines.CoroutineContext

/** Mix-in to enable `parMapN` 3-arity on IO's companion directly. */
interface IOParMap3 {

  fun <E, A, B, C, D> parMapN(
    ctx: CoroutineContext,
    fa: IOOf<E, A>,
    fb: IOOf<E, B>,
    fc: IOOf<E, C>,
    f: (A, B, C) -> D
  ): IO<E, D> = IO.Async { conn, cb ->

    val state: AtomicRefW<Option<Tuple3<Option<A>, Option<B>, Option<C>>>> = AtomicRefW(none())
    val active = AtomicBooleanW(true)

    val connA = IOConnection()
    val connB = IOConnection()
    val connC = IOConnection()

    // Composite cancelable that cancels all ops.
    // NOTE: conn.pop() called when cb gets called below in complete.
    conn.push(connA.cancel(), connB.cancel(), connC.cancel())

    fun complete(a: A, b: B, c: C) {
      conn.pop()
      val result: IOResult<E, D> = try {
        IOResult.Success(f(a, b, c))
      } catch (e: Throwable) {
        IOResult.Exception(e.nonFatalOrThrow())
      }
      cb(result)
    }

    fun tryComplete(result: Option<Tuple3<Option<A>, Option<B>, Option<C>>>): Unit =
      result.fold({ Unit }, { (a, b, c) -> Option.applicative().map(a, b, c) { (a, b, c) -> complete(a, b, c) } })

    fun sendException(other: IOConnection, other2: IOConnection, e: Throwable) =
      if (active.getAndSet(false)) { // We were already cancelled so don't do anything.
        other.cancel().unsafeRunAsync { r1 ->
          other2.cancel().unsafeRunAsync { r2 ->
            conn.pop()
            cb(IOResult.Exception(r1.fold({ e2 ->
              r2.fold({ e3 -> Platform.composeErrors(e, e2, e3) }, { Platform.composeErrors(e, e2) })
            }, {
              r2.fold({ e3 -> Platform.composeErrors(e, e3) }, { e })
            })))
          }
        }
      } else Unit

    fun sendError(other: IOConnection, other2: IOConnection, e: E) =
      if (active.getAndSet(false)) { // We were already cancelled so don't do anything.
        other.cancel().fix().unsafeRunAsync { r1 ->
          other2.cancel().fix().unsafeRunAsync { r2 ->
            conn.pop()
            // Send r1 & r2 to asyncErrorHandler if cancelation failed
            cb(IOResult.Error(e))
          }
        }
      } else Unit

    IORunLoop.startCancelable(IOForkedStart(fa, ctx), connA) { resultA ->
      resultA.fold({ e ->
        sendException(connB, connC, e)
      }, { e ->
        sendError(connB, connC, e)
      }, { a ->
        tryComplete(state.updateAndGet { current ->
          current
            .map { it.copy(a = a.some()) }
            .handleError { Tuple3(a.some(), none(), none()) }
        })
      })
    }

    IORunLoop.startCancelable(IOForkedStart(fb, ctx), connB) { resultB ->
      resultB.fold({ e ->
        sendException(connA, connC, e)
      }, { e ->
        sendError(connB, connC, e)
      }, { b ->
        tryComplete(state.updateAndGet { current ->
          current
            .map { it.copy(b = b.some()) }
            .handleError { Tuple3(none(), b.some(), none()) }
        })
      })
    }

    IORunLoop.startCancelable(IOForkedStart(fc, ctx), connC) { resultC ->
      resultC.fold({ e ->
        sendException(connA, connB, e)
      }, { e ->
        sendError(connB, connC, e)
      }, { c ->
        tryComplete(state.updateAndGet { current ->
          current
            .map { it.copy(c = c.some()) }
            .handleError { Tuple3(none(), none(), c.some()) }
        })
      })
    }
  }
}
