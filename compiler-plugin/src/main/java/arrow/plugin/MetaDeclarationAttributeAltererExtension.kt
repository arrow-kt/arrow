package arrow.plugin

import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.extensions.DeclarationAttributeAltererExtension
import org.jetbrains.kotlin.psi.KtModifierListOwner
import org.jetbrains.kotlin.resolve.BindingContext

class MetaDeclarationAttributeAltererExtension : DeclarationAttributeAltererExtension {
  override fun refineDeclarationModality(modifierListOwner: KtModifierListOwner, declaration: DeclarationDescriptor?, containingDeclaration: DeclarationDescriptor?, currentModality: Modality, bindingContext: BindingContext, isImplicitModality: Boolean): Modality? {
    println("DeclarationAttributeAltererExtension.refineDeclarationModality: $declaration")
    return super.refineDeclarationModality(modifierListOwner, declaration, containingDeclaration, currentModality, bindingContext, isImplicitModality)
  }
}
