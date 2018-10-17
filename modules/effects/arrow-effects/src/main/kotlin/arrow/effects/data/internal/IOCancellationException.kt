package arrow.effects.data.internal

import arrow.effects.internal.JavaCancellationException

object IOCancellationException : JavaCancellationException("User cancellation")
