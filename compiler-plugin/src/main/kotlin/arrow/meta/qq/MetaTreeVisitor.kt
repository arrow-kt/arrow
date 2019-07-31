package arrow.meta.qq

import arrow.meta.extensions.CompilerContext
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtVisitor

abstract class MetaTreeVisitor(val compilerContext: CompilerContext) : KtVisitor<Unit, Unit>() {
  override fun visitKtElement(element: KtElement, data: Unit?): Unit? {
    element.acceptChildren(this)
    return Unit
  }

  override fun visitKtFile(file: KtFile, data: Unit?): Unit? {
    super.visitKtFile(file, data)
    file.acceptChildren(this)
    return Unit
  }

}