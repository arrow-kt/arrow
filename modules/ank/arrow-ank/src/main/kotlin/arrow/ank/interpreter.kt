package arrow.ank

import arrow.core.*
import arrow.data.ListK
import arrow.data.k
import arrow.instances.option.monad.forEffect
import org.intellij.markdown.MarkdownElementTypes.CODE_FENCE
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.accept
import org.intellij.markdown.ast.getTextInNode
import org.intellij.markdown.ast.visitors.RecursiveVisitor
import org.intellij.markdown.flavours.gfm.GFMFlavourDescriptor
import org.intellij.markdown.parser.MarkdownParser
import java.io.File
import java.net.URL
import java.net.URLClassLoader
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

fun createTargetImpl(source: File, target: File): File {
  source.copyRecursively(target, overwrite = true)
  return target
}

fun getFileCandidatesImpl(target: File): ListK<File> =
  ListK(target.walkTopDown().filter {
    SupportedMarkdownExtensions.contains(it.extension.toLowerCase())
  }.toList())

fun readFileImpl(source: File): String =
  source.readText()

fun parseMarkDownImpl(markdown: String): ASTNode =
  MarkdownParser(GFMFlavourDescriptor()).buildMarkdownTreeFromString(markdown)

abstract class NoStackTrace(msg: String) : Throwable(msg, null, false, false)

data class CompilationException(
  val file: File,
  val snippet: Snippet,
  val underlying: Throwable,
  val msg: String) : NoStackTrace(msg) {
  override fun toString(): String = msg
}

data class CompiledMarkdown(val origin: File, val snippets: ListK<Snippet>)

private fun String.removeSpaces(): String =
  replace(" ", "")

data class Snippet(
  val fence: String,
  val lang: String,
  val startOffset: Int,
  val endOffset: Int,
  val code: String,
  val result: Option<String> = None,
  val isSilent: Boolean = fence.contains(AnkSilentBlock),
  val isReplace: Boolean = fence.contains(AnkReplaceBlock),
  val isOutFile: Boolean = fence.contains(AnkOutFileBlock))

fun extractCodeImpl(source: String, tree: ASTNode): ListK<Snippet> {
  val sb = mutableListOf<Snippet>()
  tree.accept(object : RecursiveVisitor() {
    override fun visitNode(node: ASTNode) {
      if (node.type == CODE_FENCE) {
        val fence = node.getTextInNode(source)
        val lang = fence.takeWhile { it != ':' }.toString().replace("```", "")
        if (fence.startsWith("```$lang$AnkBlock")) {
          val code = fence.split("\n").drop(1).dropLast(1).joinToString("\n")
          sb.add(Snippet(fence.toString(), lang, node.startOffset, node.endOffset, code))
        }
      }
      super.visitNode(node)
    }
  })
  return sb.k()
}

val ankMacroRegex: Regex = "ank_macro_hierarchy\\((.*?)\\)".toRegex()

fun preProcessMacrosImpl(source: Tuple2<File, String>): String {
  val matches: List<MatchResult> = ankMacroRegex.findAll(source.b).toList()
  val result: String = when {
    matches.isEmpty() -> source.b
    else -> {
      val classes: List<String> = matches.map { it.groupValues[1] }
      val cleanedSource = ankMacroRegex.replace(source.b) { "" }
      cleanedSource +
        "\n\n\n## Type Class Hierarchy\n\n" +
        generateMixedHierarchyDiagramCode(classes).joinToString("\n")
    }

  }
  val originalFile = source.a
  originalFile.writeText(result)
  return result
}

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

private fun generateHierarchyDiagramCode(fqClassName: String): List<String> =
  listOf(
    """
      |<canvas id="hierarchy-diagram"></canvas>
      |<script>
      |  drawNomNomlDiagram('hierarchy-diagram', 'diagram.nomnol')
      |</script>
    """.trimMargin(),
    """
      |```kotlin:ank:outFile(diagram.nomnol)
      |import arrow.reflect.*
      |TypeClass($fqClassName::class).hierarchyGraph()
      |```
      |""".trimMargin())

fun compileCodeImpl(snippets: Map<File, List<Snippet>>, classpath: List<String>): ListK<CompiledMarkdown> {
  val sortedSnippets = snippets.toList()
  val result = sortedSnippets.mapIndexed { n, (file, codeBlocks) ->
    val progress: Int = if (snippets.isNotEmpty()) ((n + 1) * 100 / snippets.size) else 100
    val classLoader = URLClassLoader(classpath.map { URL(it) }.toTypedArray())
    val seManager = ScriptEngineManager(classLoader)
    val engineCache: Map<String, ScriptEngine> =
      codeBlocks
        .asSequence()
        .distinctBy { it.lang }
        .map {
          it.lang to seManager.getEngineByExtension(extensionMappings.getOrDefault(it.lang, "kts"))
        }
        .toList()
        .toMap()
    val evaledSnippets: ListK<Snippet> = codeBlocks.mapIndexed { _, snippet ->
      val result: Any? = Try {
        val engine: ScriptEngine = engineCache.k().getOrElse(
          snippet.lang
        ) {
          throw CompilationException(
            file = file,
            snippet = snippet,
            underlying = IllegalStateException("No engine configured for `${snippet.lang}`"),
            msg = colored(ANSI_RED, "ΛNK compilation failed [ ${file.parentFile.name}/${file.name} ]"))
        }
        val result = engine.eval(snippet.code)
        result
      }.fold({
        println(colored(ANSI_RED, "[$progress%] ✗ ${file.parentFile.name}/${file.name} [${n + 1} of ${snippets.size}]"))
        throw CompilationException(file, snippet, it, msg = "\n" + """
                    |
                    |```
                    |${snippet.code}
                    |```
                    |${colored(ANSI_RED, it.localizedMessage)}
                    """.trimMargin())
      }, { it })
      if (snippet.isSilent) {
        snippet
      } else {
        val resultString: Option<String> = Option.fromNullable(result).fold({ None }, {
          when {
            snippet.isReplace -> Some("$it")
            snippet.isOutFile -> Some("").forEffect(Some(snippet.writeToOutFile(file.parentFile, result.toString())))
            else -> Some("// $it")
          }
        })
        snippet.copy(result = resultString)
      }
    }.k()
    CompiledMarkdown(file, evaledSnippets)
  }.k()
  return result
}

private fun Snippet.writeToOutFile(parent: File, result: String): Unit {
  val fileName = fence.lines()[0].substringAfter("(").substringBefore(")")
  val file = File(parent, fileName)
  file.writeText(result)
}

fun replaceAnkToLangImpl(compiledMarkdown: CompiledMarkdown): String =
  compiledMarkdown.snippets.fold(compiledMarkdown.origin.readText()) { content, snippet ->
    snippet.result.fold(
      { content.replace(snippet.fence, "```${snippet.lang}\n" + snippet.code + "\n```") },
      {
        when {
          snippet.isReplace -> content.replace(snippet.fence, it)
          snippet.isOutFile -> content.replace(snippet.fence, "")
          else -> content.replace(snippet.fence, "```${snippet.lang}\n" + snippet.code + "\n" + it + "\n```")
        }
      }
    )
  }

fun generateFileImpl(file: File, contents: String): File {
  file.printWriter().use {
    it.print(contents)
  }
  return file
}
