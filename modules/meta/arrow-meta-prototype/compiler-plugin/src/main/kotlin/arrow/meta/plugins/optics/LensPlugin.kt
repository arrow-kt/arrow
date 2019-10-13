package arrow.meta.plugins.optics

import arrow.meta.Meta
import arrow.meta.Plugin
import arrow.meta.invoke
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.analysis.ElementScope
import arrow.meta.quotes.ClassScope
import arrow.meta.quotes.Scope
import arrow.meta.quotes.Transform
import arrow.meta.quotes.ScopedList
import arrow.meta.quotes.classOrObject
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassBody
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.resolve.source.KotlinSourceElement
import org.jetbrains.kotlin.resolve.source.toSourceElement
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

val Meta.lenses: Plugin
  get() =
    "lenses" {
      meta(
        classOrObject(::isProductType) { c ->

          val location = c.toSourceElement().safeAs<KotlinSourceElement>()?.psi?.textRange

          validateMaxArityAllowed(this)
          Transform.replace(
            replacing = c,
            newDeclaration =
            if (c.companionObjects.isEmpty())
              """|
                 |$modality $visibility data $kind $name($`(valueParameters)`) {
                 |  
                 |  companion object {
                 |    ${lenses(this)}
                 |    ${iso(this)}
                 |  }
                 |}""".`class`
            else
              """
                 |$modality $visibility data $kind $name($`(valueParameters)`) {
                 |  ${body.value?.addDeclarationToBody(lenses = lenses(this))}
                 |  
                 |}""".`class`
          )
        }
      )
    }

private fun CompilerContext.validateMaxArityAllowed(classScope: ClassScope) {
  if (classScope.`(valueParameters)`.value.size > 10)
    // Question: error message file location
    messageCollector?.report(
      CompilerMessageSeverity.WARNING,
      "Iso cannot be generated for product type with ${classScope.`(valueParameters)`.value.size}. Maximum support is $maxArity"
    )
}

private const val maxArity: Int = 10

private fun ElementScope.lenses(classScope: ClassScope): ScopedList<KtProperty> =
  classScope.run {
    ScopedList(
      separator = "\n",
      value = `(valueParameters)`.value.map { param: KtParameter ->
        lens(source = value, focus = param)
      }
    )
  }

private fun ElementScope.lens(source: KtClass, focus: KtParameter): Scope<KtProperty> =
  """|val ${focus.name}: arrow.optics.Lens<${source.name}, ${focus.typeReference!!.text}> = arrow.optics.Lens(
     |  get = { ${source.name!!.toLowerCase()} -> ${source.name!!.toLowerCase()}.${focus.name} },
     |  set = { ${source.name!!.toLowerCase()}, ${focus.name} -> ${source.name!!.toLowerCase()}.copy(${focus.name} = ${focus.name}) }
     |)""".property.synthetic

private fun ElementScope.iso(classScope: ClassScope): Scope<KtProperty> =
  classScope.run {
    """|val iso: arrow.optics.Iso<${value.name}, ${`(valueParameters)`.tupledType}> = arrow.optics.Iso(
       |  get = { (${`(valueParameters)`.destructured}) -> ${`(valueParameters)`.tupled} },
       |  reverseGet = { (${`(valueParameters)`.destructured}) -> ${value.name}(${`(valueParameters)`.destructured}) }
       |)""".property.synthetic
  }

val ScopedList<KtParameter>.tupledType: String
  // get() = "Tuple${value.size}<${value.joinToString { it.typeReference!!.text }}>"
  get() = "Pair<${value.joinToString { it.typeReference!!.text }}>"

val ScopedList<KtParameter>.tupled: String
  // get() = "Tuple${value.size}($destructured)"
  get() = "Pair($destructured)"

val ScopedList<KtParameter>.destructured: String
  get() = value.joinToString { it.name!! }

private fun isProductType(ktClass: KtClass): Boolean =
  ktClass.isData() &&
    ktClass.primaryConstructorParameters.isNotEmpty() &&
    ktClass.primaryConstructorParameters.all { !it.isMutable } &&
    ktClass.typeParameters.isEmpty()

fun KtClassBody.addDeclarationToBody(lenses: ScopedList<KtProperty>): String =
  declarations.joinToString("\n") { declaration ->
    if (declaration is KtObjectDeclaration && declaration.isCompanion()) lenses.toString()
    else declaration.text
  }
