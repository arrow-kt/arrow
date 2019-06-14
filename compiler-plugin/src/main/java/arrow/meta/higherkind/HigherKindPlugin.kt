package arrow.meta.higherkind

import arrow.meta.extensions.ExtensionPhase
import arrow.meta.extensions.MetaCompilerPlugin
import com.google.auto.service.AutoService
import org.jetbrains.kotlin.backend.common.BackendContext
import org.jetbrains.kotlin.compiler.plugin.ComponentRegistrar
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.PackageFragmentDescriptor
import org.jetbrains.kotlin.descriptors.SourceElement
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.descriptors.impl.ClassDescriptorImpl
import org.jetbrains.kotlin.descriptors.impl.TypeParameterDescriptorImpl
import org.jetbrains.kotlin.descriptors.resolveClassByFqName
import org.jetbrains.kotlin.incremental.components.NoLookupLocation
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrDeclaration
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.symbols.impl.IrTypeParameterSymbolImpl
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.IrTypeArgument
import org.jetbrains.kotlin.ir.types.impl.IrSimpleTypeImpl
import org.jetbrains.kotlin.ir.visitors.IrElementVisitorVoid
import org.jetbrains.kotlin.ir.visitors.acceptChildrenVoid
import org.jetbrains.kotlin.ir.visitors.acceptVoid
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.resolve.descriptorUtil.getAllSuperclassesWithoutAny
import org.jetbrains.kotlin.resolve.descriptorUtil.getSuperInterfaces
import org.jetbrains.kotlin.resolve.descriptorUtil.module
import org.jetbrains.kotlin.resolve.descriptorUtil.parents
import org.jetbrains.kotlin.resolve.lazy.LazyClassContext
import org.jetbrains.kotlin.types.ErrorUtils
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


fun ClassDescriptor.kindMarker(ctx: LazyClassContext): ClassDescriptor =
  ClassDescriptorImpl(
    parents.first(),
    kindMarkerName.shortName(),
    Modality.FINAL,
    ClassKind.CLASS,
    emptyList<KotlinType>(),
    SourceElement.NO_SOURCE,
    false,
    ctx.storageManager
  )

@AutoService(ComponentRegistrar::class)
class HigherKindPlugin : MetaCompilerPlugin {
  override fun intercept(): List<ExtensionPhase> =
    meta(
      //enableIr(),
      syntheticResolver(
        generateSyntheticClasses = { descriptor, name, ctx, declarationProvider, result ->
          println("generateSyntheticClasses.result: $result")
        },
        generatePackageSyntheticClasses = { descriptor: PackageFragmentDescriptor, name, ctx, declarationProvider, result ->
          val classDescriptor = result.firstOrNull { it.name == name }
          println("generatePackageSyntheticClasses: $classDescriptor")
          classDescriptor?.let {
            if (it.shouldGenerateKindMarker()) {
              val kindMarker = it.kindMarker(ctx)
              println("Kind Marker -> ${kindMarker.fqNameSafe}")
              result.add(kindMarker)
            }
          }
//          println("generatePackageSyntheticClasses.result: ${descriptor.name}, result: $result, name: $name")
        },
        addSyntheticSupertypes = { descriptor, supertypes ->
          val isSubtype = supertypes.any { !(it.constructor.declarationDescriptor?.defaultType?.isInterface() ?: false) }
          if (!isSubtype && descriptor.shouldApplyKind()) {
            val hk = descriptor.higherKind()
            println("syntheticResolver.addSyntheticSupertypes: ${descriptor.parents.toList()}, $supertypes, hk: $hk")
            println("SuperType -> ${descriptor.name} : $hk")
            supertypes.add(hk)
          } else {
//            println("skipped: " + descriptor.name)
          }
        }
      ),
      IrGeneration { compilerContext, file, backendContext, bindingContext ->
        backendContext.run {
          file.declarations.forEach { decl ->
            println("Found IrDeclaration: $decl")
            if (decl is IrClass && decl.isTypeConstructor()) {
              val witness: ClassDescriptor = higherKindWitness()
              val higherKindSuperType = irHigherKind(witness, decl)
              decl.superTypes.add(higherKindSuperType)
            }
            decl.transform { element ->
              println("Found IrElement: $element")
            }
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
    if (debug)println("result: " + result)
    return result
  }

  fun ClassDescriptor.kotlinType(
    name: FqName,
    typeArguments: List<TypeProjection> = emptyList(),
    nullable: Boolean = false
  ): KotlinType {
    val descriptor = module.resolveClassByFqName(name, NoLookupLocation.FROM_BACKEND)
    return KotlinTypeFactory.simpleType(
      annotations = Annotations.EMPTY,
      constructor = descriptor?.typeConstructor ?: ErrorUtils.createErrorTypeConstructor("Missing descriptor for $name"),
      arguments = typeArguments,
      nullable = nullable
    )
  }

  private fun ClassDescriptor.higherKind(): KotlinType {
    return kotlinType(
      name = kindName,
      typeArguments = listOf(
        typeParameter(kindMarkerName),
        typeVariable(declaredTypeParameters[0].name, kindName)
      )
    )
  }

  fun ClassDescriptor.typeParameter(
    name: FqName,
    typeArguments: List<TypeProjection> = emptyList()
  ): TypeProjection =
    TypeProjectionImpl(
      kotlinType(name, typeArguments)
    )

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

  private fun IrClass.isTypeConstructor() =
    typeParameters.isNotEmpty()

  private fun BackendContext.irHigherKind(witness: ClassDescriptor, decl: IrClass): IrType =
    irType(
      className = "arrow.sample.Kind",
      typeArguments = listOf(
        irTypeParameter(witness.name, witness),
        irTypeParameter(decl.typeParameters[0].name, decl.descriptor)
      )
    )

  private fun BackendContext.higherKindWitness() =
    ir.context.getClass(FqName("arrow.sample.ForOption"))
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

fun irTypeParameter(
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

fun IrDeclaration.transform(f: (IrElement) -> Unit): Unit {
  acceptVoid(object : IrElementVisitorVoid {
    override fun visitElement(element: IrElement) {
      f(element)
      element.acceptChildrenVoid(this)
    }
  })
}