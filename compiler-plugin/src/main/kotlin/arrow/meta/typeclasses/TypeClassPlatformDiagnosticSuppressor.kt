package arrow.meta.typeclasses

import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor
import org.jetbrains.kotlin.descriptors.VariableDescriptor
import org.jetbrains.kotlin.resolve.checkers.PlatformDiagnosticSuppressor

class TypeClassPlatformDiagnosticSuppressor  : PlatformDiagnosticSuppressor by PlatformDiagnosticSuppressor.Default {
  override fun shouldReportUnusedParameter(parameter: VariableDescriptor): Boolean {
    println("TypeClassPlatformDiagnosticSuppressor.shouldReportUnusedParameter: $parameter")
    return PlatformDiagnosticSuppressor.Default.shouldReportUnusedParameter(parameter)
  }

  override fun shouldReportNoBody(descriptor: CallableMemberDescriptor): Boolean {
    println("TypeClassPlatformDiagnosticSuppressor.shouldReportNoBody: $descriptor")
    return PlatformDiagnosticSuppressor.Default.shouldReportNoBody(descriptor)
  }

}