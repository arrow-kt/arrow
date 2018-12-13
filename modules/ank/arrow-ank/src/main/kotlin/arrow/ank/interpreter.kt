package arrow.ank

import arrow.core.*
import arrow.instances.list.foldable.foldLeft
import arrow.instances.sequence.foldable.isEmpty
import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.accept
import org.intellij.markdown.ast.getTextInNode
import org.intellij.markdown.ast.visitors.RecursiveVisitor
import org.intellij.markdown.flavours.gfm.GFMFlavourDescriptor
import org.intellij.markdown.parser.MarkdownParser
import java.net.URL
import java.net.URLClassLoader
import java.nio.file.Files
import java.nio.file.Path
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
  val msg: String) : NoStackTrace(msg) {
  override fun toString(): String = msg
}

abstract class NoStackTrace(msg: String) : Throwable(msg, null, false, false)

data class Snippet(
  val fence: String,
  val lang: String,
  val startOffset: Int,
  val endOffset: Int,
  val code: String,
  val result: Option<String> = None,
  val isSilent: Boolean = fence.contains(AnkSilentBlock),
  val isReplace: Boolean = fence.contains(AnkReplaceBlock),
  val isOutFile: Boolean = fence.contains(AnkOutFileBlock),
  val isPlayground: Boolean = fence.contains(AnkPlayground),
  val isPlaygroundExtension: Boolean = fence.contains(AnkPlaygroundExtension)
)

const val AnkBlock = ":ank"
const val AnkSilentBlock = ":ank:silent"
const val AnkReplaceBlock = ":ank:replace"
const val AnkOutFileBlock = ":ank:outFile"
const val AnkPlayground = ":ank:playground"
const val AnkPlaygroundExtension = ":ank:playground:extension"

val ankMacroRegex: Regex = "ank_macro_hierarchy\\((.*?)\\)".toRegex()

