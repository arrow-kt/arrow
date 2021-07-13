package arrow.core.test.concurrency

public data class SideEffect(var counter: Int = 0) {
  public fun increment() {
    counter++
  }
}
