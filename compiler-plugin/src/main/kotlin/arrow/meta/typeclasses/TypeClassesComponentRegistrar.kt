package arrow.meta.typeclasses

import arrow.meta.extensions.CompilerContext
import arrow.meta.extensions.ExtensionPhase
import arrow.meta.extensions.MetaComponentRegistrar
import arrow.meta.higherkind.buildIrValueParameter
import org.jetbrains.kotlin.analyzer.AnalysisResult
import org.jetbrains.kotlin.backend.common.BackendContext
import org.jetbrains.kotlin.backend.common.serialization.irrelevantOrigin
import org.jetbrains.kotlin.container.useImpl
import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.ValueParameterDescriptor
import org.jetbrains.kotlin.descriptors.Visibilities
import org.jetbrains.kotlin.descriptors.annotations.AnnotationDescriptor
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.descriptors.impl.AnonymousFunctionDescriptor
import org.jetbrains.kotlin.descriptors.impl.LazyClassReceiverParameterDescriptor
import org.jetbrains.kotlin.descriptors.impl.ReceiverParameterDescriptorImpl
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.impl.IrClassImpl
import org.jetbrains.kotlin.ir.symbols.impl.IrClassSymbolImpl
import org.jetbrains.kotlin.ir.types.impl.IrSimpleTypeImpl
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.name.SpecialNames
import org.jetbrains.kotlin.psi.synthetics.SyntheticClassOrObjectDescriptor
import org.jetbrains.kotlin.resolve.descriptorUtil.getSuperInterfaces
import org.jetbrains.kotlin.resolve.lazy.LazyClassContext
import org.jetbrains.kotlin.resolve.lazy.declarations.ClassMemberDeclarationProvider
import org.jetbrains.kotlin.resolve.lazy.descriptors.LazyClassDescriptor
import org.jetbrains.kotlin.resolve.lazy.descriptors.LazyClassMemberScope
import org.jetbrains.kotlin.resolve.scopes.LexicalScope
import org.jetbrains.kotlin.resolve.scopes.LexicalScopeImpl
import org.jetbrains.kotlin.resolve.scopes.LexicalScopeKind
import org.jetbrains.kotlin.resolve.scopes.receivers.ExtensionReceiver

val extensionAnnotationName = FqName("arrow.extension")
val withAnnotationName = FqName("arrow.with")

class TypeClassesComponentRegistrar : MetaComponentRegistrar {

  override fun intercept(): List<ExtensionPhase> =
    meta(
      enableIr(),
      storageComponent(
        registerModuleComponents = { container, platform, moduleDescriptor ->
          container.useImpl<ExtensionResolutionCallChecker>()
        },
        check = { declaration, descriptor, context ->

        }
      ),
      analysys(
        doAnalysis = { project, module, projectContext, files, bindingTrace, componentProvider ->
          println("analysys.doAnalysis")
          null
        },
        analysisCompleted = { project, module, bindingTrace, files ->
          println("analysys.analysisCompleted")
          null
        }
      ),
      syntheticScopes(
        getSyntheticScopes = { moduleDescriptor, javaSyntheticPropertiesScope ->
          println("getSyntheticScopes")
          emptyList()
        }
      ),
      syntheticResolver(
        generatePackageSyntheticClasses = { thisDescriptor, name, ctx, declarationProvider, result ->
          //println("generatePackageSyntheticClasses: $name, result: $result")
        },
        generateSyntheticClasses = { thisDescriptor, name, ctx, declarationProvider, result ->
          //println("generateSyntheticClasses: $name, result: $result")
        },
        generateSyntheticMethods = { thisDescriptor, name, bindingContext, fromSupertypes, result ->

          //          val functionDescriptor: SimpleFunctionDescriptorImpl = result.first() as SimpleFunctionDescriptorImpl
//          functionDescriptor.initialize()
//          functionDescriptor.valueParameters.filter {
//            it.annotations.hasAnnotation(withAnnotationName)
//          }.map {
//            it.
//          }
          println("generateSyntheticMethods: $name, result: $result")
        }
      )
    )

  private fun CompilerContext.getSyntheticCompanionName(declarationDescriptor: ClassDescriptor): Name? {
    return if (storedDescriptors().contains(declarationDescriptor)) {
      SpecialNames.DEFAULT_NAME_FOR_COMPANION_OBJECT
    } else null
  }

  private fun CompilerContext.generateSyntheticCompanionIfNeeded(extensionDeclaration: ClassDescriptor, declarationProvider: ClassMemberDeclarationProvider, ctx: LazyClassContext): ClassDescriptor? {
    val annotationDescriptor: AnnotationDescriptor? = extensionDeclaration.extensionAnnotationDescriptor()
    val typeClass = extensionDeclaration.typeClassDescriptor()
    return annotationDescriptor?.let {
      typeClass?.let {
        extensionDeclaration.dataTypeDescriptor()?.let { dataTypeDescriptor ->
          storeDescriptor(dataTypeDescriptor)
          if (dataTypeDescriptor.companionObjectDescriptor == null) {
            dataTypeDescriptor.companionDescriptor(declarationProvider, ctx)?.let { companionDescriptor ->
              println("Found: ${extensionDeclaration.name} for typeclass : $it, data type: $dataTypeDescriptor, companion descriptor: $companionDescriptor")
              dataTypeDescriptor.withCompanion(companionDescriptor)
            }
          } else null
        }
      }
    }
  }

}

