package arrow.meta.kt

import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.descriptors.impl.TypeParameterDescriptorImpl
import org.jetbrains.kotlin.descriptors.resolveClassByFqName
import org.jetbrains.kotlin.incremental.components.NoLookupLocation
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtSuperTypeListEntry
import org.jetbrains.kotlin.psi.KtTreeVisitorVoid
import org.jetbrains.kotlin.psi.psiUtil.blockExpressionsOrSingle
import org.jetbrains.kotlin.psi.psiUtil.getValueParameters
import org.jetbrains.kotlin.psi2ir.deparenthesize
import org.jetbrains.kotlin.resolve.descriptorUtil.module
import org.jetbrains.kotlin.types.KotlinTypeFactory
import org.jetbrains.kotlin.types.SimpleType
import org.jetbrains.kotlin.types.TypeConstructor
import org.jetbrains.kotlin.types.TypeProjection
import org.jetbrains.kotlin.types.TypeProjectionImpl
import org.jetbrains.kotlin.types.Variance

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