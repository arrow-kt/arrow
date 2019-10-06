package arrow.meta.quotes

import arrow.meta.Meta
import arrow.meta.dsl.platform.cli
import arrow.meta.dsl.platform.ide
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.ExtensionPhase
import arrow.meta.phases.analysis.MetaFileViewProvider
import arrow.meta.phases.analysis.dfs
import kastree.ast.MutableVisitor
import kastree.ast.Node
import kastree.ast.Writer
import kastree.ast.psi.Converter
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.PackageViewDescriptor
import org.jetbrains.kotlin.js.resolve.diagnostics.findPsi
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtPropertyDelegate
import org.jetbrains.kotlin.psi.psiUtil.findDescendantOfType
import org.jetbrains.kotlin.utils.addToStdlib.safeAs
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

/**
 * A declaration quasi quote matches tree in the synthetic resolution and gives
 * users the chance to transform them before they are processed by the Kotlin compiler.
 */
interface Quote<P : KtElement, K : KtElement, S> {

  val containingDeclaration: P

  /**
   * Returns a String representation of what a match for a tree may look like. For example:
   * ```
   * "fun <$typeArgs> $name($params): $returnType = $body"
   * ```
   */
  fun K.match(): Boolean

  /**
   * Given real matches of a [quotedTemplate] the user is then given a chance to replace them with new trees
   * where also uses code as a template
   */
  fun S.map(quotedTemplate: K): Transform<K>

  interface Factory<P : KtElement, K : KtElement, S> {
    operator fun invoke(
      containingDeclaration: P,
      match: K.() -> Boolean,
      map: S.(quotedTemplate: K) -> Transform<K>
    ): Quote<P, K, S>
  }

  fun transform(ktElement: K): S

  fun process(ktElement: K): Transform<K>? {
    return if (ktElement.match()) {
      // a new scope is transformed
      val transformedScope = transform(ktElement)
      // the user transforms the expression into a new list of declarations
      transformedScope.map(ktElement)
    } else null
  }

}

fun String.metaUniqueReplacementId(): String =
  randomAlphaNumeric(length)

private val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
fun randomAlphaNumeric(count: Int): String {
  var count = count
  val builder = StringBuilder()
  while (count-- != 0) {
    val character = (Math.random() * chars.length).toInt()
    builder.append(chars[character])
  }
  return builder.toString()
}


class QuoteFactory<K : KtElement, S : Scope<K>>(
  val transform: (K) -> S
) : Quote.Factory<KtElement, K, S> {
  override operator fun invoke(
    containingDeclaration: KtElement,
    match: K.() -> Boolean,
    map: S.(quotedTemplate: K) -> Transform<K>
  ): Quote<KtElement, K, S> =
    object : Quote<KtElement, K, S> {
      override fun K.match(): Boolean = match(this)
      override fun S.map(quotedTemplate: K): Transform<K> = map(quotedTemplate)
      override val containingDeclaration: KtElement = containingDeclaration
      override fun transform(ktElement: K): S = this@QuoteFactory.transform(ktElement)
    }
}

inline fun <reified K : KtElement> Meta.quote(
  noinline match: K.() -> Boolean,
  noinline map: Scope<K>.(K) -> Transform<K>
): ExtensionPhase =
  quote(match, map) { Scope(it) }

inline fun <reified K : KtElement, S : Scope<K>> Meta.quote(
  noinline match: K.() -> Boolean,
  noinline map: S.(K) -> Transform<K>,
  noinline transform: (K) -> S
): ExtensionPhase =
  quote(QuoteFactory(transform), match, map)

@Suppress("UNCHECKED_CAST")
inline fun <P : KtElement, reified K : KtElement, S> Meta.quote(
  quoteFactory: Quote.Factory<P, K, S>,
  noinline match: K.() -> Boolean,
  noinline map: S.(K) -> Transform<K>
): ExtensionPhase =
  cli {
    analysys(
      doAnalysis = { project, module, projectContext, files, bindingTrace, componentProvider ->
        files as ArrayList
        println("START quote.doAnalysis: $files")
        val fileMutations = processFiles(files, quoteFactory, match, map)
        updateFiles(files, fileMutations)
        println("END quote.doAnalysis: $files")
        files.forEach {
          val fileText = it.text
          if (fileText.contains("//metadebug")) {
            File(it.virtualFilePath + ".meta").writeText(it.text.replaceFirst("//metadebug", "//meta: ${Date()}"))
            println("""|
            |ktFile: $it
            |----
            |${it.text}
            |----
          """.trimMargin())
          }
        }
        null
      },
      analysisCompleted = { project, module, bindingTrace, files ->
        null
      }
    )
  } ?: ExtensionPhase.Empty

fun PackageViewDescriptor.declarations(): Collection<DeclarationDescriptor> =
  memberScope.getContributedDescriptors { true }

fun DeclarationDescriptor.ktFile(): KtFile? =
  findPsi()?.containingFile.safeAs()

fun ClassDescriptor.ktClassOrObject(): KtClassOrObject? =
  findPsi() as? KtClassOrObject

fun KtClassOrObject.nestedClassNames(): List<String> =
  declarations.filterIsInstance<KtClassOrObject>().mapNotNull { it.name }

fun KtFile.ktClassNamed(name: String?): KtClass? =
  name?.let {
    findDescendantOfType { d -> d.name == it }
  }

fun KtClassOrObject.functionNames(): List<Name> =
  declarations.filterIsInstance<KtFunction>().mapNotNull { it.name }.map(Name::identifier)

@Suppress("UNCHECKED_CAST")
inline fun <reified K : KtElement, P : KtElement, S> processFiles(
  files: Collection<KtFile>,
  quoteFactory: Quote.Factory<P, K, S>,
  noinline match: K.() -> Boolean,
  noinline map: S.(K) -> Transform<K>
): List<Pair<KtFile, ArrayList<Transform<K>>>> {
  return files.map { file ->
    processKtFile(file, quoteFactory, match, map)
  }
}

