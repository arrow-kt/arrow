// This file was automatically generated from Resource.kt by Knit tool. Do not edit.
package arrow.fx.coroutines.examples.exampleResource05

import arrow.fx.coroutines.ResourceScope
import arrow.fx.coroutines.resourceScope
import arrow.fx.coroutines.parZip
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
  suspend fun processData(): List<String> = (0..10).map { "Processed : $it" }
}

suspend fun ResourceScope.userProcessor(): UserProcessor =
  install({ UserProcessor().also { it.start() } }){ p,_ -> p.shutdown() }

suspend fun ResourceScope.dataSource(): DataSource =
  install({ DataSource().also { it.connect() } }) { ds, _ -> ds.close() }

suspend fun main(): Unit = resourceScope {
  val service = parZip({ userProcessor() }, { dataSource() }) { userProcessor, ds ->
    Service(ds, userProcessor)
  }
  val data = service.processData()
  println(data)
}
