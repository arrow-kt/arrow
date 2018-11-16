import arrow.effects.IO
import arrow.effects.typeclasses.ExitCase

fun test() {
  class File(url: String) {
    fun open(): File = this
    fun close(): Unit {}
    override fun toString(): String = "This file contains some interesting content!"
  }

  fun openFile(uri: String): IO<File> = IO { File(uri).open() }

  fun closeFile(file: File): IO<Unit> = IO { file.close() }

  fun fileToString(file: File): IO<String> = IO { file.toString() }

  openFile("data.json").bracket(
    release = { file -> closeFile(file) },
    use = { file -> fileToString(file) })

  openFile("data.json").bracketCase(
    release = { file, exitCase ->
      when (exitCase) {
        is ExitCase.Completed -> { /* do something */ }
        is ExitCase.Cancelled -> { /* do something */ }
        is ExitCase.Error -> { /* do something */ }
      }
      closeFile(file)
    },
    use = { file ->
      fileToString(file)
    })
}
