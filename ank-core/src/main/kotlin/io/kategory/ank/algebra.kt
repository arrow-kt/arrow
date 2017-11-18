package io.kategory.ank

import kategory.*
import org.intellij.markdown.ast.ASTNode
import java.io.File

@higherkind sealed class AnkOps<A> : AnkOpsKind<A> {
    data class CreateTarget(val source: File, val target: File) : AnkOps<File>()
    data class GetFileCandidates(val target: File) : AnkOps<ListKW<File>>()
    data class ReadFile(val source: File) : AnkOps<String>()
    data class ParseMarkdown(val markdown: String) : AnkOps<ASTNode>()
    data class ExtractCode(val source: String, val tree: ASTNode) : AnkOps<ListKW<Snippet>>()
    data class CompileCode(val snippets: Map<File, ListKW<Snippet>>, val compilerArgs: ListKW<String>) : AnkOps<ListKW<CompiledMarkdown>>()
    data class ReplaceAnkToLang(val compilationResults: CompiledMarkdown) : AnkOps<String>()
    data class GenerateFiles(val candidates: ListKW<File>, val newContents: ListKW<String>) : AnkOps<ListKW<File>>()
    companion object : FreeMonadInstance<AnkOpsHK>
}

fun createTarget(source: File, target: File): Free<AnkOpsHK, File> =
        Free.liftF(AnkOps.CreateTarget(source, target))

fun getFileCandidates(target: File): Free<AnkOpsHK, ListKW<File>> =
        Free.liftF(AnkOps.GetFileCandidates(target))

fun readFile(source: File): Free<AnkOpsHK, String> =
        Free.liftF(AnkOps.ReadFile(source))

fun parseMarkdown(markdown: String): Free<AnkOpsHK, ASTNode> =
        Free.liftF(AnkOps.ParseMarkdown(markdown))

fun extractCode(source: String, tree: ASTNode): Free<AnkOpsHK, ListKW<Snippet>> =
        Free.liftF(AnkOps.ExtractCode(source, tree))

fun compileCode(snippets: Map<File, ListKW<Snippet>>, compilerArgs: ListKW<String>): Free<AnkOpsHK, ListKW<CompiledMarkdown>> =
        Free.liftF(AnkOps.CompileCode(snippets, compilerArgs))

fun replaceAnkToLang(compilationResults: CompiledMarkdown): Free<AnkOpsHK, String> =
        Free.liftF(AnkOps.ReplaceAnkToLang(compilationResults))

fun generateFiles(candidates: ListKW<File>, newContents: ListKW<String>): Free<AnkOpsHK, ListKW<File>> =
        Free.liftF(AnkOps.GenerateFiles(candidates, newContents))

inline fun <reified F> Free<AnkOpsHK, ListKW<File>>.run(interpreter: FunctionK<AnkOpsHK, F>, MF: Monad<F> = monad()): HK<F, ListKW<File>> =
        this.foldMap(interpreter, MF)
