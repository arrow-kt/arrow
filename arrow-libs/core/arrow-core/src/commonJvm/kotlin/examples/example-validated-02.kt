// This file was automatically generated from Validated.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleValidated02

abstract class Read<A> {

abstract fun read(s: String): A?

 companion object {

  val stringRead: Read<String> =
   object: Read<String>() {
    override fun read(s: String): String? = s
   }

  val intRead: Read<Int> =
   object: Read<Int>() {
    override fun read(s: String): Int? =
     if (s.matches(Regex("-?[0-9]+"))) s.toInt() else null
   }
 }
}
