package arrow.meta.higherkind

import arrow.meta.extensions.CompilerContext
import arrow.meta.utils.ContributedPackageFragmentDescriptor
import arrow.meta.utils.kindMarker
import arrow.meta.utils.kindTypeAlias
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.PackageFragmentDescriptor
import org.jetbrains.kotlin.descriptors.PackageFragmentProvider
import org.jetbrains.kotlin.descriptors.PackageViewDescriptor
import org.jetbrains.kotlin.descriptors.TypeAliasDescriptor
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe

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