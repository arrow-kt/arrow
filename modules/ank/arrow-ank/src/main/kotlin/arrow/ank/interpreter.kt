package arrow.ank

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.Try
import arrow.core.Tuple2
import arrow.core.Tuple3
import arrow.core.extensions.sequence.foldable.foldLeft
import arrow.core.some
import arrow.core.toT
import java.io.PrintWriter
import java.io.StringWriter
import java.net.URL
import java.net.URLClassLoader
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import javax.script.ScriptContext
import javax.script.ScriptEngine
import javax.script.ScriptEngineManager

val extensionMappings = mapOf(
  "java" to "java",
  "kotlin" to "kts"
)

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

data class CompilationException(
  val path: Path,
  val snippet: Snippet,
  val underlying: Throwable,
  val msg: String
) : NoStackTrace(msg) {
  override fun toString(): String = msg
}

data class AnkFailedException(val msg: String) : NoStackTrace(msg) {
  override fun toString(): String = msg
}

abstract class NoStackTrace(msg: String) : Throwable(msg, null, false, false)

data class Snippet(
  val fence: String,
  val lang: String,
  val code: String,
  val result: Option<String> = None,
  val isSilent: Boolean = fence.contains(AnkSilentBlock),
  val isReplace: Boolean = fence.contains(AnkReplaceBlock),
  val isOutFile: Boolean = fence.contains(AnkOutFileBlock),
  val isFail: Boolean = fence.contains(AnkFailBlock),
  val isPlayground: Boolean = fence.contains(AnkPlayground),
  val isPlaygroundExtension: Boolean = fence.contains(AnkPlaygroundExtension)
)

const val AnkBlock = ":ank"
const val AnkSilentBlock = ":ank:silent"
const val AnkReplaceBlock = ":ank:replace"
const val AnkOutFileBlock = ":ank:outFile"
const val AnkPlayground = ":ank:playground"
const val AnkFailBlock = ":ank:fail"
const val AnkPlaygroundExtension = ":ank:playground:extension"

val ankMacroRegex: Regex = "ank_macro_hierarchy\\((.*?)\\)".toRegex()

sealed class SnippetParserState {
  data class CollectingCode(val snippet: Snippet) : SnippetParserState()
  object Searching : SnippetParserState()
}

