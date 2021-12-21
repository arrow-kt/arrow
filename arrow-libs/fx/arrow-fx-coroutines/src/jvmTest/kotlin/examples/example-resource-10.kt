// This file was automatically generated from Resource.kt by Knit tool. Do not edit.
package arrow.fx.coroutines.examples.exampleResource10

import arrow.fx.coroutines.*

class File(url: String) {
  suspend fun open(): File = this
  suspend fun close(): Unit {}
  override fun toString(): String = "This file contains some interesting content!"
}

suspend fun openFile(uri: String): File = File(uri).open()
suspend fun closeFile(file: File): Unit = file.close()
suspend fun fileToString(file: File): String = file.toString()

suspend fun main(): Unit {
  //sampleStart
  val res: List<String> = listOf(
    "data.json",
    "user.json",
    "resource.json"
  ).traverseResource { uri ->
    resource {
     openFile(uri)
    } release { file ->
      closeFile(file)
    }
  }.use { files ->
    files.map { fileToString(it) }
  }
  //sampleEnd
  res.forEach(::println)
}
