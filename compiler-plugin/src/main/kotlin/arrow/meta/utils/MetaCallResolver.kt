package arrow.meta.utils

import arrow.meta.higherkind.get
import org.jetbrains.annotations.NotNull
import org.jetbrains.kotlin.builtins.KotlinBuiltIns
import org.jetbrains.kotlin.config.LanguageVersionSettings
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.ClassConstructorDescriptor
import org.jetbrains.kotlin.descriptors.ConstructorDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.VariableDescriptor
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.Call
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtCollectionLiteralExpression
import org.jetbrains.kotlin.psi.KtConstructorDelegationCall
import org.jetbrains.kotlin.psi.KtReferenceExpression
import org.jetbrains.kotlin.psi.KtSimpleNameExpression
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.TypeResolver
import org.jetbrains.kotlin.resolve.calls.ArgumentTypeResolver
import org.jetbrains.kotlin.resolve.calls.CallCompleter
import org.jetbrains.kotlin.resolve.calls.CallResolver
import org.jetbrains.kotlin.resolve.calls.GenericCandidateResolver
import org.jetbrains.kotlin.resolve.calls.context.BasicCallResolutionContext
import org.jetbrains.kotlin.resolve.calls.context.ResolutionContext
import org.jetbrains.kotlin.resolve.calls.model.MutableDataFlowInfoForArguments
import org.jetbrains.kotlin.resolve.calls.results.OverloadResolutionResults
import org.jetbrains.kotlin.resolve.calls.smartcasts.DataFlowInfo
import org.jetbrains.kotlin.resolve.calls.smartcasts.DataFlowValueFactory
import org.jetbrains.kotlin.resolve.calls.tasks.ResolutionCandidate
import org.jetbrains.kotlin.resolve.calls.tasks.TracingStrategy
import org.jetbrains.kotlin.resolve.calls.tower.NewResolutionOldInference
import org.jetbrains.kotlin.resolve.calls.tower.PSICallResolver
import org.jetbrains.kotlin.resolve.scopes.LexicalScope
import org.jetbrains.kotlin.resolve.scopes.SyntheticScopes
import org.jetbrains.kotlin.resolve.scopes.receivers.ExpressionReceiver
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.expressions.ExpressionTypingContext
import org.jetbrains.kotlin.types.expressions.ExpressionTypingServices

class MetaCallResolver(
  builtIns: KotlinBuiltIns,
  languageVersionSettings: LanguageVersionSettings,
  dataFlowValueFactory: DataFlowValueFactory
) : CallResolver(builtIns, languageVersionSettings, dataFlowValueFactory) {

  override fun resolveCallForMember(nameExpression: KtSimpleNameExpression, context: BasicCallResolutionContext): OverloadResolutionResults<CallableDescriptor> {
    println("MetaCallResolver.resolveCallForMember: $nameExpression")
    return super.resolveCallForMember(nameExpression, context)
  }

  override fun resolveFunctionCall(trace: BindingTrace, scope: LexicalScope, call: Call, expectedType: KotlinType, dataFlowInfo: DataFlowInfo, isAnnotationContext: Boolean): OverloadResolutionResults<FunctionDescriptor> {
    println("MetaCallResolver.resolveFunctionCall(X): ${call.calleeExpression?.text}")
    return super.resolveFunctionCall(trace, scope, call, expectedType, dataFlowInfo, isAnnotationContext)
  }

  override fun resolveFunctionCall(context: BasicCallResolutionContext): OverloadResolutionResults<FunctionDescriptor> {
    println("MetaCallResolver.resolveFunctionCall: ${context.call}")
    return super.resolveFunctionCall(context)
  }

  override fun resolveBinaryCall(context: ExpressionTypingContext?, receiver: ExpressionReceiver?, binaryExpression: KtBinaryExpression?, name: Name?): OverloadResolutionResults<FunctionDescriptor> {
    println("MetaCallResolver.resolveBinaryCall: $binaryExpression")
    return super.resolveBinaryCall(context, receiver, binaryExpression, name)
  }

  override fun resolveEqualsCallWithGivenDescriptors(context: ExpressionTypingContext, expression: KtReferenceExpression, receiver: ExpressionReceiver, call: Call, functionDescriptors: MutableCollection<FunctionDescriptor>): OverloadResolutionResults<FunctionDescriptor> {
    println("MetaCallResolver.resolveEqualsCallWithGivenDescriptors: $expression, functionDescriptors: $functionDescriptors")
    return super.resolveEqualsCallWithGivenDescriptors(context, expression, receiver, call, functionDescriptors)
  }

  override fun resolveCallWithGivenName(context: ResolutionContext<*>, call: Call, functionReference: KtReferenceExpression, name: Name): OverloadResolutionResults<FunctionDescriptor> {
    println("MetaCallResolver.resolveCallWithGivenName: $name, functionReference: $functionReference")
    return super.resolveCallWithGivenName(context, call, functionReference, name)
  }

  override fun resolveCallWithGivenName(context: ResolutionContext<*>, call: Call, name: Name, tracing: TracingStrategy): OverloadResolutionResults<FunctionDescriptor> {
    println("MetaCallResolver.resolveCallWithGivenName: $name")
    return super.resolveCallWithGivenName(context, call, name, tracing)
  }

  override fun resolveConstructorCall(context: BasicCallResolutionContext, functionReference: KtReferenceExpression, constructedType: KotlinType): OverloadResolutionResults<ConstructorDescriptor> {
    println("MetaCallResolver.resolveConstructorCall: $functionReference")
    return super.resolveConstructorCall(context, functionReference, constructedType)
  }

  override fun resolveConstructorDelegationCall(trace: BindingTrace, scope: LexicalScope, dataFlowInfo: DataFlowInfo, constructorDescriptor: ClassConstructorDescriptor, call: KtConstructorDelegationCall): OverloadResolutionResults<ConstructorDescriptor>? {
    println("MetaCallResolver.resolveConstructorDelegationCall: $call")
    return super.resolveConstructorDelegationCall(trace, scope, dataFlowInfo, constructorDescriptor, call)
  }

  override fun resolveCollectionLiteralCallWithGivenDescriptor(context: ExpressionTypingContext, expression: KtCollectionLiteralExpression, call: Call, functionDescriptors: MutableCollection<FunctionDescriptor>): OverloadResolutionResults<FunctionDescriptor> {
    println("MetaCallResolver.resolveCollectionLiteralCallWithGivenDescriptor: $expression")
    return super.resolveCollectionLiteralCallWithGivenDescriptor(context, expression, call, functionDescriptors)
  }

  override fun resolveSimpleProperty(context: BasicCallResolutionContext): OverloadResolutionResults<VariableDescriptor> {
    println("MetaCallResolver.resolveSimpleProperty: $context")
    return super.resolveSimpleProperty(context)
  }

  override fun resolveCallWithKnownCandidate(
    call: Call,
    tracing: TracingStrategy,
    context: ResolutionContext<*>,
    candidate: ResolutionCandidate<FunctionDescriptor>,
    dataFlowInfoForArguments: MutableDataFlowInfoForArguments?
  ): OverloadResolutionResults<FunctionDescriptor> {
    println("MetaCallResolver.resolveCallWithKnownCandidate: $call")
    return super.resolveCallWithKnownCandidate(call, tracing, context, candidate, dataFlowInfoForArguments)
  }
}