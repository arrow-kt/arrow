package arrow.ank

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.some
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
import kotlin.streams.asSequence

public val extensionMappings: Map<String, String> = mapOf(
  "java" to "java",
  "kotlin" to "kts"
)

public val SupportedMarkdownExtensions: Set<String> = setOf(
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

public data class CompilationException(
  val path: Path,
  val snippet: Snippet,
  val underlying: Throwable,
  val msg: String
) : NoStackTrace(msg) {
  override fun toString(): String = msg
}

public data class AnkFailedException(val msg: String) : NoStackTrace(msg) {
  override fun toString(): String = msg
}

public abstract class NoStackTrace(msg: String) : Throwable(msg, null, false, false)

public data class Snippet(
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

public const val AnkBlock: String = ":ank"
public const val AnkSilentBlock: String = ":ank:silent"
public const val AnkReplaceBlock: String = ":ank:replace"
public const val AnkOutFileBlock: String = ":ank:outFile"
public const val AnkPlayground: String = ":ank:playground"
public const val AnkFailBlock: String = ":ank:fail"
public const val AnkPlaygroundExtension: String = ":ank:playground:extension"

public sealed class SnippetParserState {
  public data class CollectingCode(val snippet: Snippet) : SnippetParserState()
  public object Searching : SnippetParserState()
}

public val interpreter: AnkOps = object : AnkOps {

  public override suspend fun printConsole(msg: String): Unit {
    println(msg)
  }

  private fun Path.containsAnkSnippets(): Boolean =
    toFile().bufferedReader().use {
      it.lines().anyMatch { s -> s.contains(AnkBlock) }
    }

  public override suspend fun Path.ankFiles(): Sequence<AnkProcessingContext> =
    withContext(Dispatchers.IO) { Files.walk(this@ankFiles) }
      .filter { Files.isDirectory(it).not() }
      .filter { path ->
        SupportedMarkdownExtensions.fold(false) { c, ext ->
          c || path.toString().endsWith(ext)
        } && path.containsAnkSnippets()
      }.asSequence().mapIndexed { index, path ->
        AnkProcessingContext(index, path)
      }

  public override suspend fun createTargetDirectory(source: Path, target: Path): Path {
    source.toFile().copyRecursively(target.toFile(), overwrite = true)
    return target
  }

  val fenceRegexStart = "```(.*):ank.*".toRegex()
  val fenceRegexEnd = "```.*".toRegex()

  override fun extractCode(content: Sequence<String>): Pair<Sequence<String>, Sequence<Snippet>> {
    val result: Triple<SnippetParserState, Sequence<String>, Sequence<Snippet>> =
      content
        .fold(
          Triple(SnippetParserState.Searching as SnippetParserState, emptySequence(), emptySequence())
        ) { (state: SnippetParserState, lines, snippets), line ->
          when (state) {
            is SnippetParserState.Searching -> {
              val startMatch = fenceRegexStart.matchEntire(line)
              if (startMatch != null) { // found a fence start
                val lang = startMatch.groupValues[1].trim()
                val snippet = Snippet(line, lang, "")
                Triple(SnippetParserState.CollectingCode(snippet), lines + line, snippets)
              } else Triple(state, lines + line, snippets) // we are still searching
            }
            is SnippetParserState.CollectingCode -> {
              val endMatch = fenceRegexEnd.matchEntire(line)
              if (endMatch != null) { // found a fence end
                Triple(SnippetParserState.Searching, lines + line, snippets + state.snippet.copy(fence = state.snippet.fence + "\n" + line))
              } else { // accumulating code inside a fence
                val modifiedSnippet = state.snippet.copy(
                  fence = state.snippet.fence + "\n" + line,
                  code = state.snippet.code + (if (state.snippet.code.isEmpty()) "" else "\n") + line
                )
                Triple(state.copy(snippet = modifiedSnippet), lines + line, snippets)
              }
            }
          }
        }
    return result.second to result.third
  }

  public override suspend fun compileCode(snippets: Pair<Path, Sequence<Snippet>>, compilerArgs: List<String>): Sequence<Snippet> =
    getEngineCache(snippets.second, compilerArgs).let { engineCache ->
      // run each snipped and handle its result
      snippets.second.mapIndexed { i, snip ->
        val result = try {
          if (snip.isPlaygroundExtension) ""
          else engineCache.getOrElse(snip.lang) {
            throw CompilationException(
              path = snippets.first,
              snippet = snip,
              underlying = IllegalStateException("No engine configured for `${snip.lang}`"),
              msg = colored(ANSI_RED, "ΛNK compilation failed [ ${snippets.first} ]")
            )
          }.eval(snip.code)
        } catch (e: Exception) {
          // raise error and print to console
          if (snip.isFail) {
            val sw = StringWriter()
            val pw = PrintWriter(sw)
            e.printStackTrace(pw)
            return@mapIndexed snip.copy(result = sw.toString().some())
          } else {
            println(colored(ANSI_RED, "[✗ ${snippets.first} [${i + 1}]"))
            throw CompilationException(snippets.first, snip, e, msg = "\n" + """
                    | File located at: ${snippets.first}
                    |
                    |```
                    |${snip.code}
                    |```
                    |${colored(ANSI_RED, e.localizedMessage)}
                    """.trimMargin())
          }
        }

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
                val dir = snippets.first.parent
                Files.write(dir.resolve(fileName), result.toString().toByteArray())
                Some("")
              }
              // simply append result
              else -> Some("// ${it.toString().replace("\n", "\n// ")}")
            }
          })
          snip.copy(result = resultString)
        }
      }
    }

  override fun replaceAnkToLang(content: Sequence<String>, compiledSnippets: Sequence<Snippet>): Sequence<String> =
    sequenceOf(compiledSnippets.fold(content.joinToString("\n")) { snippetContent, snippet ->
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

  public override suspend fun generateFile(path: Path, newContent: Sequence<String>): Path =
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
      cache.forEach { (_, engine) ->
        engine.setBindings(engine.createBindings(), ScriptContext.ENGINE_SCOPE)
      }
      cache
    }
  }
}
