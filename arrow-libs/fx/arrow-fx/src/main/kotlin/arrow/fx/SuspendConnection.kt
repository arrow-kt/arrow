package arrow.fx

import arrow.fx.coroutines.SuspendConnection
import arrow.fx.internal.JavaCancellationException
import arrow.fx.typeclasses.Disposable

enum class OnCancel {
  ThrowCancellationException, Silent;

  companion object {
    val CancellationException = ConnectionCancellationException
  }
}

object ConnectionCancellationException : JavaCancellationException("User cancellation")

internal fun SuspendConnection.toDisposable(): Disposable = {
  IO.effect { cancel() }.unsafeRunSync()
}
