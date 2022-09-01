// This file was automatically generated from Resource.kt by Knit tool. Do not edit.
package arrow.fx.coroutines.examples.exampleResource01

class UserProcessor {
  fun start(): Unit = println("Creating UserProcessor")
  fun shutdown(): Unit = println("Shutting down UserProcessor")
}

class DataSource {
  fun connect(): Unit = println("Connecting dataSource")
  fun close(): Unit = println("Closed dataSource")
}

class Service(val db: DataSource, val userProcessor: UserProcessor) {
  suspend fun processData(): List<String> = throw RuntimeException("I'm going to leak resources by not closing them")
}

suspend fun main(): Unit {
  val userProcessor = UserProcessor().also { it.start() }
  val dataSource = DataSource().also { it.connect() }
  val service = Service(dataSource, userProcessor)

  service.processData()

  dataSource.close()
  userProcessor.shutdown()
}
