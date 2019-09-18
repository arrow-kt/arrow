package arrow.meta.phases.analysis

import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtTreeVisitorVoid
import org.jetbrains.kotlin.psi.psiUtil.blockExpressionsOrSingle
import org.jetbrains.kotlin.psi.psiUtil.getValueParameters
import org.jetbrains.kotlin.psi2ir.deparenthesize

fun KtClass.renderValueParameters(): String =
  if (getValueParameters().isEmpty()) ""
  else getValueParameters().joinToString(separator = ", ") { it.text }

fun KtClass.renderSuperTypes(): String =
  superTypeListEntries.joinToString(", ") { it.name.orEmpty() }

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

fun String.removeReturn(): String =
  replace("return ", "")