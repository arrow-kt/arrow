package arrow.ank

import java.nio.file.Path

data class AnkProcessingContext(
  val index: Int,
  val path: Path
)

interface AnkOps {

  suspend fun createTargetDirectory(source: Path, target: Path): Path

  suspend fun Path.ankFiles(): Sequence<AnkProcessingContext>

  fun extractCode(content: Sequence<String>): Pair<Sequence<String>, Sequence<Snippet>>

  suspend fun compileCode(snippets: Pair<Path, Sequence<Snippet>>, compilerArgs: List<String>): Sequence<Snippet>

  fun replaceAnkToLang(content: Sequence<String>, compiledSnippets: Sequence<Snippet>): Sequence<String>

  suspend fun generateFile(path: Path, newContent: Sequence<String>): Path

  suspend fun printConsole(msg: String): Unit
}
