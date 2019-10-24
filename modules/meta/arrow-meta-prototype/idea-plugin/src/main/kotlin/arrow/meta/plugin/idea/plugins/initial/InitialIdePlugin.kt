package arrow.meta.plugin.idea.plugins.initial

import arrow.meta.Plugin
import arrow.meta.invoke
import arrow.meta.plugin.idea.IdeMetaPlugin
import arrow.meta.plugin.idea.phases.resolve.LOG
import org.jetbrains.kotlin.cfg.ClassMissingCase
import org.jetbrains.kotlin.cfg.WhenMissingCase
import org.jetbrains.kotlin.descriptors.VariableDescriptor
import org.jetbrains.kotlin.diagnostics.Diagnostic
import org.jetbrains.kotlin.diagnostics.DiagnosticWithParameters1
import org.jetbrains.kotlin.diagnostics.DiagnosticWithParameters2
import org.jetbrains.kotlin.diagnostics.Errors
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtWhenExpression
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.checker.KotlinTypeChecker
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

val IdeMetaPlugin.initialIdeSetUp: Plugin
  get() = "Initial Ide Setup" {
    meta(
     addDiagnosticSuppressor { diagnostic ->
       LOG.debug("isSupressed: ${diagnostic.factory.name}: \n ${diagnostic.psiElement.text}")
       val result = diagnostic.suppressMetaDiagnostics()
       diagnostic.logSuppression(result)
       result
     }
    )
  }

private fun Diagnostic.suppressMetaDiagnostics(): Boolean =
  suppressInvisibleMember() ||
    suppressNoElseInWhen() ||
    kindsTypeMismatch() ||
    suppressUnusedParameter()

private fun Diagnostic.suppressInvisibleMember(): Boolean =
  factory == Errors.INVISIBLE_MEMBER

private fun Diagnostic.kindsTypeMismatch(): Boolean =
  factory == Errors.TYPE_INFERENCE_EXPECTED_TYPE_MISMATCH && safeAs<DiagnosticWithParameters2<KtElement, KotlinType, KotlinType>>()?.let { diagnosticWithParameters ->
    val a = diagnosticWithParameters.a
    val b = diagnosticWithParameters.b
    KotlinTypeChecker.DEFAULT.isSubtypeOf(a, b) //if this is the kind type checker then it will do the right thing otherwise this proceeds as usual with the regular type checker
  } == true

private fun Diagnostic.suppressUnusedParameter(): Boolean =
  factory == Errors.UNUSED_PARAMETER && safeAs<DiagnosticWithParameters1<KtParameter, VariableDescriptor>>()?.let { diagnosticWithParameters ->
    diagnosticWithParameters.psiElement.defaultValue?.text == "given" //TODO move to typeclasses plugin
  } == true

private fun Diagnostic.suppressNoElseInWhen(): Boolean {
  val result = factory == Errors.NO_ELSE_IN_WHEN && safeAs<DiagnosticWithParameters1<KtWhenExpression, List<WhenMissingCase>>>()?.let { diagnosticWithParameters ->
    val declaredCases = diagnosticWithParameters.psiElement.entries.flatMap { it.conditions.map { it.text } }.toSet()
    val missingCases = diagnosticWithParameters.a.filterIsInstance<ClassMissingCase>().map { it.toString() }.toSet()
    declaredCases.containsAll(missingCases)
  } ?: false
  return result
}

private fun Diagnostic.logSuppression(result: Boolean) {
  LOG.debug("Suppressing ${factory.name} on: `${psiElement.text}`: $result")
}
