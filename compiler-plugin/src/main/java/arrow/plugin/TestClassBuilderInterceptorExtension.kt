package arrow.plugin

import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity.ERROR
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity.WARNING
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.codegen.ClassBuilder
import org.jetbrains.kotlin.codegen.ClassBuilderFactory
import org.jetbrains.kotlin.codegen.DelegatingClassBuilder
import org.jetbrains.kotlin.codegen.extensions.ClassBuilderInterceptorExtension
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.com.intellij.psi.PsiElementVisitor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptorVisitor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.diagnostics.DiagnosticSink
import org.jetbrains.kotlin.js.resolve.diagnostics.findPsi
import org.jetbrains.kotlin.psi.Call
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.callUtil.getCall
import org.jetbrains.kotlin.resolve.calls.callUtil.getType
import org.jetbrains.kotlin.resolve.calls.callUtil.isSafeCall
import org.jetbrains.kotlin.resolve.jvm.diagnostics.JvmDeclarationOrigin
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.typeUtil.isUnit
import org.jetbrains.org.objectweb.asm.MethodVisitor

internal class DebugLogClassBuilder(
  val messageCollector: MessageCollector,
  private val builder: ClassBuilder,
  private val bindingContext: BindingContext
) : DelegatingClassBuilder() {
  override fun getDelegate(): ClassBuilder = builder

  override fun newMethod(
    origin: JvmDeclarationOrigin,
    access: Int,
    name: String,
    desc: String,
    signature: String?,
    exceptions: Array<out String>?
  ): MethodVisitor {
    //delegate to the parent method visitor for construction
    val original: MethodVisitor = super.newMethod(origin, access, name, desc, signature, exceptions)
    //bail quickly if this is not a function
    val function: FunctionDescriptor = origin.descriptor as? FunctionDescriptor ?: return original
    //we ignore suspend functions as they are already safe
    if (!function.isSuspend) {
      val functionPsi = function.findPsi()
      //if the function returns Unit then it should have been suspended since all it can do is produce effects
      if (function.returnType?.isUnit() == true) {
        functionPsi.let {
          messageCollector.report(ERROR, "Unit return on a non suspended function: ${function.name}")
        }
      } else {
        //if the return type is not Unit this function may still contain effecting calls in it's child expressions
        // scattered throughout its body and should be suspended
        functionPsi?.children?.filterIsInstance<KtCallExpression>()?.forEach { expression ->
          //the binding context allows us to type-check any expression
          val expressionRetType: KotlinType? = expression.getType(bindingContext)
          println(expressionRetType)
          if (expressionRetType?.isUnit() == true) {
            messageCollector.report(ERROR, "The expression `${expression.text}` returns `Unit` and should be encapsulated in `suspend`: ${function.name}")
          }
        }
      }

    }
    return original
  }

}

class TestClassBuilderInterceptorExtension(val messageCollector: MessageCollector) : ClassBuilderInterceptorExtension {

  override fun interceptClassBuilderFactory(
    interceptedFactory: ClassBuilderFactory,
    bindingContext: BindingContext,
    diagnostics: DiagnosticSink
  ): ClassBuilderFactory = object : ClassBuilderFactory by interceptedFactory {
    override fun newClassBuilder(origin: JvmDeclarationOrigin) =
      DebugLogClassBuilder(messageCollector, interceptedFactory.newClassBuilder(origin), bindingContext)
  }

}
