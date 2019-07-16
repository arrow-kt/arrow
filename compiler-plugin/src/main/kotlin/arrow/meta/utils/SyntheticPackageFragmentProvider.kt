package arrow.meta.utils

import arrow.meta.extensions.CompilerContext
import arrow.meta.higherkind.ContributedPackageFragmentDescriptor
import arrow.meta.qq.Transformation
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.PackageFragmentDescriptor
import org.jetbrains.kotlin.descriptors.PackageFragmentProvider
import org.jetbrains.kotlin.descriptors.PackageViewDescriptor
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.utils.addToStdlib.cast

class SyntheticPackageFragmentProvider(
  val compilerContext: CompilerContext,
  val module: ModuleDescriptor
) : PackageFragmentProvider {

  override fun getPackageFragments(fqName: FqName): List<PackageFragmentDescriptor> {
    val pckg: PackageViewDescriptor = module.getPackage(fqName)
    val transformations: List<Transformation<DeclarationDescriptor>> = compilerContext.transformations.cast()
    val descriptorsInPackage = transformations.filter {
      it.oldDescriptor.fqNameSafe.parent() == fqName
    }.map { it.newDescriptor }
    val result = descriptorsInPackage.flatMap { descriptor ->
      listOf(descriptor)
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