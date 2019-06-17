package arrow.meta.higherkind

import arrow.meta.extensions.ExtensionPhase
import arrow.meta.extensions.MetaCompilerPlugin
import com.google.auto.service.AutoService
import org.jetbrains.kotlin.backend.common.BackendContext
import org.jetbrains.kotlin.compiler.plugin.ComponentRegistrar
import org.jetbrains.kotlin.descriptors.ClassConstructorDescriptor
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.PackageFragmentDescriptor
import org.jetbrains.kotlin.descriptors.SourceElement
import org.jetbrains.kotlin.descriptors.Visibilities
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.descriptors.impl.ClassDescriptorImpl
import org.jetbrains.kotlin.descriptors.impl.TypeParameterDescriptorImpl
import org.jetbrains.kotlin.descriptors.resolveClassByFqName
import org.jetbrains.kotlin.incremental.components.NoLookupLocation
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.builders.declarations.IrClassBuilder
import org.jetbrains.kotlin.ir.builders.declarations.IrFunctionBuilder
import org.jetbrains.kotlin.ir.builders.declarations.IrValueParameterBuilder
import org.jetbrains.kotlin.ir.builders.declarations.build
import org.jetbrains.kotlin.ir.builders.declarations.buildClass
import org.jetbrains.kotlin.ir.builders.declarations.buildConstructor
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrConstructor
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOriginImpl
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.declarations.addMember
import org.jetbrains.kotlin.ir.declarations.impl.IrConstructorImpl
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.impl.IrBlockBodyImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrDelegatingConstructorCallImpl
import org.jetbrains.kotlin.ir.symbols.impl.IrTypeParameterSymbolImpl
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.IrTypeArgument
import org.jetbrains.kotlin.ir.types.impl.IrSimpleTypeImpl
import org.jetbrains.kotlin.ir.types.toIrType
import org.jetbrains.kotlin.ir.util.getPackageFragment
import org.jetbrains.kotlin.ir.util.transformDeclarationsFlat
import org.jetbrains.kotlin.js.resolve.diagnostics.findPsi
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtClassBody
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtModifierList
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtPrimaryConstructor
import org.jetbrains.kotlin.psi.KtPureClassOrObject
import org.jetbrains.kotlin.psi.KtPureElement
import org.jetbrains.kotlin.psi.KtSecondaryConstructor
import org.jetbrains.kotlin.psi.KtSuperTypeListEntry
import org.jetbrains.kotlin.psi.synthetics.SyntheticClassOrObjectDescriptor
import org.jetbrains.kotlin.resolve.DescriptorFactory
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.resolve.descriptorUtil.getAllSuperclassesWithoutAny
import org.jetbrains.kotlin.resolve.descriptorUtil.getSuperInterfaces
import org.jetbrains.kotlin.resolve.descriptorUtil.module
import org.jetbrains.kotlin.resolve.descriptorUtil.parents
import org.jetbrains.kotlin.resolve.lazy.LazyClassContext
import org.jetbrains.kotlin.resolve.lazy.declarations.DeclarationProvider
import org.jetbrains.kotlin.resolve.lazy.declarations.PackageMemberDeclarationProvider
import org.jetbrains.kotlin.resolve.scopes.MemberScope
import org.jetbrains.kotlin.storage.LockBasedStorageManager
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.KotlinTypeFactory
import org.jetbrains.kotlin.types.TypeProjection
import org.jetbrains.kotlin.types.TypeProjectionImpl
import org.jetbrains.kotlin.types.Variance
import org.jetbrains.kotlin.types.typeUtil.isInterface

val kindName: FqName = FqName("arrow.sample.Kind")

val ModuleDescriptor.kindDescriptor: ClassDescriptor?
  get () = module.resolveClassByFqName(kindName, NoLookupLocation.FROM_BACKEND)

val ClassDescriptor.kindMarkerName: FqName
  get () {
    val segments = fqNameSafe.pathSegments()
    val pck = segments.dropLast(1)
    val simpleName = segments.last()
    return FqName("${pck.joinToString(".")}.For${simpleName.asString()}")
  }

class KindMarkerDescriptor(descriptor: ClassDescriptor) : ClassDescriptorImpl(
  descriptor.parents.first(),
  descriptor.kindMarkerName.shortName(),
  Modality.FINAL,
  ClassKind.CLASS,
  listOf(descriptor.module.builtIns.any.defaultType),
  SourceElement.NO_SOURCE,
  false,
  LockBasedStorageManager.NO_LOCKS
) {
  override fun getUnsubstitutedMemberScope(): MemberScope = MemberScope.Empty
  override fun getConstructors(): Collection<ClassConstructorDescriptor> =
    listOf(DescriptorFactory.createPrimaryConstructorForObject(this, SourceElement.NO_SOURCE))
}

