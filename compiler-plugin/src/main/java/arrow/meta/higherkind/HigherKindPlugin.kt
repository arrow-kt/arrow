package arrow.meta.higherkind

import arrow.meta.extensions.CompilerContext
import arrow.meta.extensions.ExtensionPhase
import arrow.meta.extensions.MetaCompilerPlugin
import com.google.auto.service.AutoService
import org.jetbrains.kotlin.analyzer.ModuleInfo
import org.jetbrains.kotlin.backend.common.BackendContext
import org.jetbrains.kotlin.backend.common.lower.SimpleMemberScope
import org.jetbrains.kotlin.com.intellij.openapi.project.Project
import org.jetbrains.kotlin.compiler.plugin.ComponentRegistrar
import org.jetbrains.kotlin.descriptors.ClassConstructorDescriptor
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.PackageFragmentDescriptor
import org.jetbrains.kotlin.descriptors.PackageFragmentProvider
import org.jetbrains.kotlin.descriptors.SourceElement
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.descriptors.impl.ClassDescriptorImpl
import org.jetbrains.kotlin.descriptors.impl.DeclarationDescriptorVisitorEmptyBodies
import org.jetbrains.kotlin.descriptors.impl.PackageFragmentDescriptorImpl
import org.jetbrains.kotlin.descriptors.impl.TypeParameterDescriptorImpl
import org.jetbrains.kotlin.descriptors.resolveClassByFqName
import org.jetbrains.kotlin.incremental.components.LookupTracker
import org.jetbrains.kotlin.incremental.components.NoLookupLocation
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
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.symbols.impl.IrTypeParameterSymbolImpl
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.IrTypeArgument
import org.jetbrains.kotlin.ir.types.impl.IrSimpleTypeImpl
import org.jetbrains.kotlin.ir.util.transformDeclarationsFlat
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.DescriptorFactory
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.resolve.descriptorUtil.getAllSuperclassesWithoutAny
import org.jetbrains.kotlin.resolve.descriptorUtil.getSuperInterfaces
import org.jetbrains.kotlin.resolve.descriptorUtil.module
import org.jetbrains.kotlin.resolve.descriptorUtil.parents
import org.jetbrains.kotlin.resolve.scopes.MemberScope
import org.jetbrains.kotlin.storage.LockBasedStorageManager
import org.jetbrains.kotlin.storage.StorageManager
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.KotlinTypeFactory
import org.jetbrains.kotlin.types.TypeConstructor
import org.jetbrains.kotlin.types.TypeProjection
import org.jetbrains.kotlin.types.TypeProjectionImpl
import org.jetbrains.kotlin.types.Variance
import org.jetbrains.kotlin.types.typeUtil.isInterface

val kindName: FqName = FqName("arrow.sample.Kind")

val ModuleDescriptor.kindDescriptor: ClassDescriptor?
  get () = module.resolveClassByFqName(kindName, NoLookupLocation.FROM_BACKEND)

val FqName.kindMarkerName: FqName
  get () {
    val segments = pathSegments()
    val pck = segments.dropLast(1)
    val simpleName = segments.last()
    return FqName("${pck.joinToString(".")}.For${simpleName.asString()}")
  }

class KindMarkerDescriptor(containingDeclaration: DeclarationDescriptor, targetName : FqName) : ClassDescriptorImpl(
  containingDeclaration,
  targetName.kindMarkerName.shortName(),
  Modality.FINAL,
  ClassKind.CLASS,
  listOf(containingDeclaration.module.builtIns.any.defaultType),
  SourceElement.NO_SOURCE,
  false,
  LockBasedStorageManager.NO_LOCKS
) {
  override fun getUnsubstitutedMemberScope(): MemberScope = MemberScope.Empty
  override fun getConstructors(): Collection<ClassConstructorDescriptor> =
    listOf(DescriptorFactory.createPrimaryConstructorForObject(this, SourceElement.NO_SOURCE))
}

fun DeclarationDescriptor.kindMarker(targetName : FqName): ClassDescriptor =
  KindMarkerDescriptor(this, targetName)

class ContributedPackageFragmentDescriptor(
  val module: ModuleDescriptor,
  val name: FqName,
  val members: List<DeclarationDescriptor>
) : PackageFragmentDescriptorImpl(module, name) {
  override fun getMemberScope(): MemberScope = SimpleMemberScope(members)
}


