package arrow.meta.quotes

import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassBody
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtSuperTypeListEntry
import org.jetbrains.kotlin.psi.KtTypeParameter
import org.jetbrains.kotlin.psi.psiUtil.getValueParameters
import org.jetbrains.kotlin.psi.psiUtil.modalityModifierType
import org.jetbrains.kotlin.psi.psiUtil.visibilityModifierType

data class ScopedList<K: KtElement>(val value: List<K>, val separator: String = ", ") {
  override fun toString(): String =
    value.joinToString(separator) { it.text }
}

class ClassScope(
  override val value: KtClass,
  val modality: Name? = value.modalityModifierType()?.value?.let(Name::identifier),
  val visibility: Name? = value.visibilityModifierType()?.value?.let(Name::identifier),
  val kind: Name = Name.identifier(value.getClassOrInterfaceKeyword()?.text.orEmpty()),
  val name: Name? = value.nameAsName,
  val typeParameters: ScopedList<KtTypeParameter> = ScopedList(value.typeParameters),
  val valueParameters: ScopedList<KtParameter> = ScopedList(value.getValueParameters()),
  val supertypes: ScopedList<KtSuperTypeListEntry> = ScopedList(value.superTypeListEntries),
  val body: Scope<KtClassBody>? = value.body?.let(::Scope)
) : Scope<KtClass>(value)
