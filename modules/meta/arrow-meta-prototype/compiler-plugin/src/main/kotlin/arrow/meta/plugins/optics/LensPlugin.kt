package arrow.meta.plugins.optics

import arrow.meta.phases.ExtensionPhase
import arrow.meta.MetaComponentRegistrar
import arrow.meta.quotes.QuasiQuoteContext
import arrow.meta.quotes.classOrObject
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassBody
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.resolve.calls.callUtil.getType
import kotlin.reflect.KProperty

val MetaComponentRegistrar.lenses: Pair<Name, List<ExtensionPhase>>
  get() =
    Name.identifier("lenses") to
      meta(
        classOrObject(::isProductType) { c ->
          println("Processing lenses : ${c.name}")
          listOf(
            if (c.companionObjects.isEmpty())
              """
              |$modality $visibility data $kind $name($valueParameters) {
              |  
              |  companion object {
              |${c.lenses(context).joinToString("\n\n") { it.text }}
              |  }
              |}""" else """
              |$modality $visibility data $kind $name($valueParameters) {
              |  ${body!!.value.addDeclerationTobody(lenses = c.lenses(context))}
              |}"""
          )
        }
      )

fun KtClass.lenses(context: QuasiQuoteContext): List<KtProperty> =
  primaryConstructorParameters.map { param: KtParameter ->
    context.compilerContext.ktPsiElementFactory.createProperty(
      """
   |    val ${param.name}: arrow.optics.Lens<${name}, ${param.typeReference!!.text}> = arrow.optics.Lens(
   |      get = { ${name!!.toLowerCase()} -> ${name!!.toLowerCase()}.${param.name} },
   |      set = { ${name!!.toLowerCase()}, ${param.name} -> ${name!!.toLowerCase()}.copy(${param.name} = ${param.name}) }
   |    )""".trimMargin()
    )
  }

fun isProductType(ktClass: KtClass): Boolean =
  ktClass.isData() &&
    ktClass.primaryConstructorParameters.isNotEmpty() &&
    ktClass.primaryConstructorParameters.all { !it.isMutable } &&
    ktClass.typeParameters.isEmpty()

fun KtClassBody.addDeclerationTobody(lenses: List<KtProperty>): String =
  declarations.joinToString("\n") { declaration ->

    if (declaration is KtObjectDeclaration && declaration.isCompanion()) declaration.addLenses(lenses).text
    else declaration.text
  }


fun KtObjectDeclaration.addLenses(lenses: List<KtProperty>): KtObjectDeclaration = apply {

  (declarations as MutableList).addAll(lenses)
}
