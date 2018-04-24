package arrow.test.concurrency

data class SideEffect(var counter: Int = 0) {
  fun increment(): Unit {
    counter++
  }
}