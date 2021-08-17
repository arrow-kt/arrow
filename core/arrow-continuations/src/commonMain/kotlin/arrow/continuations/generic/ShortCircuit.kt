package arrow.continuations.generic

public class ShortCircuit internal constructor(internal val token: Token, public val raiseValue: Any?) : ControlThrowable()
