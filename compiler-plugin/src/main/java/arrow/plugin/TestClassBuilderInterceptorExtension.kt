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
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.psiUtil.referenceExpression
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
      } else function.findPsi()?.checkPurity()
    }
    return original
  }

  private tailrec fun loop(remaining: List<PsiElement>): Unit {
    if (remaining.isEmpty()) Unit
    else {
      val nestedExpressions =
        when (val current = remaining[0]) {
          is KtBlockExpression -> current.children.toList()
          else -> emptyList<PsiElement>()
        }
      loop(nestedExpressions + remaining.drop(1))
    }
  }

  fun PsiElement.checkPurity() {
    if (this is KtBlockExpression) loop(children.toList())
    if (this is KtExpression) checkExpressionPurity(this)
    else loop(children.toList())
  }

  private fun checkExpressionPurity(expression: KtExpression) {
    val expressionRetType: KotlinType? = expression.getType(bindingContext)
    if (expressionRetType?.isUnit() == true) {
      messageCollector.report(ERROR, "Impure expression in function ${expression.referenceExpression()}: `${expression.text}` returning `Unit` only allowed in `suspend` functions")
    }
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
