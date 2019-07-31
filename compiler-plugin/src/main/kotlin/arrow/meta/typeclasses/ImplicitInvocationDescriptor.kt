package arrow.meta.typeclasses

import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.ReceiverParameterDescriptor
import org.jetbrains.kotlin.descriptors.TypeParameterDescriptor
import org.jetbrains.kotlin.descriptors.ValueParameterDescriptor
import org.jetbrains.kotlin.descriptors.Visibility
import org.jetbrains.kotlin.descriptors.impl.ReceiverParameterDescriptorImpl
import org.jetbrains.kotlin.types.KotlinType

class ImplicitInvocationDescriptor(
  val originalDeclaration: ValueParameterDescriptor,
  val valueParameterDescriptor: ValueParameterDescriptor) :
  ValueParameterDescriptor by originalDeclaration {

  override fun getOriginal(): ValueParameterDescriptor =
    originalDeclaration
//    if (valueParameterDescriptor.isWithAnnotated) this
//    else originalDeclaration

  private lateinit var extensionReceiverParamDescriptor: ReceiverParameterDescriptorImpl
  private lateinit var typeParameters: List<TypeParameterDescriptor>
  private lateinit var valueParameters: MutableList<ValueParameterDescriptor>
  private var returnType: KotlinType? = null
  private lateinit var modality: Modality
  private lateinit var visibility: Visibility

  fun initialize(
    extensionReceiverParamDescriptor: ReceiverParameterDescriptorImpl,
    typeParameters: List<TypeParameterDescriptor>,
    valueParameters: List<ValueParameterDescriptor>,
    returnType: KotlinType?,
    modality: Modality,
    visibility: Visibility
  ) {
    this.extensionReceiverParamDescriptor = extensionReceiverParamDescriptor
    this.typeParameters = typeParameters
    this.valueParameters = valueParameters.toMutableList()
    this.returnType = returnType
    this.modality = modality
    this.visibility = visibility
  }

  override fun getVisibility(): Visibility = this.visibility
  override fun getReturnType(): KotlinType? = this.returnType
  override fun getValueParameters(): MutableList<ValueParameterDescriptor> = this.valueParameters
  override fun getTypeParameters(): List<TypeParameterDescriptor> = this.typeParameters
  override fun getExtensionReceiverParameter(): ReceiverParameterDescriptor? = this.extensionReceiverParamDescriptor
}