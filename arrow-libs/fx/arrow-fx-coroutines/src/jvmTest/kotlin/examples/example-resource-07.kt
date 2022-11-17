// This file was automatically generated from Resource.kt by Knit tool. Do not edit.
package arrow.fx.coroutines.examples.exampleResource07

import arrow.fx.coroutines.*

object Connection
class DataSource {
  fun connect(): Unit = println("Connecting dataSource")
  fun connection(): Connection = Connection
  fun close(): Unit = println("Closed dataSource")
}

class Database(private val database: DataSource) {
  fun init(): Unit = println("Database initialising . . .")
  fun shutdown(): Unit = println("Database shutting down . . .")
}

suspend fun main(): Unit {
  val dataSource = resource {
    DataSource().also { it.connect() }
  } release DataSource::close

  fun database(ds: DataSource): Resource<Database> =
    resource {
      Database(ds).also(Database::init)
    } release Database::shutdown

  dataSource.flatMap(::database)
    .use { println("Using database which uses dataSource") }
}
