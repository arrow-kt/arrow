package arrow.generic


fun main() {
  val a = Person(name = "X", age = 98, p = Person2(name = "Y", age = 99, p = null)).generic()
  val b = 1.generic()
  val c = Pair(1, "Hello, World!").generic()

  println(a)
  println(b)
  println(c)
}

