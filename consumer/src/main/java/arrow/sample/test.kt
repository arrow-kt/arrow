package arrow.x

import arrow.sample.ForOption
import arrow.sample.ForOption
import arrow.sample.Option


object test {
  @JvmStatic
  fun main(args: Array<String>) {
    val x = Option.whatever
    println(Option::class.java.interfaces.toList().map { it.toGenericString() })
    println(Class.forName("arrow.sample.ForOption"))
  }
}