package arrow.meta.typeclasses

import org.jetbrains.kotlin.com.intellij.openapi.project.Project
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.ReceiverParameterDescriptor
import org.jetbrains.kotlin.descriptors.ValueParameterDescriptor
import org.jetbrains.kotlin.descriptors.impl.ReceiverParameterDescriptorImpl
import org.jetbrains.kotlin.descriptors.impl.ValueParameterDescriptorImpl
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.scopes.receivers.CastImplicitClassReceiver
import org.jetbrains.kotlin.resolve.scopes.receivers.ExpressionReceiver
import org.jetbrains.kotlin.resolve.scopes.receivers.ImplicitClassReceiver

class MetaValueParameterDescriptor(
  val project: Project,
  val trace: BindingTrace,
  val delegate: ValueParameterDescriptorImpl
) : ValueParameterDescriptor by delegate {

  private val extensionReceiverParam = ReceiverParameterDescriptorImpl(
    this,
    CastImplicitClassReceiver(this.type.constructor.declarationDescriptor as ClassDescriptor, this.type),
    this.annotations
  )

  override fun getExtensionReceiverParameter(): ReceiverParameterDescriptor? = extensionReceiverParam


}