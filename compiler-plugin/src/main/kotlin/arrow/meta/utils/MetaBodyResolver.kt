package arrow.meta.utils

import arrow.meta.typeclasses.MetaValueParameterDescriptor
import arrow.meta.typeclasses.MetaValueParameterResolver
import arrow.meta.typeclasses.isWithAnnotated
import org.jetbrains.kotlin.builtins.KotlinBuiltIns
import org.jetbrains.kotlin.com.intellij.openapi.project.Project
import org.jetbrains.kotlin.config.LanguageVersionSettings
import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor
import org.jetbrains.kotlin.descriptors.ClassConstructorDescriptor
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.ClassDescriptorWithResolutionScopes
import org.jetbrains.kotlin.descriptors.ConstructorDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.PropertyAccessorDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.descriptors.ValueParameterDescriptor
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.descriptors.impl.AnonymousFunctionDescriptor
import org.jetbrains.kotlin.descriptors.impl.LazyClassReceiverParameterDescriptor
import org.jetbrains.kotlin.descriptors.impl.ReceiverParameterDescriptorImpl
import org.jetbrains.kotlin.descriptors.impl.SyntheticFieldDescriptor
import org.jetbrains.kotlin.descriptors.impl.ValueParameterDescriptorImpl
import org.jetbrains.kotlin.diagnostics.Errors
import org.jetbrains.kotlin.psi.KtAnonymousInitializer
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtDeclarationWithBody
import org.jetbrains.kotlin.psi.KtPrimaryConstructor
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.kotlin.psi.KtSecondaryConstructor
import org.jetbrains.kotlin.resolve.AnalyzerExtensions
import org.jetbrains.kotlin.resolve.AnnotationChecker
import org.jetbrains.kotlin.resolve.AnnotationResolver
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.BodiesResolveContext
import org.jetbrains.kotlin.resolve.BodyResolveCache
import org.jetbrains.kotlin.resolve.BodyResolver
import org.jetbrains.kotlin.resolve.ControlFlowAnalyzer
import org.jetbrains.kotlin.resolve.DeclarationsChecker
import org.jetbrains.kotlin.resolve.DelegatedPropertyResolver
import org.jetbrains.kotlin.resolve.FunctionDescriptorUtil
import org.jetbrains.kotlin.resolve.OverloadChecker
import org.jetbrains.kotlin.resolve.calls.CallResolver
import org.jetbrains.kotlin.resolve.calls.smartcasts.DataFlowInfo
import org.jetbrains.kotlin.resolve.lazy.descriptors.LazyClassDescriptor
import org.jetbrains.kotlin.resolve.lazy.descriptors.LazyClassMemberScope
import org.jetbrains.kotlin.resolve.scopes.LexicalScope
import org.jetbrains.kotlin.resolve.scopes.LexicalScopeImpl
import org.jetbrains.kotlin.resolve.scopes.LexicalScopeKind
import org.jetbrains.kotlin.resolve.scopes.LocalRedeclarationChecker
import org.jetbrains.kotlin.resolve.scopes.receivers.ExpressionReceiver
import org.jetbrains.kotlin.resolve.scopes.receivers.ExtensionReceiver
import org.jetbrains.kotlin.types.DeferredType
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.expressions.ExpressionTypingServices
import org.jetbrains.kotlin.types.expressions.PreliminaryDeclarationVisitor
import org.jetbrains.kotlin.types.expressions.ValueParameterResolver

/**
 * These service overrides would need to be reviewed on each compiler release to encure
 * they stay up to date with the mutable requirements and initialization order the
 * Kotlin compiler assumes.
 */
