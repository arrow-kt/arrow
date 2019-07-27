package arrow.meta.qq

import arrow.meta.extensions.CompilerContext
import arrow.meta.extensions.ExtensionPhase
import arrow.meta.extensions.MetaComponentRegistrar
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.PsiWhiteSpaceImpl
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtFunction

/**
 * A tree transformation given an existing ktElement
 */
data class Transformation<out K>(
  val oldDescriptor: K,
  val newDeclarations: List<KtDeclaration>
)

/**
 * A declaration quasi quote matches tree in the synthetic resolution and gives
 * users the chance to transform them before they are processed by the Kotlin compiler.
 */
interface Quote<P : KtElement, K : KtElement, S> {

  val containingDeclaration: P

  /**
   * Provides access to compiler context services and factories including the binding trace
   */
  val quasiQuoteContext: QuasiQuoteContext

  /**
   * Turn a string template into a [KtElement]
   */
  fun parse(template: String): K

  /**
   * Returns a String representation of what a match for a tree may look like. For example:
   * ```
   * "fun <$typeArgs> $name($params): $returnType = $body"
   * ```
   */
  fun K.match(): Boolean

  /**
   * Given real matches of a [quotedTemplate] the user is then given a chance to transform it into a new tree
   * where also uses code as a template
   */
  fun S.map(quotedTemplate: K): List<String>

  interface Factory<P : KtElement, K : KtElement, S> {
    operator fun invoke(
      quasiQuoteContext: QuasiQuoteContext,
      containingDeclaration: P,
      match: K.() -> Boolean,
      map: S.(quotedTemplate: K) -> List<String>
    ): Quote<P, K, S>
  }

  fun transform(ktElement: K): S

  fun K.cleanUserQuote(quoteDeclaration: String): String = quoteDeclaration

  fun process(ktElement: K): Transformation<K>? {
    return if (ktElement.match()) {
      // a new scope is transformed
      val transformedScope = transform(ktElement)
      // the user transforms the expression into a new list of declarations
      val declarations = transformedScope.map(ktElement).map { quoteDeclaration ->
        val declaration =
          quasiQuoteContext.compilerContext.ktPsiElementFactory
            .createDeclaration<KtDeclaration>(ktElement.cleanUserQuote(quoteDeclaration))
        declaration
      }
      if (declarations.isEmpty()) null
      else Transformation(ktElement, declarations)
    } else null
  }

}

fun MetaComponentRegistrar.func(
  match: KtFunction.() -> Boolean,
  map: Func.FuncScope.(KtFunction) -> List<String>
): ExtensionPhase.AnalysisHandler =
  quote(Func.Companion, match, map)

fun MetaComponentRegistrar.classOrObject(
  match: KtClass.() -> Boolean,
  map: ClassOrObject.ClassScope.(KtClass) -> List<String>
): ExtensionPhase.AnalysisHandler =
  quote(ClassOrObject.Companion, match, map)

@Suppress("UNCHECKED_CAST")
inline fun <P : KtElement, reified K : KtElement, S, Q : Quote<P, K, S>> MetaComponentRegistrar.quote(
  quoteFactory: Quote.Factory<P, K, S>,
  noinline match: K.() -> Boolean,
  noinline map: S.(K) -> List<String>
): ExtensionPhase.AnalysisHandler =
  analysys(
    doAnalysis = { project, module, projectContext, files, bindingTrace, componentProvider ->
      files as ArrayList
      //files.clear()
      val fileMutations = processFiles(files, quoteFactory, match, map)
      files.updateFiles(fileMutations)
      null
    },
    analysisCompleted = { project, module, bindingTrace, files ->
      null
    }
  )

@Suppress("UNCHECKED_CAST")
inline fun <reified K : KtElement, P : KtElement, S> CompilerContext.processFiles(
  files: Collection<KtFile>,
  quoteFactory: Quote.Factory<P, K, S>,
  noinline match: K.() -> Boolean,
  noinline map: S.(K) -> List<String>
): List<Pair<KtFile, ArrayList<Transformation<K>>>> {
  return files.map { file ->
    val mutatingDocument = file.viewProvider.document
    val mutations = arrayListOf<Transformation<K>>()
    if (mutatingDocument != null) {
      file.accept(object : MetaTreeVisitor(this) {
        override fun visitKtElement(element: KtElement, data: Unit?): Unit? {
          println("visitKtElement: ${element.javaClass}")
          if (element.javaClass == K::class.java) {
            println("found element: ${element.javaClass}")
            val transformation = quoteFactory(
              quasiQuoteContext = QuasiQuoteContext(compilerContext),
              containingDeclaration = element.psiOrParent as P,
              match = match,
              map = map
            ).process(element as K)
            transformation?.let { mutations.add(it) }
          }
          return super.visitKtElement(element, data)
        }
      })
    }
    file to mutations
  }
}

inline fun <reified K : KtElement> java.util.ArrayList<KtFile>.updateFiles(fileMutations: List<Pair<KtFile, java.util.ArrayList<Transformation<K>>>>) {
  fileMutations.forEach { (file, mutations) ->
    if (mutations.isNotEmpty()) {
      val newSource = file.sourceWithTransformations(mutations)
      val newFile = changeSource(file, newSource)
      replaceFiles(file, newFile)
      file.printDiff(newFile)
    }
  }
}

inline fun <reified K : KtElement> KtFile.sourceWithTransformations(mutations: ArrayList<Transformation<K>>): String =
  (listOfNotNull(packageDirective) +
    importDirectives +
    declarations).joinToString("\n") { ktDeclaration ->
    val transformation = mutations.find { transformation ->
      transformation.oldDescriptor == ktDeclaration
    }
    transformation?.newDeclarations?.joinToString("\n\n") { it.text } ?: ktDeclaration.text
  }

fun KtFile.printDiff(newFile: KtFile) {
  println("""
              |
              |----------------------------------
              |*Tree Mutation*
              |----------------------------------
              |Old 
              |---
              |$text
              |---
              |New
              |---
              |${newFile.text}
              |---
              |----------------------------------
              |
              |""".trimMargin())
}

fun java.util.ArrayList<KtFile>.replaceFiles(file: KtFile, newFile: KtFile) {
  val fileIndex = indexOf(file)
  removeAt(fileIndex)
  add(fileIndex, newFile)
}

fun changeSource(file: KtFile, newSource: String): KtFile {
  return KtFile(
    viewProvider = MetaFileViewProvider(file.manager, file.virtualFile) {
      it?.also {
        it.setText(newSource)
      }
    },
    isCompiled = false
  )
}