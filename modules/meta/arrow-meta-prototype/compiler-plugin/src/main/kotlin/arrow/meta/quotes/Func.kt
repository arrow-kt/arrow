package arrow.meta.quotes

import arrow.meta.Meta
import arrow.meta.phases.ExtensionPhase
import arrow.meta.phases.analysis.body
import arrow.meta.phases.analysis.bodySourceAsExpression
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtTypeParameter
import org.jetbrains.kotlin.psi.KtTypeReference
import org.jetbrains.kotlin.psi.psiUtil.modalityModifierType
import org.jetbrains.kotlin.psi.psiUtil.visibilityModifierType

fun Meta.func(
  match: KtNamedFunction.() -> Boolean,
  map: FuncScope.(KtNamedFunction) -> Transform<KtNamedFunction>
): ExtensionPhase =
  quote(match, map) { FuncScope(it) }

class FuncScope(
  override val value: KtNamedFunction,
  val modality: Name? = value.modalityModifierType()?.value?.let(Name::identifier),
  val visibility: Name? = value.visibilityModifierType()?.value?.let(Name::identifier),
  val `(typeParameters)`: ScopedList<KtTypeParameter> = ScopedList(prefix = "<", value = value.typeParameters, postfix = ">"),
  val receiver: ScopedList<KtTypeReference> = ScopedList(listOfNotNull(value.receiverTypeReference), postfix = "."),
  val name: Name? = value.nameAsName,
  val `(valueParameters)`: ScopedList<KtParameter> = ScopedList(
    prefix = "(",
    value = value.valueParameters,
    postfix = ")",
    forceRenderSurroundings = true
  ),
  val returnType: ScopedList<KtTypeReference> = ScopedList(listOfNotNull(value.typeReference), prefix = " : "),
  val body: FunctionBodyScope? = value.body()?.let { FunctionBodyScope(it) }
) : Scope<KtNamedFunction>(value)

class FunctionBodyScope(
  override val value: KtExpression
) : Scope<KtExpression>(value) {
  override fun toString(): String =
    value.bodySourceAsExpression() ?: ""
}
