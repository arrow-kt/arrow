package arrow.meta.phases.resolve.synthetics

import arrow.meta.phases.CompilerContext
import arrow.meta.phases.ExtensionPhase
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.PackageFragmentDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.descriptors.SimpleFunctionDescriptor
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.lazy.LazyClassContext
import org.jetbrains.kotlin.resolve.lazy.declarations.ClassMemberDeclarationProvider
import org.jetbrains.kotlin.resolve.lazy.declarations.PackageMemberDeclarationProvider
import org.jetbrains.kotlin.types.KotlinType
import java.util.ArrayList

interface SyntheticResolver : ExtensionPhase {
  fun CompilerContext.addSyntheticSupertypes(
    thisDescriptor: ClassDescriptor,
    supertypes: MutableList<KotlinType>
  ): Unit

  fun CompilerContext.generateSyntheticClasses(
    thisDescriptor: ClassDescriptor,
    name: Name,
    ctx: LazyClassContext,
    declarationProvider: ClassMemberDeclarationProvider,
    result: MutableSet<ClassDescriptor>
  ): Unit

  fun CompilerContext.generatePackageSyntheticClasses(
    thisDescriptor: PackageFragmentDescriptor,
    name: Name,
    ctx: LazyClassContext,
    declarationProvider: PackageMemberDeclarationProvider,
    result: MutableSet<ClassDescriptor>
  ): Unit

  fun CompilerContext.generateSyntheticMethods(
    thisDescriptor: ClassDescriptor,
    name: Name,
    bindingContext: BindingContext,
    fromSupertypes: List<SimpleFunctionDescriptor>,
    result: MutableCollection<SimpleFunctionDescriptor>
  ): Unit

  fun CompilerContext.generateSyntheticProperties(
    thisDescriptor: ClassDescriptor,
    name: Name,
    bindingContext: BindingContext,
    fromSupertypes: ArrayList<PropertyDescriptor>,
    result: MutableSet<PropertyDescriptor>
  ): Unit

  fun CompilerContext.getSyntheticCompanionObjectNameIfNeeded(thisDescriptor: ClassDescriptor): Name?

  fun CompilerContext.getSyntheticFunctionNames(thisDescriptor: ClassDescriptor): List<Name>

  fun CompilerContext.getSyntheticNestedClassNames(thisDescriptor: ClassDescriptor): List<Name>
}