@Suppress("UNCHECKED_CAST")
inline fun <reified K : KtElement, P : KtElement, S> processKtFile(
  file: KtFile,
  quoteFactory: Quote.Factory<P, K, S>,
  noinline match: K.() -> Boolean,
  noinline map: S.(K) -> Transform<K>
): Pair<KtFile, ArrayList<Transform<K>>> {
  val mutatingDocument = file.viewProvider.document
  val mutations = arrayListOf<Transform<K>>()
  if (mutatingDocument != null) {
    val matches = file.dfs { element ->
      val result = K::class.java.isAssignableFrom(element.javaClass)
      result
    }
    matches.forEach { element ->
      val transformation = quoteFactory(
        containingDeclaration = element.psiOrParent as P,
        match = match,
        map = map
      ).process(element as K)
      transformation?.let { mutations.add(it) }
    }
//    file.accept(object : MetaTreeVisitor() {
//      override fun visitKtElement(element: KtElement) {
//
//        return super.visitKtElement(element)
//      }
//    })
  }
  return file to mutations
}

inline fun <reified K : KtElement> CompilerContext.updateFiles(
  result: java.util.ArrayList<KtFile>,
  fileMutations: List<Pair<KtFile, java.util.ArrayList<Transform<K>>>>
) {
  fileMutations.forEach { (file, mutations) ->
    val newFile = updateFile(mutations, file)
    result.replaceFiles(file, newFile)
  }
}

inline fun <reified K : KtElement> CompilerContext.updateFile(
  mutations: java.util.ArrayList<Transform<K>>,
  file: KtFile
): KtFile =
  if (mutations.isNotEmpty()) {
    transformFile(file, mutations)
  } else file

inline fun <reified K : KtElement> CompilerContext.transformFile(
  ktFile: KtFile,
  mutations: java.util.ArrayList<Transform<K>>
): KtFile {
  val newSource = ktFile.sourceWithTransformationsAst(mutations)
  val newFile = newSource?.let { changeSource(ktFile, it) } ?: ktFile
  println("Transformed file: $ktFile. New contents: \n$newSource")
  return newFile
}

fun <K : KtElement> KtFile.sourceWithTransformationsAst(mutations: ArrayList<Transform<K>>): String? {
  var dummyFile = Converter.convertFile(this)
  mutations.forEach { transform ->
    when (transform) {
      is Transform.Replace -> {
        val replacingNode = when {
          transform.replacing is KtClassOrObject -> Converter.convertDecl(transform.replacing)
          transform.replacing is KtNamedFunction -> Converter.convertFunc(transform.replacing)
          transform.replacing is KtExpression -> Converter.convertExpr(transform.replacing)
          else -> TODO("Unsupported ${transform.replacing}")
        }
        dummyFile = MutableVisitor.preVisit(dummyFile) { element, _ ->
          if (element != null && element == replacingNode) {
            val newContents = transform.newDeclarations.joinToString("\n") { it.value?.text ?: "" }
            println("Replacing ${element.javaClass} with ${transform.newDeclarations.map { it.value?.javaClass }}: newContents: \n$newContents")
            element.dynamic = newContents
            element
          } else element
        }
        Unit
      }
      Transform.Empty -> Unit
    }
  }
  return Writer.write(dummyFile)
}

fun <K : KtElement> KtFile.sourceWithTransformations(mutations: ArrayList<Transform<K>>): String? {
  val (taggedSource, replacements) =
    mutations.fold(text to emptyList<Transform<K>>()) { (source, taggedReplacements), quoteResult ->
      when (quoteResult) {
        is Transform.Replace -> {
          quoteResult.replacing.text?.metaUniqueReplacementId()?.let { replacementId ->
            quoteResult.replacing.textRange?.replace(source, replacementId)?.let { newSource ->
              val replacement = quoteResult.copy(replacementId = replacementId)
              newSource to (taggedReplacements + replacement)
            }
          } ?: text to emptyList()
        }
        is Transform.Empty -> source to emptyList()
      }
    }
  val replacedSource =
    replacements.fold(taggedSource) { source, quoteResult ->
      when (quoteResult) {
        is Transform.Replace -> {
          quoteResult.replacementId?.let { replacementId ->
            quoteResult.replacing.textRange?.replace(text, replacementId)
              ?.replaceFirst(replacementId, quoteResult.newDeclarations.joinToString("\n\n"))
          }
        }
        is Transform.Empty -> text
      }
    }
  return replacedSource
}


fun java.util.ArrayList<KtFile>.replaceFiles(file: KtFile, newFile: KtFile) {
  val fileIndex = indexOf(file)
  removeAt(fileIndex)
  add(fileIndex, newFile)
}

fun CompilerContext.changeSource(file: KtFile, newSource: String): KtFile =
  cli {
    KtFile(
      viewProvider = MetaFileViewProvider(file.manager, file.virtualFile) {
        it?.also {
          it.setText(newSource)
        }
      },
      isCompiled = false
    )
  } ?: ide {
    ktPsiElementFactory.createAnalyzableFile("_meta_${file.name}", newSource, file)
  }!!

@Suppress("UNCHECKED_CAST")
inline operator fun <reified A, B> A.get(field: String): B {
  val clazz = A::class.java
  return try {
    clazz.getDeclaredField(field).also { it.isAccessible = true }.get(this) as B
  } catch (e: Exception) {
    clazz.getField(field).also { it.isAccessible = true }.get(this) as B
  }
}

fun KtFile.isMetaFile(): Boolean =
  name.startsWith("_meta_")
