package arrow.ank

import arrow.Kind
import arrow.core.FunctionK
import arrow.data.ListK
import arrow.free.Free
import arrow.free.foldMap
import arrow.free.instances.FreeMonadInstance
import arrow.higherkind
import arrow.typeclasses.Monad
import org.intellij.markdown.ast.ASTNode
import java.io.File

@higherkind
sealed class AnkOps<A> : AnkOpsOf<A> {
  data class CreateTarget(val source: File, val target: File) : AnkOps<File>()
  data class GetFileCandidates(val target: File) : AnkOps<ListK<File>>()
  data class ReadFile(val source: File) : AnkOps<String>()
  data class ParseMarkdown(val markdown: String) : AnkOps<ASTNode>()
  data class ExtractCode(val source: String, val tree: ASTNode) : AnkOps<ListK<Snippet>>()
  data class CompileCode(val snippets: Map<File, ListK<Snippet>>, val compilerArgs: ListK<String>) : AnkOps<ListK<CompiledMarkdown>>()
  data class ReplaceAnkToLang(val compilationResults: CompiledMarkdown) : AnkOps<String>()
  data class GenerateFiles(val candidates: ListK<File>, val newContents: ListK<String>) : AnkOps<ListK<File>>()
  companion object : FreeMonadInstance<ForAnkOps>
}

fun createTarget(source: File, target: File): Free<ForAnkOps, File> =
  Free.liftF(AnkOps.CreateTarget(source, target))

fun getFileCandidates(target: File): Free<ForAnkOps, ListK<File>> =
  Free.liftF(AnkOps.GetFileCandidates(target))

fun readFile(source: File): Free<ForAnkOps, String> =
  Free.liftF(AnkOps.ReadFile(source))

fun parseMarkdown(markdown: String): Free<ForAnkOps, ASTNode> =
  Free.liftF(AnkOps.ParseMarkdown(markdown))

fun extractCode(source: String, tree: ASTNode): Free<ForAnkOps, ListK<Snippet>> =
  Free.liftF(AnkOps.ExtractCode(source, tree))

fun compileCode(snippets: Map<File, ListK<Snippet>>, compilerArgs: ListK<String>): Free<ForAnkOps, ListK<CompiledMarkdown>> =
  Free.liftF(AnkOps.CompileCode(snippets, compilerArgs))

fun replaceAnkToLang(compilationResults: CompiledMarkdown): Free<ForAnkOps, String> =
  Free.liftF(AnkOps.ReplaceAnkToLang(compilationResults))

fun generateFiles(candidates: ListK<File>, newContents: ListK<String>): Free<ForAnkOps, ListK<File>> =
  Free.liftF(AnkOps.GenerateFiles(candidates, newContents))

inline fun <reified F> Free<ForAnkOps, ListK<File>>.run(interpreter: FunctionK<ForAnkOps, F>, MF: Monad<F>): Kind<F, ListK<File>> =
  this.foldMap(interpreter, MF)
