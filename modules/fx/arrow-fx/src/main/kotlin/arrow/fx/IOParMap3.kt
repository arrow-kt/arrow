package arrow.fx

import arrow.core.Either
import arrow.core.Left
import arrow.core.Tuple3
import arrow.core.nonFatalOrThrow
import arrow.fx.internal.IOForkedStart
import arrow.fx.internal.Platform
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference
import kotlin.coroutines.CoroutineContext

/** Mix-in to enable `parMapN` 2-arity on IO's companion directly. */
interface IOParMap3 {

  fun <A, B, C, D> parMapN(
    ctx: CoroutineContext,
    fa: IOOf<Throwable, A>,
    fb: IOOf<Throwable, B>,
    fc: IOOf<Throwable, C>,
    f: (A, B, C) -> D
  ): IO<Throwable, D> = IO.Async { conn, cb ->

    val state: AtomicReference<Tuple3<A?, B?, C?>?> = AtomicReference(null)
    val active = AtomicBoolean(true)

    val connA = IOConnection<Throwable>()
    val connB = IOConnection<Throwable>()
    val connC = IOConnection<Throwable>()

    // Composite cancelable that cancels both.
    // NOTE: conn.pop() happens when cb gets called!
    conn.push(connA.cancel(), connB.cancel(), connC.cancel())

    fun complete(a: A, b: B, c: C) {
      conn.pop()
      val result: Either<Throwable, D> = try {
        Either.Right(f(a, b, c))
      } catch (e: Throwable) {
        Either.Left(e.nonFatalOrThrow())
      }
      cb(result)
    }

    fun tryComplete(tuple: Tuple3<A?, B?, C?>?) = tuple?.let { (a, b, c) ->
      if (a != null && b != null && c != null) complete(a, b, c)
      else Unit
    } ?: Unit

    fun sendError(other: IOConnection<Throwable>, other2: IOConnection<Throwable>, e: Throwable) =
      if (active.getAndSet(false)) { // We were already cancelled so don't do anything.
        other.cancel().fix().unsafeRunAsync { r1 ->
          other2.cancel().fix().unsafeRunAsync { r2 ->
            conn.pop()
            cb(Left(r1.fold({ e2 ->
              r2.fold({ e3 -> Platform.composeErrors(e, e2, e3) }, { Platform.composeErrors(e, e2) })
            }, {
              r2.fold({ e3 -> Platform.composeErrors(e, e3) }, { e })
            })))
          }
        }
      } else Unit

    IORunLoop.startCancelable(IOForkedStart(fa, ctx), connA) { resultA ->
      resultA.fold({ e ->
        sendError(connB, connC, e)
      }, { a ->
        tryComplete(state.updateAndGet { current ->
          current?.copy(a = a) ?: Tuple3(a, null, null)
        })
      })
    }

    IORunLoop.startCancelable(IOForkedStart(fb, ctx), connB) { resultB ->
      resultB.fold({ e ->
        sendError(connA, connC, e)
      }, { b ->
        tryComplete(state.updateAndGet { current ->
          current?.copy(b = b) ?: Tuple3(null, b, null)
        })
      })
    }

    IORunLoop.startCancelable(IOForkedStart(fc, ctx), connC) { resultC ->
      resultC.fold({ e ->
        sendError(connA, connB, e)
      }, { c ->
        tryComplete(state.updateAndGet { current ->
          current?.copy(c = c) ?: Tuple3(null, null, c)
        })
      })
    }
  }
}
