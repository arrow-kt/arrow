// This file was automatically generated from Resource.kt by Knit tool. Do not edit.
package arrow.fx.coroutines.examples.exampleResource02

import arrow.fx.coroutines.*
import arrow.fx.coroutines.continuations.resource

val resourceA = resource {
  "A"
} release { a ->
  println("Releasing $a")
}

val resourceB = resource {
 "B"
} releaseCase { b, exitCase ->
  println("Releasing $b with exit: $exitCase")
}
