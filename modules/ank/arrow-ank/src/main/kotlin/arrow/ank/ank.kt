package arrow.ank

import arrow.data.ForListK
import arrow.data.ListK
import arrow.data.fix
import arrow.data.k
import arrow.free.Free
import arrow.free.map
import arrow.instances.listk.traverse.traverse
import arrow.typeclasses.Traverse
import org.intellij.markdown.ast.ASTNode
import java.io.File

const val AnkBlock = ":ank"
const val AnkSilentBlock = ":ank:silent"

fun ank(source: File, target: File, compilerArgs: ListK<String>) =
  AnkOps.binding {
    val targetDirectory: File = createTarget(source, target).bind()
    val files: ListK<File> = getFileCandidates(targetDirectory).bind()
    val filesContents: ListK<String> = files.map(::readFile).k().sequence().bind().fix()
    val parsedMarkDowns: ListK<ASTNode> = filesContents.map(::parseMarkdown).k().sequence().bind().fix()
    val allSnippets: ListK<ListK<Snippet>> = parsedMarkDowns.mapIndexed { n, tree ->
      extractCode(filesContents[n], tree)
    }.k().sequence().bind().fix()
    val compilationResults = compileCode(allSnippets.mapIndexed { n, s -> files[n] to s }.toMap(), compilerArgs).bind()
    val replacedResults: ListK<String> = compilationResults.map { c -> replaceAnkToLang(c) }.k().sequence().bind().fix()
    val resultingFiles: ListK<File> = generateFiles(files, replacedResults).bind()
    resultingFiles
  }

fun <A> ListK<Free<ForAnkOps, A>>.sequence(T: Traverse<ForListK> = ListK.traverse()): Free<ForAnkOps, ListK<A>> =
  T.run { sequence(AnkOps) }.map { it.fix() }