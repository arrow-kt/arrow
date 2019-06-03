package arrow.plugin

import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity.ERROR
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.codegen.ClassBuilder
import org.jetbrains.kotlin.codegen.ClassBuilderFactory
import org.jetbrains.kotlin.codegen.DelegatingClassBuilder
import org.jetbrains.kotlin.codegen.extensions.ClassBuilderInterceptorExtension
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.diagnostics.DiagnosticSink
import org.jetbrains.kotlin.js.resolve.diagnostics.findPsi
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.jvm.diagnostics.JvmDeclarationOrigin
import org.jetbrains.kotlin.types.typeUtil.isUnit
import org.jetbrains.org.objectweb.asm.MethodVisitor

internal class DebugLogClassBuilder(
  val messageCollector: MessageCollector,
  private val builder: ClassBuilder,
  private val bindingContext: BindingContext
) : DelegatingClassBuilder() {
  override fun getDelegate(): ClassBuilder = builder

  override fun newMethod(origin: JvmDeclarationOrigin, access: Int, name: String, desc: String, signature: String?, exceptions: Array<out String>?): MethodVisitor {
    val original = super.newMethod(origin, access, name, desc, signature, exceptions)
    val function: FunctionDescriptor = origin.descriptor as? FunctionDescriptor ?: return original
    if (function.returnType?.isUnit() == true) {
      function.findPsi()?.let {
        messageCollector.report(ERROR, "Unit return on a non suspended function")
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

//
//  override fun interceptClassBuilderFactory(
//    interceptedFactory: ClassBuilderFactory,
//    bindingContext: BindingContext,
//    diagnostics: DiagnosticSink
//  ): ClassBuilderFactory {
//
//
//    val builder: ClassBuilder = interceptedFactory.newClassBuilder(JvmDeclarationOrigin.NO_ORIGIN)
//    builder.visitor.
//
////    if (descriptor is FunctionDescriptor && declaration is KtFunction && !descriptor.isSuspend) {
////      val element = declaration.nameIdentifier ?: declaration
////      if (descriptor.returnType?.isUnit() == true) {
////        context.trace.reportFromPlugin(ARROW_FX_PURE_UNIT_RETURN.on(element), ArrowDefaultErrorMessages)
////      }
////      val call: KtCallExpression? = declaration.bodyExpression as? KtCallExpression
////      declaration.bodyExpression?.acceptChildren(object : PsiElementVisitor() {
////        override fun visitElement(element: PsiElement?) {
////          println("element: $element")
////          super.visitElement(element)
////        }
////      })
////    }
//
//    val exp : KtCallExpression = TODO()
//    val type: KotlinType? = bindingContext.getType(exp)
//    type.isUnit()
//
//    messageCollector.report(WARNING, "*** IT'S ALIVE ***")
//    return interceptedFactory
//  }

}