class MetaBodyResolver(
  val project: Project,
  annotationChecker: AnnotationChecker,
  val expressionTypingServices: ExpressionTypingServices,
  callResolver: CallResolver,
  val trace: BindingTrace,
  controlFlowAnalyzer: ControlFlowAnalyzer,
  declarationsChecker: DeclarationsChecker,
  annotationResolver: AnnotationResolver,
  delegatedPropertyResolver: DelegatedPropertyResolver,
  analyzerExtensions: AnalyzerExtensions,
  underlying: ValueParameterResolver,
  val valueParameterResolver: MetaValueParameterResolver,
  bodyResolveCache: BodyResolveCache,
  builtIns: KotlinBuiltIns,
  val overloadChecker: OverloadChecker,
  val languageVersionSettings: LanguageVersionSettings
) : BodyResolver(
  project,
  annotationResolver,
  bodyResolveCache,
  callResolver,
  controlFlowAnalyzer,
  declarationsChecker,
  delegatedPropertyResolver,
  expressionTypingServices,
  analyzerExtensions,
  trace,
  underlying,
  annotationChecker,
  builtIns,
  overloadChecker,
  languageVersionSettings
) {

  override fun resolveAnonymousInitializer(outerDataFlowInfo: DataFlowInfo, anonymousInitializer: KtAnonymousInitializer, classDescriptor: ClassDescriptorWithResolutionScopes) {
    println("MetaBodyResolver.resolveAnonymousInitializer")
    super.resolveAnonymousInitializer(outerDataFlowInfo, anonymousInitializer, classDescriptor)
  }

  override fun resolveConstructorParameterDefaultValues(outerDataFlowInfo: DataFlowInfo, trace: BindingTrace, constructor: KtPrimaryConstructor, constructorDescriptor: ConstructorDescriptor, declaringScope: LexicalScope) {
    println("MetaBodyResolver.resolveConstructorParameterDefaultValues")
    super.resolveConstructorParameterDefaultValues(outerDataFlowInfo, trace, constructor, constructorDescriptor, declaringScope)
  }

  override fun resolveProperty(c: BodiesResolveContext, property: KtProperty, propertyDescriptor: PropertyDescriptor) {
    println("MetaBodyResolver.resolveProperty")
    super.resolveProperty(c, property, propertyDescriptor)
  }

  override fun resolveSecondaryConstructorBody(outerDataFlowInfo: DataFlowInfo, trace: BindingTrace, constructor: KtSecondaryConstructor, descriptor: ClassConstructorDescriptor, declaringScope: LexicalScope) {
    println("MetaBodyResolver.resolveSecondaryConstructorBody")
    super.resolveSecondaryConstructorBody(outerDataFlowInfo, trace, constructor, descriptor, declaringScope)
  }

  override fun resolveSuperTypeEntryList(outerDataFlowInfo: DataFlowInfo, ktClass: KtClassOrObject, descriptor: ClassDescriptor, primaryConstructor: ConstructorDescriptor?, scopeForConstructorResolution: LexicalScope, scopeForMemberResolution: LexicalScope) {
    println("MetaBodyResolver.resolveSuperTypeEntryList")
    super.resolveSuperTypeEntryList(outerDataFlowInfo, ktClass, descriptor, primaryConstructor, scopeForConstructorResolution, scopeForMemberResolution)
  }

  override fun resolveBodies(c: BodiesResolveContext) {
    println("MetaBodyResolver.resolveBodies")
    super.resolveBodies(c)
  }

  private fun computeDeferredType(type: KotlinType?) {
    // handle type inference loop: function or property body contains a reference to itself
    // fun f() = { f() }
    // val x = x
    // type resolution must be started before body resolution
    if (type is DeferredType) {
      val deferredType = type as DeferredType?
      if (!deferredType!!.isComputed()) {
        deferredType.delegate
      }
    }
  }

  override fun resolveFunctionBody(outerDataFlowInfo: DataFlowInfo, trace: BindingTrace, function: KtDeclarationWithBody, functionDescriptor: FunctionDescriptor, declaringScope: LexicalScope) {
    println("MetaBodyResolver.resolveFunctionBody: $functionDescriptor")
    //super.resolveFunctionBody(outerDataFlowInfo, trace, function, functionDescriptor, declaringScope)
    computeDeferredType(functionDescriptor.returnType)
    resolveFunctionBody(outerDataFlowInfo, trace, function, functionDescriptor, declaringScope, null, null)
  }

  private fun resolveFunctionBody(
    outerDataFlowInfo: DataFlowInfo,
    trace: BindingTrace,
    function: KtDeclarationWithBody,
    functionDescriptor: FunctionDescriptor,
    scope: LexicalScope,
    beforeBlockBody: Function1<LexicalScope, DataFlowInfo>?,
    // Creates wrapper scope for header resolution if necessary (see resolveSecondaryConstructorBody)
    headerScopeFactory: Function1<LexicalScope, LexicalScope>?
  ) {
    var scope = scope
    PreliminaryDeclarationVisitor.createForDeclaration(function, trace, languageVersionSettings)

    val receiverParameterDescriptor = functionDescriptor.dispatchReceiverParameter
    if (receiverParameterDescriptor is LazyClassReceiverParameterDescriptor) {
      val lazyClassReceiverParameterDescriptor = receiverParameterDescriptor as LazyClassReceiverParameterDescriptor?
      val descriptor = lazyClassReceiverParameterDescriptor!!.containingDeclaration
      if (descriptor is LazyClassDescriptor) {
        val lazyClassDescriptor = descriptor
        val memberScope = lazyClassDescriptor.unsubstitutedMemberScope
        if (memberScope is LazyClassMemberScope) {
          val lazyClassMemberScope = memberScope
          val constructor = lazyClassMemberScope.getPrimaryConstructor()
          if (constructor != null) {
            val members = constructor.valueParameters
            for (member in members) {
              if (member.isWithAnnotated) {
                scope = getScopeForExtensionParameter(functionDescriptor, scope, member)
              }
            }
          }
        }
      }
    }

    var innerScope = FunctionDescriptorUtil.getFunctionInnerScope(scope, functionDescriptor, trace, overloadChecker)
    val valueParameters = function.valueParameters
    val valueParameterDescriptors = functionDescriptor.valueParameters.map {
      if (it.isWithAnnotated) MetaValueParameterDescriptor(project, trace, it as ValueParameterDescriptorImpl)
      else it
    }

    val headerScope = if (headerScopeFactory != null) headerScopeFactory!!.invoke(innerScope) else innerScope
    valueParameterResolver.resolveValueParameters(
      valueParameters, valueParameterDescriptors, headerScope, outerDataFlowInfo, trace
    )

    // Synthetic "field" creation
    if (functionDescriptor is PropertyAccessorDescriptor && functionDescriptor.getExtensionReceiverParameter() == null) {
      val property = function.parent as KtProperty
      val fieldDescriptor = SyntheticFieldDescriptor(functionDescriptor, property)
      innerScope = LexicalScopeImpl(innerScope, functionDescriptor, true, null,
        LexicalScopeKind.PROPERTY_ACCESSOR_BODY,
        LocalRedeclarationChecker.DO_NOTHING) {
        addVariableDescriptor(fieldDescriptor)
      }
      // Check parameter name shadowing
      for (parameter in function.valueParameters) {
        if (SyntheticFieldDescriptor.NAME == parameter.nameAsName) {
          trace.report(Errors.ACCESSOR_PARAMETER_NAME_SHADOWING.on(parameter))
        }
      }
    }

    for (valueParameterDescriptor in valueParameterDescriptors) {
      if (valueParameterDescriptor.isWithAnnotated) {
        innerScope = getScopeForExtensionParameter(functionDescriptor, innerScope, valueParameterDescriptor)
      }
    }

    var dataFlowInfo: DataFlowInfo? = null

    if (beforeBlockBody != null) {
      dataFlowInfo = beforeBlockBody.invoke(headerScope)
    }

    if (function.hasBody()) {
      expressionTypingServices.checkFunctionReturnType(
        innerScope, function, functionDescriptor, if (dataFlowInfo != null) dataFlowInfo else outerDataFlowInfo, null, trace)
    }

    assert(functionDescriptor.returnType != null)
  }

  private fun getScopeForExtensionParameter(
    functionDescriptor: FunctionDescriptor,
    innerScope: LexicalScope,
    valueParameterDescriptor: ValueParameterDescriptor
  ): LexicalScope {
    val metaValueParameterDescriptor =
      if (valueParameterDescriptor is MetaValueParameterDescriptor) valueParameterDescriptor
      else MetaValueParameterDescriptor(project, trace, valueParameterDescriptor as ValueParameterDescriptorImpl)
    var innerScope = innerScope
    val ownerDescriptor = AnonymousFunctionDescriptor(valueParameterDescriptor, //@with S:
      metaValueParameterDescriptor.annotations,
      CallableMemberDescriptor.Kind.DECLARATION,
      metaValueParameterDescriptor.source,
      false)

    ownerDescriptor.initialize(metaValueParameterDescriptor.extensionReceiverParameter, null,
      metaValueParameterDescriptor.typeParameters,
      metaValueParameterDescriptor.valueParameters,
      metaValueParameterDescriptor.returnType,
      Modality.FINAL,
      metaValueParameterDescriptor.visibility)
    innerScope = LexicalScopeImpl(
      parent = innerScope,
      ownerDescriptor = metaValueParameterDescriptor,
      isOwnerDescriptorAccessibleByLabel = true,
      implicitReceiver = metaValueParameterDescriptor.extensionReceiverParameter,
      kind = LexicalScopeKind.FUNCTION_INNER_SCOPE
    )
    return innerScope
  }
}