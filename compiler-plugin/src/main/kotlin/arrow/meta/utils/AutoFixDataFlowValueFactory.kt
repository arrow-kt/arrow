package arrow.meta.higherkind

import org.jetbrains.kotlin.config.LanguageVersionSettings
import org.jetbrains.kotlin.config.LanguageVersionSettingsImpl
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.VariableDescriptor
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.context.ResolutionContext
import org.jetbrains.kotlin.resolve.calls.smartcasts.DataFlowValue
import org.jetbrains.kotlin.resolve.calls.smartcasts.DataFlowValueFactory
import org.jetbrains.kotlin.resolve.calls.smartcasts.DataFlowValueFactoryImpl
import org.jetbrains.kotlin.resolve.scopes.receivers.ReceiverValue
import org.jetbrains.kotlin.types.KotlinType

class AutoFixDataFlowValueFactory(val delegate: DataFlowValueFactory = DataFlowValueFactoryImpl(LanguageVersionSettingsImpl.DEFAULT)): DataFlowValueFactory by delegate {

  override fun createDataFlowValue(expression: KtExpression, type: KotlinType, bindingContext: BindingContext, containingDeclarationOrModule: DeclarationDescriptor): DataFlowValue {
    println("createDataFlowValue: $expression, $type")
    return delegate.createDataFlowValue(expression, type, bindingContext, containingDeclarationOrModule)
  }

  override fun createDataFlowValue(expression: KtExpression, type: KotlinType, resolutionContext: ResolutionContext<*>): DataFlowValue {
    println("createDataFlowValue: $expression, $type")
    return delegate.createDataFlowValue(expression, type, resolutionContext)
  }

  override fun createDataFlowValue(receiverValue: ReceiverValue, bindingContext: BindingContext, containingDeclarationOrModule: DeclarationDescriptor): DataFlowValue {
    println("createDataFlowValue.receiverValue : $receiverValue")
    return delegate.createDataFlowValue(receiverValue, bindingContext, containingDeclarationOrModule)
  }

  override fun createDataFlowValue(receiverValue: ReceiverValue, resolutionContext: ResolutionContext<*>): DataFlowValue {
    println("createDataFlowValue.receiverValue : $receiverValue")
    return delegate.createDataFlowValue(receiverValue, resolutionContext)
  }

  override fun createDataFlowValueForProperty(property: KtProperty, variableDescriptor: VariableDescriptor, bindingContext: BindingContext, usageContainingModule: ModuleDescriptor?): DataFlowValue {
    println("createDataFlowValueForProperty : $property")
    return delegate.createDataFlowValueForProperty(property, variableDescriptor, bindingContext, usageContainingModule)
  }

  override fun createDataFlowValueForStableReceiver(receiver: ReceiverValue): DataFlowValue {
    println("createDataFlowValueForStableReceiver : $receiver")
    return delegate.createDataFlowValueForStableReceiver(receiver)
  }
}