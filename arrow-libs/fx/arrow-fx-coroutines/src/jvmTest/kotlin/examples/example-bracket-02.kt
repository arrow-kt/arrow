// This file was automatically generated from Bracket.kt by Knit tool. Do not edit.
package arrow.fx.coroutines.examples.exampleBracket02

import arrow.fx.coroutines.*

class File(val url: String) {
  fun open(): File = this
  fun close(): Unit {}
}

suspend fun File.content(): String =
    "This file contains some interesting content from $url!"
suspend fun openFile(uri: String): File = File(uri).open()
suspend fun closeFile(file: File): Unit = file.close()

suspend fun main(): Unit {
  //sampleStart
  val res = bracketCase(
    acquire = { openFile("data.json") },
    use = { file -> file.content() },
    release = { file, exitCase ->
      when (exitCase) {
        is ExitCase.Completed -> println("File closed with $exitCase")
        is ExitCase.Cancelled -> println("Program cancelled with $exitCase")
        is ExitCase.Failure -> println("Program failed with $exitCase")
      }
      closeFile(file)
    }
  )
  //sampleEnd
  println(res)
}
