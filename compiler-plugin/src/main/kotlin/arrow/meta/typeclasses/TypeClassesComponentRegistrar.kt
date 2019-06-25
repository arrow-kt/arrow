package arrow.meta.typeclasses

import arrow.meta.extensions.CompilerContext
import arrow.meta.extensions.ExtensionPhase
import arrow.meta.extensions.MetaComponentRegistrar
import arrow.meta.higherkind.buildIrValueParameter
import org.jetbrains.kotlin.backend.common.BackendContext
import org.jetbrains.kotlin.backend.common.ir.addChild
import org.jetbrains.kotlin.backend.common.serialization.irrelevantOrigin
import org.jetbrains.kotlin.descriptors.ClassConstructorDescriptor
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.SourceElement
import org.jetbrains.kotlin.descriptors.Visibilities
import org.jetbrains.kotlin.descriptors.Visibility
import org.jetbrains.kotlin.descriptors.annotations.AnnotationDescriptor
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.descriptors.impl.ClassDescriptorImpl
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.impl.IrClassImpl
import org.jetbrains.kotlin.ir.symbols.impl.IrClassSymbolImpl
import org.jetbrains.kotlin.ir.types.impl.IrSimpleTypeImpl
import org.jetbrains.kotlin.ir.util.transformDeclarationsFlat
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.name.SpecialNames
import org.jetbrains.kotlin.psi.synthetics.SyntheticClassOrObjectDescriptor
import org.jetbrains.kotlin.resolve.DescriptorFactory
import org.jetbrains.kotlin.resolve.descriptorUtil.getSuperInterfaces
import org.jetbrains.kotlin.resolve.descriptorUtil.module
import org.jetbrains.kotlin.resolve.lazy.LazyClassContext
import org.jetbrains.kotlin.resolve.lazy.declarations.ClassMemberDeclarationProvider
import org.jetbrains.kotlin.resolve.lazy.descriptors.LazyClassDescriptor
import org.jetbrains.kotlin.resolve.scopes.MemberScope
import org.jetbrains.kotlin.storage.LockBasedStorageManager

val extensionAnnotationName = FqName("arrow.extension")

class TypeClassesComponentRegistrar : MetaComponentRegistrar {

  override fun intercept(): List<ExtensionPhase> =
    meta(
      enableIr(),
      syntheticResolver(
        generateSyntheticClasses = { extensionDeclaration: ClassDescriptor,
                                     name: Name,
                                     ctx: LazyClassContext,
                                     declarationProvider: ClassMemberDeclarationProvider,
                                     result: MutableSet<ClassDescriptor> ->
          println("generateSyntheticClasses: $name, result: $result")
          generateSyntheticCompanionIfNeeded(extensionDeclaration, declarationProvider, ctx)?.let {
            result.add(it)
          }
        },
        getSyntheticCompanionObjectNameIfNeeded = { declarationDescriptor ->
          getSyntheticCompanionName(declarationDescriptor)
        },
        getSyntheticFunctionNames = { declarationDescriptor ->
          println("getSyntheticFunctionNames: $declarationDescriptor")
          null
        }
      ),
      IrGeneration { compilerContext, file, backendContext, bindingContext ->
        println("~> IrGeneration")
        backendContext.run {
          file.transformDeclarationsFlat { decl ->
            val result = if (decl is IrClass && decl.descriptor.isTypeclassExtension()) {
              val annotationDescriptor: AnnotationDescriptor? = decl.descriptor.extensionAnnotationDescriptor()
              val typeClass = decl.descriptor.typeClassDescriptor()
              annotationDescriptor?.let {
                typeClass?.let {
                  val dataTypeDescriptor = decl.descriptor.dataTypeDescriptor()
                  if (dataTypeDescriptor != null) {
                    val companionDescriptor = irCompanionDescriptor(dataTypeDescriptor)
                    if (companionDescriptor != null) {
                      println("IR Generating: ${decl.descriptor.name} for typeclass : $it, data type: $dataTypeDescriptor, companion descriptor: $companionDescriptor, result: $companionDescriptor")
                      decl.addChild(companionDescriptor)
                      listOf(decl)
                    } else listOf(decl)
                  } else listOf(decl)
                }
              }
            } else {
              listOf(decl)
            }
            result
          }
        }
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


