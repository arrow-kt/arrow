package arrow.ank

import arrow.core.Validated
import arrow.core.ValidatedNel
import arrow.core.sequenceValidated
import java.nio.file.Path
import kotlin.math.ln
import kotlin.math.pow

/**
 * From https://stackoverflow.com/questions/3758606/how-to-convert-byte-size-into-human-readable-format-in-java
 */
public fun Long.humanBytes(): String {
  val unit = 1024
  if (this < unit) return toString() + " B"
  val exp = (ln(toDouble()) / ln(unit.toDouble())).toInt()
  val pre = ("KMGTPE")[exp - 1] + "i"
  return String.format("%.1f %sB", this / unit.toDouble().pow(exp.toDouble()), pre)
}

public suspend fun ank(source: Path, target: Path, compilerArgs: List<String>, ankOps: AnkOps): Unit = with(ankOps) {
  printConsole(colored(ANSI_PURPLE, AnkHeader))
  val heapSize = Runtime.getRuntime().totalMemory()
  val heapMaxSize = Runtime.getRuntime().maxMemory()
  printConsole("Current heap used: ${(heapSize - Runtime.getRuntime().freeMemory()).humanBytes()}")
  printConsole("Starting ank with Heap Size: ${heapSize.humanBytes()}, Max Heap Size: ${heapMaxSize.humanBytes()}")
  val path = createTargetDirectory(source, target)

  val validatedPaths = path.ankFiles().fold(listOf<ValidatedNel<Throwable, Path>>()) { acc, file ->
    val res = Validated.catchNel {
      val totalHeap = Runtime.getRuntime().totalMemory()
      val usedHeap = totalHeap - Runtime.getRuntime().freeMemory()
      val p = file.path
      val message = "Ank Compile: [${file.index}] ${path.relativize(p)} | Used Heap: ${usedHeap.humanBytes()}"
      printConsole(colored(ANSI_GREEN, message))
      val lines = p.toFile().readLines().asSequence()
      val (processed, snippets) = extractCode(lines)
      val compiledResult = compileCode(p to snippets, compilerArgs)
      val result = replaceAnkToLang(processed, compiledResult)
      val generatedPath = generateFile(p, result)
      generatedPath
    }

    acc + res
  }.sequenceValidated()

  validatedPaths.fold({ errors ->
    val separator = "\n----------------------------------------------------------------\n"
    throw AnkFailedException(errors.all
      .flatMap {
        if (it is CompilationException) listOf(it)
        else emptyList()
      }.joinToString(prefix = separator, separator = separator) {
        it.msg
      })
  }, { paths ->
    val message = colored(ANSI_GREEN, "Ank Processed ${paths.size} files")
    printConsole(message)
  })
}
