package arrow.meta.autofold

import arrow.meta.extensions.ExtensionPhase
import arrow.meta.extensions.MetaComponentRegistrar
import arrow.meta.qq.classOrObject
import arrow.meta.utils.SealedSubclass
import arrow.meta.utils.arity
import arrow.meta.utils.isKinded
import arrow.meta.utils.sealedSubclasses
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageLocation
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtParameterList
import org.jetbrains.kotlin.psi.KtValueArgumentList
import org.jetbrains.kotlin.psi.psiUtil.findFunctionByName
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

val MetaComponentRegistrar.autoFold: List<ExtensionPhase>
  get() =
    meta(
      classOrObject(::isAutoFoldable) { c ->
        val sealedExtraTypes = sealedVariants.map { it.typeVariables }.flatten()
        val typeInfoString = sealedExtraTypes.joinToString(separator = ", ")
        val returnType = "ARROW_FOLD"
        listOfNotNull(
          if (sealedVariants.any { it.typeVariables.size > c.arity }) {
            messageCollector.cantCreateAutoFold(c)
            c.text
          } else
            """
              |$visibility $modality $kind $name${typeParameters.let { if (it.identifier.isNotEmpty()) "<$it>" else "" }}${valueParameters.let { if (it.identifier.isNotEmpty()) "($it)" else "" }}${if ((String::isNotEmpty)(supertypes.identifier)) ({ it: String -> " : $it" })(supertypes.identifier) else supertypes.identifier} {
              |  ${body.asString().trimMargin()}
              |  @Suppress("UNCHECKED_CAST", "USELESS_CAST", "NO_ELSE_IN_WHEN")
              |  fun <${typeInfoString.let { if (it.isNotEmpty()) "$it, " else "" }}$returnType> fold(
              |  ${sealedVariants.params(returnType)}
              |  ): $returnType = when (val x = this) {
              |  ${sealedVariants.patternMatch()}
              |  }
              |}
            """.trimMargin()
        )
      }
    )

private fun KtClass.hasFoldFunction(): Boolean =
  with(findFunctionByName("fold").safeAs<KtNamedFunction>()) {
    typeParameters.size > 1 && safeAs<KtParameterList>().safeAs<KtValueArgumentList>()?.arguments?.size == sealedSubclasses().size
  }

private fun isAutoFoldable(ktClass: KtClass): Boolean =
  ktClass.isSealed() && !ktClass.isAnnotation() &&
    ktClass.isKinded() &&
    !ktClass.hasFoldFunction() && ktClass.sealedSubclasses().isNotEmpty()

private fun List<SealedSubclass>.patternMatch(): String =
  joinToString(
    transform = { s ->
      "  is ${s.simpleName.identifier} -> ${s.simpleName.identifier.decapitalize()}(x${if (s.typeVariables.isNotEmpty()) " as ${s.simpleName.identifier}<${s.typeVariables.joinToString(separator = ", ")}>" else ""})"
    },
    separator = "\n  ")

private fun List<SealedSubclass>.params(returns: String): String =
  joinToString(
    transform = { "  ${it.simpleName.identifier.decapitalize()}: (${it.simpleName.identifier}${if (it.typeVariables.isNotEmpty()) "<${it.typeVariables.joinToString(separator = ", ")}>" else ""}) -> $returns" }
    , separator = ",\n  ")

private fun MessageCollector.cantCreateAutoFold(sealedClass: KtClass): Unit =
  report(
    CompilerMessageSeverity.INFO,
    "AutoFold can not be created for $sealedClass",
    CompilerMessageLocation.create(null)
  )