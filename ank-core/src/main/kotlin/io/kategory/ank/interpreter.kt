package io.kategory.ank

import kategory.*
import kotlinx.coroutines.experimental.CoroutineDispatcher
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking
import org.intellij.markdown.MarkdownElementTypes.CODE_FENCE
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.accept
import org.intellij.markdown.ast.getTextInNode
import org.intellij.markdown.ast.visitors.RecursiveVisitor
import org.intellij.markdown.flavours.gfm.GFMFlavourDescriptor
import org.intellij.markdown.parser.MarkdownParser
import org.jetbrains.kotlin.cli.jvm.K2JVMCompiler
import java.io.File
import java.net.URL
import java.net.URLClassLoader
import java.util.concurrent.Executors
import javax.script.ScriptEngine
import javax.script.ScriptEngineManager
import javax.script.ScriptException
import kotlin.coroutines.experimental.CoroutineContext

val extensionMappings = mapOf(
        "java" to "java",
        "kotlin" to "kts"
)

@Suppress("UNCHECKED_CAST")
inline fun <reified F> ankMonadErrorInterpreter(ME: MonadError<F, Throwable> = monadError()): FunctionK<AnkOpsHK, F> =
        object : FunctionK<AnkOpsHK, F> {
            override fun <A> invoke(fa: HK<AnkOpsHK, A>): HK<F, A> {
                val op = fa.ev()
                return when (op) {
                    is AnkOps.CreateTarget -> ME.catch({ createTargetImpl(op.source, op.target) })
                    is AnkOps.GetFileCandidates -> ME.catch({ getFileCandidatesImpl(op.target) })
                    is AnkOps.ReadFile -> ME.catch({ readFileImpl(op.source) })
                    is AnkOps.ParseMarkdown -> ME.catch({ parseMarkDownImpl(op.markdown) })
                    is AnkOps.ExtractCode -> ME.catch({ extractCodeImpl(op.source, op.tree) })
                    is AnkOps.CompileCode -> ME.catch({ compileCodeImpl(op.snippets, op.compilerArgs) })
                    is AnkOps.ReplaceAnkToLang -> ME.catch({ replaceAnkToLangImpl(op.compilationResults) })
                    is AnkOps.GenerateFiles -> ME.catch({ generateFilesImpl(op.candidates, op.newContents) })
                } as HK<F, A>
            }
        }

val SupportedMarkdownExtensions: Set<String> = setOf(
        "markdown",
        "mdown",
        "mkdn",
        "md",
        "mkd",
        "mdwn",
        "mdtxt",
        "mdtext",
        "text",
        "Rmd"
)

fun createTargetImpl(source: File, target: File): File {
    source.copyRecursively(target, overwrite = true)
    return target
}

fun getFileCandidatesImpl(target: File): ListKW<File> =
        ListKW(target.walkTopDown().filter {
            SupportedMarkdownExtensions.contains(it.extension.toLowerCase())
        }.toList())

fun readFileImpl(source: File): String =
        source.readText()

fun parseMarkDownImpl(markdown: String): ASTNode =
        MarkdownParser(GFMFlavourDescriptor()).buildMarkdownTreeFromString(markdown)

data class CompilationException(
        val file: File,
        val snippet: Snippet,
        val underlying: Throwable,
        val msg: String = """
            |
            |
            |### ΛNK Compilation Error ###
            |file: $file
            |lang: ${snippet.lang}
            |```
            |${snippet.code}
            |```
            |
            |${underlying.message}
            |##############################
            |
        """.trimMargin()) : ScriptException(msg) {
    override fun toString(): String = msg
}

data class CompiledMarkdown(val origin: File, val snippets: ListKW<Snippet>)

data class Snippet(
        val fence: String,
        val lang: String,
        val silent: Boolean,
        val startOffset: Int,
        val endOffset: Int,
        val code: String,
        val result: Option<String> = None)

