package arrow.meta.plugin.idea.plugins.typeclasses

import arrow.meta.Meta
import arrow.meta.Plugin
import arrow.meta.dsl.platform.ideRegistry
import arrow.meta.invoke
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.ExtensionPhase
import arrow.meta.plugin.idea.IdeMetaPlugin
import arrow.meta.plugins.typeclasses.hasExtensionDefaultValue
import arrow.meta.quotes.get
import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.ReceiverParameterDescriptor
import org.jetbrains.kotlin.descriptors.SimpleFunctionDescriptor
import org.jetbrains.kotlin.descriptors.ValueParameterDescriptor
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.descriptors.impl.AnonymousFunctionDescriptor
import org.jetbrains.kotlin.descriptors.impl.ReceiverParameterDescriptorImpl
import org.jetbrains.kotlin.js.resolve.diagnostics.findPsi
import org.jetbrains.kotlin.psi.KtDeclarationWithBody
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.psiUtil.blockExpressionsOrSingle
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.FunctionDescriptorUtil
import org.jetbrains.kotlin.resolve.OverloadChecker
import org.jetbrains.kotlin.resolve.StatementFilter
import org.jetbrains.kotlin.resolve.calls.results.TypeSpecificityComparator
import org.jetbrains.kotlin.resolve.calls.smartcasts.DataFlowInfo
import org.jetbrains.kotlin.resolve.calls.smartcasts.ImplicitSmartCasts
import org.jetbrains.kotlin.resolve.lazy.ResolveSession
import org.jetbrains.kotlin.resolve.lazy.descriptors.LazyClassDescriptor
import org.jetbrains.kotlin.resolve.scopes.LexicalScope
import org.jetbrains.kotlin.resolve.scopes.LexicalScopeImpl
import org.jetbrains.kotlin.resolve.scopes.LexicalScopeKind
import org.jetbrains.kotlin.resolve.scopes.receivers.ExpressionReceiver
import org.jetbrains.kotlin.resolve.scopes.receivers.ExtensionReceiver
import org.jetbrains.kotlin.types.IntersectionTypeConstructor
import org.jetbrains.kotlin.types.KotlinTypeFactory
import org.jetbrains.kotlin.types.SimpleType
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

val IdeMetaPlugin.typeclassesIdePlugin: Plugin
  get() = "TypeclassesIdePlugin" {
    meta(
      ideSyntheticBodyResolution()
    )
  }


private fun Meta.ideSyntheticBodyResolution(): ExtensionPhase = ideRegistry {
  syntheticResolver(
    generateSyntheticMethods = { thisDescriptor, name, bindingContext, fromSupertypes, result ->
      thisDescriptor.safeAs<LazyClassDescriptor>()?.let { lazyDescriptor ->
        val lazyClassContext: Any = lazyDescriptor["c"]
        lazyClassContext.safeAs<ResolveSession>()?.let { session ->
          result.firstOrNull { it.name == name }?.let { function ->
            resolveBodyWithExtensionsScope(session, function)
          }
          println("typeclasses.syntheticResolver.generateSyntheticMethods: $thisDescriptor, $name, $fromSupertypes, $result")
        }
      }
    }
  )
}

private fun CompilerContext.resolveBodyWithExtensionsScope(session: ResolveSession, function: SimpleFunctionDescriptor): Unit {
  function.findPsi().safeAs<KtDeclarationWithBody>()?.let { ktCallable ->
    val functionScope = session.declarationScopeProvider.getResolutionScopeForDeclaration(ktCallable)
    val innerScope = FunctionDescriptorUtil.getFunctionInnerScope(functionScope, function, session.trace, OverloadChecker(TypeSpecificityComparator.NONE))
    val bodyResolver = analyzer?.createBodyResolver(
      session, session.trace, ktCallable.containingKtFile, StatementFilter.NONE
    )
    function.extensionReceiverParameter?.let { originalReceiver ->
      val intersectedTypes = function.valueParameters.filter { it.shouldEnhanceScope() }.map { it.type }
      val modifiedScope = function.valueParameters.scopeForExtensionParameters(innerScope)
      val typeIntersection =
        if (intersectedTypes.isNotEmpty()) {
          val constructor = IntersectionTypeConstructor(intersectedTypes)
          KotlinTypeFactory.simpleTypeWithNonTrivialMemberScope(
            Annotations.EMPTY,
            constructor,
            emptyList(),
            false,
            constructor.createScopeForKotlinType()
          )
        } else null
      typeIntersection?.let { uberExtendedType ->
        //function.applySmartCast(originalReceiver, uberExtendedType, ktCallable, session)
        bodyResolver?.resolveFunctionBody(DataFlowInfo.EMPTY, session.trace, ktCallable, function, modifiedScope)
      }
    }
  }
}


private fun SimpleFunctionDescriptor.applySmartCast(
  originalReceiver: ReceiverParameterDescriptor,
  uberExtendedType: SimpleType,
  ktCallable: KtDeclarationWithBody,
  session: ResolveSession
) {
  val extensionReceiver = ExtensionReceiver(this, originalReceiver.type, null)
  val smartCast = ImplicitSmartCasts(extensionReceiver, uberExtendedType)
  ktCallable.blockExpressionsOrSingle().filterIsInstance<KtExpression>().firstOrNull()?.let { expression ->
    session.trace.record(BindingContext.IMPLICIT_RECEIVER_SMARTCAST, expression, smartCast)
    ExpressionReceiver.create(expression, uberExtendedType, session.trace.bindingContext)
  }
}

private fun List<ValueParameterDescriptor>.scopeForExtensionParameters(innerScope: LexicalScope): LexicalScope =
  fold(innerScope) { currentScope, valueParameterDescriptor ->
    val ownerDescriptor = AnonymousFunctionDescriptor(valueParameterDescriptor, valueParameterDescriptor.annotations, CallableMemberDescriptor.Kind.DECLARATION, valueParameterDescriptor.source, false)
    val extensionReceiver = ExtensionReceiver(ownerDescriptor, valueParameterDescriptor.type, null)
    val extensionReceiverParamDescriptor = ReceiverParameterDescriptorImpl(ownerDescriptor, extensionReceiver, ownerDescriptor.annotations)
    ownerDescriptor.initialize(extensionReceiverParamDescriptor, null, valueParameterDescriptor.typeParameters, valueParameterDescriptor.valueParameters, valueParameterDescriptor.returnType, Modality.FINAL, valueParameterDescriptor.visibility)
    LexicalScopeImpl(currentScope, ownerDescriptor, true, extensionReceiverParamDescriptor, LexicalScopeKind.FUNCTION_INNER_SCOPE)
  }

private fun ValueParameterDescriptor.shouldEnhanceScope(): Boolean {
  val ktParameter = findPsi().safeAs<KtParameter>()
  return ktParameter?.hasExtensionDefaultValue() == true
}
