package arrow.meta.quotes

import arrow.meta.phases.analysis.renderTypeParametersWithVariance
import arrow.meta.phases.analysis.renderValueParameters
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.modalityModifierType
import org.jetbrains.kotlin.psi.psiUtil.visibilityModifierType

internal val EmptyElement: Name = Name.identifier("_EMPTY_ELEMENT_")

interface Func : Quote<KtElement, KtNamedFunction, Func.FuncScope> {

  class FuncScope(
    val modality: Name,
    val visibility: Name,
    val typeParameters: Name,
    val receiver: Name,
    val name: Name,
    val valueParameters: Name,
    val returnType: Name,
    val body: Name
  )

  override fun transform(ktElement: KtNamedFunction): FuncScope =
    FuncScope(
      modality = ktElement.modalityModifierType()?.value?.let(Name::identifier) ?: EmptyElement,
      visibility = ktElement.visibilityModifierType()?.value?.let(Name::identifier) ?: EmptyElement,
      name = ktElement.nameAsName ?: EmptyElement,
      typeParameters = ktElement.renderTypeParametersWithVariance()?.let(Name::identifier)
        ?: EmptyElement,
      receiver = ktElement.receiverTypeReference?.text?.let(Name::identifier) ?: EmptyElement,
      valueParameters = ktElement.renderValueParameters()?.let(Name::identifier) ?: EmptyElement,
      returnType = ktElement.typeReference?.text?.let(Name::identifier) ?: EmptyElement,
      body = (ktElement.bodyExpression?.text
        ?: ktElement.bodyBlockExpression?.text?.drop(1)?.dropLast(1)
        )?.let(Name::identifier) ?: EmptyElement
    )

  override fun KtNamedFunction.cleanUserQuote(quoteDeclaration: String): String =
    quoteDeclaration.trimMargin().removeEmptyTypeArgs()

  private fun String.removeEmptyTypeArgs(): String =
    replace("<$EmptyElement>", "")
      .replace("$EmptyElement.", "")
      .replace("$EmptyElement", "")

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

