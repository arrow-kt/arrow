package io.kategory.ank

import kategory.*
import org.intellij.markdown.ast.ASTNode
import java.io.File

const val AnkBlock = ":ank"
const val AnkSilentBlock = ":ank:silent"

fun ank(source: File, target: File, compilerArgs: ListKW<String>) =
        AnkOps.binding {
            val targetDirectory: File = createTarget(source, target).bind()
            val files: ListKW<File> = getFileCandidates(targetDirectory).bind()
            val filesContents: ListKW<String> = files.map(::readFile).k().sequence().bind()
            val parsedMarkDowns: ListKW<ASTNode> = filesContents.map(::parseMarkdown).k().sequence().bind()
            val allSnippets: ListKW<ListKW<Snippet>> = parsedMarkDowns.mapIndexed { n, tree ->
                extractCode(filesContents.list[n], tree)
            }.k().sequence().bind()
            val compilationResults =
                    ListKW(allSnippets.mapIndexed { n, s -> compileCode(files.list[n], s, compilerArgs) }).k().sequence().bind()
            val replacedResults: ListKW<String> = compilationResults.map { c -> replaceAnkToLang(c) }.k().sequence().bind()
            val resultingFiles: ListKW<File> = generateFiles(files, replacedResults).bind()
            yields(resultingFiles)
        }

fun <A> ListKW<Free<AnkOpsHK, A>>.sequence(T: Traverse<ListKWHK> = ListKW.traverse()): Free<AnkOpsHK, ListKW<A>> =
        T.sequence(this, AnkOps).ev().map { it.ev() }