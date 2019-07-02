package arrow.meta.typeclasses

import org.jetbrains.kotlin.descriptors.ReceiverParameterDescriptor
import org.jetbrains.kotlin.descriptors.impl.ReceiverParameterDescriptorImpl
import org.jetbrains.kotlin.descriptors.impl.ValueParameterDescriptorImpl
import org.jetbrains.kotlin.resolve.scopes.receivers.ReceiverValue

class MetaReceiverParameterDescriptor(
  val callableDescriptor: ImplicitInvocationDescriptor,
  val receiverValue: ReceiverValue,
  delegate: ReceiverParameterDescriptor
) : ReceiverParameterDescriptor by delegate {
  override fun getOriginal(): ReceiverParameterDescriptor =
    if (callableDescriptor.valueParameterDescriptor is ValueParameterDescriptorImpl && callableDescriptor.valueParameterDescriptor.isWithAnnotated) {
      ReceiverParameterDescriptorImpl(callableDescriptor, receiverValue, callableDescriptor.annotations)
    } else callableDescriptor.extensionReceiverParameter!!
}