class SyntheticDeclaration(
  private val _parent: KtPureElement,
  private val _name: String
) : KtPureClassOrObject {

  override fun getName(): String? = _name
  override fun isLocal(): Boolean = false

  override fun getDeclarations(): List<KtDeclaration> = emptyList()
  override fun getSuperTypeListEntries(): List<KtSuperTypeListEntry> = emptyList()
  override fun getCompanionObjects(): List<KtObjectDeclaration> = emptyList()

  override fun hasExplicitPrimaryConstructor(): Boolean = false
  override fun hasPrimaryConstructor(): Boolean = false
  override fun getPrimaryConstructor(): KtPrimaryConstructor? = null
  override fun getPrimaryConstructorModifierList(): KtModifierList? = null
  override fun getPrimaryConstructorParameters(): List<KtParameter> = emptyList()
  override fun getSecondaryConstructors(): List<KtSecondaryConstructor> = emptyList()

  override fun getPsiOrParent() = _parent.psiOrParent
  override fun getParent() = _parent.psiOrParent
  @Suppress("USELESS_ELVIS")
  override fun getContainingKtFile() =
    // in theory `containingKtFile` is `@NotNull` but in practice EA-114080
    _parent.containingKtFile ?: throw IllegalStateException("containingKtFile was null for $_parent of ${_parent.javaClass}")

  override fun getBody(): KtClassBody? = null
}

fun ClassDescriptor.kindMarker(ctx: LazyClassContext, declarationProvider: PackageMemberDeclarationProvider): ClassDescriptor =
  SyntheticClassOrObjectDescriptor(
    ctx,
    SyntheticDeclaration(declarationProvider.getPackageFiles().first(), parents.first().name.asString()),
    parents.first().containingDeclaration!!,
    kindMarkerName.shortName(),
    source,
    ctx.declarationScopeProvider.getResolutionScopeForDeclaration(findPsi()!!),
    Modality.FINAL,
    Visibilities.PUBLIC,
    Visibilities.PRIVATE,
    ClassKind.CLASS,
    false
  )

@AutoService(ComponentRegistrar::class)
class HigherKindPlugin : MetaCompilerPlugin {
  override fun intercept(): List<ExtensionPhase> =
    meta(
      enableIr(),
      syntheticResolver(
        generatePackageSyntheticClasses = { descriptor: PackageFragmentDescriptor, name, ctx, declarationProvider, result ->
          val classDescriptor = result.firstOrNull { it.name == name }
          classDescriptor?.let {
            if (it.shouldGenerateKindMarker()) {
              val kindMarker = it.kindMarker(ctx, declarationProvider)
              println("generatePackageSyntheticClasses.Kind Marker -> ${kindMarker.fqNameSafe}")
              result.add(kindMarker)
            }
          }
        }
//        addSyntheticSupertypes = { descriptor, supertypes ->
//          println("addSyntheticSupertypes: $descriptor")
//          val isSubtype = supertypes.any {
//            !(it.constructor.declarationDescriptor?.defaultType?.isInterface() ?: false)
//          }
//          if (!isSubtype && descriptor.shouldApplyKind()) {
//            val hk = descriptor.higherKind()
//            println("syntheticResolver.addSyntheticSupertypes: ${descriptor.parents.toList()}, $supertypes, hk: $hk")
//            println("SuperType -> ${descriptor.name} : $hk")
//            supertypes.add(hk)
//          } else {
//            // println("skipped: " + descriptor.name)
//          }
//        }
      ),
      IrGeneration { compilerContext, file, backendContext, bindingContext ->
        backendContext.run {
          file.transformDeclarationsFlat { decl ->
            val result = if (decl is IrClass && decl.descriptor.shouldGenerateKindMarker()) {
              println("Found IrDeclaration: ${decl.descriptor.name}")
              val witness: ClassDescriptor = decl.descriptor.module.resolveClassByFqName(decl.descriptor.kindMarkerName, NoLookupLocation.FROM_BACKEND)!!
              val higherKindSuperType = irHigherKind(witness, decl)
              decl.superTypes.add(higherKindSuperType)
              val marker = kindMarker(decl)
              decl.getPackageFragment()?.declarations?.add(marker)
              listOf(decl, marker)
            } else if (decl is IrClass) {
              println("Found declaration: ${decl.descriptor.name}")
              listOf(decl)
            } else {
              listOf(decl)
            }
            result
          }
        }
      }
    )

  private fun ClassDescriptor.shouldGenerateKindMarker(): Boolean =
    declaredTypeParameters.isNotEmpty() &&
      fqNameSafe != kindName &&
      !getAllSuperclassesWithoutAny().any { s -> !s.defaultType.isInterface() }

