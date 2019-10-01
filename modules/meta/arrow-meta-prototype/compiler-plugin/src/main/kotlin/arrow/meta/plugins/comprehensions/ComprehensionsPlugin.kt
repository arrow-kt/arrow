package arrow.meta.plugins.comprehensions

import arrow.meta.MetaComponentRegistrar
import arrow.meta.phases.ExtensionPhase
import arrow.meta.phases.analysis.bfs
import arrow.meta.phases.analysis.body
import arrow.meta.phases.analysis.countDescendantsOfType
import arrow.meta.quotes.func
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtReferenceExpression
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
              |  ${ktFunction.replaceBindingsWithFlatMap()}
              |"""
          )
        }
      )

private fun KtFunction.replaceBindingsWithFlatMap(): String =
  fxBlocks().joinToString("\n", transform = KtReferenceExpression::replaceBindingsWithFlatMap)

private fun KtReferenceExpression.replaceBindingsWithFlatMap(): String {
  val parentCall = parent.safeAs<KtCallExpression>()
  println("replaceBindingsWithFlatMap.parentCall.parent: ${parentCall?.parent?.reference}")
  val fxBody = parentCall?.findDescendantOfType<KtBlockExpression>()
  val newSource: List<String> = fxBody.replaceBindingsWithFlatMap(parentCall)
  return parentCall.encloseFlatMapBodies(newSource)
}

private fun KtCallExpression?.encloseFlatMapBodies(newSource: List<String>): String {
  val bindCount = countDescendantsOfType()
  return (newSource + (0 until bindCount).map { "}" }).joinToString("\n")
}

private fun KtBlockExpression?.replaceBindingsWithFlatMap(parentCall: KtCallExpression?): List<String> =
  this?.statements?.foldIndexed(emptyList()) { n, source, expression ->
    when {
      expression is KtProperty && expression.hasDelegate() ->
        source + expression.boundPropertyToFlatMap()
      n + 1 == statements.size ->
        source + parentCall.just(expression.text)
      else ->
        source + expression.text
    }
  } ?: emptyList()

private fun KtProperty.boundPropertyToFlatMap(): String =
  "${delegateExpression?.text}.flatMap { $name ${typeReference?.let { ": ${it.text}" }
    ?: ""} -> "

fun KtCallExpression?.just(value: String): String =
  this?.parent?.firstChild?.text?.let { "$it.just($value)" }
    ?: "just($value)"

private fun KtElement.isBindingCall(): Boolean =
  this is KtReferenceExpression &&
    text == "fx" &&
    parent.findDescendantOfType<KtProperty>()?.hasDelegate() != null

private fun KtFunction.hasBindings(): Boolean =
  body()?.fxBlocks()?.isNotEmpty() == true

private fun KtExpression.fxBlocks(): List<KtReferenceExpression> =
  bfs { it.isBindingCall() }.filterIsInstance<KtReferenceExpression>()
