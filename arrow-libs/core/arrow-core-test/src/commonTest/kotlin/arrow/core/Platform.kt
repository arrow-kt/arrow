package arrow.core

expect fun isJvm(): Boolean
expect fun isJs(): Boolean

fun stackSafeIteration(): Int =
  if (isJvm()) 500_000 else 1000
