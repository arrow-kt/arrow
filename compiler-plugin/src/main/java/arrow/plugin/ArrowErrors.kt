package arrow.plugin

import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.diagnostics.DiagnosticFactory0
import org.jetbrains.kotlin.diagnostics.rendering.DefaultErrorMessages
import org.jetbrains.kotlin.diagnostics.rendering.DiagnosticFactoryToRendererMap

// Error messages are modeled as DiagnosticFactory0, and afterwards attahed to an actual element so the error message can be linked to said element.
val ARROW_ERROR: DiagnosticFactory0<PsiElement> = DiagnosticFactory0.create<PsiElement>(org.jetbrains.kotlin.diagnostics.Severity.ERROR)


// This is a map of errors -> message you have to pass along with the error you want to send to the compiler.
object ArrowDefaultErrorMessages : DefaultErrorMessages.Extension {
  private val MAP = DiagnosticFactoryToRendererMap("Arrow")

  override fun getMap(): DiagnosticFactoryToRendererMap = MAP

  init {
    MAP.put(ARROW_ERROR, "Something went wrong - deal with it.")
  }
}
