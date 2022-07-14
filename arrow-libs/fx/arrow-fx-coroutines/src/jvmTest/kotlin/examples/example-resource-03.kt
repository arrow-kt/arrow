// This file was automatically generated from Resource.kt by Knit tool. Do not edit.
package arrow.fx.coroutines.examples.exampleResource03

import arrow.fx.coroutines.*
import arrow.fx.coroutines.continuations.resource

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

val userProcessor = resource {
  UserProcessor().also(UserProcessor::start)
} release UserProcessor::shutdown

val dataSource = resource {
  DataSource().also { it.connect() }
} release DataSource::close

suspend fun main(): Unit {
  resource {
    parZip({ userProcessor.bind() }, { dataSource.bind() }) { userProcessor, ds ->
      Service(ds, userProcessor)
    }
  }.use { service -> service.processData() }
}
