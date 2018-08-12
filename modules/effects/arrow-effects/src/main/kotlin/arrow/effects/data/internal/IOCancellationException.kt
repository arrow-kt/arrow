package arrow.effects.data.internal

import arrow.effects.internal.JavaCancellationException

data class IOCancellationException(override val message: String? = null) : JavaCancellationException()
