package arrow.meta

import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.PackageFragmentDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.descriptors.SimpleFunctionDescriptor
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.extensions.SyntheticResolveExtension
import org.jetbrains.kotlin.resolve.lazy.LazyClassContext
import org.jetbrains.kotlin.resolve.lazy.declarations.ClassMemberDeclarationProvider
import org.jetbrains.kotlin.resolve.lazy.declarations.PackageMemberDeclarationProvider
import org.jetbrains.kotlin.types.KotlinType
import java.util.ArrayList

class MetaSyntheticResolveExtension : SyntheticResolveExtension {
  override fun addSyntheticSupertypes(thisDescriptor: ClassDescriptor, supertypes: MutableList<KotlinType>) {
    println("MetaSyntheticResolveExtension.addSyntheticSupertypes : ${thisDescriptor.name}")
    super.addSyntheticSupertypes(thisDescriptor, supertypes)
  }

  override fun generateSyntheticClasses(thisDescriptor: ClassDescriptor, name: Name, ctx: LazyClassContext, declarationProvider: ClassMemberDeclarationProvider, result: MutableSet<ClassDescriptor>) {
    println("MetaSyntheticResolveExtension.generateSyntheticClasses [classes] : ${thisDescriptor.name}")
    super.generateSyntheticClasses(thisDescriptor, name, ctx, declarationProvider, result)
  }

  override fun generateSyntheticClasses(thisDescriptor: PackageFragmentDescriptor, name: Name, ctx: LazyClassContext, declarationProvider: PackageMemberDeclarationProvider, result: MutableSet<ClassDescriptor>) {
    println("MetaSyntheticResolveExtension.generateSyntheticClasses [packages] : ${thisDescriptor.name}")
    super.generateSyntheticClasses(thisDescriptor, name, ctx, declarationProvider, result)
  }

  override fun generateSyntheticMethods(thisDescriptor: ClassDescriptor, name: Name, bindingContext: BindingContext, fromSupertypes: List<SimpleFunctionDescriptor>, result: MutableCollection<SimpleFunctionDescriptor>) {
    println("MetaSyntheticResolveExtension.generateSyntheticMethods: ${thisDescriptor.name}")
    super.generateSyntheticMethods(thisDescriptor, name, bindingContext, fromSupertypes, result)
  }

  override fun generateSyntheticProperties(thisDescriptor: ClassDescriptor, name: Name, bindingContext: BindingContext, fromSupertypes: ArrayList<PropertyDescriptor>, result: MutableSet<PropertyDescriptor>) {
    println("MetaSyntheticResolveExtension.generateSyntheticProperties : ${thisDescriptor.name}")
    super.generateSyntheticProperties(thisDescriptor, name, bindingContext, fromSupertypes, result)
  }

  override fun getSyntheticCompanionObjectNameIfNeeded(thisDescriptor: ClassDescriptor): Name? {
    println("MetaSyntheticResolveExtension.getSyntheticCompanionObjectNameIfNeeded : ${thisDescriptor.name}")
    return super.getSyntheticCompanionObjectNameIfNeeded(thisDescriptor)
  }

  override fun getSyntheticFunctionNames(thisDescriptor: ClassDescriptor): List<Name> {
    println("MetaSyntheticResolveExtension.getSyntheticFunctionNames : ${thisDescriptor.name}")
    return super.getSyntheticFunctionNames(thisDescriptor)
  }

  override fun getSyntheticNestedClassNames(thisDescriptor: ClassDescriptor): List<Name> {
    println("MetaSyntheticResolveExtension.getSyntheticNestedClassNames : ${thisDescriptor.name}")
    return super.getSyntheticNestedClassNames(thisDescriptor)
  }
}