  private fun ClassDescriptor.shouldApplyKind(debug: Boolean = false): Boolean {
    if (debug) println("$name shouldApplyKind = ${getAllSuperclassesWithoutAny().toList()}, ${getAllSuperclassesWithoutAny().any { it.name.isSpecial }}, superInterfaces = ${getSuperInterfaces()}")
    val result = declaredTypeParameters.isNotEmpty() &&
      fqNameSafe != kindName &&
      !getSuperInterfaces().contains(module.kindDescriptor)
    if (debug) println("result: " + result)
    return result
  }

  fun ClassDescriptor.kotlinType(
    name: FqName,
    typeArguments: List<TypeProjection> = emptyList(),
    nullable: Boolean = false
  ): KotlinType {
    val descriptor = module.resolveClassByFqName(kindMarkerName, NoLookupLocation.FROM_BACKEND)
    val typeConstructor = descriptor?.typeConstructor!!
    return KotlinTypeFactory.simpleType(
      annotations = Annotations.EMPTY,
      constructor = typeConstructor,
      arguments = typeArguments,
      nullable = nullable
    )
  }

  fun ClassDescriptor.typeVariable(
    name: Name,
    containingDeclaration: FqName
  ): TypeProjection =
    TypeProjectionImpl(TypeParameterDescriptorImpl.createWithDefaultBound(
      module.resolveClassByFqName(containingDeclaration, NoLookupLocation.FROM_BACKEND)!!,
      Annotations.EMPTY,
      false,
      Variance.INVARIANT,
      name,
      0).defaultType)

  private fun BackendContext.irHigherKind(witness: ClassDescriptor, decl: IrClass): IrType =
    irType(
      className = "arrow.sample.Kind",
      typeArguments = listOf(
        irTypeArgument(witness.name, witness),
        irTypeArgument(decl.typeParameters[0].name, decl.descriptor)
      )
    )

}

fun BackendContext.irType(
  className: String,
  typeArguments: List<IrTypeArgument> = emptyList(),
  nullable: Boolean = false,
  annotations: List<IrCall> = emptyList()
): IrType =
  IrSimpleTypeImpl(
    classifier = ir.symbols.externalSymbolTable.referenceClass(
      ir.context.getClass(FqName(className))
    ),
    hasQuestionMark = nullable,
    arguments = typeArguments,
    annotations = annotations
  )

fun irTypeArgument(
  name: Name,
  containingDeclaration: DeclarationDescriptor,
  nullable: Boolean = false,
  annotations: List<IrCall> = emptyList()
): IrTypeArgument =
  IrSimpleTypeImpl(
    classifier = IrTypeParameterSymbolImpl(
      TypeParameterDescriptorImpl.createWithDefaultBound(
        containingDeclaration,
        Annotations.EMPTY,
        false,
        Variance.INVARIANT,
        name,
        0
      )),
    hasQuestionMark = nullable,
    arguments = emptyList(),
    annotations = annotations
  )

val IRRELEVANT_ORIGIN = object : IrDeclarationOriginImpl("HIGHER_KIND_GENERATOR", true) {}

fun buildIrClass(b: IrClassBuilder.() -> Unit) =
  IrClassBuilder().run {
    b()
    buildClass()
  }

fun buildIrValueParameter(b: IrValueParameterBuilder.() -> Unit): IrValueParameter =
  IrValueParameterBuilder().run {
    b()
    build()
  }

fun buildIrConstructor(b: IrFunctionBuilder.() -> Unit): IrConstructor = IrFunctionBuilder().run {
  b()
  buildConstructor()
}

fun BackendContext.kindMarker(target: IrClass): IrClass {
  val markerDescriptor = target.descriptor.module.resolveClassByFqName(target.descriptor.kindMarkerName, NoLookupLocation.FROM_BACKEND)!!
  val anyConstructor = builtIns.any.constructors.single()
  return buildIrClass {
    origin = IRRELEVANT_ORIGIN
    name = markerDescriptor.name
  }.apply {
    val irClass = this
    parent = target.parent
    addMember(
      IrConstructorImpl(
        UNDEFINED_OFFSET,
        UNDEFINED_OFFSET,
        IRRELEVANT_ORIGIN,
        markerDescriptor.constructors.first(),
        markerDescriptor.defaultType.toIrType()!!
      ).also {
        it.body = IrBlockBodyImpl(
          UNDEFINED_OFFSET,
          UNDEFINED_OFFSET,
          listOf(
            IrDelegatingConstructorCallImpl(
              UNDEFINED_OFFSET, UNDEFINED_OFFSET,
              irBuiltIns.unitType,
              ir.symbols.externalSymbolTable.referenceConstructor(anyConstructor),
              anyConstructor
            )
          )
        )
      }
    )
    superTypes.add(irBuiltIns.anyType)
    thisReceiver = buildIrValueParameter {
      type = IrSimpleTypeImpl(symbol, hasQuestionMark = false, arguments = emptyList(), annotations = emptyList())
      name = Name.identifier("$this")
    }.also {
      it.parent = irClass
    }
  }
}
