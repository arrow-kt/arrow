package arrow.meta.higherkind

import arrow.meta.extensions.CompilerContext
import org.jetbrains.kotlin.backend.common.BackendContext
import org.jetbrains.kotlin.backend.common.lower.SimpleMemberScope
import org.jetbrains.kotlin.descriptors.ClassConstructorDescriptor
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.PackageFragmentDescriptor
import org.jetbrains.kotlin.descriptors.PackageFragmentProvider
import org.jetbrains.kotlin.descriptors.PackageViewDescriptor
import org.jetbrains.kotlin.descriptors.SourceElement
import org.jetbrains.kotlin.descriptors.TypeAliasDescriptor
import org.jetbrains.kotlin.descriptors.Visibilities
import org.jetbrains.kotlin.descriptors.Visibility
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.descriptors.impl.AbstractTypeAliasDescriptor
import org.jetbrains.kotlin.descriptors.impl.ClassDescriptorImpl
import org.jetbrains.kotlin.descriptors.impl.DeclarationDescriptorVisitorEmptyBodies
import org.jetbrains.kotlin.descriptors.impl.PackageFragmentDescriptorImpl
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
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrConstructorCall
import org.jetbrains.kotlin.ir.symbols.impl.IrTypeParameterSymbolImpl
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.IrTypeArgument
import org.jetbrains.kotlin.ir.types.impl.IrSimpleTypeImpl
import org.jetbrains.kotlin.js.resolve.diagnostics.findPsi
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.kotlin.psi.KtTreeVisitorVoid
import org.jetbrains.kotlin.psi.KtTypeProjection
import org.jetbrains.kotlin.psi.KtTypeReference
import org.jetbrains.kotlin.psi.KtUserType
import org.jetbrains.kotlin.psi.KtVisitorVoid
import org.jetbrains.kotlin.psi.psiUtil.astReplace
import org.jetbrains.kotlin.resolve.DescriptorFactory
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.resolve.descriptorUtil.getAllSuperclassesWithoutAny
import org.jetbrains.kotlin.resolve.descriptorUtil.getSuperInterfaces
import org.jetbrains.kotlin.resolve.descriptorUtil.module
import org.jetbrains.kotlin.resolve.lazy.descriptors.LazyTypeAliasDescriptor
import org.jetbrains.kotlin.resolve.scopes.MemberScope
import org.jetbrains.kotlin.storage.LockBasedStorageManager
import org.jetbrains.kotlin.types.KotlinTypeFactory
import org.jetbrains.kotlin.types.SimpleType
import org.jetbrains.kotlin.types.TypeConstructor
import org.jetbrains.kotlin.types.TypeProjection
import org.jetbrains.kotlin.types.TypeProjectionImpl
import org.jetbrains.kotlin.types.Variance
import org.jetbrains.kotlin.types.typeUtil.isInterface
import java.lang.reflect.Array.setInt
import java.lang.reflect.AccessibleObject.setAccessible
import java.lang.reflect.Field
import java.lang.reflect.Modifier


val kindName: FqName = FqName("arrow.sample.Kind")

val ModuleDescriptor.kindDescriptor: ClassDescriptor?
  get() = module.resolveClassByFqName(kindName, NoLookupLocation.FROM_BACKEND)

val FqName.kindMarkerName: FqName
  get() {
    val segments = pathSegments()
    val pck = segments.dropLast(1)
    val simpleName = segments.last()
    return FqName("${pck.joinToString(".")}.For${simpleName.asString()}")
  }

val FqName.kindTypeAliasName: Name
  get() {
    val segments = pathSegments()
    val simpleName = segments.last()
    return Name.identifier("${simpleName}Of")
  }

