package arrow.resilience.common

public enum class Platform {
  JVM, JS, Native
}

public expect val platform: Platform