fun FunctionDescriptor.resolveCallArguments(): Unit {
  valueParameters.replaceAll {
    if (it.isWithAnnotated) ExtensionValueArgument(it)
    else it
  }
}

fun FunctionDescriptor.scopesFromWithParameters(lexicalScope: LexicalScope): LexicalScope {
  var acc = lexicalScope
  (dispatchReceiverParameter as? LazyClassReceiverParameterDescriptor)?.let {
    it.containingDeclaration as? LazyClassDescriptor }?.let {
    it.unsubstitutedMemberScope as? LazyClassMemberScope}?.let {
    it.getPrimaryConstructor()?.valueParameters?.forEach {
      acc = getScopeForExtensionParameter(this, acc, it)
    }
  }
  return acc
}

private fun getScopeForExtensionParameter(
  functionDescriptor: FunctionDescriptor,
  innerScope: LexicalScope,
  valueParameterDescriptor: ValueParameterDescriptor
): LexicalScope {
  var innerScope = innerScope
  val ownerDescriptor = AnonymousFunctionDescriptor(valueParameterDescriptor,
    valueParameterDescriptor.annotations,
    CallableMemberDescriptor.Kind.DECLARATION,
    valueParameterDescriptor.getSource(),
    false)
  val extensionReceiver = ExtensionReceiver(ownerDescriptor,
    valueParameterDescriptor.getType(),
    null)

  val extensionReceiverParamDescriptor = ReceiverParameterDescriptorImpl(ownerDescriptor,
    extensionReceiver,
    ownerDescriptor.annotations)

  ownerDescriptor.initialize(extensionReceiverParamDescriptor, null,
    valueParameterDescriptor.typeParameters,
    valueParameterDescriptor.valueParameters,
    valueParameterDescriptor.returnType,
    Modality.FINAL,
    valueParameterDescriptor.visibility)
  innerScope = LexicalScopeImpl(innerScope, ownerDescriptor, true, extensionReceiverParamDescriptor, LexicalScopeKind.FUNCTION_INNER_SCOPE)
  return innerScope
}

val ClassDescriptor.isExtensionAnnotated: Boolean
  get() = annotations.findAnnotation(extensionAnnotationName) != null

val ValueParameterDescriptor.isWithAnnotated: Boolean
  get() = annotations.findAnnotation(withAnnotationName) != null

private fun BackendContext.irCompanionDescriptor(descriptor: ClassDescriptor): IrClass? =
  IrClassImpl(
    UNDEFINED_OFFSET,
    UNDEFINED_OFFSET,
    irrelevantOrigin,
    IrClassSymbolImpl(descriptor)
  ).apply {
    val irClass = this
    superTypes.add(irBuiltIns.anyType)
    thisReceiver = buildIrValueParameter {
      type = IrSimpleTypeImpl(
        symbol,
        hasQuestionMark = false,
        arguments = emptyList(),
        annotations = emptyList()
      )
      name = Name.identifier("$this")
    }.also {
      it.parent = irClass
    }
  }

private fun ClassDescriptor.companionDescriptor(
  declarationProvider: ClassMemberDeclarationProvider,
  ctx: LazyClassContext
): ClassDescriptor? =
  (companionObjectDescriptor
    ?: (this as LazyClassDescriptor).createSyntheticCompanionObjectDescriptor(declarationProvider, ctx))

private fun ClassDescriptor.typeClassDescriptor(): ClassDescriptor? =
  getSuperInterfaces().firstOrNull {
    it.declaredTypeParameters.isNotEmpty()
  }

private fun ClassDescriptor.dataTypeDescriptor() =
  typeConstructor.supertypes.first().arguments.first().type.constructor.declarationDescriptor as? ClassDescriptor

private fun ClassDescriptor.extensionAnnotationDescriptor(): AnnotationDescriptor? =
  annotations.findAnnotation(extensionAnnotationName)

private fun ClassDescriptor.isTypeclassExtension(): Boolean =
  extensionAnnotationDescriptor() != null

private fun LazyClassDescriptor.createSyntheticCompanionObjectDescriptor(declarationProvider: ClassMemberDeclarationProvider, c: LazyClassContext): SyntheticClassOrObjectDescriptor? {
  val syntheticCompanionName = c.syntheticResolveExtension.getSyntheticCompanionObjectNameIfNeeded(this) ?: return null
  val companionDescriptor = SyntheticClassOrObjectDescriptor(c,
    /* parentClassOrObject= */ declarationProvider.correspondingClassOrObject!!,
    this, syntheticCompanionName, source,
    /* outerScope= */ c.declarationScopeProvider.getResolutionScopeForDeclaration(declarationProvider.ownerInfo!!.scopeAnchor),
    Modality.FINAL, Visibilities.PUBLIC, Annotations.EMPTY, Visibilities.PRIVATE, ClassKind.OBJECT, true)
  companionDescriptor.initialize()
  return companionDescriptor
}

class AddCompanionClassDescriptor(
  delegate: ClassDescriptor,
  val companionDescriptor: ClassDescriptor
) : ClassDescriptor by delegate {
  override fun getCompanionObjectDescriptor(): ClassDescriptor? =
    companionDescriptor
}

fun ClassDescriptor.withCompanion(companion: ClassDescriptor): ClassDescriptor =
  AddCompanionClassDescriptor(this, companion)


