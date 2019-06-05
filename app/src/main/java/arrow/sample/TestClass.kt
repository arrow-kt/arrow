package arrow.sample

class TestClass {
  fun sideEffect() =
    println("BOOM!")

  fun other(): Unit {
    println("other")
  }

  val x = { println() }

  fun another2(): String {
    1; println("test");
    {
      { println() }
    }()
    return "another"
  }
}
