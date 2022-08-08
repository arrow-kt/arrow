// This file was automatically generated from Resource.kt by Knit tool. Do not edit.
package arrow.fx.coroutines.examples.exampleResource06

import arrow.fx.coroutines.resource
import arrow.fx.coroutines.release
import arrow.fx.coroutines.use
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DataSource {
  suspend fun connect(): Unit = withContext(Dispatchers.IO) { println("Connecting dataSource") }
  suspend fun close(): Unit = withContext(Dispatchers.IO) { println("Closed dataSource") }
  suspend fun users(): List<String> = listOf("User-1", "User-2", "User-3")
}

suspend fun main(): Unit {
  val dataSource = resource {
    DataSource().also { it.connect() }
  } release DataSource::close

  val res = dataSource
    .use { ds -> "Using data source: ${ds.users()}" }
    .also(::println)
}