fun interpreter(): AnkOps = object : AnkOps {

  override fun printConsole(msg: String): Unit = println(msg)

  override fun createTargetDirectory(source: Path, target: Path): Path {
    source.toFile().copyRecursively(target.toFile(), overwrite = true)
    return target
  }

  override fun isAnkCandidate(path: Path): Boolean =
    Files.newBufferedReader(path).useLines { line ->
      line.find { content -> content.contains(AnkBlock) } != null
    }

  override fun <A> readFile(path: Path, f: (String) -> A): A =
    Files.newBufferedReader(path).useLines { f(it.toList().joinToString("\n")) }

  override fun <A> withDocs(root: Path, f: (List<Path>) -> A): A =
      f(Sequence {
        Files.walk(root).filter { Files.isDirectory(it).not() }.filter { path ->
          SupportedMarkdownExtensions.fold(false) { c, ext ->
            c || path.toString().endsWith(ext)
          }
        }.iterator()
      }.toList())

  override fun preProcessMacros(path: Path, content: String): String {
    val matches = ankMacroRegex.findAll(content)
    return if (matches.isEmpty()) content else {
      val classes = matches.map { it.groupValues[1] }
      val cleanedSource = ankMacroRegex.replace(content) { "" }
      cleanedSource + "\n\n\n## Type Class Hierarchy\n\n" +
        generateMixedHierarchyDiagramCode(classes.toList()).joinToString("\n")
    }
  }

  val markDownParser = MarkdownParser(GFMFlavourDescriptor())

  override fun parseMarkdown(markdown: String): Tuple2<String, ASTNode> =
    markdown toT markDownParser.buildMarkdownTreeFromString(markdown)

  override fun extractCode(content: Tuple2<String, ASTNode>): Tuple2<String, List<Snippet>> {
      val snippets = mutableListOf<Snippet>()
      content.b.accept(object : RecursiveVisitor() {
        override fun visitNode(node: ASTNode) {
          if (node.type == MarkdownElementTypes.CODE_FENCE) {
            val fence = node.getTextInNode(content.a)
            val lang = fence.takeWhile { it != ':' }.toString().replace("```", "")
            if (fence.startsWith("```$lang$AnkBlock")) {
              val code = fence.split("\n").drop(1).dropLast(1).joinToString("\n")
              snippets.add(Snippet(fence.toString(), lang, node.startOffset, node.endOffset, code))
            }
          }
          super.visitNode(node)
        }
      })
      return content.a toT snippets.toList()
    }

  override fun compileCode(
    path: Path,
    snippets: List<Snippet>,
    compilerArgs: List<String>
  ): List<Snippet> {
    val engineCache = createEngineCache(snippets, compilerArgs)
    return snippets.mapIndexed { i: Int, snip: Snippet ->
      snip.compile(engineCache, path, i, snippets.size)
    }
  }

  private fun Snippet.compile(
    engineCache: Map<String, ScriptEngine>,
    path: Path,
    i: Int,
    size: Int
  ): Snippet =
    Try {
      eval(engineCache, path)
    }.fold<Snippet>({
      // raise error and print to console
      println(colored(ANSI_RED, "[${(i + 1) * 100 / size}%] ✗ $path [${i + 1} of $size]"))
      throw CompilationException(path, this, it, msg = "\n" + """
                      |
                      |```
                      |$code
                      |```
                      |${colored(ANSI_RED, it.localizedMessage)}
                      """.trimMargin())
    }, { result ->
      // handle results, ignore silent snippets
      if (isSilent) this@compile
      else {
        val resultString: Option<String> = Option.fromNullable(result).fold({ None }, {
          when {
            // replace entire snippet with result
            isReplace -> Some("$it")
            isPlaygroundExtension -> Some("$it")
            // write result to a new file
            isOutFile -> {
              val fileName = fence.lines()[0].substringAfter("(").substringBefore(")")
              val dir = path.parent
              Files.write(dir.resolve(fileName), result.toString().toByteArray())
              Some("")
            }
            // simply append result
            else -> Some("// $it")
          }
        })
        copy(result = resultString)
      }
    })

  private fun Snippet.eval(engineCache: Map<String, ScriptEngine>, path: Path): Any? =
    if (isPlaygroundExtension) ""
    else engineCache.getOrElse(lang) {
      throw CompilationException(
        path = path,
        snippet = this,
        underlying = IllegalStateException("No engine configured for `$lang`"),
        msg = colored(ANSI_RED, "ΛNK compilation failed [ $path ]")
      )
    }.eval(code)

  override fun replaceAnkToLang(compiledSnippets: Tuple2<String, List<Snippet>>): String =
      compiledSnippets.b.foldLeft(compiledSnippets.a) { content, snippet ->
        snippet.result.fold(
          { content.replace(snippet.fence, "{: data-executable='true'}\n\n```${snippet.lang}\n${snippet.code}\n```") },
          {
            when {
              // these are extensions declared in type classes that should be removed since the extension generator
              // processor is the one in charge of projecting those examples in the generated markdown files
              snippet.isPlaygroundExtension -> content.replace(snippet.fence, it)
              // a regular playground
              snippet.isPlayground -> content.replace(snippet.fence, "{: data-executable='true'}\n\n```${snippet.lang}\n${snippet.code}\n$it\n```")
              snippet.isReplace -> content.replace(snippet.fence, it)
              snippet.isOutFile -> content.replace(snippet.fence, "")
              else -> content.replace(snippet.fence, "```${snippet.lang}\n" + snippet.code + "\n" + it + "\n```")
            }
          }
        )
      }

  override fun generateFile(path: Path, newContent: String): Path =
    Files.write(path, newContent.toByteArray())

  private fun createEngineCache(snippets: List<Snippet>, compilerArgs: List<String>): Map<String, ScriptEngine> {
    val langs = snippets.map { s -> s.lang }.distinct()
    val seManager = initializeManager(compilerArgs)
    val engine = langs.map { lang -> lang to initializeScriptEngine(lang, seManager) }.toMap()
    return snippets.distinctBy { it.lang }.map {
      it.lang to engine.getValue(it.lang)
        }.toMap()
  }

  private fun initializeScriptEngine(
    lang: String,
    seManager: ScriptEngineManager
  ): ScriptEngine =
    seManager.getEngineByExtension(extensionMappings.getOrDefault(lang, "kts"))

  private fun initializeManager(compilerArgs: List<String>): ScriptEngineManager =
    ScriptEngineManager(URLClassLoader(compilerArgs.map { URL(it) }.toTypedArray()))

  //TODO Try by overriding dokka settings for packages so it does not create it's markdown package file, then for regular type classes pages we only check the first result with the comment but remove them all regardless
  private fun generateMixedHierarchyDiagramCode(classes: List<String>): List<String> {
    val packageName = classes.firstOrNull()?.substringBeforeLast(".")
    //careful meta-meta-programming ahead
    val hierarchyGraphsJoined =
      "listOf(" + classes
        .map { "TypeClass($it::class)" }
        .joinToString(", ") + ").mixedHierarchyGraph()"

    return listOf(
      """
      |<canvas id="$packageName-hierarchy-diagram"></canvas>
      |<script>
      |  drawNomNomlDiagram('$packageName-hierarchy-diagram', '$packageName-diagram.nomnol')
      |</script>
    """.trimMargin(),
      """
      |```kotlin:ank:outFile($packageName-diagram.nomnol)
      |import arrow.reflect.*
      |$hierarchyGraphsJoined
      |```
      |""".trimMargin())
  }
}
