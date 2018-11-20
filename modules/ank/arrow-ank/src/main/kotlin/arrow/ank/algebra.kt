package arrow.ank

import arrow.core.Tuple2
import org.intellij.markdown.ast.ASTNode
import java.io.File

fun createTarget(source: File, target: File): File =
  createTargetImpl(source, target)

fun getFileCandidates(target: File): List<File> =
  getFileCandidatesImpl(target)

fun readFile(source: File): String =
  readFileImpl(source)

fun preProcessMacros(source: Tuple2<File, String>): String =
  preProcessMacrosImpl(source)

fun parseMarkdown(markdown: String): ASTNode =
  parseMarkDownImpl(markdown)

fun extractCode(source: String, tree: ASTNode): List<Snippet> =
  extractCodeImpl(source, tree)

fun compileCode(snippets: Map<File, List<Snippet>>, compilerArgs: List<String>): List<CompiledMarkdown> =
  compileCodeImpl(snippets, compilerArgs)

fun replaceAnkToLang(compilationResults: CompiledMarkdown): String =
  replaceAnkToLangImpl(compilationResults)

fun generateFile(candidate: File, newContents: String): File =
  generateFileImpl(candidate, newContents)

