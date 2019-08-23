package arrow.meta.qq

import arrow.meta.utils.SealedSubclass
import arrow.meta.utils.sealedSubclasses
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtNamedDeclaration
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.psiUtil.getValueParameters
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
    val sealedVariants: List<SealedSubclass>,
    val body: Name,
    val messageCollector: MessageCollector,
    val functions: List<Name>,
    val valueParameterNames: List<Name>,
    val properties: List<Name>
  )

  override fun transform(ktElement: KtClass): ClassScope =
    ClassScope(
      modality = Name.identifier(ktElement.modalityModifierType()?.value.orEmpty()),
      visibility = Name.identifier(ktElement.visibilityModifierType()?.value.orEmpty()),
      kind = Name.identifier(ktElement.getClassOrInterfaceKeyword()?.text.orEmpty()),
      name = Name.identifier(ktElement.nameAsSafeName.identifier),
      typeParameters = Name.identifier(ktElement.renderTypeParametersWithVariance()),
      valueParameters = if (ktElement.isInterface()) Name.identifier("")
      else Name.identifier(ktElement.renderValueParameters()),
      supertypes = Name.identifier(ktElement.renderSuperTypes()),
      body = Name.identifier(ktElement.body?.text?.drop(1)?.dropLast(1).orEmpty()),
      functions = ktElement.renderFunctions().map { it.nameAsSafeName },
      properties = ktElement.renderProperties().map { it.nameAsSafeName },
      valueParameterNames = ktElement.renderPrimaryParameters().map { it.nameAsSafeName },
      sealedVariants = ktElement.sealedSubclasses(),
      messageCollector = messageCollector()
    )

  override fun KtClass.cleanUserQuote(quoteDeclaration: String): String =
    quoteDeclaration.trimMargin().let {
      if (isInterface()) it.replace("interface (.*?)\\(\\)".toRegex(), "interface $1")
      else it
    }

  override fun parse(template: String): KtClass =
    quasiQuoteContext.compilerContext.ktPsiElementFactory.createClass(template)

  fun KtClass.renderValueParameters(): String =
    if (getValueParameters().isEmpty()) ""
    else getValueParameters().joinToString(separator = ", ") { it.text }

  fun messageCollector() =
    quasiQuoteContext.compilerContext.messageCollector

  fun KtClass.renderSuperTypes(): String =
    superTypeListEntries.map { it.text }.joinToString(", ")

  fun KtClass.renderTypeParametersWithVariance(): String =
    typeParameters.joinToString(separator = ", ") { it.text }

  fun KtClassOrObject.renderFunctions(): List<KtNamedDeclaration> =
    declarations.filterIsInstance<KtNamedFunction>()

  fun KtClassOrObject.renderProperties(): List<KtNamedDeclaration> =
    declarations.filterIsInstance<KtProperty>()

  fun KtClassOrObject.renderPrimaryParameters(): List<KtNamedDeclaration> =
    primaryConstructorParameters.filter { it.hasValOrVar() }.fold(emptyList())
    { acc, ktParameter -> acc + (ktParameter as KtNamedDeclaration) }

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