package arrow.meta.plugin.idea

import org.jetbrains.kotlin.descriptors.ClassConstructorDescriptor
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.PackageFragmentDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.descriptors.SimpleFunctionDescriptor
import org.jetbrains.kotlin.js.resolve.diagnostics.findPsi
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.extensions.SyntheticResolveExtension
import org.jetbrains.kotlin.resolve.lazy.LazyClassContext
import org.jetbrains.kotlin.resolve.lazy.declarations.ClassMemberDeclarationProvider
import org.jetbrains.kotlin.resolve.lazy.declarations.PackageMemberDeclarationProvider
import org.jetbrains.kotlin.types.KotlinType
import java.util.*

class MetaSyntheticResolverExtension : SyntheticResolveExtension {

  override fun addSyntheticSupertypes(thisDescriptor: ClassDescriptor, supertypes: MutableList<KotlinType>) {
    println("MetaSyntheticResolverExtension.addSyntheticSupertypes: $thisDescriptor")
    super.addSyntheticSupertypes(thisDescriptor, supertypes)
  }

  override fun generateSyntheticClasses(thisDescriptor: ClassDescriptor, name: Name, ctx: LazyClassContext, declarationProvider: ClassMemberDeclarationProvider, result: MutableSet<ClassDescriptor>) {
    println("MetaSyntheticResolverExtension.generateSyntheticClasses: $thisDescriptor")
    super.generateSyntheticClasses(thisDescriptor, name, ctx, declarationProvider, result)
  }

  override fun generateSyntheticClasses(thisDescriptor: PackageFragmentDescriptor, name: Name, ctx: LazyClassContext, declarationProvider: PackageMemberDeclarationProvider, result: MutableSet<ClassDescriptor>) {
    println("MetaSyntheticResolverExtension.generatePckSyntheticClasses: $thisDescriptor, results: $result")
    thisDescriptor.findPsi()?.containingFile?.clearCaches()
    super.generateSyntheticClasses(thisDescriptor, name, ctx, declarationProvider, result)
  }

  override fun generateSyntheticMethods(thisDescriptor: ClassDescriptor, name: Name, bindingContext: BindingContext, fromSupertypes: List<SimpleFunctionDescriptor>, result: MutableCollection<SimpleFunctionDescriptor>) {
    println("MetaSyntheticResolverExtension.generateSyntheticMethods: $thisDescriptor")
    super.generateSyntheticMethods(thisDescriptor, name, bindingContext, fromSupertypes, result)
  }

  override fun generateSyntheticProperties(thisDescriptor: ClassDescriptor, name: Name, bindingContext: BindingContext, fromSupertypes: ArrayList<PropertyDescriptor>, result: MutableSet<PropertyDescriptor>) {
    println("MetaSyntheticResolverExtension.generateSyntheticProperties: $thisDescriptor")
    super.generateSyntheticProperties(thisDescriptor, name, bindingContext, fromSupertypes, result)
  }

  override fun generateSyntheticSecondaryConstructors(thisDescriptor: ClassDescriptor, bindingContext: BindingContext, result: MutableCollection<ClassConstructorDescriptor>) {
    println("MetaSyntheticResolverExtension.generateSyntheticSecondaryConstructors: $thisDescriptor")
    super.generateSyntheticSecondaryConstructors(thisDescriptor, bindingContext, result)
  }

  override fun getSyntheticCompanionObjectNameIfNeeded(thisDescriptor: ClassDescriptor): Name? {
    println("MetaSyntheticResolverExtension.getSyntheticCompanionObjectNameIfNeeded: $thisDescriptor")
    return super.getSyntheticCompanionObjectNameIfNeeded(thisDescriptor)
  }

  override fun getSyntheticFunctionNames(thisDescriptor: ClassDescriptor): List<Name> {
    println("MetaSyntheticResolverExtension.getSyntheticFunctionNames: $thisDescriptor")
    return super.getSyntheticFunctionNames(thisDescriptor)
  }

  override fun getSyntheticNestedClassNames(thisDescriptor: ClassDescriptor): List<Name> {
    println("MetaSyntheticResolverExtension.getSyntheticNestedClassNames: $thisDescriptor")
    return super.getSyntheticNestedClassNames(thisDescriptor)
  }
}