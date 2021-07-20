package arrow.core.test

public expect fun isJvm(): Boolean
public expect fun isJs(): Boolean

public fun stackSafeIteration(): Int =
  if (isJvm()) 500_000 else 1000
