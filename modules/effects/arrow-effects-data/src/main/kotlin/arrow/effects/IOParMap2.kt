package arrow.effects

import arrow.core.Either
import arrow.core.Left
import arrow.core.nonFatalOrThrow
import arrow.effects.internal.IOForkedStart
import arrow.effects.internal.Platform
import java.util.concurrent.atomic.AtomicReference
import kotlin.coroutines.CoroutineContext

fun <A, B, C> IO.Companion.parMapN(ctx: CoroutineContext, fa: IOOf<A>, fb: IOOf<B>, f: (A, B) -> C): IO<C> = IO.Async { conn, cb ->

  val state = AtomicReference<Either<A, B>?>(null)

  val connA = IOConnection()
  val connB = IOConnection()

  // Composite cancelable that cancels both.
  // NOTE: conn.pop() happens when cb gets called!
  conn.pushPair(connA, connB)

  fun complete(a: A, b: B) {
    conn.pop()
    val result = try {
      Either.Right(f(a, b))
    } catch (e: Throwable) {
      Either.Left(e.nonFatalOrThrow())
    }
    cb(result)
  }

  /** Called when an error is generated. */
  fun sendError(other: IOConnection, e: Throwable) {
    other.cancel().fix().unsafeRunAsync { r ->
      conn.pop()
      cb(Left(r.fold({ e2 -> Platform.composeErrors(e, e2) }, { e })))
    }
  }

  IORunLoop.startCancelable(IOForkedStart(fa, ctx), connA) {
    it.fold({ e ->
      sendError(connB, e)
    }, { a ->
      when (val original = state.getAndSet(Either.Left(a))) {
        null -> Unit // wait for B to finish
        is Either.Right -> complete(a, original.b)
      }
    })
  }

  IORunLoop.startCancelable(IOForkedStart(fb, ctx), connB) {
    it.fold({ e ->
      sendError(connA, e)
    }, { b ->
      when (val original = state.getAndSet(Either.Right(b))) {
        null -> Unit // wait for B to finish
        is Either.Left -> complete(original.a, b)
      }
    })
  }
}
