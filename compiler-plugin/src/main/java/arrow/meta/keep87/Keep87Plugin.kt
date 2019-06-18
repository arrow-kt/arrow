package arrow.meta.keep87

import arrow.meta.extensions.ExtensionPhase
import arrow.meta.extensions.MetaCompilerPlugin

//@AutoService(ComponentRegistrar::class)
class Keep87Plugin : MetaCompilerPlugin {
  override fun intercept(): List<ExtensionPhase> =
    meta(
      updateConfig {},
      analysys(
        doAnalysis = { project, module, projectContext, files, bindingTrace, componentProvider -> TODO() },
        analysisCompleted = { project, module, bindingTrace, files -> TODO() }
      ),
      classBuilderFactory { interceptedFactory, bindingContext, diagnostics ->
        TODO()
      },
      storageComponent { declaration, descriptor, context -> TODO() },
      codegen(
        applyFunction = { receiver, resolvedCall, c -> TODO() },
        applyProperty = { receiver, resolvedCall, c -> TODO() },
        generateClassSyntheticParts = { codegen -> TODO() }
      ),
      declarationAttributeAlterer {
        modifierListOwner, declaration, containingDeclaration, currentModality, bindingContext, isImplicitModality -> TODO()
      },
      packageFragmentProvider { project, module, storageManager, trace, moduleInfo, lookupTracker -> TODO() },
      syntheticResolver(
        addSyntheticSupertypes = { thisDescriptor, supertypes -> TODO() },
        generateSyntheticClasses = { thisDescriptor, name, ctx, declarationProvider, result -> TODO() },
        generatePackageSyntheticClasses = { thisDescriptor, name, ctx, declarationProvider, result -> TODO() },
        generateSyntheticMethods = { thisDescriptor, name, bindingContext, fromSupertypes, result -> TODO() },
        generateSyntheticProperties = { thisDescriptor, name, bindingContext, fromSupertypes, result -> TODO() },
        getSyntheticCompanionObjectNameIfNeeded = { thisDescriptor -> TODO() },
        getSyntheticFunctionNames = { thisDescriptor -> TODO() },
        getSyntheticNestedClassNames = { thisDescriptor -> TODO() }
      )
    )
}