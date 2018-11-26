package arrow.ank

import arrow.Kind
import arrow.core.Option
import arrow.core.Tuple2
import arrow.data.Nel
import arrow.typeclasses.MonadThrow
import org.intellij.markdown.ast.ASTNode
import java.nio.file.Path

interface AnkOps<F> {
  fun MF(): MonadThrow<F>

  fun createTargetDirectory(source: Path, target: Path): Kind<F, Path>

  fun getCandidatePaths(root: Path): Kind<F, Option<Nel<Path>>>

  fun readFile(path: Path): Kind<F, String>

  fun preProcessMacros(pathAndContent: Tuple2<Path, String>): String

  fun parseMarkdown(markdown: String): Kind<F, ASTNode>

  fun extractCode(content: String, ast: ASTNode): Kind<F, Option<Nel<Snippet>>>

  fun compileCode(snippets: Tuple2<Path, Nel<Snippet>>, compilerArgs: List<String>): Kind<F, Nel<Snippet>>

  fun replaceAnkToLang(content: String, compiledSnippets: Nel<Snippet>): String

  fun generateFile(path: Path, newContent: String): Kind<F, Unit>

  fun printConsole(msg: String): Kind<F, Unit>
}