package arrow.meta.quotes

import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassBody
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtModifierList
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtSuperTypeListEntry
import org.jetbrains.kotlin.psi.KtTypeParameter
import org.jetbrains.kotlin.psi.psiUtil.getValueParameters
import org.jetbrains.kotlin.psi.psiUtil.modalityModifierType
import org.jetbrains.kotlin.psi.psiUtil.visibilityModifierType

data class ScopedList<K : KtElement>(val value: List<K>, val separator: String = ", ") {
  override fun toString(): String =
    value.joinToString(separator) { it.text }
}

data class ClassBodyScope(
  override val value: KtClassBody,
  override val context: QuasiQuoteContext
) : Scope<KtClassBody>(value, context) {
  override fun toString(): String =
    value.text.drop(1).dropLast(1)
}

class ClassScope(
  override val value: KtClass,
  override val context: QuasiQuoteContext,
  val annotations: ScopedList<KtAnnotationEntry> = ScopedList(value.annotationEntries),
  val modifiers: Scope<KtModifierList>? = value.modifierList?.let {
    Scope(it, context)
  },
  val modality: Name? = value.modalityModifierType()?.value?.let(Name::identifier),
  val visibility: Name? = value.visibilityModifierType()?.value?.let(Name::identifier),
  val kind: Name = Name.identifier(
    (if (value.isSealed()) "sealed " else "")
      + value.getClassOrInterfaceKeyword()?.text.orEmpty()),
  val name: Name? = value.nameAsName,
  val typeParameters: ScopedList<KtTypeParameter> = ScopedList(value.typeParameters),
  val valueParameters: ScopedList<KtParameter> = ScopedList(value.getValueParameters()),
  val supertypes: ScopedList<KtSuperTypeListEntry> = ScopedList(value.superTypeListEntries),
  val body: ClassBodyScope? = value.body?.let { ClassBodyScope(it, context) }
) : Scope<KtClass>(value, context)
