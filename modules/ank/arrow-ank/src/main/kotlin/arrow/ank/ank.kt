package arrow.ank

import arrow.core.toT
import arrow.instances.list.foldable.foldLeft
import java.nio.file.Path

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

fun ank(source: Path, target: Path, compilerArgs: List<String>, ankOps: AnkOps): Int =
  with(ankOps) {
    printConsole(colored(ANSI_PURPLE, AnkHeader))
    val heapSize = Runtime.getRuntime().totalMemory()
    val heapMaxSize = Runtime.getRuntime().maxMemory()
    println("Current heap used: ${(heapSize - Runtime.getRuntime().freeMemory()).humanBytes()}")
    println("Starting ank with Heap Size: ${heapSize.humanBytes()}, Max Heap Size: ${heapMaxSize.humanBytes()}")
    val path = createTargetDirectory(source, target)
    withDocs(path) { candidates ->
      val generated = candidates.foldIndexed(0) { curr, generated, doc ->
        if (curr % 1000 == 0) System.gc() //gc hint every 100 processed files.
        // ignore first one, then output on every 100 or every fifth when below 1000
        if (isAnkCandidate(doc)) {
          readFile(doc) { contentReader ->
            val preProcessed = preProcessMacros(doc, contentReader)
            val parsedMarkdown = parseMarkdown(preProcessed)
            val snippets = extractCode(parsedMarkdown)
            val compiledResult = compileCode(doc, snippets.b, compilerArgs)
            val result = replaceAnkToLang(snippets.a toT compiledResult)
            generateFile(doc, result)
            val totalHeap = Runtime.getRuntime().totalMemory()
            val usedHeap = totalHeap - Runtime.getRuntime().freeMemory()
            val message = "Ank: Compiled ~> [$curr] ${path.relativize(doc)} | Used Heap: ${usedHeap.humanBytes()}"
            printConsole(colored(ANSI_GREEN, message))
            generated + 1
          }
        } else {
          readFile(doc) { contentReader ->
            generateFile(doc, contentReader)
            generated + 1
          }
        }
      }
      val message = "Ank: Generated $generated files"
      printConsole(colored(ANSI_GREEN, message))
      generated
    }
  }