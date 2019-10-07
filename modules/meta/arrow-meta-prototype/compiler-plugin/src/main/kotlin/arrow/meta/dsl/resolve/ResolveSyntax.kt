package arrow.meta.dsl.resolve

import arrow.meta.dsl.platform.ide
import arrow.meta.internal.Noop
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.ExtensionPhase
import arrow.meta.phases.resolve.DeclarationAttributeAlterer
import arrow.meta.phases.resolve.PackageProvider
import arrow.meta.phases.resolve.synthetics.SyntheticResolver
import arrow.meta.phases.resolve.synthetics.SyntheticScopeProvider
import org.jetbrains.kotlin.analyzer.ModuleInfo
import org.jetbrains.kotlin.com.intellij.openapi.project.Project
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.ConstructorDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.PackageFragmentDescriptor
import org.jetbrains.kotlin.descriptors.PackageFragmentProvider
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.descriptors.SimpleFunctionDescriptor
import org.jetbrains.kotlin.incremental.components.LookupLocation
import org.jetbrains.kotlin.incremental.components.LookupTracker
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtModifierListOwner
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.lazy.LazyClassContext
import org.jetbrains.kotlin.resolve.lazy.declarations.ClassMemberDeclarationProvider
import org.jetbrains.kotlin.resolve.lazy.declarations.PackageMemberDeclarationProvider
import org.jetbrains.kotlin.resolve.scopes.ResolutionScope
import org.jetbrains.kotlin.storage.StorageManager
import org.jetbrains.kotlin.types.KotlinType
import java.util.*

interface ResolveSyntax {
  fun declarationAttributeAlterer(
    refineDeclarationModality: CompilerContext.(modifierListOwner: KtModifierListOwner, declaration: DeclarationDescriptor?, containingDeclaration: DeclarationDescriptor?, currentModality: Modality, bindingContext: BindingContext, isImplicitModality: Boolean) -> Modality?
  ): DeclarationAttributeAlterer =
    object : DeclarationAttributeAlterer {
      override fun CompilerContext.refineDeclarationModality(
        modifierListOwner: KtModifierListOwner,
        declaration: DeclarationDescriptor?,
        containingDeclaration: DeclarationDescriptor?,
        currentModality: Modality,
        bindingContext: BindingContext,
        isImplicitModality: Boolean
      ): Modality? =
        refineDeclarationModality(
          modifierListOwner,
          declaration,
          containingDeclaration,
          currentModality,
          bindingContext,
          isImplicitModality
        )
    }

  fun packageFragmentProvider(getPackageFragmentProvider: CompilerContext.(project: Project, module: ModuleDescriptor, storageManager: StorageManager, trace: BindingTrace, moduleInfo: ModuleInfo?, lookupTracker: LookupTracker) -> PackageFragmentProvider?): PackageProvider =
    object : PackageProvider {
      override fun CompilerContext.getPackageFragmentProvider(
        project: Project,
        module: ModuleDescriptor,
        storageManager: StorageManager,
        trace: BindingTrace,
        moduleInfo: ModuleInfo?,
        lookupTracker: LookupTracker
      ): PackageFragmentProvider? =
        getPackageFragmentProvider(
          project,
          module,
          storageManager,
          trace,
          moduleInfo,
          lookupTracker
        )
    }

  fun syntheticScopes(
    syntheticConstructor: CompilerContext.(constructor: ConstructorDescriptor) -> ConstructorDescriptor? = Noop.nullable2(),
    syntheticConstructors: CompilerContext.(scope: ResolutionScope) -> Collection<FunctionDescriptor> = Noop.emptyCollection2(),
    syntheticConstructorsForName: CompilerContext.(scope: ResolutionScope, name: Name, location: LookupLocation) -> Collection<FunctionDescriptor> = Noop.emptyCollection4(),
    syntheticExtensionProperties: CompilerContext.(receiverTypes: Collection<KotlinType>, location: LookupLocation) -> Collection<PropertyDescriptor> = Noop.emptyCollection3(),
    syntheticExtensionPropertiesForName: CompilerContext.(receiverTypes: Collection<KotlinType>, name: Name, location: LookupLocation) -> Collection<PropertyDescriptor> = Noop.emptyCollection4(),
    syntheticMemberFunctions: CompilerContext.(receiverTypes: Collection<KotlinType>) -> Collection<FunctionDescriptor> = Noop.emptyCollection2(),
    syntheticMemberFunctionsForName: CompilerContext.(receiverTypes: Collection<KotlinType>, name: Name, location: LookupLocation) -> Collection<FunctionDescriptor> = Noop.emptyCollection4(),
    syntheticStaticFunctions: CompilerContext.(scope: ResolutionScope) -> Collection<FunctionDescriptor> = Noop.emptyCollection2(),
    syntheticStaticFunctionsForName: CompilerContext.(scope: ResolutionScope, name: Name, location: LookupLocation) -> Collection<FunctionDescriptor> = Noop.emptyCollection4()
  ): ExtensionPhase =
    ide {
      object : SyntheticScopeProvider {
        override fun CompilerContext.syntheticConstructor(constructor: ConstructorDescriptor): ConstructorDescriptor? =
          syntheticConstructor(constructor)

        override fun CompilerContext.syntheticConstructors(scope: ResolutionScope): Collection<FunctionDescriptor> =
          syntheticConstructors(scope)

        override fun CompilerContext.syntheticConstructors(scope: ResolutionScope, name: Name, location: LookupLocation): Collection<FunctionDescriptor> =
          syntheticConstructorsForName(scope, name, location)

        override fun CompilerContext.syntheticExtensionProperties(receiverTypes: Collection<KotlinType>, location: LookupLocation): Collection<PropertyDescriptor> =
          syntheticExtensionProperties(receiverTypes, location)

        override fun CompilerContext.syntheticExtensionProperties(receiverTypes: Collection<KotlinType>, name: Name, location: LookupLocation): Collection<PropertyDescriptor> =
          syntheticExtensionPropertiesForName(receiverTypes, name, location)

        override fun CompilerContext.syntheticMemberFunctions(receiverTypes: Collection<KotlinType>): Collection<FunctionDescriptor> =
          syntheticMemberFunctions(receiverTypes)

        override fun CompilerContext.syntheticMemberFunctions(receiverTypes: Collection<KotlinType>, name: Name, location: LookupLocation): Collection<FunctionDescriptor> =
          syntheticMemberFunctionsForName(receiverTypes, name, location)

        override fun CompilerContext.syntheticStaticFunctions(scope: ResolutionScope): Collection<FunctionDescriptor> =
          syntheticStaticFunctions(scope)

        override fun CompilerContext.syntheticStaticFunctions(scope: ResolutionScope, name: Name, location: LookupLocation): Collection<FunctionDescriptor> =
          syntheticStaticFunctionsForName(scope, name, location)
      }
    } ?: ExtensionPhase.Empty

