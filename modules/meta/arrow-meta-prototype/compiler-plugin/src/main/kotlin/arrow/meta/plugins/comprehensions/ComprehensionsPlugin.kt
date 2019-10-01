package arrow.meta.plugins.comprehensions

import arrow.meta.phases.ExtensionPhase
import arrow.meta.MetaComponentRegistrar
import arrow.meta.phases.analysis.bfs
import arrow.meta.phases.analysis.body
import arrow.meta.quotes.func
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtReferenceExpression
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType
import org.jetbrains.kotlin.psi.psiUtil.findDescendantOfType
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

/**
 * 1. replace all instances of bind application with flatMap:
 *  - Anonymous application using a generated flatMapArg0, flatMapArg1 etc name for each binding found
 * 2. flatMap replacements takes care scoping all bodies with as many nested flatMap as bindings are found within a body by folding inside out all binds
 */
val MetaComponentRegistrar.comprehensions: Pair<Name, List<ExtensionPhase>>
  get() =
    Name.identifier("comprehensions") to
      meta(
        func(KtFunction::hasBindings) { ktFunction ->
          listOf(
            """
              |$modality $visibility fun $`(typeParameters)` $receiver $name $`(valueParameters)` $returnType =
              |  ${replaceBindingsWithFlatMap(ktFunction)}
              |"""
          )
        }
      )

private fun replaceBindingsWithFlatMap(ktFunction: KtFunction): String {
  val bindings = ktFunction.fxBlocks()
  val result = bindings.mapIndexed { n, fxBinding ->
    val parentCall = fxBinding.parent.safeAs<KtCallExpression>()
    fun just(value: String) =
      parentCall?.parent?.firstChild?.text?.let { "$it.just($value)" }
        ?: "just($value)"
    println("replaceBindingsWithFlatMap.parentCall.parent: ${parentCall?.parent?.reference}")
    val fxBody = parentCall?.findDescendantOfType<KtBlockExpression>()
    val newSource: List<String> = fxBody?.statements?.foldIndexed(emptyList()) { n, source, expression ->
      if (expression is KtProperty && expression.hasDelegate())
        source + "${expression.delegateExpression?.text}.flatMap { ${expression.name} ${expression.typeReference?.let { ": ${it.text}" } ?: ""} -> "
      else if (n + 1 == fxBody.statements.size) source + just(expression.text)
      else source + expression.text
    } ?: emptyList()
    val bindCount = parentCall?.collectDescendantsOfType<KtProperty>()?.size ?: 0
    (newSource + (0 until bindCount).map { "}" }).joinToString("\n")
  }.joinToString("\n")
  println("replaceBindingsWithFlatMap.result: $result")
  return result
}

private fun KtElement.isBindingCall(): Boolean =
  this is KtReferenceExpression &&
    text == "fx" &&
    parent.findDescendantOfType<KtProperty>()?.hasDelegate() != null

private fun KtFunction.hasBindings(): Boolean =
  body()?.fxBlocks()?.isNotEmpty() == true

private fun KtExpression.fxBlocks(): List<KtReferenceExpression> =
  bfs { it.isBindingCall() }.filterIsInstance<KtReferenceExpression>()
