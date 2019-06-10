package arrow.plugin

import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.js.translate.context.TranslationContext
import org.jetbrains.kotlin.js.translate.declaration.DeclarationBodyVisitor
import org.jetbrains.kotlin.js.translate.extensions.JsSyntheticTranslateExtension
import org.jetbrains.kotlin.psi.KtPureClassOrObject

class MetaJsSyntheticTranslateExtension : JsSyntheticTranslateExtension {
  override fun generateClassSyntheticParts(
    declaration: KtPureClassOrObject,
    descriptor: ClassDescriptor,
    translator: DeclarationBodyVisitor,
    context: TranslationContext
  ) {
    println("JsSyntheticTranslateExtension.generateClassSyntheticParts")
  }
}
