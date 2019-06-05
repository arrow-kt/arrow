package arrow.sample

class TestClass {
  suspend fun sideEffect() =
    println("BOOM!")

  suspend fun x(): Unit = TODO()

  suspend fun other(): Unit {
    println("other")
  }

  fun another2(): String {
    1; println("test")
    return "another"
  }
}
