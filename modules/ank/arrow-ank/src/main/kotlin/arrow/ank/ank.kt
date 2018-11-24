package arrow.ank

import arrow.core.toT
import org.intellij.markdown.ast.ASTNode
import java.io.File

const val AnkBlock = ":ank"
const val AnkSilentBlock = ":ank:silent"
const val AnkReplaceBlock = ":ank:replace"
const val AnkOutFileBlock = ":ank:outFile"

fun ank(source: File, target: File, compilerArgs: List<String>): List<File> {
    val heapSize = Runtime.getRuntime().totalMemory()
    val heapMaxSize = Runtime.getRuntime().maxMemory()
    println("Current heap used: ${(heapSize - Runtime.getRuntime().freeMemory()).humanBytes()}")
    println("Starting ank with Heap Size: ${heapSize.humanBytes()}, Max Heap Size: ${heapMaxSize.humanBytes()}")
    println(colored(ANSI_PURPLE, AnkHeader))
    val targetDirectory: File = createTarget(source, target)
    val files: List<File> = getFileCandidates(targetDirectory)
    return files.mapIndexed { n, file -> n toT file }.flatMap { (n, file) ->
        if (files.size < 1000 || n % 100 == 0 || n < 100) {
            //report first 100 hundred then every 100 files, or report them all if under 1000
            val message = "Ank: Processed ~> [${n + 1} of ${files.size}] ~ Heap in use ${(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()).humanBytes()}"
            println(colored(ANSI_GREEN, message))
        }
        val content: String = readFile(file)
        val preProcessedMacros: String = preProcessMacros(file toT content)
        val parsedMarkDown: ASTNode = parseMarkdown(preProcessedMacros)
        val snippets = extractCode(preProcessedMacros, parsedMarkDown)
        val compilationResult = compileCode(mapOf(file to snippets), compilerArgs)
        val replacedResult = compilationResult.map(::replaceAnkToLang)
        replacedResult.map { newContent -> generateFile(file, newContent) }
    }
  }

/**
 * From https://stackoverflow.com/questions/3758606/how-to-convert-byte-size-into-human-readable-format-in-java
 */
fun Long.humanBytes(): String {
    val unit = 1024
    if (this < unit) return toString() + " B"
    val exp = (Math.log(toDouble()) / Math.log(unit.toDouble())).toInt()
    val pre = ("KMGTPE")[exp - 1] + "i"
    return String.format("%.1f %sB", this / Math.pow(unit.toDouble(), exp.toDouble()), pre)
}