// This file was automatically generated from Either.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEither33

import arrow.core.*

 fun main(args: Array<String>) {
  //sampleStart
  Either.Left("foo").isEmpty()  // Result: true
  Either.Right("foo").isEmpty() // Result: false
}
