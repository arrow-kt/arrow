package arrow.meta.qq

import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.KtParameter

interface Parameter : Quote<KtFunction, KtParameter, Parameter.ParameterScope> {

  class ParameterScope(
    val name: Name,
    val type: Name,
    val defaultValue: Name?
  )

  override fun transform(ktElement: KtParameter): ParameterScope =
    ParameterScope(
      name = Name.identifier(ktElement.name.orEmpty()),
      type = Name.identifier(ktElement.typeReference?.name.orEmpty()),
      defaultValue = ktElement.defaultValue?.text?.let(Name::identifier)
    )

  override fun KtParameter.cleanUserQuote(quoteDeclaration: String): String =
    quoteDeclaration.trimMargin()

  override fun parse(template: String): KtParameter =
    quasiQuoteContext.compilerContext.ktPsiElementFactory.createParameter(template)

  companion object : Quote.Factory<KtFunction, KtParameter, ParameterScope> {
    override operator fun invoke(
      quasiQuoteContext: QuasiQuoteContext,
      containingDeclaration: KtFunction,
      match: KtParameter.() -> Boolean,
      map: ParameterScope.(quotedTemplate: KtParameter) -> List<String>
    ): Parameter =
      object : Parameter {
        override val quasiQuoteContext: QuasiQuoteContext = quasiQuoteContext
        override fun KtParameter.match(): Boolean = match(this)
        override fun ParameterScope.map(quotedTemplate: KtParameter): List<String> = map(quotedTemplate)
        override val containingDeclaration: KtFunction = containingDeclaration
      }
  }

}
