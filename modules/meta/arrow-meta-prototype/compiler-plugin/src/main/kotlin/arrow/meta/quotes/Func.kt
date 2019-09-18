package arrow.meta.quotes

import arrow.meta.phases.analysis.body
import arrow.meta.phases.analysis.bodySourceAsExpression
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtTypeParameter
import org.jetbrains.kotlin.psi.KtTypeReference
import org.jetbrains.kotlin.psi.psiUtil.modalityModifierType
import org.jetbrains.kotlin.psi.psiUtil.visibilityModifierType

internal val EmptyElement: Name = Name.identifier("_EMPTY_ELEMENT_")

interface Func : Quote<KtElement, KtNamedFunction, Func.FuncScope> {

  class FunctionBodyScope(override val value: KtExpression) : Scope<KtExpression>(value) {
    override fun toString(): String =
      value.bodySourceAsExpression() ?: ""
  }

  class FuncScope(
    override val value: KtNamedFunction,
    val modality: Name? = value.modalityModifierType()?.value?.let(Name::identifier),
    val visibility: Name? = value.visibilityModifierType()?.value?.let(Name::identifier),
    val typeParameters: List<Scope<KtTypeParameter>> = value.typeParameters.map(::Scope),
    val receiver: Name? = value.receiverTypeReference?.text?.let(Name::identifier),
    val name: Name? = value.nameAsName,
    val valueParameters: List<Scope<KtParameter>> = value.valueParameters.map(::Scope),
    val returnType: Scope<KtTypeReference>? = value.typeReference?.let(::Scope),
    val body: FunctionBodyScope? = value.body()?.let(::FunctionBodyScope)
  ) : Scope<KtNamedFunction>(value)

  override fun transform(ktElement: KtNamedFunction): FuncScope =
    FuncScope(ktElement)

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

