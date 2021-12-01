// This file was automatically generated from Either.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEither42

import arrow.core.*

 fun main(args: Array<String>) {
  //sampleStart
  Left("foo").isEmpty()  // Result: true
  Right("foo").isEmpty() // Result: false
}
