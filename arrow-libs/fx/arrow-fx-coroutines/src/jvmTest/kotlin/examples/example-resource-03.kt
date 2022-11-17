// This file was automatically generated from Resource.kt by Knit tool. Do not edit.
package arrow.fx.coroutines.examples.exampleResource03

import arrow.fx.coroutines.Resource
import arrow.fx.coroutines.resource
import arrow.fx.coroutines.resourceScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserProcessor {
  suspend fun start(): Unit = withContext(Dispatchers.IO) { println("Creating UserProcessor") }
  suspend fun shutdown(): Unit = withContext(Dispatchers.IO) {
    println("Shutting down UserProcessor")
  }
}

class DataSource {
  fun connect(): Unit = println("Connecting dataSource")
  fun close(): Unit = println("Closed dataSource")
}

class Service(val db: DataSource, val userProcessor: UserProcessor) {
  suspend fun processData(): List<String> = throw RuntimeException("I'm going to leak resources by not closing them")
}

val userProcessor: Resource<UserProcessor> = resource({
  UserProcessor().also { it.start() }
}) { p, _ -> p.shutdown() }

val dataSource: Resource<DataSource> = resource({
  DataSource().also { it.connect() }
}) { ds, exitCase ->
  println("Releasing $ds with exit: $exitCase")
  withContext(Dispatchers.IO) { ds.close() }
}

val service: Resource<Service> = resource {
  Service(dataSource.bind(), userProcessor.bind())
}

suspend fun main(): Unit = resourceScope {
  val data = service.bind().processData()
  println(data)
}
