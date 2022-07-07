package arrow.core.test.concurrency

@Deprecated(deprecateArrowTestModules)
public data class SideEffect(var counter: Int = 0) {
  @Deprecated(deprecateArrowTestModules)
  public fun increment() {
    counter++
  }
}

public const val deprecateArrowTestModules: String =
  "arrow test modules are being deprecated in favour of kotest-arrow-extension libraries"