fun extractCodeImpl(source: String, tree: ASTNode): ListKW<Snippet> {
    val sb = mutableListOf<Snippet>()
    tree.accept(object : RecursiveVisitor() {
        override fun visitNode(node: ASTNode) {
            if (node.type == CODE_FENCE) {
                val fence = node.getTextInNode(source)
                val lang = fence.takeWhile { it != ':' }.toString().replace("```", "")
                if (fence.startsWith("```$lang$AnkBlock")) {
                    val code = fence.split("\n").drop(1).dropLast(1).joinToString("\n")
                    sb.add(Snippet(fence.toString(), lang, fence.startsWith("```$lang$AnkSilentBlock"), node.startOffset, node.endOffset, code))
                }
            }
            super.visitNode(node)
        }
    })
    return sb.k()
}

val Snippet.compile: Boolean
    get() = fence.startsWith("```$lang$AnkCompileBlock")

fun compileCodeImpl(snippets: Map<File, ListKW<Snippet>>, classpath: ListKW<String>): ListKW<CompiledMarkdown> {
    println(":runAnk -> started compilation")
    val ioBoundPool = Executors.newFixedThreadPool(snippets.size)
    val IOPool = object : CoroutineDispatcher() {
        override fun dispatch(context: CoroutineContext, block: Runnable) {
            ioBoundPool.submit(block)
        }
    }

    val classPath: String = classpath.joinToString(separator = File.pathSeparatorChar.toString()) {
        it.replace("file:", "")
    }

    return runBlocking {
        snippets.map { (file, codeBlocks) ->
            async(IOPool) {
                val classLoader = URLClassLoader(classpath.map { URL(it) }.ev().list.toTypedArray())
                val seManager = ScriptEngineManager(classLoader)
                val engineCache: Map<String, ScriptEngine> =
                        codeBlocks.list
                                .distinctBy { it.lang }
                                .map {
                                    it.lang to seManager.getEngineByExtension(extensionMappings.getOrDefault(it.lang, "kts"))
                                }
                                .toMap()

                if (codeBlocks.find { it.compile }.nonEmpty()) {
                    val tmpFile = createTempFile(prefix = file.nameWithoutExtension + "__", suffix = ".kts")

                    val (imports, code) = codeBlocks.fold(emptySet<String>() toT emptyList<String>()) { acc, snippet ->
                        snippet.code.lines().fold(acc) { (imports, code), line ->
                            if (line.startsWith("import")) imports + line else code + line
                            imports toT code
                        }
                    }

                    tmpFile.printWriter().use {
                        it.println(imports.joinToString("\n"))
                        it.println(code.joinToString("\n"))
                    }

                    val args = listOf("-Xcoroutines=enable", "-cp", classPath, "-d", file.parentFile.absolutePath, tmpFile.absolutePath).toTypedArray()

                    //If compilation fails compiler shuts down VM. However correct output is shown in Gradle.
                    K2JVMCompiler.main(args)

                    tmpFile.delete()
                    File("${file.parentFile.absolutePath}/${tmpFile.name.replace(".kts", ".class")}").delete()
                }

                val evaledSnippets: ListKW<Snippet> = codeBlocks.map { snippet ->
                    val result: Any? = Try {
                        val engine: ScriptEngine = engineCache.k().getOrElse(
                                snippet.lang,
                                { throw CompilationException(file, snippet, IllegalStateException("No engine configured for `${snippet.lang}`")) })
                        if (!snippet.compile) engine.eval(snippet.code) else null
                    }.fold({
                        throw CompilationException(file, snippet, it)
                    }, ::identity)

                    if (snippet.silent || snippet.compile) {
                        snippet
                    } else {
                        val resultString: Option<String> = Option.fromNullable(result).fold({ None }, { Some("//$it") })
                        snippet.copy(result = resultString)
                    }
                }
                CompiledMarkdown(file, evaledSnippets)
            }
        }.map { it.await() }.k()
    }.also { ioBoundPool.shutdownNow() }
}

fun replaceAnkToLangImpl(compiledMarkdown: CompiledMarkdown): String =
        compiledMarkdown.snippets.fold(compiledMarkdown.origin.readText(), { content, snippet ->
            snippet.result.fold(
                    { content },
                    { content.replace(snippet.fence, "```${snippet.lang}\n" + snippet.code + "\n" + it + "\n```") }
            )
        })

fun generateFilesImpl(candidates: ListKW<File>, newContents: ListKW<String>): ListKW<File> =
        ListKW(candidates.mapIndexed { n, file ->
            file.printWriter().use {
                it.print(newContents.list[n])
            }
            file
        })
