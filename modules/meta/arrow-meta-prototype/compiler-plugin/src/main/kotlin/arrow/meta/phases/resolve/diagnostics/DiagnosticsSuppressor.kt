package arrow.meta.phases.resolve.diagnostics

import arrow.meta.phases.CompilerContext
import arrow.meta.phases.ExtensionPhase
import org.jetbrains.kotlin.diagnostics.Diagnostic

interface DiagnosticsSuppressor : ExtensionPhase {
  fun CompilerContext.isSuppressed(diagnostic: Diagnostic): Boolean
}