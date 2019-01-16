package arrow.effects.data.internal

import arrow.effects.internal.JavaCancellationException

data class BindingCancellationException(override val message: String? = null) : JavaCancellationException()
