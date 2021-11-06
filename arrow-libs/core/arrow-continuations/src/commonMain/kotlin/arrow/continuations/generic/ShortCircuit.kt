package arrow.continuations.generic

@Deprecated("This is deprecated in favor of using kotlin.coroutines.cancellation for short-circuiting.")
public class ShortCircuit internal constructor(internal val token: Token, public val raiseValue: Any?) : ControlThrowable()
