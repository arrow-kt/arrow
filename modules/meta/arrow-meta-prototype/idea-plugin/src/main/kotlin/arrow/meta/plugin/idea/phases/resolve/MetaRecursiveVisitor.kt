package arrow.meta.plugin.idea.phases.resolve

import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.ConstructorDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptorVisitor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.PackageFragmentDescriptor
import org.jetbrains.kotlin.descriptors.PackageViewDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.descriptors.PropertyGetterDescriptor
import org.jetbrains.kotlin.descriptors.PropertySetterDescriptor
import org.jetbrains.kotlin.descriptors.ReceiverParameterDescriptor
import org.jetbrains.kotlin.descriptors.ScriptDescriptor
import org.jetbrains.kotlin.descriptors.TypeAliasDescriptor
import org.jetbrains.kotlin.descriptors.TypeParameterDescriptor
import org.jetbrains.kotlin.descriptors.ValueParameterDescriptor
import org.jetbrains.kotlin.descriptors.VariableDescriptor
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.resolve.DescriptorUtils

internal class MetaRecursiveVisitor constructor(private val worker: DeclarationDescriptorVisitor<Unit, Unit>) : DeclarationDescriptorVisitor<Unit, Unit> {

  private fun visitChildren(descriptors: Collection<DeclarationDescriptor>, data: Unit): Unit {
    for (descriptor in descriptors) {
      descriptor.accept(this, data)
    }
  }

  private fun visitChildren(descriptor: DeclarationDescriptor?, data: Unit): Unit {
    descriptor?.accept(this, data)
  }

  private fun applyWorker(descriptor: DeclarationDescriptor, data: Unit): Unit {
    descriptor.accept(worker, data)
  }

  private fun processCallable(descriptor: CallableDescriptor, data: Unit): Unit? {
    applyWorker(descriptor, data)
    visitChildren(descriptor.typeParameters, data)
    visitChildren(descriptor.extensionReceiverParameter, data)
    visitChildren(descriptor.valueParameters, data)
    return null
  }

  override fun visitPackageFragmentDescriptor(descriptor: PackageFragmentDescriptor, data: Unit): Unit? {
    applyWorker(descriptor, data)
    visitChildren(DescriptorUtils.getAllDescriptors(descriptor.getMemberScope()), data)
    return null
  }

  override fun visitPackageViewDescriptor(descriptor: PackageViewDescriptor, data: Unit): Unit? {
    applyWorker(descriptor, data)
    visitChildren(DescriptorUtils.getAllDescriptors(descriptor.memberScope), data)
    return null
  }

  override fun visitVariableDescriptor(descriptor: VariableDescriptor, data: Unit): Unit? {
    processCallable(descriptor, data)
    return null
  }

  override fun visitPropertyDescriptor(descriptor: PropertyDescriptor, data: Unit): Unit? {
    processCallable(descriptor, data)
    visitChildren(descriptor.getter, data)
    return null
  }

  override fun visitFunctionDescriptor(descriptor: FunctionDescriptor, data: Unit): Unit? {
    processCallable(descriptor, data)
    return null
  }

  override fun visitTypeParameterDescriptor(descriptor: TypeParameterDescriptor, data: Unit): Unit? {
    applyWorker(descriptor, data)
    return null
  }

  override fun visitClassDescriptor(descriptor: ClassDescriptor, data: Unit): Unit? {
    applyWorker(descriptor, data)
    visitChildren(descriptor.thisAsReceiverParameter, data)
    visitChildren(descriptor.constructors, data)
    visitChildren(descriptor.typeConstructor.parameters, data)
    visitChildren(DescriptorUtils.getAllDescriptors(descriptor.defaultType.memberScope), data)
    return null
  }

  override fun visitTypeAliasDescriptor(descriptor: TypeAliasDescriptor, data: Unit): Unit? {
    applyWorker(descriptor, data)
    visitChildren(descriptor.declaredTypeParameters, data)
    return null
  }

  override fun visitModuleDeclaration(descriptor: ModuleDescriptor, data: Unit): Unit? {
    applyWorker(descriptor, data)
    visitChildren(descriptor.getPackage(FqName.ROOT), data)
    return null
  }

  override fun visitConstructorDescriptor(constructorDescriptor: ConstructorDescriptor, data: Unit): Unit? {
    visitFunctionDescriptor(constructorDescriptor, data)
    return null
  }

  override fun visitScriptDescriptor(scriptDescriptor: ScriptDescriptor, data: Unit): Unit? {
    visitClassDescriptor(scriptDescriptor, data)
    return null
  }

  override fun visitValueParameterDescriptor(descriptor: ValueParameterDescriptor, data: Unit): Unit? {
    return visitVariableDescriptor(descriptor, data)
  }

  override fun visitPropertyGetterDescriptor(descriptor: PropertyGetterDescriptor, data: Unit): Unit? {
    return visitFunctionDescriptor(descriptor, data)
  }

  override fun visitPropertySetterDescriptor(descriptor: PropertySetterDescriptor, data: Unit): Unit? {
    return visitFunctionDescriptor(descriptor, data)
  }

  override fun visitReceiverParameterDescriptor(descriptor: ReceiverParameterDescriptor, data: Unit): Unit? {
    applyWorker(descriptor, data)
    return null
  }
}