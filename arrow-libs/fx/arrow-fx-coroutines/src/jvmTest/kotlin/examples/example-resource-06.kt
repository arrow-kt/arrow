// This file was automatically generated from Resource.kt by Knit tool. Do not edit.
package arrow.fx.coroutines.examples.exampleResource06

import arrow.fx.coroutines.*

class DataSource {
  fun connect(): Unit = println("Connecting dataSource")
  fun users(): List<String> = listOf("User-1", "User-2", "User-3")
  fun close(): Unit = println("Closed dataSource")
}

suspend fun main(): Unit {
  val dataSource = resource {
    DataSource().also { it.connect() }
  } release DataSource::close

  val res = dataSource
    .use { ds -> "Using data source: ${ds.users()}" }
    .also(::println)
}
