package arrow.meta.phases.codegen.asm

import arrow.meta.phases.CompilerContext
import arrow.meta.phases.ExtensionPhase
import org.jetbrains.kotlin.codegen.ImplementationBodyCodegen
import org.jetbrains.kotlin.codegen.StackValue
import org.jetbrains.kotlin.codegen.extensions.ExpressionCodegenExtension
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall

interface Codegen : ExtensionPhase {
  fun CompilerContext.applyFunction(
    receiver: StackValue,
    resolvedCall: ResolvedCall<*>,
    c: ExpressionCodegenExtension.Context
  ): StackValue?

  fun CompilerContext.applyProperty(
    receiver: StackValue,
    resolvedCall: ResolvedCall<*>,
    c: ExpressionCodegenExtension.Context
  ): StackValue?

  fun CompilerContext.generateClassSyntheticParts(codegen: ImplementationBodyCodegen): Unit
}