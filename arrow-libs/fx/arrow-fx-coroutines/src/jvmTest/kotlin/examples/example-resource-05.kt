// This file was automatically generated from Resource.kt by Knit tool. Do not edit.
package arrow.fx.coroutines.examples.exampleResource05

import arrow.fx.coroutines.resource
import arrow.fx.coroutines.release
import arrow.fx.coroutines.parZip
import arrow.fx.coroutines.use
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserProcessor {
  suspend fun start(): Unit = withContext(Dispatchers.IO) { println("Creating UserProcessor") }
  suspend fun shutdown(): Unit = withContext(Dispatchers.IO) {
    println("Shutting down UserProcessor")
  }
}

class DataSource {
  suspend fun connect(): Unit = withContext(Dispatchers.IO) { println("Connecting dataSource") }
  suspend fun close(): Unit = withContext(Dispatchers.IO) { println("Closed dataSource") }
}

class Service(val db: DataSource, val userProcessor: UserProcessor) {
  suspend fun processData(): List<String> = throw RuntimeException("I'm going to leak resources by not closing them")
}

val userProcessor = resource {
  UserProcessor().also { it.start() }
} release UserProcessor::shutdown

val dataSource = resource {
  DataSource().also { it.connect() }
} release DataSource::close

val service = resource {
  parZip({ userProcessor.bind() }, { dataSource.bind() }) { userProcessor, ds ->
    Service(ds, userProcessor)
  }
}

suspend fun main(): Unit {
  service.use(Service::processData)
}
