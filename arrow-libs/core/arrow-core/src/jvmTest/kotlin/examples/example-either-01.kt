// This file was automatically generated from Either.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEither01

val throwsSomeStuff: (Int) -> Double = {x -> x.toDouble()}
val throwsOtherThings: (Double) -> String = {x -> x.toString()}
val moreThrowing: (String) -> List<String> = {x -> listOf(x)}
val magic: (Int) -> List<String> = { x ->
  val y = throwsSomeStuff(x)
  val z = throwsOtherThings(y)
  moreThrowing(z)
}
fun main() {
 println ("magic = $magic")
}
