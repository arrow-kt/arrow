package arrow.meta.phases.analysis

import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtReturnExpression
import org.jetbrains.kotlin.psi.KtTreeVisitorVoid
import org.jetbrains.kotlin.psi.psiUtil.blockExpressionsOrSingle
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType
import org.jetbrains.kotlin.psi.psiUtil.getValueParameters
import org.jetbrains.kotlin.psi.psiUtil.lastBlockStatementOrThis
import org.jetbrains.kotlin.psi2ir.deparenthesize

fun KtClass.renderValueParameters(): String =
  if (getValueParameters().isEmpty()) ""
  else getValueParameters().joinToString(separator = ", ") { it.text }

fun KtClass.renderSuperTypes(): String =
  superTypeListEntries.joinToString(", ") { it.text }

fun KtClass.renderTypeParametersWithVariance(): String =
  typeParameters.joinToString(separator = ", ") { it.text }

fun KtNamedFunction.renderTypeParametersWithVariance(): String? =
  if (typeParameters.isNotEmpty()) typeParameters.joinToString(separator = ", ") { it.text }
  else null

fun KtNamedFunction.renderValueParameters(): String? =
  if (valueParameters.isEmpty()) null
  else valueParameters.joinToString(separator = ", ") { it.text }


fun KtFunction.body(): KtExpression? =
  bodyExpression ?: bodyBlockExpression

fun KtExpression.bodySourceAsExpression(): String? =
  when (this) {
    is KtBlockExpression -> statements.map {
      when (it) {
        is KtReturnExpression -> it.returnedExpression?.text
        else -> text
      }
    }.joinToString("\n").drop(1).dropLast(1)
    else -> text
  }

fun KtFunction.bodyExpressionText(): String =
  bodyBlockExpression?.blockExpressionsOrSingle()
    ?.joinToString("\n") { it.deparenthesize().text }
    ?.trim()
    ?: bodyExpression?.text
    ?: ""

fun KtElement.dfs(f: (KtElement) -> Boolean): List<KtElement> {
  val found = arrayListOf<KtElement>()
  accept(object : KtTreeVisitorVoid() {
    override fun visitKtElement(element: KtElement) {
      val result = f(element)
      if (result) found.add(element)
      super.visitKtElement(element)
    }
  })
  return found
}

fun KtElement.bfs(f: (KtElement) -> Boolean): List<KtElement> {
  val found = arrayListOf<KtElement>()
  accept(object : KtTreeVisitorVoid() {
    override fun visitKtElement(element: KtElement) {
      super.visitKtElement(element)
      val result = f(element)
      if (result) found.add(element)
    }
  })
  return found
}

fun KtElement.transform(f: (KtElement) -> String): String {
  val builder = StringBuilder()
  accept(object : KtTreeVisitorVoid() {
    override fun visitKtElement(element: KtElement) {
      val result = f(element)
      builder.append(result)
      super.visitKtElement(element)
    }
  })
  return builder.toString()
}

fun String.removeReturn(): String =
  replaceAfterLast("return ", "")

fun KtElement?.countDescendantsOfType(): Int =
  this?.collectDescendantsOfType<KtProperty>()?.size ?: 0
