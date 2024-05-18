// This file was automatically generated from Resource.kt by Knit tool. Do not edit.
package arrow.fx.coroutines.examples.exampleResource04

import arrow.fx.coroutines.ResourceScope
import arrow.fx.coroutines.Resource
import arrow.fx.coroutines.install
import arrow.fx.coroutines.resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserProcessor {
  suspend fun start(): Unit = withContext(Dispatchers.IO) { println("Creating UserProcessor") }
  suspend fun shutdown(): Unit = withContext(Dispatchers.IO) {
    println("Shutting down UserProcessor")
  }
}

suspend fun ResourceScope.userProcessor(): UserProcessor =
  install({  UserProcessor().also { it.start() } }) { processor, _ ->
    processor.shutdown()
  }

val userProcessor: Resource<UserProcessor> = resource {
  val x: UserProcessor = install(
    {  UserProcessor().also { it.start() } },
    { processor, _ -> processor.shutdown() }
  )
  x
}

val userProcessor2: Resource<UserProcessor> = resource({
  UserProcessor().also { it.start() }
}) { processor, _ -> processor.shutdown() }

val userProcessor3: Resource<UserProcessor> = ResourceScope::userProcessor