class KindMarkerDescriptor(containingDeclaration: DeclarationDescriptor, targetName: FqName) : ClassDescriptorImpl(
  containingDeclaration,
  targetName.kindMarkerName.shortName(),
  Modality.FINAL,
  ClassKind.CLASS,
  listOf(containingDeclaration.module.builtIns.any.defaultType),
  SourceElement.NO_SOURCE,
  false,
  LockBasedStorageManager.NO_LOCKS
) {
  override fun getVisibility(): Visibility = Visibilities.PUBLIC
  override fun getUnsubstitutedMemberScope(): MemberScope = MemberScope.Empty
  override fun getConstructors(): Collection<ClassConstructorDescriptor> =
    listOf(DescriptorFactory.createPrimaryConstructorForObject(this, SourceElement.NO_SOURCE))
}

fun DeclarationDescriptor.kindMarker(targetName: FqName): ClassDescriptor =
  KindMarkerDescriptor(this, targetName)

fun ClassDescriptor.replaceKinds(ktPsiFactory: KtPsiFactory): ClassDescriptor? {
  val element = findPsi()
  return if (element is KtClass) {
    element.accept(object : KtTreeVisitorVoid() {
      override fun visitTypeReference(typeReference: KtTypeReference) {
        val typeElement = typeReference.typeElement
        if (typeElement is KtUserType) {
          val kindReference = typeElement.referencedName
          if (kindReference != null && this@replaceKinds.declaredTypeParameters.any { it.name.asString() == kindReference }) {
            val kindedTypeReference = ktPsiFactory.createType("Kind<$kindReference, A>")
            println("kindReference: $kindReference should be replaced for kind")
          }
        }
        super.visitTypeReference(typeReference)
      }
    })
    return null
  } else this
}



fun CompilerContext.kindTypeAlias(target: ClassDescriptor): TypeAliasDescriptor =
  LazyTypeAliasDescriptor.create( //not really lazy but better factory than implementing the full blown interface
    storageManager = LockBasedStorageManager.NO_LOCKS,
    trace = bindingTrace,
    containingDeclaration = target.containingDeclaration,
    annotations = Annotations.EMPTY,
    name = target.fqNameSafe.kindTypeAliasName,
    sourceElement = SourceElement.NO_SOURCE,
    visibility = Visibilities.PUBLIC
  ).also { aliasDescriptor ->
    aliasDescriptor.initialize(
      listOf(TypeParameterDescriptorImpl.createWithDefaultBound(
        aliasDescriptor,
        Annotations.EMPTY,
        false,
        Variance.INVARIANT,
        target.declaredTypeParameters[0].name,
        0
      )),
      LockBasedStorageManager.NO_LOCKS.createLazyValue { target.module.kindDescriptor?.defaultType!! },
      LockBasedStorageManager.NO_LOCKS.createLazyValue {  aliasDescriptor.underlyingHigherKind(target) }
    )
  }

class ContributedPackageFragmentDescriptor(
  val module: ModuleDescriptor,
  val name: FqName,
  val members: List<DeclarationDescriptor>
) : PackageFragmentDescriptorImpl(module, name) {
  override fun getMemberScope(): MemberScope = SimpleMemberScope(members)
}

