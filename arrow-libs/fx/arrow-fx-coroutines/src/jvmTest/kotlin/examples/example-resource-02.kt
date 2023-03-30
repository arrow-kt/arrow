// This file was automatically generated from Resource.kt by Knit tool. Do not edit.
package arrow.fx.coroutines.examples.exampleResource02

import java.io.Closeable

class UserProcessor : Closeable {
  fun start(): Unit = println("Creating UserProcessor")
  override fun close(): Unit = println("Shutting down UserProcessor")
}

class DataSource : Closeable {
  fun connect(): Unit = println("Connecting dataSource")
  override fun close(): Unit = println("Closed dataSource")
}

class Service(val db: DataSource, val userProcessor: UserProcessor) {
  suspend fun processData(): List<String> = throw RuntimeException("I'm going to leak resources by not closing them")
}

suspend fun main(): Unit {
  UserProcessor().use { userProcessor ->
    userProcessor.start()
    DataSource().use { dataSource ->
      dataSource.connect()
      Service(dataSource, userProcessor).processData()
    }
  }
}
