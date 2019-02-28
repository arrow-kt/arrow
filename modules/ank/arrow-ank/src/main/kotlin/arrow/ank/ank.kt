package arrow.ank

import arrow.Kind
import arrow.core.toT
import arrow.effects.typeclasses.suspended.concurrent.Fx
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

fun <F> Fx<F>.ank(source: Path, target: Path, compilerArgs: List<String>, ankOps: AnkOps): Kind<F, Unit> = with(ankOps) {
  fx {
    !effect { printConsole(colored(ANSI_PURPLE, AnkHeader)) }
    val heapSize = Runtime.getRuntime().totalMemory()
    val heapMaxSize = Runtime.getRuntime().maxMemory()
    !effect { printConsole("Current heap used: ${(heapSize - Runtime.getRuntime().freeMemory()).humanBytes()}") }
    !effect { printConsole("Starting ank with Heap Size: ${heapSize.humanBytes()}, Max Heap Size: ${heapMaxSize.humanBytes()}") }
    val path = !effect { createTargetDirectory(source, target) }
    val paths = !!effect {
      path.ankFiles().map { (index, p) ->
        suspend {
          val totalHeap = Runtime.getRuntime().totalMemory()
          val usedHeap = totalHeap - Runtime.getRuntime().freeMemory()
          val message = "Ank Compile: [$index] ${path.relativize(p)} | Used Heap: ${usedHeap.humanBytes()}"
          printConsole(colored(ANSI_GREEN, message))
          val preProcessed = p.processMacros()
          val (processed, snippets) = extractCode(preProcessed)
          val compiledResult = compileCode(p toT snippets, compilerArgs)
          val result = replaceAnkToLang(processed, compiledResult)
          generateFile(p, result)
        }
      }.toList().sequence()
    }


    val message = "Ank Processed ${paths.size} files"
    !effect { printConsole(colored(ANSI_GREEN, message)) }
  }
}