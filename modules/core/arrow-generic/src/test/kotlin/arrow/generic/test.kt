package arrow.generic

import arrow.product
import arrow.syntax.monoid.empty

@product
data class Person(val name: String, val age: Int)

object test {

    @JvmStatic fun main(args: Array<String>): Unit {
        val p = Person("raul", 37)


    }

}