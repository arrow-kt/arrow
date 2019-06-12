package arrow.meta

import org.jetbrains.kotlin.container.StorageComponentContainer
import org.jetbrains.kotlin.container.useInstance
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.extensions.StorageComponentContainerContributor
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.resolve.TargetPlatform
import org.jetbrains.kotlin.resolve.checkers.DeclarationChecker
import org.jetbrains.kotlin.resolve.checkers.DeclarationCheckerContext

// This class can be used to check for certain pre-conditions.
// In this example we bail in check if any class is OPEN or ABSTRACT.
class TestStorageComponentContainerContributor : DeclarationChecker, StorageComponentContainerContributor {

  override fun registerModuleComponents(container: StorageComponentContainer, platform: TargetPlatform, moduleDescriptor: ModuleDescriptor) {
    container.useInstance(TestStorageComponentContainerContributor())
  }

  override fun check(declaration: KtDeclaration, descriptor: DeclarationDescriptor, context: DeclarationCheckerContext) {
    println("StorageComponentContainerContributor.check, declaration: ${declaration.name}")
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