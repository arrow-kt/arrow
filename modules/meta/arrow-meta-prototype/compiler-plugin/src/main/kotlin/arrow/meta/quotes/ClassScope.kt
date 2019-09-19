package arrow.meta.quotes

import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassBody
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtSuperTypeListEntry
import org.jetbrains.kotlin.psi.KtTypeParameter
import org.jetbrains.kotlin.psi.psiUtil.getValueParameters
import org.jetbrains.kotlin.psi.psiUtil.modalityModifierType
import org.jetbrains.kotlin.psi.psiUtil.visibilityModifierType

class ClassScope(
  override val value: KtClass,
  val modality: Name? = value.modalityModifierType()?.value?.let(Name::identifier),
  val visibility: Name? = value.visibilityModifierType()?.value?.let(Name::identifier),
  val kind: Name = Name.identifier(value.getClassOrInterfaceKeyword()?.text.orEmpty()),
  val name: Name? = value.nameAsName,
  val typeParameters: List<Scope<KtTypeParameter>> = value.typeParameters.map(::Scope),
  val valueParameters: List<Scope<KtParameter>> = value.getValueParameters().map(::Scope),
  val supertypes: List<Scope<KtSuperTypeListEntry>> = value.superTypeListEntries.map(::Scope),
  val body: Scope<KtClassBody>? = value.body?.let(::Scope)
) : Scope<KtClass>(value)
