// This file was automatically generated from Bracket.kt by Knit tool. Do not edit.
package arrow.fx.coroutines.examples.exampleBracket01

import arrow.fx.coroutines.*

class File(url: String) {
  fun open(): File = this
  fun close(): Unit {}
  override fun toString(): String = "This file contains some interesting content!"
}

suspend fun openFile(uri: String): File = File(uri).open()
suspend fun closeFile(file: File): Unit = file.close()
suspend fun fileToString(file: File): String = file.toString()

suspend fun main(): Unit {
  //sampleStart
  val res = bracket(
    acquire = { openFile("data.json") },
    use = { file -> fileToString(file) },
    release = { file: File -> closeFile(file) }
  )
  //sampleEnd
  println(res)
}
