package arrow.ank

import arrow.core.Tuple2
import java.nio.file.Path

data class AnkProcessingContext(
  val index: Int,
  val path: Path
)

interface AnkOps {

  suspend fun createTargetDirectory(source: Path, target: Path): Path

  suspend fun Path.ankFiles(): Sequence<AnkProcessingContext>

  suspend fun Path.processMacros(): Sequence<String>

  fun extractCode(content: Sequence<String>): Tuple2<Sequence<String>, Sequence<Snippet>>

  suspend fun compileCode(snippets: Tuple2<Path, Sequence<Snippet>>, compilerArgs: List<String>): Sequence<Snippet>

  fun replaceAnkToLang(content: Sequence<String>, compiledSnippets: Sequence<Snippet>): Sequence<String>

  suspend fun generateFile(path: Path, newContent: Sequence<String>): Path

  suspend fun printConsole(msg: String): Unit

}