val interpreter: AnkOps = object : AnkOps {

  override suspend fun printConsole(msg: String): Unit {
    println(msg)
  }

  private fun Path.containsAnkSnippets(): Boolean =
    toFile().bufferedReader().use {
      it.lines().anyMatch { s -> s.contains(AnkBlock) || s.contains("ank_macro") }
    }

  override suspend fun Path.ankFiles(): Sequence<AnkProcessingContext> =
    Files.walk(this)
      .filter { Files.isDirectory(it).not() }
      .filter { path ->
        SupportedMarkdownExtensions.fold(false) { c, ext ->
          c || path.toString().endsWith(ext)
        } && path.containsAnkSnippets()
      }.iterator().asSequence().mapIndexed { index, path ->
        AnkProcessingContext(index, path)
      }

  override suspend fun createTargetDirectory(source: Path, target: Path): Path {
    source.toFile().copyRecursively(target.toFile(), overwrite = true)
    return target
  }

  override suspend fun Path.processMacros(): Sequence<String> =
    toFile().useLines { ls ->
      val (classes, lines) =
        ls
          .fold(emptyList<String>() toT emptySequence<String>()) { (classes, lines), line ->
            val cs = ankMacroRegex.findAll(line).map { it.groupValues[1] }
            (classes + cs) toT lines + line
          }
      val cleanedSource = lines.map { ankMacroRegex.replace(it) { "" } }
      if (classes.isNotEmpty()) {
        cleanedSource +
          sequenceOf("\n", "\n", "\n", "## Type Class Hierarchy", "\n", "\n") +
          generateMixedHierarchyDiagramCode(classes.toList())
      } else cleanedSource
    }

  val fenceRegexStart = "```(.*):ank.*".toRegex()
  val fenceRegexEnd = "```.*".toRegex()

  override fun extractCode(content: Sequence<String>): Tuple2<Sequence<String>, Sequence<Snippet>> {
    val result: Tuple3<SnippetParserState, Sequence<String>, Sequence<Snippet>> =
      content
        .fold(
          Tuple3(SnippetParserState.Searching as SnippetParserState, emptySequence(), emptySequence())
        ) { (state: SnippetParserState, lines, snippets), line ->
          when (state) {
            is SnippetParserState.Searching -> {
              val startMatch = fenceRegexStart.matchEntire(line)
              if (startMatch != null) { // found a fence start
                val lang = startMatch.groupValues[1].trim()
                val snippet = Snippet(line, lang, "")
                Tuple3(SnippetParserState.CollectingCode(snippet), lines + line, snippets)
              } else Tuple3(state, lines + line, snippets) // we are still searching
            }
            is SnippetParserState.CollectingCode -> {
              val endMatch = fenceRegexEnd.matchEntire(line)
              if (endMatch != null) { // found a fence end
                Tuple3(SnippetParserState.Searching, lines + line, snippets + state.snippet.copy(fence = state.snippet.fence + "\n" + line))
              } else { // accumulating code inside a fence
                val modifiedSnippet = state.snippet.copy(
                  fence = state.snippet.fence + "\n" + line,
                  code = state.snippet.code + "\n" + line
                )
                Tuple3(state.copy(snippet = modifiedSnippet), lines + line, snippets)
              }
            }
          }
        }
    return result.b toT result.c
  }

  override suspend fun compileCode(snippets: Tuple2<Path, Sequence<Snippet>>, compilerArgs: List<String>): Sequence<Snippet> {
    val engineCache = getEngineCache(snippets.b, compilerArgs)
    // run each snipped and handle its result
    return snippets.b.mapIndexed { i, snip ->
      Try {
        if (snip.isPlaygroundExtension) ""
        else engineCache.getOrElse(snip.lang) {
          throw CompilationException(
            path = snippets.a,
            snippet = snip,
            underlying = IllegalStateException("No engine configured for `${snip.lang}`"),
            msg = colored(ANSI_RED, "ΛNK compilation failed [ ${snippets.a} ]")
          )
        }.eval(snip.code)
      }.fold<Snippet>({
        // raise error and print to console
        if (snip.isFail) {
          val sw = StringWriter()
          val pw = PrintWriter(sw)
          it.printStackTrace(pw)
          snip.copy(result = sw.toString().some())
        } else {
          println(colored(ANSI_RED, "[✗ ${snippets.a} [${i + 1}]"))
          throw CompilationException(snippets.a, snip, it, msg = "\n" + """
                    | File located at: ${snippets.a}
                    |
                    |```
                    |${snip.code}
                    |```
                    |${colored(ANSI_RED, it.localizedMessage)}
                    """.trimMargin())
        }
      }, { result ->
        // handle results, ignore silent snippets
        if (snip.isSilent) snip
        else {
          val resultString: Option<String> = Option.fromNullable(result).fold({ None }, {
            when {
              // replace entire snippet with result
              snip.isReplace -> Some("$it")
              snip.isPlaygroundExtension -> Some("$it")
              // write result to a new file
              snip.isOutFile -> {
                val fileName = snip.fence.lines()[0].substringAfter("(").substringBefore(")")
                val dir = snippets.a.parent
                Files.write(dir.resolve(fileName), result.toString().toByteArray())
                Some("")
              }
              // simply append result
              else -> Some("// $it")
            }
          })
          snip.copy(result = resultString)
        }
      })
    }
  }

  override fun replaceAnkToLang(content: Sequence<String>, compiledSnippets: Sequence<Snippet>): Sequence<String> =
    sequenceOf(compiledSnippets.foldLeft(content.joinToString("\n")) { snippetContent, snippet ->
      snippet.result.fold(
        {
          if (snippet.isPlayground)
            snippetContent.replace(snippet.fence, "{: data-executable='true'}\n\n```${snippet.lang}\n${snippet.code}\n```")
          else
            snippetContent.replace(snippet.fence, "```${snippet.lang}\n${snippet.code}\n```")
        },
        {
          when {
            // these are extensions declared in type classes that should be removed since the extension generator
            // processor is the one in charge of projecting those examples in the generated markdown files
            snippet.isPlaygroundExtension -> snippetContent.replace(snippet.fence, it)
            // a regular playground
            snippet.isPlayground -> snippetContent.replace(snippet.fence, "{: data-executable='true'}\n\n```${snippet.lang}\n${snippet.code}\n$it\n```")
            snippet.isReplace -> snippetContent.replace(snippet.fence, it)
            snippet.isOutFile -> snippetContent.replace(snippet.fence, "")
            else -> snippetContent.replace(snippet.fence, "```${snippet.lang}\n" + snippet.code + "\n" + it + "\n```")
          }
        }
      )
    })

  override suspend fun generateFile(path: Path, newContent: Sequence<String>): Path =
    Files.write(path, newContent.asIterable())

  private val engineCache: ConcurrentMap<List<String>, Map<String, ScriptEngine>> = ConcurrentHashMap()

  private fun getEngineCache(snippets: Sequence<Snippet>, compilerArgs: List<String>): Map<String, ScriptEngine> {
    val cache = engineCache[compilerArgs]
    return if (cache == null) { // create a new engine
      val classLoader = URLClassLoader(compilerArgs.map { URL(it) }.toTypedArray())
      val seManager = ScriptEngineManager(classLoader)
      val langs = snippets.map { it.lang }.distinct()
      val engines = langs.toList().map {
        it to seManager.getEngineByExtension(extensionMappings.getOrDefault(it, "kts"))
      }.toMap()
      engineCache.putIfAbsent(compilerArgs, engines) ?: engines
    } else { // reset an engine. Non thread-safe
      cache.forEach { _, engine ->
        engine.setBindings(engine.createBindings(), ScriptContext.ENGINE_SCOPE)
      }
      cache
    }
  }

  // TODO Try by overriding dokka settings for packages so it does not create it's markdown package file, then for regular type classes pages we only check the first result with the comment but remove them all regardless
  private fun generateMixedHierarchyDiagramCode(classes: List<String>): Sequence<String> {
    // careful meta-meta-programming ahead
    val hierarchyGraphsJoined =
      "listOf(" + classes
        .map { "TypeClass($it::class)" }
        .joinToString(", ") + ").mixedHierarchyGraph()"

    return sequenceOf(
      """<canvas id="hierarchy-diagram"></canvas>""",
      """<script>""",
      """  drawNomNomlDiagram('hierarchy-diagram', 'diagram.nomnol')""",
      """</script>""",
      "",
      """```kotlin:ank:outFile(diagram.nomnol)""",
      """import arrow.reflect.*""",
      hierarchyGraphsJoined,
      """```""""
    )
  }
}
