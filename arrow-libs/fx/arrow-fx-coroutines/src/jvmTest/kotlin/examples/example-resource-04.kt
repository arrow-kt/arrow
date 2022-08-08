// This file was automatically generated from Resource.kt by Knit tool. Do not edit.
package arrow.fx.coroutines.examples.exampleResource04

import arrow.fx.coroutines.resource
import arrow.fx.coroutines.Resource
import arrow.fx.coroutines.release
import arrow.fx.coroutines.releaseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserProcessor {
  suspend fun start(): Unit = withContext(Dispatchers.IO) { println("Creating UserProcessor") }
  suspend fun shutdown(): Unit = withContext(Dispatchers.IO) {
    println("Shutting down UserProcessor")
  }
}

val userProcessor: Resource<UserProcessor> =
  resource(
    {  UserProcessor().also { it.start() } },
    { processor, _ -> processor.shutdown() }
  )

val userProcessor2: Resource<UserProcessor> = resource {
  UserProcessor().also { it.start() }
} release UserProcessor::shutdown

val userProcessor3 = userProcessor2 releaseCase { _, exitCase ->
  println("Composed finalizer to log exitCase: $exitCase")
}