  fun syntheticResolver(
    addSyntheticSupertypes: CompilerContext.(thisDescriptor: ClassDescriptor, supertypes: MutableList<KotlinType>) -> Unit = Noop.effect3,
    /**
     * For a given package fragment it iterates over all the package declaration
     * allowing the user to contribute new synthetic declarations.
     * The result mutable set includes the descriptors as seen from the Kotlin compiler
     * initial analysis and allows us to mutate it to add new descriptor or change the existing ones
     */
    generatePackageSyntheticClasses: CompilerContext.(thisDescriptor: PackageFragmentDescriptor, name: Name, ctx: LazyClassContext, declarationProvider: PackageMemberDeclarationProvider, result: MutableSet<ClassDescriptor>) -> Unit = Noop.effect6,
    generateSyntheticClasses: CompilerContext.(thisDescriptor: ClassDescriptor, name: Name, ctx: LazyClassContext, declarationProvider: ClassMemberDeclarationProvider, result: MutableSet<ClassDescriptor>) -> Unit = Noop.effect6,
    generateSyntheticMethods: CompilerContext.(thisDescriptor: ClassDescriptor, name: Name, bindingContext: BindingContext, fromSupertypes: List<SimpleFunctionDescriptor>, result: MutableCollection<SimpleFunctionDescriptor>) -> Unit = Noop.effect6,
    generateSyntheticProperties: CompilerContext.(thisDescriptor: ClassDescriptor, name: Name, bindingContext: BindingContext, fromSupertypes: ArrayList<PropertyDescriptor>, result: MutableSet<PropertyDescriptor>) -> Unit = Noop.effect6,
    getSyntheticCompanionObjectNameIfNeeded: CompilerContext.(thisDescriptor: ClassDescriptor) -> Name? = Noop.nullable2(),
    getSyntheticFunctionNames: CompilerContext.(thisDescriptor: ClassDescriptor) -> List<Name>? = Noop.nullable2(),
    getSyntheticNestedClassNames: CompilerContext.(thisDescriptor: ClassDescriptor) -> List<Name>? = Noop.nullable2()
  ): SyntheticResolver =
    object : SyntheticResolver {
      override fun CompilerContext.addSyntheticSupertypes(
        thisDescriptor: ClassDescriptor,
        supertypes: MutableList<KotlinType>
      ) {
        addSyntheticSupertypes(thisDescriptor, supertypes)
      }

      override fun CompilerContext.generateSyntheticClasses(
        thisDescriptor: ClassDescriptor,
        name: Name,
        ctx: LazyClassContext,
        declarationProvider: ClassMemberDeclarationProvider,
        result: MutableSet<ClassDescriptor>
      ) {
        generateSyntheticClasses(thisDescriptor, name, ctx, declarationProvider, result)
      }

      override fun CompilerContext.generatePackageSyntheticClasses(
        thisDescriptor: PackageFragmentDescriptor,
        name: Name,
        ctx: LazyClassContext,
        declarationProvider: PackageMemberDeclarationProvider,
        result: MutableSet<ClassDescriptor>
      ) {
        generatePackageSyntheticClasses(thisDescriptor, name, ctx, declarationProvider, result)
      }

      override fun CompilerContext.generateSyntheticMethods(
        thisDescriptor: ClassDescriptor,
        name: Name,
        bindingContext: BindingContext,
        fromSupertypes: List<SimpleFunctionDescriptor>,
        result: MutableCollection<SimpleFunctionDescriptor>
      ) {
        generateSyntheticMethods(thisDescriptor, name, bindingContext, fromSupertypes, result)
      }

      override fun CompilerContext.generateSyntheticProperties(
        thisDescriptor: ClassDescriptor,
        name: Name,
        bindingContext: BindingContext,
        fromSupertypes: ArrayList<PropertyDescriptor>,
        result: MutableSet<PropertyDescriptor>
      ) {
        generateSyntheticProperties(thisDescriptor, name, bindingContext, fromSupertypes, result)
      }

      override fun CompilerContext.getSyntheticCompanionObjectNameIfNeeded(thisDescriptor: ClassDescriptor): Name? =
        getSyntheticCompanionObjectNameIfNeeded(thisDescriptor)

      override fun CompilerContext.getSyntheticFunctionNames(thisDescriptor: ClassDescriptor): List<Name> =
        getSyntheticFunctionNames(thisDescriptor) ?: emptyList()

      override fun CompilerContext.getSyntheticNestedClassNames(thisDescriptor: ClassDescriptor): List<Name> =
        getSyntheticNestedClassNames(thisDescriptor) ?: emptyList()
    }


}
