package arrow.meta.qq

import arrow.meta.extensions.CompilerContext
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtNamedFunction

data class Transformation<D>(
  val oldDescriptor: D,
  val newDescriptor: D
)

interface DeclarationQuote<D, K : KtElement, S> {
  val quasiQuoteContext: QuasiQuoteContext
  fun parse(template: String): K
  fun scope(): S
  fun S.match(): String
  fun map(quotedTemplate: K): String
  fun D?.transform(transformation: K): D

  interface Factory<D, K : KtElement, S> {
    operator fun invoke(
      quasiQuoteContext: QuasiQuoteContext,
      match: S.() -> String,
      map: (quotedTemplate: K) -> String
    ): DeclarationQuote<D, K, S>
    fun empty(quasiQuoteContext: QuasiQuoteContext): DeclarationQuote<D, K ,S>
  }

  fun CompilerContext.process(descriptor: D): Transformation<D> {
    val quotedTemplate = scope().match() //user provided match
    val quotedExpression = parse(quotedTemplate) // the template is turned into an expression containing context placeholders
    val transformationTemplate = map(quotedExpression) // the user transforms the expression into a new tree
    val transformation = parse(transformationTemplate) // the new transformation is turned into an expression
    val appliedTransformation = descriptor.transform(transformation) // internally we fill all the context placeholder with the descriptor over the user transformation
    return Transformation(descriptor, appliedTransformation) // We return the old and the new descriptor
  }

}

