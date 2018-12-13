package arrow.ank

import arrow.Kind
import arrow.core.toT
import arrow.data.Nel
import java.nio.file.Path

fun <F> ank(source: Path, target: Path, compilerArgs: List<String>, ankOps: AnkOps<F>): Kind<F, Int> = with(ankOps) {
  MF().binding {
    printConsole(colored(ANSI_PURPLE, AnkHeader)).bind()

    val path = createTargetDirectory(source, target).bind()

    val candidates = getCandidatePaths(path).bind()
      .fold({ MF().raiseError<Nel<Path>>(Exception("No matching files found")).bind() }, { it })

    val generated = candidates.all.foldIndexed(MF().just(0)) { curr, acc, p ->
      acc.flatMap { generated ->
        MF().binding {
          // ignore first one, then output on every 100 or every fifth when below 1000
          val printAnkProgress = (curr % 100 == 0 || (candidates.size < 1000 && curr % 5 == 0))
          if (curr != 0 && printAnkProgress) {
            val message = "Ank: Processed ~> [$curr of ${candidates.size}]"
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