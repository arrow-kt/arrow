package arrow.ank

import arrow.Kind
import arrow.core.toT
import arrow.data.Nel
import arrow.typeclasses.MonadContinuation
import java.nio.file.Path

suspend fun <F, A> MonadContinuation<F, *>.profile(name: String, f: Kind<F, A>): Kind<F, A> {
  val start = System.currentTimeMillis()
  // Get current size of heap in bytes
  val initialHeapSize = Runtime.getRuntime().totalMemory()
  val result = f.bind()
  val heapUsed = initialHeapSize - Runtime.getRuntime().freeMemory()
  val end = (start - System.currentTimeMillis()).toDouble() / 1000.0
  println("[$name] completed in $end seconds. Heap size: ${initialHeapSize.humanBytes()}. Heap used: ${heapUsed.humanBytes()}")
  return just(result)
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

fun <F> ank(source: Path, target: Path, compilerArgs: List<String>, ankOps: AnkOps<F>): Kind<F, Int> = with(ankOps) {
  MF().binding {
    printConsole(colored(ANSI_PURPLE, AnkHeader)).bind()
    val heapSize = Runtime.getRuntime().totalMemory()
    val heapMaxSize = Runtime.getRuntime().maxMemory()
    println("Current heap used: ${(heapSize - Runtime.getRuntime().freeMemory()).humanBytes()}")
    println("Starting ank with Heap Size: ${heapSize.humanBytes()}, Max Heap Size: ${heapMaxSize.humanBytes()}")
    printConsole("")
    val path = profile("createTargetDirectory", createTargetDirectory(source, target)).bind()

    val candidates = profile("getCandidatePaths", getCandidatePaths(path)).bind()
      .fold({ MF().raiseError<Nel<Path>>(Exception("No matching files found")).bind() }, { it })

    val generated = candidates.all.foldIndexed(MF().just(0)) { curr, acc, p ->
      acc.flatMap { generated ->
        MF().binding {
          if (curr % 1000 == 0) System.gc() // hint force GC every 1000 files
          // ignore first one, then output on every 100 or every fifth when below 1000
          val printAnkProgress = (curr % 100 == 0 || (candidates.size < 1000 && curr % 5 == 0))
          if (curr != 0 && printAnkProgress) {
            val totalHeap = Runtime.getRuntime().totalMemory()
            val usedHeap = totalHeap - Runtime.getRuntime().freeMemory()
            val message = "Ank: Processed ~> [$curr of ${candidates.size}] Heap total: ${totalHeap.humanBytes()}. Used Heap: ${usedHeap.humanBytes()} ]"
            printConsole(colored(ANSI_GREEN, message)).bind()
          }

          val content = readFile(p).bind()

          val preProcessed = preProcessMacros(p toT content)

          val parsedMarkdown = parseMarkdown(preProcessed).bind()

          val snippets = extractCode(preProcessed, parsedMarkdown).bind()
            .fold({ return@binding generated }, { it })

          val compiledResult = compileCode(p toT snippets, compilerArgs).bind()

          val result = replaceAnkToLang(preProcessed, compiledResult)

          generateFile(p, result).bind()

          generated + snippets.foldLeft(1) { a, s -> if (s.isOutFile) a + 1 else a }
        }
      }
    }.bind()

    val message = "Ank: Processed ~> [${candidates.size} of ${candidates.size}]"
    printConsole(colored(ANSI_GREEN, message)).bind()

    generated
  }
}