package arrow.ank

import java.nio.file.Path

public data class AnkProcessingContext(
  val index: Int,
  val path: Path
)

public interface AnkOps {

  public suspend fun createTargetDirectory(source: Path, target: Path): Path

  public suspend fun Path.ankFiles(): Sequence<AnkProcessingContext>

  public fun extractCode(content: Sequence<String>): Pair<Sequence<String>, Sequence<Snippet>>

  public suspend fun compileCode(snippets: Pair<Path, Sequence<Snippet>>, compilerArgs: List<String>): Sequence<Snippet>

  public fun replaceAnkToLang(content: Sequence<String>, compiledSnippets: Sequence<Snippet>): Sequence<String>

  public suspend fun generateFile(path: Path, newContent: Sequence<String>): Path

  public suspend fun printConsole(msg: String): Unit
}
