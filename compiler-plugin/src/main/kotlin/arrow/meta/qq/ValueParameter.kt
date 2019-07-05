package arrow.meta.qq

import org.jetbrains.kotlin.codegen.kotlinType
import org.jetbrains.kotlin.descriptors.ValueParameterDescriptor
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.descriptors.impl.ValueParameterDescriptorImpl
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.psiUtil.parameterIndex
import org.jetbrains.kotlin.resolve.source.KotlinSourceElement

interface ValueParameter : DeclarationQuote<ValueParameterDescriptor, KtParameter, ValueParameter.ValueParameterScope> {

  override fun scope(): ValueParameterScope = ValueParameterScope

  object ValueParameterScope {
    val name = Name.identifier("_value_parameter_name_")
    val type = Name.identifier("_value_parameter_type_")
  }

  override fun parse(template: String): KtParameter =
    quasiQuoteContext.compilerContext.ktPsiElementFactory.createParameter(template)

  override fun ValueParameterDescriptor?.transform(transformation: KtParameter): ValueParameterDescriptor =
    if (this != null) { //we are modifying an existing descriptor
      ValueParameterDescriptorImpl(
        containingDeclaration = containingDeclaration,
        original = null,
        index = transformation.parameterIndex(),
        annotations = transformation.annotations(),
        name = transformation.nameAsSafeName,
        outType = transformation.kotlinType(quasiQuoteContext.bindingTrace.bindingContext)!!,
        declaresDefaultValue = transformation.defaultValue != null,
        isCrossinline = false,
        isNoinline = false,
        varargElementType = null,
        source = KotlinSourceElement(transformation)
      )
    } else {
      TODO() // we are creating a new descriptor and need to parent it accordingly
    }

  fun KtParameter.annotations(): Annotations =
    Annotations.create(annotationEntries.map { ktAnnotationEntry ->
      Annotation.transform(quasiQuoteContext, ktAnnotationEntry)
    })

  companion object {
    operator fun invoke(
      context: QuasiQuoteContext,
      match: ValueParameterScope.() -> String,
      map: (quotedTemplate: KtParameter) -> String
    ): ValueParameter =
      object : ValueParameter {
        override fun ValueParameterScope.match(): String = match(ValueParameterScope)
        override val quasiQuoteContext: QuasiQuoteContext = context
        override fun map(quotedTemplate: KtParameter): String = map(quotedTemplate)
      }
  }

}