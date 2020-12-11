package arrow.continuations.generic

class ShortCircuit internal constructor(internal val token: Token, val raiseValue: Any?) : ControlThrowable()
