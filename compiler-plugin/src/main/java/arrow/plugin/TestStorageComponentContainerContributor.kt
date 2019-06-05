package arrow.plugin

import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.com.intellij.psi.PsiElementVisitor
import org.jetbrains.kotlin.container.StorageComponentContainer
import org.jetbrains.kotlin.container.useInstance
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.diagnostics.reportFromPlugin
import org.jetbrains.kotlin.extensions.StorageComponentContainerContributor
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.resolve.TargetPlatform
import org.jetbrains.kotlin.resolve.checkers.DeclarationChecker
import org.jetbrains.kotlin.resolve.checkers.DeclarationCheckerContext
import org.jetbrains.kotlin.types.typeUtil.isUnit

// This class can be used to check for certain pre-conditions.
// In this example we bail in check if any class is OPEN or ABSTRACT.
class TestStorageComponentContainerContributor : DeclarationChecker, StorageComponentContainerContributor {

  override fun registerModuleComponents(container: StorageComponentContainer, platform: TargetPlatform, moduleDescriptor: ModuleDescriptor) {
    container.useInstance(TestStorageComponentContainerContributor())
  }

  override fun check(declaration: KtDeclaration, descriptor: DeclarationDescriptor, context: DeclarationCheckerContext) {
//    if (descriptor is FunctionDescriptor && declaration is KtFunction && !descriptor.isSuspend) {
//      val element = declaration.nameIdentifier ?: declaration
//      if (descriptor.returnType?.isUnit() == true) {
//        context.trace.reportFromPlugin(ARROW_FX_PURE_UNIT_RETURN.on(element), ArrowDefaultErrorMessages)
//      }
//      val call: KtCallExpression? = declaration.bodyExpression as? KtCallExpression
//      declaration.bodyExpression?.acceptChildren(object : PsiElementVisitor() {
//        override fun visitElement(element: PsiElement?) {
//          println("element: $element")
//          super.visitElement(element)
//        }
//      })
//    }
  }
}

fun <A, B> A?.fold(ifEmpty: () -> B, ifFull: (A) -> B): B =
  this?.let(ifFull) ?: ifEmpty()
fun <A, B> A?.map(f: (A) -> B): B? =
  this?.let(f)
fun <A, B> A?.flatMap(f: (A) -> B?): B? =
  this?.let(f)