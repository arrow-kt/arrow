package arrow.generic


fun main() {
  val generic = Person("X", 98, Person2("Y", 99)).generic()
  //val generic = 1.generic()
  println("$generic")
}

