package arrow.ank

import arrow.core.Tuple2
import arrow.typeclasses.MonadThrow
import java.nio.file.Path

data class AnkProcessingContext(
  val index: Int,
  val path: Path
)

interface AnkOps<F> {
  fun MF(): MonadThrow<F>

  fun createTargetDirectory(source: Path, target: Path): Path

  fun <A> Path.withAnkFiles(f: (AnkProcessingContext) -> A): Sequence<A>

  fun Path.processMacros(): Sequence<String>

  fun extractCode(content: Sequence<String>): Tuple2<Sequence<String>, Sequence<Snippet>>

  fun compileCode(snippets: Tuple2<Path, Sequence<Snippet>>, compilerArgs: List<String>): Sequence<Snippet>

  fun replaceAnkToLang(content: Sequence<String>, compiledSnippets: Sequence<Snippet>): Sequence<String>

  fun generateFile(path: Path, newContent: Sequence<String>): Path

  fun printConsole(msg: String): Unit

}