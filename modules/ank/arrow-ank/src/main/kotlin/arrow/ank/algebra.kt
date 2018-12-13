package arrow.ank

import arrow.core.Tuple2
import org.intellij.markdown.ast.ASTNode
import java.nio.file.Path

interface AnkOps {

  fun createTargetDirectory(source: Path, target: Path): Path

  fun isAnkCandidate(path: Path): Boolean

  fun <A> withDocs(root: Path, f: (List<Path>) -> A): A

  fun <A> readFile(path: Path, f: (String) -> A): A

  fun preProcessMacros(path: Path, content: String): String

  fun parseMarkdown(markdown: String): Tuple2<String, ASTNode>

  fun extractCode(content: Tuple2<String, ASTNode>): Tuple2<String, List<Snippet>>

  fun compileCode(path: Path, snippets: List<Snippet>, compilerArgs: List<String>): List<Snippet>

  fun replaceAnkToLang(compiledSnippets: Tuple2<String, List<Snippet>>): String

  fun generateFile(path: Path, newContent: String): Path

  fun printConsole(msg: String): Unit
}