package arrow.meta.qq

import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtElement

interface ClassOrObject : Quote<KtElement, KtClass, ClassOrObject.ClassScope> {

  override fun scope(): ClassScope = ClassScope()

  class ClassScope(
    val visibility: Name = Name.identifier("_class_visibility_"),
    val modality: Name = Name.identifier("_class_modality_"),
    val name: Name = Name.identifier("_class_name_"),
    val typeArgs: Name = Name.identifier("_class_type_arguments_"),
    val typeArg: Name = Name.identifier("_class_type_argument_"),
    val params: Name = Name.identifier("_class_value_parameters_"),
    val param: Name = Name.identifier("_class_value_parameter_"),
    val supertypes: Name = Name.identifier("_class_supertypes_"),
    val body: Name = Name.identifier("_class_body_")
  )

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
      name = encode(
        originalScope.name.identifier,
        original.nameAsSafeName.identifier,
        transformation.nameAsSafeName.identifier
      ),
      body = encode(
        originalScope.body.identifier,
        originalBody ?: "",
        transformation.body?.text ?: ""
      )
    )
  }

  override fun KtClass.matches(transformation: KtClass): Boolean {
    println("$nameAsSafeName == ${transformation.nameAsSafeName} : ${nameAsSafeName == transformation.nameAsSafeName}")
    return nameAsSafeName == transformation.nameAsSafeName
  }

  override fun KtClass?.transform(transformation: KtClass): KtClass =
    transformation

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
