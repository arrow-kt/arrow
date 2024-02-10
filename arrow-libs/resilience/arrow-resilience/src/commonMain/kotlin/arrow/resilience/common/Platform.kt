package arrow.resilience.common

public enum class Platform {
  JVM, JS, Native, Wasm
}

public expect val platform: Platform
