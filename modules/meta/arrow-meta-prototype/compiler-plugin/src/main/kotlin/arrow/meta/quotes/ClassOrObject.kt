package arrow.meta.quotes

import arrow.meta.phases.analysis.renderSuperTypes
import arrow.meta.phases.analysis.renderTypeParametersWithVariance
import arrow.meta.phases.analysis.renderValueParameters
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.psiUtil.modalityModifierType
import org.jetbrains.kotlin.psi.psiUtil.visibilityModifierType

interface ClassOrObject : Quote<KtElement, KtClass, ClassOrObject.ClassScope> {

  class ClassScope(
    val modality: Name,
    val visibility: Name,
    val kind: Name,
    val name: Name,
    val typeParameters: Name,
    val valueParameters: Name,
    val supertypes: Name,
    val body: Name
  )

  override fun transform(ktElement: KtClass): ClassScope =
    ClassScope(
      modality = Name.identifier(ktElement.modalityModifierType()?.value.orEmpty()),
      visibility = Name.identifier(ktElement.visibilityModifierType()?.value.orEmpty()),
      kind = Name.identifier(ktElement.getClassOrInterfaceKeyword()?.text.orEmpty()),
      name = Name.identifier(ktElement.name ?: ""),
      typeParameters = Name.identifier(ktElement.renderTypeParametersWithVariance()),
      valueParameters = if (ktElement.isInterface()) Name.identifier("")
      else Name.identifier(ktElement.renderValueParameters()),
      supertypes = Name.identifier(ktElement.renderSuperTypes()),
      body = Name.identifier(ktElement.body?.text?.drop(1)?.dropLast(1).orEmpty())
    )

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
