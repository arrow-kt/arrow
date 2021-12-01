// This file was automatically generated from Either.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEither34

val result: Either<Exception, Value> = possiblyFailingOperation()
result.fold(
     { log("operation failed with $it") },
     { log("operation succeeded with $it") }
)
