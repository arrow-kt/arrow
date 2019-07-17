package arrow.meta.qq

import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.psiUtil.getValueParameters
import org.jetbrains.kotlin.psi.psiUtil.modalityModifier
import org.jetbrains.kotlin.psi.psiUtil.modalityModifierType
import org.jetbrains.kotlin.psi.psiUtil.visibilityModifier

interface ClassOrObject : Quote<KtElement, KtClass, ClassOrObject.ClassScope> {

  override fun scope(): ClassScope = ClassScope.initial

  class ClassScope(
    val modality: Name = Name.identifier("_class_modality_"),
    val visibility: Name = Name.identifier("_class_visibility_"),
    val name: Name = Name.identifier("_class_name_"),
    val typeArgs: Name = Name.identifier("_class_type_arguments_"),
    val typeArgsWithVariance: Name = Name.identifier("_class_type_arguments_with_variance_"),
    val params: Name = Name.identifier("_class_value_parameters_"),
    val supertypes: Name = Name.identifier("_class_supertypes_"),
    val body: Name = Name.identifier("_class_body_")
  ) {
    companion object {
      val initial: ClassScope = ClassScope()
    }
  }

  override fun parse(template: String): KtClass =
    quasiQuoteContext.compilerContext.ktPsiElementFactory.createClass(template)

  override fun substitute(template: String, original: KtClass, transformation: KtClass): ClassScope {
    val originalScope = scope()
    fun encode(originalToken: String, oldCode: String, userSelection: String): Name =
      // if a template contains a template original default value we should substitute for that of the matched descriptor
      if (template.contains(originalToken)) Name.identifier(oldCode)
      // if the template has been altered by the user
      else Name.identifier(userSelection)

    val originalBody: String? = original.body?.text?.drop(1)?.dropLast(1)

    return ClassScope(
      visibility = encode(
        originalScope.visibility.identifier,
        original.visibilityModifier()?.text ?: "",
        transformation.visibilityModifier()?.text ?: ""
      ),
      modality = encode(
        originalScope.modality.identifier,
        original.modalityModifier()?.text ?: "",
        transformation.modalityModifier()?.text ?: ""
      ),
      name = encode(
        originalScope.name.identifier,
        original.nameAsSafeName.identifier,
        transformation.nameAsSafeName.identifier
      ),
      body = encode(
        originalScope.body.identifier,
        originalBody ?: "",
        transformation.body?.text ?: ""
      ),
      typeArgs = encode(
        originalScope.typeArgs.identifier,
        original.typeParameters.joinToString(", ") { it.name.orEmpty() } ?: "",
        transformation.typeParameters.joinToString(", ") { it.name.orEmpty() }
      ),
      typeArgsWithVariance = encode(
        originalScope.typeArgs.identifier,
        original.typeParameters.joinToString(", ") { it.text } ?: "",
        transformation.typeParameters.joinToString(", ") { it.text }
      ),
      supertypes = encode(
        originalScope.supertypes.identifier,
        original.superTypeListEntries.joinToString(", ") { it.name ?: "Any" } ?: "",
        transformation.superTypeListEntries.joinToString(", ") { it.name ?: "Any" }
      ),
      params = encode(
        originalScope.params.identifier,
        original.getValueParameters().joinToString(", ") { it.name.orEmpty() } ?: "",
        transformation.getValueParameters().joinToString(", ") { it.name.orEmpty() }
      )
    )
  }

  override fun KtClass.matches(filter: KtClass): Boolean {
    val result =
      (filter.nameAsSafeName == ClassScope.initial.name || filter.nameAsSafeName == nameAsSafeName)
    return result
  }

  companion object : Quote.Factory<KtElement, KtClass, ClassScope> {
    override operator fun invoke(
      quasiQuoteContext: QuasiQuoteContext,
      containingDeclaration: KtElement,
      match: ClassScope.() -> String,
      map: ClassScope.(quotedTemplate: KtClass) -> List<String>
    ): ClassOrObject =
      object : ClassOrObject {
        override val quasiQuoteContext: QuasiQuoteContext = quasiQuoteContext
        override fun ClassScope.match(): String = match(ClassScope())
        override fun ClassScope.map(quotedTemplate: KtClass): List<String> = map(quotedTemplate)
        override val containingDeclaration: KtElement = containingDeclaration
      }
  }

}
