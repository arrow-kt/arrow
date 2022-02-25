package arrow.continuations.generic

@Deprecated(deprecateArrowContinuation)
public class ShortCircuit internal constructor(internal val token: Token, public val raiseValue: Any?) : ControlThrowable()