@AutoService(ComponentRegistrar::class)
class HigherKindPlugin : MetaCompilerPlugin {
  override fun intercept(): List<ExtensionPhase> =
    meta(
      enableIr(),
      syntheticResolver(
        addSyntheticSupertypes = { descriptor, supertypes ->
          storeDescriptor(descriptor) //store the target descriptor for a later phase
          val isSubtype = supertypes.any {
            !(it.constructor.declarationDescriptor?.defaultType?.isInterface() ?: false)
          }
          if (!isSubtype && descriptor.shouldApplyKind()) {
            val hk = descriptor.higherKind()
            println("${descriptor.name} ~> addSyntheticSupertypes = $hk")
            supertypes.add(hk)
          } else {
            // println("skipped: " + descriptor.name)
          }
        }
      ),
      packageFragmentProvider { project: Project, module: ModuleDescriptor, storageManager: StorageManager, trace: BindingTrace, moduleInfo: ModuleInfo?, lookupTracker: LookupTracker ->
        AddSupertypesPackageFragmentProvider(this, module)
      },
      syntheticResolver(
        generatePackageSyntheticClasses = { descriptor: PackageFragmentDescriptor, name, ctx, declarationProvider, result ->
          val classDescriptor = result.firstOrNull { it.name == name }
          classDescriptor?.let {
            if (it.shouldGenerateKindMarker()) {
              val kindMarker = descriptor.kindMarker(it.fqNameSafe)
              println("${descriptor.name} : ${it.fqNameSafe} ~> generatePackageSyntheticClasses = $kindMarker")
              result.add(kindMarker)
            }
          }
        }
      ),
      syntheticResolver(
        generateSyntheticClasses = { thisDescriptor, name, ctx, declarationProvider, result ->
          println("${thisDescriptor.name} : ${name} ~> syntheticResolver.generateSyntheticClasses = $result")
        },
        getSyntheticNestedClassNames = { thisDescriptor ->
          println("${thisDescriptor.name} ~> syntheticResolver.getSyntheticNestedClassNames")
          listOf(Name.identifier("whatever"))
        },
        generatePackageSyntheticClasses = { thisDescriptor, name, ctx, declarationProvider, result ->
          println("${thisDescriptor.name} ~> PASS 2 ~> syntheticResolver.generatePackageSyntheticClasses,  result : $result")
        }
      ),
      IrGeneration { compilerContext, file, backendContext, bindingContext ->
        backendContext.run {
          file.transformDeclarationsFlat { decl ->
            val result = if (decl is IrClass && decl.descriptor.shouldGenerateKindMarker()) {
              val higherKindSuperType = irHigherKind(decl)
              decl.superTypes.add(higherKindSuperType)
              println("${decl.name} ~> IrGeneration.supertypes.add = $higherKindSuperType")
              val marker = kindMarker(decl)
              //decl.getPackageFragment()?.declarations?.add(marker)
              println("${decl.name} : ${marker.name} ~> IrGeneration.generation = $marker")
              listOf(decl, marker)
            } else {
              listOf(decl)
            }
            result
          }
        }
      }
    )

}

class AddSupertypesPackageFragmentProvider(val compilerContext: CompilerContext, val module: ModuleDescriptor) : PackageFragmentProvider {

  override fun getPackageFragments(fqName: FqName): List<PackageFragmentDescriptor> {
    val pckg = module.getPackage(fqName)
    val descriptorsInPackage = compilerContext.storedDescriptors().filter {
      it.fqNameSafe.parent() == fqName
    }
    val result = descriptorsInPackage.map { descriptor ->
        val kindMarker = pckg.kindMarker(descriptor.fqNameSafe)
        kindMarker
    }
    println("$fqName ~> getPackageFragments = $result")
    return listOf(
      ContributedPackageFragmentDescriptor(module, fqName, result)
    )
  }

  override fun getSubPackagesOf(fqName: FqName, nameFilter: (Name) -> Boolean): Collection<FqName> {
    println("AddSupertypesPackageFramgmentProvider.getSubPackagesOf: $fqName")
    return emptyList()
  }
}

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

fun kotlinType(
  typeConstructor: TypeConstructor,
  typeArguments: List<TypeProjection> = emptyList(),
  nullable: Boolean = false
): KotlinType {
  return KotlinTypeFactory.simpleType(
    annotations = Annotations.EMPTY,
    constructor = typeConstructor,
    arguments = typeArguments,
    nullable = nullable
  )
}

private fun ClassDescriptor.higherKind(): KotlinType {
  return kotlinType(
    typeConstructor = module.resolveClassByFqName(kindName, NoLookupLocation.FROM_BACKEND)?.typeConstructor!!,
    typeArguments = listOf(
      typeVariable(fqNameSafe.kindMarkerName.shortNameOrSpecial()),
      // TypeProjectionImpl(kindMarker().defaultType),
      typeVariable(declaredTypeParameters[0].name)
    )
  )
}

fun ClassDescriptor.typeVariable(
  name: Name
): TypeProjection =
  TypeProjectionImpl(TypeParameterDescriptorImpl.createWithDefaultBound(
    this,
    Annotations.EMPTY,
    false,
    Variance.INVARIANT,
    name,
    0).defaultType)

private fun BackendContext.irHigherKind(decl: IrClass): IrType =
  irType(
    className = "arrow.sample.Kind",
    typeArguments = listOf(
      //irTypeArgument(decl.descriptor.kindMarkerName.as, witness),
      irTypeArgument(decl.descriptor.fqNameSafe.kindMarkerName.shortNameOrSpecial(), decl.descriptor),
      irTypeArgument(decl.typeParameters[0].name, decl.descriptor)
    )
  )

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
  return buildIrClass {
    origin = IRRELEVANT_ORIGIN
    name = target.descriptor.fqNameSafe.kindMarkerName.shortNameOrSpecial()
  }.apply {
    val irClass = this
    parent = target.parent
//    addMember(
//      IrConstructorImpl(
//        UNDEFINED_OFFSET,
//        UNDEFINED_OFFSET,
//        IRRELEVANT_ORIGIN,
//        markerDescriptor.constructors.first(),
//        markerDescriptor.defaultType.toIrType()!!
//      ).also {
//        it.body = IrBlockBodyImpl(
//          UNDEFINED_OFFSET,
//          UNDEFINED_OFFSET,
//          listOf(
//            IrDelegatingConstructorCallImpl(
//              UNDEFINED_OFFSET, UNDEFINED_OFFSET,
//              irBuiltIns.unitType,
//              ir.symbols.externalSymbolTable.referenceConstructor(anyConstructor),
//              anyConstructor
//            )
//          )
//        )
//      }
//    )
    superTypes.add(irBuiltIns.anyType)
    thisReceiver = buildIrValueParameter {
      type = IrSimpleTypeImpl(symbol, hasQuestionMark = false, arguments = emptyList(), annotations = emptyList())
      name = Name.identifier("$this")
    }.also {
      it.parent = irClass
    }
  }
}
