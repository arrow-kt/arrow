package arrow.meta.comprehensions

import arrow.meta.extensions.ExtensionPhase
import arrow.meta.extensions.MetaComponentRegistrar
import arrow.meta.utils.body
import arrow.meta.utils.bodyExpressionText
import arrow.meta.utils.dfs
import arrow.meta.utils.removeReturn
import arrow.meta.qq.func
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.KtUnaryExpression
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/**
 * 1. replace all instances of bind application with flatMap:
 *  - Anonymous application using a generated flatMapArg0, flatMapArg1 etc name for each binding found
 * 2. flatMap replacements takes care scoping all bodies with as many nested flatMap as bindings are found within a body by folding inside out all binds
 */
@ExperimentalContracts
val MetaComponentRegistrar.comprehensions: List<ExtensionPhase>
  get() =
    meta(
      func(KtFunction::hasBindings) { fn ->
        val bindings = fn.bindings()
        if (bindings.isNotEmpty()) {
          listOf(
            """
              |$modality $visibility fun <$typeParameters> $receiver.$name($valueParameters): $returnType =
              |  ${fn.replaceBodyWithFlatMap(bindings).removeReturn()}
              |"""
          )
        } else listOf(fn.text)
      }
    )

@ExperimentalContracts
private fun KtFunction.replaceBodyWithFlatMap(bindings: List<KtElement>): String =
  bindings.foldRightIndexed(bodyExpressionText()) { n, binding, currentBody ->
    val (replacedBody, receiver) = flatMapBodyAndReceiver(binding, currentBody, n)
    "$receiver.flatMap { flatMapArg$n -> $replacedBody }"
  }

@ExperimentalContracts
private fun flatMapBodyAndReceiver(binding: KtElement, currentBody: String, n: Int): Pair<String?, String?> =
  when {
    binding.isBindingCall() -> binding.text.let {
      currentBody.replaceFirst(it, "flatMapArg$n") to
        binding.baseExpression?.text
    }
    else -> null to null
  }

@ExperimentalContracts
private fun KtElement.isBindingCall(): Boolean {
  contract {
    returns() implies (this@isBindingCall is KtUnaryExpression)
  }
  return this is KtUnaryExpression && operationReference.text == "!"
}

@ExperimentalContracts
private fun KtFunction.hasBindings(): Boolean =
  body()?.bindings()?.isNotEmpty() == true

@ExperimentalContracts
private fun KtExpression.bindings(): List<KtElement> =
  dfs { it.isBindingCall() }
