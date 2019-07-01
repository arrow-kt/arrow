package arrow.fx

import arrow.core.Either
import arrow.core.Left
import arrow.core.Tuple3
import arrow.core.nonFatalOrThrow
import arrow.fx.internal.IOForkedStart
import arrow.fx.internal.Platform
import java.util.concurrent.atomic.AtomicReference
import kotlin.coroutines.CoroutineContext

/** Mix-in to enable `parMapN` 2-arity on IO's companion directly. */
interface IOParMap3 {

  fun <A, B, C, D> parMapN(
    ctx: CoroutineContext,
    fa: IOOf<A>,
    fb: IOOf<B>,
    fc: IOOf<C>,
    f: (A, B, C) -> D
  ): IO<D> = IO.Async { conn, cb ->

    val state: AtomicReference<Tuple3<A?, B?, C?>?> = AtomicReference(null)

    val connA = IOConnection()
    val connB = IOConnection()
    val connC = IOConnection()

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

    /** Called when an error is generated. */
    fun sendError(other: IOConnection, other2: IOConnection, e: Throwable) {
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
    }

    IORunLoop.startCancelable(IOForkedStart(fa, ctx), connA) {
      it.fold({ e ->
        sendError(connB, connC, e)
      }, { a ->
        val newState: Tuple3<A?, B?, C?>? = state.updateAndGet { intermediate ->
          intermediate?.copy(a = a) ?: Tuple3(a, null, null)
        }

        val resA = newState?.a
        val resB = newState?.b
        val resC = newState?.c

        if (resA != null && resB != null && resC != null) complete(resA, resB, resC)
        else Unit
      })
    }

    IORunLoop.startCancelable(IOForkedStart(fb, ctx), connB) {
      it.fold({ e ->
        sendError(connA, connC, e)
      }, { b ->
        val newState: Tuple3<A?, B?, C?>? = state.updateAndGet { intermediate ->
          intermediate?.copy(b = b) ?: Tuple3(null, b, null)
        }

        val resA = newState?.a
        val resB = newState?.b
        val resC = newState?.c

        if (resA != null && resB != null && resC != null) complete(resA, resB, resC)
        else Unit
      })
    }

    IORunLoop.startCancelable(IOForkedStart(fc, ctx), connC) {
      it.fold({ e ->
        sendError(connA, connB, e)
      }, { c ->
        val newState: Tuple3<A?, B?, C?>? = state.updateAndGet { intermediate ->
          intermediate?.copy(c = c) ?: Tuple3(null, null, c)
        }

        val resA = newState?.a
        val resB = newState?.b
        val resC = newState?.c

        if (resA != null && resB != null && resC != null) complete(resA, resB, resC)
        else Unit
      })
    }
  }
}
