package arrow.ank

import arrow.Kind
import arrow.core.toT
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

fun <F> ank(source: Path, target: Path, compilerArgs: List<String>, ankOps: AnkOps<F>): Kind<F, Unit> = with(ankOps) {
  MF().fx {
    printConsole(colored(ANSI_PURPLE, AnkHeader))
    val heapSize = Runtime.getRuntime().totalMemory()
    val heapMaxSize = Runtime.getRuntime().maxMemory()
    printConsole("Current heap used: ${(heapSize - Runtime.getRuntime().freeMemory()).humanBytes()}")
    printConsole("Starting ank with Heap Size: ${heapSize.humanBytes()}, Max Heap Size: ${heapMaxSize.humanBytes()}")
    val path = createTargetDirectory(source, target)
    val paths = path.withAnkFiles { (index, p) ->
      val totalHeap = Runtime.getRuntime().totalMemory()
      val usedHeap = totalHeap - Runtime.getRuntime().freeMemory()
      val message = "Ank Compile: [$index] ${path.relativize(p)} | Used Heap: ${usedHeap.humanBytes()}"
      printConsole(colored(ANSI_GREEN, message))
      val preProcessed = p.processMacros()
      val (processed, snippets) = extractCode(preProcessed)
      val compiledResult = compileCode(p toT snippets, compilerArgs)
      val result = replaceAnkToLang(processed, compiledResult)
      generateFile(p, result)
    }.toList()

    val message = "Ank Processed ${paths.size} files"
    printConsole(colored(ANSI_GREEN, message))
  }
}