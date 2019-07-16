package arrow.meta.qq

import arrow.meta.extensions.CompilerContext
import arrow.meta.extensions.ExtensionPhase
import arrow.meta.extensions.MetaComponentRegistrar
import org.jetbrains.kotlin.analyzer.AnalysisResult
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtFile

/**
 * A tree transformation given an existing ktElement
 */
data class Transformation<out K>(
  val oldDescriptor: K,
  val newDescriptor: K
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
   * The element quote scope which includes all permitted matching variables in a given [D] source
   */
  fun scope(): S

  /**
   * Returns a String representation of what a match for a tree may look like. For example:
   * ```
   * "fun <$typeArgs> $name($params): $returnType = $body"
   * ```
   */
  fun S.match(): String

  /**
   * Does the descriptor currently being evaluated match this user applied transformation?
   */
  fun K.matches(transformation: K): Boolean

  /**
   * substitute this scope with new model replacements
   */
  fun substitute(template: String, original: K, transformation: K): S

  /**
   * Given real matches of a [quotedTemplate] the user is then given a chance to transform it into a new tree
   * where also uses code as a template
   */
  fun S.map(quotedTemplate: K): List<String>

  /**
   * Given an old descriptor [D] apply all transformations of [K] over [D] returning a
   * new descriptor
   */
  fun K?.transform(transformation: K): K

  interface Factory<P : KtElement, K : KtElement, S> {
    operator fun invoke(
      quasiQuoteContext: QuasiQuoteContext,
      containingDeclaration: P,
      match: S.() -> String,
      map: S.(quotedTemplate: K) -> List<String>
    ): Quote<P, K, S>
  }

  fun process(descriptor: K): List<Transformation<K>> {
    //user provided match
    val quoteToMatch = scope().match()
    // the template is turned into an expression containing context placeholders
    val quotedExpression = parse(quoteToMatch)
    return if (descriptor.matches(quotedExpression)) {
      println("Descriptor matches: $descriptor, $quotedExpression")
      // a new scope is transformed
      val transformedScope = substitute(quoteToMatch, descriptor, quotedExpression)
      // the user transforms the expression into a new tree
      transformedScope.map(quotedExpression).map { transformedQuote ->
        // the new transformation is turned into an expression
        val transformedExpression = parse(transformedQuote)
        // internally we fill all the context placeholder with the descriptor over the user transformation
        val transformedDescriptor = descriptor.transform(transformedExpression)
        // We return the old and the new descriptor which the synthetic resolution phase takes care of replacing
        Transformation(descriptor, transformedDescriptor)
      }
    } else emptyList()
  }

}

fun MetaComponentRegistrar.classOrObject(
  match: ClassOrObject.ClassScope.() -> String,
  map: ClassOrObject.ClassScope.(KtClass) -> List<String>
): ExtensionPhase.AnalysisHandler =
  quote(ClassOrObject.Companion, match, map)

@Suppress("UNCHECKED_CAST")
inline fun <P : KtElement, reified K : KtElement, S, Q : Quote<P, K, S>> MetaComponentRegistrar.quote(
  quoteFactory: Quote.Factory<P, K, S>,
  noinline match: S.() -> String,
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
  noinline match: S.() -> String,
  noinline map: S.(K) -> List<String>
): List<Pair<KtFile, ArrayList<Transformation<K>>>> {
  return files.map { file ->
    val mutatingDocument = file.viewProvider.document
    val mutations = arrayListOf<Transformation<K>>()
    if (mutatingDocument != null) {
      file.accept(object : MetaTreeVisitor(this) {
        override fun visitKtElement(element: KtElement, data: Unit?): Unit? {
          if (element.javaClass == K::class.java) {
            mutations.addAll(
              quoteFactory(
                quasiQuoteContext = QuasiQuoteContext(compilerContext),
                containingDeclaration = element.psiOrParent as P,
                match = match,
                map = map
              ).process(element as K)
            )
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
  (listOfNotNull(packageDirective) + declarations).joinToString("\n") { ktDeclaration ->
    val transformation = mutations.find { transformation ->
      transformation.oldDescriptor == ktDeclaration
    }
    if (transformation != null) transformation.newDescriptor.text
    else ktDeclaration.text
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