package arrow.meta.quotes

import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtElement

interface ClassOrObject : Quote<KtElement, KtClass, ClassScope> {

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
