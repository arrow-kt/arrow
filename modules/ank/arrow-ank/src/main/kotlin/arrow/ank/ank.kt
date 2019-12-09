package arrow.ank

import arrow.Kind
import arrow.core.Either
import arrow.core.ListK
import arrow.core.Nel
import arrow.core.NonEmptyList
import arrow.core.Validated
import arrow.core.ValidatedNel
import arrow.core.combine
import arrow.core.extensions.list.foldable.reduceLeftOption
import arrow.core.extensions.list.semigroup.List.semigroup
import arrow.core.extensions.nonemptylist.semigroup.semigroup
import arrow.core.getOrElse
import arrow.core.toT
import arrow.core.validNel
import arrow.fx.typeclasses.Concurrent
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

fun <A> toValidatedNel(a: Either<Throwable, A>): ValidatedNel<Throwable, A> =
  a.fold({ e -> Validated.Invalid(NonEmptyList(e)) }, { a -> Validated.Valid(a) })

fun <F> Concurrent<F>.ank(source: Path, target: Path, compilerArgs: List<String>, ankOps: AnkOps): Kind<F, Unit> = with(ankOps) {
  fx.concurrent {
    !effect { printConsole(colored(ANSI_PURPLE, AnkHeader)) }
    val heapSize = Runtime.getRuntime().totalMemory()
    val heapMaxSize = Runtime.getRuntime().maxMemory()
    !effect { printConsole("Current heap used: ${(heapSize - Runtime.getRuntime().freeMemory()).humanBytes()}") }
    !effect { printConsole("Starting ank with Heap Size: ${heapSize.humanBytes()}, Max Heap Size: ${heapMaxSize.humanBytes()}") }
    val path = !effect { createTargetDirectory(source, target) }
    val validatedPaths = !effect {
      path.ankFiles().map { (index, p) ->
        effect {
          val totalHeap = Runtime.getRuntime().totalMemory()
          val usedHeap = totalHeap - Runtime.getRuntime().freeMemory()
          val message = "Ank Compile: [$index] ${path.relativize(p)} | Used Heap: ${usedHeap.humanBytes()}"
          printConsole(colored(ANSI_GREEN, message))
          val preProcessed = p.processMacros()
          val (processed, snippets) = extractCode(preProcessed)
          val compiledResult = compileCode(p toT snippets, compilerArgs)
          val result = replaceAnkToLang(processed, compiledResult)
          generateFile(p, result)
        }.attempt().map(::toValidatedNel)
      }.toList()
    }

    // We need to help compiler here a bit.
    val empty: Kind<F, ValidatedNel<Nothing, List<Path>>> = just(emptyList<Path>().validNel())

    val combinedResults = !!effect {
      validatedPaths
        .map { fa -> fa.map { validated -> validated.map { path -> ListK.just(path) } } } // wrap in ListK to accumulate Paths.
        .reduceLeftOption { fa, fb ->
          fa.map2(fb) { (a: Validated<Nel<Throwable>, ListK<Path>>, b: Validated<Nel<Throwable>, ListK<Path>>) ->
            a.combine(Nel.semigroup(), semigroup(), b)
          }
        }.getOrElse { empty }
    }

    !combinedResults.fold({ errors ->
      val seperator = "\n----------------------------------------------------------------\n"
      throw AnkFailedException(errors.all
        .flatMap {
          if (it is CompilationException) listOf(it)
          else emptyList()
        }.joinToString(prefix = seperator, separator = seperator) {
          it.msg
        }
      )
    }, { paths ->
      val message = colored(ANSI_GREEN, "Ank Processed ${paths.size} files")
      effect { printConsole(message) }
    })
  }
}
