// This file was automatically generated from Resource.kt by Knit tool. Do not edit.
package arrow.fx.coroutines.examples.exampleResource06

import arrow.fx.coroutines.install
import arrow.fx.coroutines.resourceScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DataSource {
  suspend fun connect(): Unit = withContext(Dispatchers.IO) { println("Connecting dataSource") }
  suspend fun close(): Unit = withContext(Dispatchers.IO) { println("Closed dataSource") }
  suspend fun users(): List<String> = listOf("User-1", "User-2", "User-3")
}

suspend fun main(): Unit = resourceScope {
  val dataSource = install({
    DataSource().also { it.connect() }
  }) { ds, _ -> ds.close() }

  println("Using data source: ${dataSource.users()}")
}
