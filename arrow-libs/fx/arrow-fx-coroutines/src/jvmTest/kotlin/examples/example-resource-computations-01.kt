// This file was automatically generated from resource.kt by Knit tool. Do not edit.
package arrow.fx.coroutines.examples.exampleResourceComputations01

import arrow.fx.coroutines.continuations.resource
import arrow.fx.coroutines.release

class UserProcessor {
  fun start(): Unit = println("Creating UserProcessor")
  fun shutdown(): Unit = println("Shutting down UserProcessor")
  fun process(ds: DataSource): List<String> =
   ds.users().map { "Processed $it" }
}

class DataSource {
  fun connect(): Unit = println("Connecting dataSource")
  fun users(): List<String> = listOf("User-1", "User-2", "User-3")
  fun close(): Unit = println("Closed dataSource")
}

class Service(val db: DataSource, val userProcessor: UserProcessor) {
  suspend fun processData(): List<String> = userProcessor.process(db)
}

suspend fun main(): Unit {
  resource<Service> {
    val dataSource = resource {
      DataSource().also { it.connect() }
    } release DataSource::close

    val userProcessor = resource {
      UserProcessor().also(UserProcessor::start)
    } release UserProcessor::shutdown

    Service(dataSource, userProcessor)
  }.use { service -> service.processData() }
}
