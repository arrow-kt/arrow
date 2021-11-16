// This file was automatically generated from map.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleMap05

import arrow.core.*

fun main(args: Array<String>) {
  //sampleStart
  val result =
   mapOf(
     "first" to ("A" to 1).bothIor(),
     "second" to ("B" to 2).bothIor(),
     "third" to "C".leftIor()
   ).unalign()
  //sampleEnd
  println(result)
}
