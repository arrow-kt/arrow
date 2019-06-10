package arrow.plugin

import org.jetbrains.kotlin.codegen.ImplementationBodyCodegen
import org.jetbrains.kotlin.codegen.StackValue
import org.jetbrains.kotlin.codegen.extensions.ExpressionCodegenExtension
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall

class MetaExpressionCodegenExtension : ExpressionCodegenExtension {

  override fun applyFunction(receiver: StackValue, resolvedCall: ResolvedCall<*>, c: ExpressionCodegenExtension.Context): StackValue? {
    println("ExpressionCodegenExtension.applyFunction: call: ${resolvedCall.call.calleeExpression?.text}")
    return super.applyFunction(receiver, resolvedCall, c)
  }

  override fun applyProperty(receiver: StackValue, resolvedCall: ResolvedCall<*>, c: ExpressionCodegenExtension.Context): StackValue? {
    println("ExpressionCodegenExtension.applyFunction: call: ${resolvedCall.call.calleeExpression?.text}")
    return super.applyProperty(receiver, resolvedCall, c)
  }

  override fun generateClassSyntheticParts(codegen: ImplementationBodyCodegen) {
    println("ExpressionCodegenExtension.generateClassSyntheticParts: codegen: $codegen")
    super.generateClassSyntheticParts(codegen)
  }
}
