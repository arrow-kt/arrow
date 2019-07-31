package arrow.meta.qq

import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.psiUtil.getValueParameters
import org.jetbrains.kotlin.psi.psiUtil.modalityModifierType
import org.jetbrains.kotlin.psi.psiUtil.visibilityModifierType

interface Func : Quote<KtElement, KtNamedFunction, Func.FuncScope> {

  class FuncScope(
    val modality: Name,
    val visibility: Name,
    val typeParameters: Name,
    val receiver: Name?,
    val name: Name,
    val valueParameters: Name,
    val returnType: Name,
    val body: Name
  )

  override fun transform(ktElement: KtNamedFunction): FuncScope =
    FuncScope(
      modality = Name.identifier(ktElement.modalityModifierType()?.value.orEmpty()),
      visibility = Name.identifier(ktElement.visibilityModifierType()?.value.orEmpty()),
      name = Name.identifier(ktElement.nameAsSafeName.identifier),
      typeParameters = Name.identifier(ktElement.renderTypeParametersWithVariance()),
      receiver = ktElement.receiverTypeReference?.text?.let(Name::identifier),
      valueParameters = Name.identifier(ktElement.renderValueParameters()),
      returnType = Name.identifier(ktElement.typeReference?.text.orEmpty()),
      body = Name.identifier(ktElement.bodyExpression?.text ?: ktElement.bodyBlockExpression?.text?.drop(1)?.dropLast(1) ?: "")
    )

  fun KtNamedFunction.renderTypeParametersWithVariance(): String =
    typeParameters.joinToString(separator = ", ") { it.text }

  fun KtNamedFunction.renderValueParameters(): String =
    if (valueParameters.isEmpty()) ""
    else valueParameters.joinToString(separator = ", ") { it.text }

  override fun KtNamedFunction.cleanUserQuote(quoteDeclaration: String): String =
    quoteDeclaration.trimMargin()

  override fun parse(template: String): KtNamedFunction =
    quasiQuoteContext.compilerContext.ktPsiElementFactory.createFunction(template)

  companion object : Quote.Factory<KtElement, KtNamedFunction, FuncScope> {
    override operator fun invoke(
      quasiQuoteContext: QuasiQuoteContext,
      containingDeclaration: KtElement,
      match: KtNamedFunction.() -> Boolean,
      map: FuncScope.(quotedTemplate: KtNamedFunction) -> List<String>
    ): Func =
      object : Func {
        override val quasiQuoteContext: QuasiQuoteContext = quasiQuoteContext
        override fun KtNamedFunction.match(): Boolean = match(this)
        override fun FuncScope.map(quotedTemplate: KtNamedFunction): List<String> = map(quotedTemplate)
        override val containingDeclaration: KtElement = containingDeclaration
      }
  }

}
