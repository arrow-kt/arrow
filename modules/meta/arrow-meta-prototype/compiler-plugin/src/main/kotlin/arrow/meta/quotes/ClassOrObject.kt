package arrow.meta.quotes

import arrow.meta.phases.analysis.renderSuperTypes
import arrow.meta.phases.analysis.renderTypeParametersWithVariance
import arrow.meta.phases.analysis.renderValueParameters
import org.jetbrains.kotlin.lexer.KtModifierKeywordToken
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassBody
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtSuperTypeListEntry
import org.jetbrains.kotlin.psi.KtTypeParameter
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.psi.psiUtil.getValueParameters
import org.jetbrains.kotlin.psi.psiUtil.modalityModifierType
import org.jetbrains.kotlin.psi.psiUtil.visibilityModifierType

open class Scope<K : KtElement>(open val value: K) {

  operator fun <K: KtElement> List<Scope<K>>.rangeTo(other: String): Name =
    Name.identifier((map { it.value.text } + other).joinToString(", "))

  override fun toString(): String =
    value.text
}

interface ClassOrObject : Quote<KtElement, KtClass, ClassOrObject.ClassScope> {

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

  override fun transform(ktElement: KtClass): ClassScope =
    ClassScope(ktElement)

  override fun KtClass.cleanUserQuote(quoteDeclaration: String): String =
    quoteDeclaration.trimMargin().let {
      if (isInterface()) it.replace("interface (.*?)\\(\\)".toRegex(), "interface $1")
      else it
    }.replace("<>", "")

  override fun parse(template: String): KtClass =
    quasiQuoteContext.compilerContext.ktPsiElementFactory.createClass(template)

  companion object : Quote.Factory<KtElement, KtClass, ClassScope> {
    override operator fun invoke(
      quasiQuoteContext: QuasiQuoteContext,
      containingDeclaration: KtElement,
      match: KtClass.() -> Boolean,
      map: ClassScope.(quotedTemplate: KtClass) -> List<String>
    ): ClassOrObject =
      object : ClassOrObject {
        override val quasiQuoteContext: QuasiQuoteContext = quasiQuoteContext
        override fun KtClass.match(): Boolean = match(this)
        override fun ClassScope.map(quotedTemplate: KtClass): List<String> = map(quotedTemplate)
        override val containingDeclaration: KtElement = containingDeclaration
      }
  }

}
