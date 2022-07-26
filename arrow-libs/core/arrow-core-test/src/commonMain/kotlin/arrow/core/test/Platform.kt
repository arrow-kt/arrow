package arrow.core.test

import arrow.core.test.concurrency.deprecateArrowTestModules

@Deprecated(deprecateArrowTestModules)
public expect fun isJvm(): Boolean
@Deprecated(deprecateArrowTestModules)
public expect fun isJs(): Boolean
@Deprecated(deprecateArrowTestModules)
public expect fun isNative(): Boolean

@Deprecated(deprecateArrowTestModules)
public fun stackSafeIteration(): Int =
  if (isJvm()) 500_000 else 1000
