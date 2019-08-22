package arrow.meta.comprehensions

import arrow.meta.extensions.ExtensionPhase
import arrow.meta.extensions.MetaComponentRegistrar
import arrow.meta.qq.func
import arrow.meta.utils.body
import arrow.meta.utils.bodyExpressionText
import arrow.meta.utils.dfs
import arrow.meta.utils.removeReturn
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.KtUnaryExpression
import org.jetbrains.kotlin.psi.psiUtil.findDescendantOfType

/**
 * 1. replace all instances of bind application with flatMap:
 *  - Anonymous application using a generated flatMapArg0, flatMapArg1 etc name for each binding found
 * 2. flatMap replacements takes care scoping all bodies with as many nested flatMap as bindings are found within a body by folding inside out all binds
 */
val MetaComponentRegistrar.comprehensions: List<ExtensionPhase>
  get() =
    meta(
      func(KtFunction::hasBindings) {
        listOf(
          """
              |$modality $visibility fun <$typeParameters> $receiver.$name($valueParameters): $returnType =
              |  ${it.replaceBindingsWithFlatMap(it.bindings())}
              |"""
        )
      }
    )

private fun KtFunction.replaceBindingsWithFlatMap(bindings: List<KtElement>): String =
  bindings.foldRightIndexed(bodyExpressionText()) { n, binding, currentBody ->
    val (replacedBody, receiver) = flatMapBodyAndReceiver(binding, currentBody, n)
    "$receiver.flatMap { flatMapArg$n -> $replacedBody }"
  }.removeReturn()

private fun flatMapBodyAndReceiver(binding: KtElement, currentBody: String, n: Int): Pair<String?, String?> =
  when {
    binding.isBindingCall() && binding is KtUnaryExpression -> binding.text.let {
      currentBody.replaceFirst(it, "flatMapArg$n") to
        binding.baseExpression?.text
    }
    else -> null to null
  }

private fun KtElement.isBindingCall(): Boolean =
  this is KtUnaryExpression && operationReference.getReferencedName() == "!" // TODO enhance with type checks and validation

private fun KtFunction.hasBindings(): Boolean =
  body()?.bindings()?.isNotEmpty() == true

private fun KtExpression.bindings(): List<KtElement> =
  dfs { it.isBindingCall() }.reversed()