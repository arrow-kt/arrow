package arrow.sample

//import arrow.sample.ForOption

object test {
  @JvmStatic
  fun main(args: Array<String>) {
    println(Option::class.java.interfaces.toList().map { it.toGenericString() })
    println(Class.forName("arrow.sample.ForOption"))
  }
}