package arrow.effects.data.internal

import java.util.concurrent.CancellationException as JavaCancellationException

data class BindingCancellationException(override val message: String? = null) : JavaCancellationException()