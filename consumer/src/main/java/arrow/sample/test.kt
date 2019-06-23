package arrow.sample

//import arrow.sample.ForOption (this won't work because in a different module)

object test {
  @JvmStatic
  fun main(args: Array<String>) {
    println("Option supertypes: ${Option::class.java.interfaces.toList().map { it.toGenericString() }}")
    println("For Option Class through reflection:" + Class.forName("arrow.sample.ForOption"))
    println(None.h())
  }
}