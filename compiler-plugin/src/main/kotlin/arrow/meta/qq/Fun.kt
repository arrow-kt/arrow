package arrow.meta.qq

import arrow.meta.extensions.MetaComponentRegistrar
import org.jetbrains.kotlin.codegen.kotlinType
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.TypeParameterDescriptor
import org.jetbrains.kotlin.descriptors.annotations.AnnotationDescriptor
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.descriptors.impl.SimpleFunctionDescriptorImpl
import org.jetbrains.kotlin.descriptors.impl.TypeParameterDescriptorImpl
import org.jetbrains.kotlin.descriptors.impl.ValueParameterDescriptorImpl
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtAnnotation
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtTypeParameter
import org.jetbrains.kotlin.psi.psiUtil.parameterIndex
import org.jetbrains.kotlin.resolve.source.KotlinSourceElement

interface Fun : DeclarationQuote<FunctionDescriptor, KtNamedFunction, Fun.FunScope> {

  override fun scope(): FunScope = FunScope

  object FunScope {
    val visibility = Name.identifier("_function_visibility_")
    val modality = Name.identifier("_function_modality_")
    val typeArgs = Name.identifier("_function_type_arguments_")
    val receiverType = Name.identifier("_function_receiver_type_")
    val name = Name.identifier("_function_name_")
    val typeArg = Name.identifier("_function_type_argument_")
    val params = Name.identifier("_function_value_parameters_")
    val param = Name.identifier("_function_value_parameter_")
    val body = Name.identifier("_function_body_")
    val returnType = Name.identifier("_function_return_type_")
  }


  override fun parse(template: String): KtNamedFunction =
    quasiQuoteContext.compilerContext.ktPsiElementFactory.createFunction(template)

  override fun FunctionDescriptor?.transform(transformation: KtNamedFunction): FunctionDescriptor {
    if (this != null) { //if we are modifying an existing descriptor
      val source: KotlinSourceElement = source as KotlinSourceElement
      val original = source.psi as KtNamedFunction
      val result = original
      if (transformation.nameAsSafeName != MetaComponentRegistrar.FunctionScope.name && result.name != transformation.name) {
        result.setName(transformation.nameAsSafeName.asString())
      }
      return SimpleFunctionDescriptorImpl.create(
        containingDeclaration,
        transformation.annotationsDescriptors(),
        transformation.nameAsSafeName,
        kind,
        KotlinSourceElement(transformation)
      ).run {
        initialize(
          extensionReceiverParameter,
          dispatchReceiverParameter,
          typeParametersDescriptors(transformation),
          valueParametersDescriptors(transformation),
          returnType,
          modality,
          visibility
        )
      }
    } else {
      TODO() //we are creating a new descriptor from scratch and need to find a parent
    }
  }

  fun KtNamedFunction.annotationsDescriptors(): Annotations =
    Annotations.create(annotations.flatMap { ktAnnotation ->
      ktAnnotation.annotationsDescriptors()
    })

  fun SimpleFunctionDescriptorImpl.valueParametersDescriptors(transformation: KtNamedFunction): List<ValueParameterDescriptorImpl> =
    transformation.valueParameters.map { ktParameter ->
      valueParameterDescriptor(ktParameter)
    }

  fun SimpleFunctionDescriptorImpl.typeParametersDescriptors(transformation: KtNamedFunction): List<TypeParameterDescriptor> =
    transformation.typeParameters.map { ktTypeParameter ->
      typeParameterDescriptor(ktTypeParameter)
    }

  fun SimpleFunctionDescriptorImpl.typeParameterDescriptor(ktTypeParameter: KtTypeParameter): TypeParameterDescriptor =
    TypeParameterDescriptorImpl.createWithDefaultBound(
      this,
      ktTypeParameter.annotations(),
      false,
      ktTypeParameter.variance,
      ktTypeParameter.nameAsSafeName,
      ktTypeParameter.parameterIndex()
    )

  fun SimpleFunctionDescriptorImpl.valueParameterDescriptor(ktParameter: KtParameter): ValueParameterDescriptorImpl =
    ValueParameterDescriptorImpl(
      containingDeclaration = this,
      original = null,
      index = ktParameter.parameterIndex(),
      annotations = Annotations.create(ktParameter.annotations.flatMap {
        it.annotationsDescriptors()
      }),
      name = ktParameter.nameAsSafeName,
      outType = ktParameter.kotlinType(quasiQuoteContext.bindingTrace.bindingContext)!!,
      declaresDefaultValue = ktParameter.defaultValue != null,
      isCrossinline = false,
      isNoinline = false,
      varargElementType = null,
      source = KotlinSourceElement(ktParameter)
    )

  fun KtAnnotation.annotationsDescriptors(): List<AnnotationDescriptor> =
    entries.map { ktAnnotationEntry ->
      Annotation.transform(quasiQuoteContext, ktAnnotationEntry)
    }

  fun KtTypeParameter.annotations(): Annotations =
    Annotations.create(annotationEntries.map { ktAnnotationEntry ->
      Annotation.transform(quasiQuoteContext, ktAnnotationEntry)
    })

  companion object : DeclarationQuote.Factory<FunctionDescriptor, KtNamedFunction, Fun.FunScope> {
    override operator fun invoke(
      quasiQuoteContext: QuasiQuoteContext,
      match: FunScope.() -> String,
      map: (quotedTemplate: KtNamedFunction) -> String
    ): Fun =
      object : Fun {
        override val quasiQuoteContext: QuasiQuoteContext  = quasiQuoteContext
        override fun FunScope.match(): String = match(FunScope)
        override fun map(quotedTemplate: KtNamedFunction): String = map(quotedTemplate)
      }

    override fun empty(quasiQuoteContext: QuasiQuoteContext): DeclarationQuote<FunctionDescriptor, KtNamedFunction, FunScope> =
      invoke(quasiQuoteContext, { "" }, { it.text })
  }

}