class AddSupertypesPackageFragmentProvider(
  val compilerContext: CompilerContext,
  val module: ModuleDescriptor
) : PackageFragmentProvider {

  override fun getPackageFragments(fqName: FqName): List<PackageFragmentDescriptor> {
    val pckg: PackageViewDescriptor = module.getPackage(fqName)

    val descriptorsInPackage = compilerContext.storedDescriptors().filter {
      it.fqNameSafe.parent() == fqName
    }
    val result = descriptorsInPackage.flatMap { descriptor ->
      val kindMarker = pckg.kindMarker(descriptor.fqNameSafe)
      val typeAlias: TypeAliasDescriptor = compilerContext.kindTypeAlias(descriptor)
      listOf(kindMarker, typeAlias)
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

fun ClassDescriptor.shouldGenerateKindMarker(): Boolean =
  declaredTypeParameters.isNotEmpty() &&
      fqNameSafe != kindName &&
      !getAllSuperclassesWithoutAny().any { s -> !s.defaultType.isInterface() }

//TODO alternative way of creating descriptors?
//fun LazyClassDescriptor.kindMarkerSynthetic(containingDeclaration: DeclarationDescriptor, targetName: FqName): ClassDescriptor =
//  SyntheticPackageClassOrObjectDescriptor(
//    c = c,
//    parentClassOrObject = TODO(),
//    containingDeclaration = containingDeclaration,
//    name = targetName.kindMarkerName.shortName(),
//    source = SourceElement.NO_SOURCE,
//    outerScope = TODO(),
//    modality = Modality.FINAL,
//    visibility = Visibilities.PUBLIC,
//    constructorVisibility = Visibilities.PUBLIC,
//    kind = ClassKind.CLASS,
//    isCompanionObject = false
//  )

fun ClassDescriptor.shouldApplyKind(debug: Boolean = false): Boolean {
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
): SimpleType {
  return KotlinTypeFactory.simpleType(
    annotations = Annotations.EMPTY,
    constructor = typeConstructor,
    arguments = typeArguments,
    nullable = nullable
  )
}

fun ClassDescriptor.higherKind(): SimpleType =
  kotlinType(
    typeConstructor = module.resolveClassByFqName(kindName, NoLookupLocation.FROM_BACKEND)?.typeConstructor!!,
    typeArguments = listOf(
      typeVariable(fqNameSafe.kindMarkerName.shortNameOrSpecial()),
      // TypeProjectionImpl(kindMarker().defaultType),
      typeVariable(declaredTypeParameters[0].name)
    )
  )

fun TypeAliasDescriptor.underlyingHigherKind(targetDescriptor: ClassDescriptor): SimpleType =
  kotlinType(
    typeConstructor = module.resolveClassByFqName(kindName, NoLookupLocation.FROM_BACKEND)?.typeConstructor!!,
    typeArguments = listOf(
      typeVariable(targetDescriptor.fqNameSafe.kindMarkerName.shortNameOrSpecial()),
      // TypeProjectionImpl(kindMarker().defaultType),
      typeVariable(declaredTypeParameters[0].name)
    )
  )

fun DeclarationDescriptor.typeVariable(
  name: Name
): TypeProjection =
  TypeProjectionImpl(
    TypeParameterDescriptorImpl.createWithDefaultBound(
      this,
      Annotations.EMPTY,
      false,
      Variance.INVARIANT,
      name,
      0
    ).defaultType
  )

fun BackendContext.irHigherKind(decl: IrClass): IrType =
  irType(
    className = "arrow.sample.Kind",
    typeArguments = listOf(
      //irTypeArgument(decl.descriptor.kindMarkerName.as, witness),
      irTypeArgument(
        decl.descriptor.fqNameSafe.kindMarkerName.shortNameOrSpecial(),
        decl.descriptor
      ),
      irTypeArgument(decl.typeParameters[0].name, decl.descriptor)
    )
  )

fun BackendContext.irType(
  className: String,
  typeArguments: List<IrTypeArgument> = emptyList(),
  nullable: Boolean = false,
  annotations: List<IrConstructorCall> = emptyList()
): IrType =
  IrSimpleTypeImpl(
    classifier = ir.symbols.externalSymbolTable.referenceClass(
      ir.irModule.descriptor.resolveClassByFqName(FqName(className), NoLookupLocation.FROM_BACKEND)!!
    ),
    hasQuestionMark = nullable,
    arguments = typeArguments,
    annotations = annotations
  )

fun irTypeArgument(
  name: Name,
  containingDeclaration: DeclarationDescriptor,
  nullable: Boolean = false,
  annotations: List<IrConstructorCall> = emptyList()
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
      )
    ),
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
}

@Throws(Exception::class)
fun setFinalStatic(field: Field, newValue: Any) {
  field.isAccessible = true

  val modifiersField = Field::class.java.getDeclaredField("modifiers")
  modifiersField.isAccessible = true
  modifiersField.setInt(field, field.modifiers and Modifier.FINAL.inv())

  field.set(null, newValue)
}