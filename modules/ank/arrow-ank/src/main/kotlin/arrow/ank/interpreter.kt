package arrow.ank

import arrow.Kind
import arrow.core.*
import arrow.data.Nel
import arrow.data.fix
import arrow.effects.typeclasses.MonadDefer
import arrow.instances.list.traverse.sequence
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
import java.util.stream.Collectors
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
  val isOutFile: Boolean = fence.contains(AnkOutFileBlock)
)

const val AnkBlock = ":ank"
const val AnkSilentBlock = ":ank:silent"
const val AnkReplaceBlock = ":ank:replace"
const val AnkOutFileBlock = ":ank:outFile"

val ankMacroRegex: Regex = "ank_macro_hierarchy\\((.*?)\\)".toRegex()

fun <F> monadDeferInterpreter(MF: MonadDefer<F>): AnkOps<F> = object : AnkOps<F> {
  override fun MF(): MonadDefer<F> = MF

  override fun printConsole(msg: String): Kind<F, Unit> = MF.delay { println(msg) }

  override fun createTargetDirectory(source: Path, target: Path): Kind<F, Path> = MF.delay {
    source.toFile().copyRecursively(target.toFile(), overwrite = true); target
  }

  override fun getCandidatePaths(root: Path): Kind<F, Option<Nel<Path>>> = MF.delay {
    Nel.fromList<Path>(
      Files.walk(root).filter { Files.isDirectory(it).not() }.filter { path ->
        SupportedMarkdownExtensions.fold(false) { c, ext ->
          c || path.toString().endsWith(ext)
        }
      }.collect(Collectors.toList())
    )
  }

  override fun readFile(path: Path): Kind<F, String> = MF.delay {
    String(Files.readAllBytes(path))
  }

  override fun preProcessMacros(pathAndContent: Tuple2<Path, String>): String {
    val matches = ankMacroRegex.findAll(pathAndContent.b)
    return if (matches.isEmpty()) pathAndContent.b else {
      val classes = matches.map { it.groupValues[1] }
      val cleanedSource = ankMacroRegex.replace(pathAndContent.b) { "" }
      cleanedSource + "\n\n\n## Type Class Hierarchy\n\n" +
        generateMixedHierarchyDiagramCode(classes.toList()).joinToString("\n")
    }
  }

  override fun parseMarkdown(markdown: String): Kind<F, ASTNode> = MF.delay {
    MarkdownParser(GFMFlavourDescriptor()).buildMarkdownTreeFromString(markdown)
  }

  override fun extractCode(content: String, ast: ASTNode): Kind<F, Option<Nel<Snippet>>> = MF.delay {
    val snippets = mutableListOf<Snippet>()
    ast.accept(object : RecursiveVisitor() {
      override fun visitNode(node: ASTNode) {
        if (node.type == MarkdownElementTypes.CODE_FENCE) {
          val fence = node.getTextInNode(content)
          val lang = fence.takeWhile { it != ':' }.toString().replace("```", "")
          if (fence.startsWith("```$lang$AnkBlock")) {
            val code = fence.split("\n").drop(1).dropLast(1).joinToString("\n")
            snippets.add(Snippet(fence.toString(), lang, node.startOffset, node.endOffset, code))
          }
        }
        super.visitNode(node)
      }
    })

    Nel.fromList(snippets)
  }

  override fun compileCode(snippets: Tuple2<Path, Nel<Snippet>>, compilerArgs: List<String>): Kind<F, Nel<Snippet>> = MF.run {
    binding {
      val engineCache = createEngineCache(snippets.b, compilerArgs).bind()
      // run each snipped and handle its result
      snippets.b.all.mapIndexed { i, snip ->
        binding {
          Try {
            engineCache.getOrElse(snip.lang) {
              throw CompilationException(
                path = snippets.a,
                snippet = snip,
                underlying = IllegalStateException("No engine configured for `${snip.lang}`"),
                msg = colored(ANSI_RED, "ΛNK compilation failed [ ${snippets.a} ]")
              )
            }.eval(snip.code)
          }.fold({
            // raise error and print to console
            defer {
              println(colored(ANSI_RED, "[${(i + 1) * 100 / snippets.b.size}%] ✗ ${snippets.a} [${i + 1} of ${snippets.b.size}]"))
              raiseError<Snippet>(
                CompilationException(snippets.a, snip, it, msg = "\n" + """
                    |
                    |```
                    |${snip.code}
                    |```
                    |${colored(ANSI_RED, it.localizedMessage)}
                    """.trimMargin())
              )
            }.bind()
          }, { result ->
            // handle results, ignore silent snippets
            if (snip.isSilent) snip
            else {
              val resultString: Option<String> = Option.fromNullable(result).fold({ None }, {
                when {
                  // replace entire snippet with result
                  snip.isReplace -> Some("$it")
                  // write result to a new file
                  snip.isOutFile -> delay {
                    val fileName = snip.fence.lines()[0].substringAfter("(").substringBefore(")")
                    val dir = snippets.a.parent
                    Files.write(dir.resolve(fileName), result.toString().toByteArray())
                    Some("")
                  }.bind()
                  // simply append result
                  else -> Some("// $it")
                }
              })
              snip.copy(result = resultString)
            }
          })
        }
      }.sequence(MF)
    }.flatten().map { Nel.fromListUnsafe(it.fix()) }
  }

  override fun replaceAnkToLang(content: String, compiledSnippets: Nel<Snippet>): String =
    compiledSnippets.foldLeft(content) { content, snippet ->
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

  override fun generateFile(path: Path, newContent: String): Kind<F, Unit> = MF.delay {
    Files.write(path, newContent.toByteArray())
    Unit
  }

  private fun createEngineCache(snippets: Nel<Snippet>, compilerArgs: List<String>): Kind<F, Map<String, ScriptEngine>> = MF.run {
    bindingCatch {
      val classLoader = delay { URLClassLoader(compilerArgs.map { URL(it) }.toTypedArray()) }.bind()
      val seManager = delay { ScriptEngineManager(classLoader) }.bind()
      delay {
        snippets.all.asSequence().distinctBy { it.lang }.map {
          it.lang to seManager.getEngineByExtension(extensionMappings.getOrDefault(it.lang, "kts"))
        }.toMap()
      }.bind()
    }
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
}