package arrow.sample

class TestClass {
  suspend fun sideEffect() =
    println("BOOM!")

  suspend fun x(): Unit = TODO()

  fun other(): Unit {
    println("other")
  }

  suspend fun another2(): String {
    println("another")
    return "another"
  }
}
