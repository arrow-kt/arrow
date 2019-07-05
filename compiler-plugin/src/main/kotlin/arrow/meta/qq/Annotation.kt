package arrow.meta.qq

import org.jetbrains.kotlin.container.get
import org.jetbrains.kotlin.descriptors.annotations.AnnotationDescriptor
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.resolve.lazy.descriptors.LazyAnnotationDescriptor
import org.jetbrains.kotlin.resolve.lazy.descriptors.LazyAnnotationsContextImpl

interface Annotation : DeclarationQuote<AnnotationDescriptor, KtAnnotationEntry, Annotation.AnnotationScope> {

  override fun scope(): AnnotationScope = AnnotationScope

  object AnnotationScope {
    val name = Name.identifier("_annotation_name_")
  }

  override fun parse(template: String): KtAnnotationEntry =
    quasiQuoteContext.compilerContext.ktPsiElementFactory.createAnnotationEntry(template)

  override fun AnnotationDescriptor?.transform(transformation: KtAnnotationEntry): AnnotationDescriptor =
    transform(quasiQuoteContext, transformation)

  companion object {
    operator fun invoke(
      context: QuasiQuoteContext,
      match: AnnotationScope.() -> String,
      map: (quotedTemplate: KtAnnotationEntry) -> String
    ): Annotation =
      object : Annotation {
        override val quasiQuoteContext: QuasiQuoteContext = context
        override fun AnnotationScope.match(): String = match(AnnotationScope)
        override fun map(quotedTemplate: KtAnnotationEntry): String = map(quotedTemplate)
      }

    fun transform(context: QuasiQuoteContext, transformation: KtAnnotationEntry): AnnotationDescriptor =
      LazyAnnotationDescriptor(LazyAnnotationsContextImpl(
        context.compilerContext.componentProvider.get(),
        context.compilerContext.componentProvider.get(),
        context.bindingTrace,
        TODO()
      ), transformation)
  }


}
