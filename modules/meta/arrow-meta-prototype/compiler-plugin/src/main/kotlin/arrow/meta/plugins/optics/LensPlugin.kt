package arrow.meta.plugins.optics

import arrow.meta.phases.ExtensionPhase
import arrow.meta.MetaComponentRegistrar
import arrow.meta.quotes.ClassScope
import arrow.meta.quotes.ScopedList
import arrow.meta.quotes.classOrObject
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassBody
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtProperty

val MetaComponentRegistrar.lenses: Pair<Name, List<ExtensionPhase>>
  get() =
    Name.identifier("lenses") to
      meta(
        classOrObject(::isProductType) { c ->
          if (`(valueParameters)`.value.size > 10) context.compilerContext.messageCollector?.report(CompilerMessageSeverity.WARNING, "Iso cannot be generated for product type with ${`(valueParameters)`.value.size}. Maximum support is $maxArity")
          listOf(
            if (c.companionObjects.isEmpty())
              """
              |$modality $visibility data $kind $name($`(valueParameters)`) {
              |  
              |  companion object {
              |${lenses().joinToString("\n\n") { it.text }}
              |
              |$iso
              |  }
              |}""" else """
              |$modality $visibility data $kind $name($`(valueParameters)`) {
              |  ${body!!.value.addDeclerationTobody(lenses = lenses())}
              |}"""
          )
        }
      )

private const val maxArity: Int = 10

private fun ClassScope.lenses(): List<KtProperty> =
  `(valueParameters)`.value.map { param: KtParameter ->
    lens(source = value, focus = param)
  }.map(context.compilerContext.ktPsiElementFactory::createProperty)

private fun lens(source: KtClass, focus: KtParameter): String =
  """
  |    val ${focus.name}: arrow.optics.Lens<${source.name}, ${focus.typeReference!!.text}> = arrow.optics.Lens(
  |      get = { ${source.name!!.toLowerCase()} -> ${source.name!!.toLowerCase()}.${focus.name} },
  |      set = { ${source.name!!.toLowerCase()}, ${focus.name} -> ${source.name!!.toLowerCase()}.copy(${focus.name} = ${focus.name}) }
  |    )""".trimMargin()

val ClassScope.iso: String
  get() = """
   |    val iso: arrow.optics.Iso<${value.name}, ${`(valueParameters)`.tupledType}> = arrow.optics.Iso(
   |      get = { (${`(valueParameters)`.destructured}) -> ${`(valueParameters)`.tupled} },
   |      reverseGet = { (${`(valueParameters)`.destructured}) -> ${value.name}(${`(valueParameters)`.destructured}) }
   |    )""".trimMargin()

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

fun KtClassBody.addDeclerationTobody(lenses: List<KtProperty>): String =
  declarations.joinToString("\n") { declaration ->
    if (declaration is KtObjectDeclaration && declaration.isCompanion()) lenses.joinToString("\n\n") { it.text }
    else declaration.text
  }
