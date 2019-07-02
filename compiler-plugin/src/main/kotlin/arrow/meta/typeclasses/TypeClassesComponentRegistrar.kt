package arrow.meta.typeclasses

import arrow.meta.extensions.CompilerContext
import arrow.meta.extensions.ExtensionPhase
import arrow.meta.extensions.MetaComponentRegistrar
import arrow.meta.higherkind.buildIrValueParameter
import arrow.meta.utils.MetaBodyResolver
import arrow.meta.utils.MetaCallResolver
import org.jetbrains.kotlin.backend.common.BackendContext
import org.jetbrains.kotlin.backend.common.serialization.irrelevantOrigin
import org.jetbrains.kotlin.container.StorageComponentContainer
import org.jetbrains.kotlin.container.registerSingleton
import org.jetbrains.kotlin.container.useImpl
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.ValueParameterDescriptor
import org.jetbrains.kotlin.descriptors.Visibilities
import org.jetbrains.kotlin.descriptors.annotations.AnnotationDescriptor
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.impl.IrClassImpl
import org.jetbrains.kotlin.ir.symbols.impl.IrClassSymbolImpl
import org.jetbrains.kotlin.ir.types.impl.IrSimpleTypeImpl
import org.jetbrains.kotlin.js.resolve.diagnostics.findPsi
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.name.SpecialNames
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.synthetics.SyntheticClassOrObjectDescriptor
import org.jetbrains.kotlin.resolve.descriptorUtil.getSuperInterfaces
import org.jetbrains.kotlin.resolve.lazy.LazyClassContext
import org.jetbrains.kotlin.resolve.lazy.declarations.ClassMemberDeclarationProvider
import org.jetbrains.kotlin.resolve.lazy.descriptors.LazyClassDescriptor

val extensionAnnotationName = FqName("arrow.extension")
val withAnnotationName = FqName("arrow.with")

class TypeClassesComponentRegistrar : MetaComponentRegistrar {

  override fun intercept(): List<ExtensionPhase> =
    meta(
      enableIr(),
      func({
        "fun <$typeArguments> foo($valueParameters): Unit = $body"
      }) { fn ->
        "fun <$typeArguments> foo(): Unit = $body"
      },
//      storageComponent(
//        registerModuleComponents = { container: StorageComponentContainer, platform, moduleDescriptor ->
//          container.useImpl<ExtensionResolutionCallChecker>()
//          container.useImpl<TypeClassPlatformDiagnosticSuppressor>()
//          container.useImpl<MetaCallResolver>()
//          container.useImpl<MetaBodyResolver>()
//
//        },
//        check = { declaration, descriptor, context ->
//
//        }
//      ),
//      syntheticResolver(
//        generateSyntheticMethods = { thisDescriptor, name, bindingContext, fromSupertypes, result ->
//          println(result)
//        }
//      )
//      ,
//      analysys(
//        doAnalysis = { project, module, projectContext, files, bindingTrace, componentProvider ->
//          componentProvider as StorageComponentContainer
//          println("analysys.doAnalysis")
//          null
//        },
//        analysisCompleted = { project, module, bindingTrace, files ->
//          println("analysys.analysisCompleted")
//          null
//        }
//      ),
//      syntheticScopes(
//        getSyntheticScopes = { moduleDescriptor, javaSyntheticPropertiesScope ->
//          println("getSyntheticScopes")
//          emptyList()
//        }
//      ),
      IrGeneration { compilerContext, file, backendContext, bindingContext ->
        println("IRGeneration!")
      }
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


/*
val descriptor = receiver.declarationDescriptor
                if (descriptor is ValueParameterDescriptorImpl && descriptor.isExtension) {
                    val receiverParameterDescriptor = ReceiverParameterDescriptorImpl(descriptor, receiver, descriptor.annotations)
                    context.symbolTable.declareValueParameter(descriptor.startOffset!!,
                                                              descriptor.endOffset!!,
                                                              IrDeclarationOrigin.DEFINED,
                                                              receiverParameterDescriptor,
                                                              receiverParameterDescriptor.type.toIrType())
                    IrGetValueImpl(
                            defaultStartOffset, defaultStartOffset, irReceiverType,
                            context.symbolTable.referenceValueParameter(receiverParameterDescriptor)
                    )
                } else {
                    IrGetValueImpl(
                            defaultStartOffset, defaultStartOffset, irReceiverType,
                            context.symbolTable.referenceValueParameter(receiver.declarationDescriptor.extensionReceiverParameter!!)
                    )
                }
 */
//
//class ExtensionAwareValueParameterDescriptor(delegate: ValueParameterDescriptor, val symbolTable: SymbolTable): ValueParameterDescriptor by delegate {
//  override fun getExtensionReceiverParameter(): ReceiverParameterDescriptor? {
//
//    val receiverParameterDescriptor = ReceiverParameterDescriptorImpl(this, , annotations)
//    symbolTable.declareValueParameter(startOffset!!,
//      endOffset!!,
//      IrDeclarationOrigin.DEFINED,
//      receiverParameterDescriptor,
//      receiverParameterDescriptor.type.toIrType())
//    return receiverParameterDescriptor
//  }
//}