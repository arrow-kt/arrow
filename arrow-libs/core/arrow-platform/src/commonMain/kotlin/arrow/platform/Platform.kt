package arrow.platform

public enum class Platform {
  JVM, JS, Native, WebAssembly, Android
}

public expect val platform: Platform

/**
 * Heuristic about the maximum amount of stack space
 * one can reasonably consume in the executing platform.
 */
public fun stackSafeIteration(): Int = when (platform) {
  Platform.JVM -> 200_000
  else -> 1000
}
