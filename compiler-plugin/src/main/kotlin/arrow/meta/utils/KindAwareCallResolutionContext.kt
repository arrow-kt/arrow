package arrow.meta.higherkind

import org.jetbrains.annotations.NotNull
import org.jetbrains.kotlin.config.LanguageVersionSettings
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.StatementFilter
import org.jetbrains.kotlin.resolve.calls.components.InferenceSession
import org.jetbrains.kotlin.resolve.calls.context.BasicCallResolutionContext
import org.jetbrains.kotlin.resolve.calls.context.CallPosition
import org.jetbrains.kotlin.resolve.calls.context.CallResolutionContext
import org.jetbrains.kotlin.resolve.calls.context.ContextDependency
import org.jetbrains.kotlin.resolve.calls.context.ResolutionResultsCache
import org.jetbrains.kotlin.resolve.calls.smartcasts.DataFlowInfo
import org.jetbrains.kotlin.resolve.calls.smartcasts.DataFlowValueFactory
import org.jetbrains.kotlin.resolve.scopes.LexicalScope
import org.jetbrains.kotlin.types.KotlinType

class KindAwareCallResolutionContext(val underlying: CallResolutionContext<BasicCallResolutionContext>) :
  CallResolutionContext<KindAwareCallResolutionContext>(
    underlying.trace,
    underlying.scope,
    underlying.call,
    underlying.expectedType,
    underlying.dataFlowInfo,
    underlying.contextDependency,
    underlying.checkArguments,
    underlying.resolutionResultsCache,
    underlying.dataFlowInfoForArguments,
    underlying.statementFilter,
    underlying.isAnnotationContext,
    underlying.isDebuggerContext,
    underlying.collectAllCandidates,
    underlying.callPosition,
    underlying.expressionContextProvider,
    underlying.languageVersionSettings,
    underlying.dataFlowValueFactory,
    underlying.inferenceSession
  ) {

  override fun create(
    trace: BindingTrace,
    scope: LexicalScope,
    dataFlowInfo: DataFlowInfo,
    expectedType: KotlinType,
    contextDependency: ContextDependency,
    resolutionResultsCache: ResolutionResultsCache,
    statementFilter: StatementFilter,
    collectAllCandidates: Boolean,
    callPosition: CallPosition,
    expressionContextProvider: Function1<KtExpression, KtExpression>,
    languageVersionSettings: LanguageVersionSettings,
    dataFlowValueFactory: DataFlowValueFactory,
    inferenceSession: InferenceSession
  ): KindAwareCallResolutionContext {
    println("KindAwareCallResolutionContext.create: $expectedType")
    return KindAwareCallResolutionContext(BasicCallResolutionContext.create(
      underlying.trace,
      underlying.scope,
      underlying.call,
      underlying.expectedType,
      underlying.dataFlowInfo,
      underlying.contextDependency,
      underlying.checkArguments,
      underlying.isAnnotationContext,
      underlying.languageVersionSettings,
      underlying.dataFlowValueFactory,
      underlying.inferenceSession
    ))
  }


}