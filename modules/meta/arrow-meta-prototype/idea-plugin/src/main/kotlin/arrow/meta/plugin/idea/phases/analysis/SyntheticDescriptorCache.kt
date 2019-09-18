package arrow.meta.plugin.idea.phases.analysis

import arrow.meta.plugin.idea.phases.resolve.MetaRecursiveVisitor
import arrow.meta.quotes.isMetaFile
import arrow.meta.quotes.ktFile
import com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.kotlin.analyzer.AnalysisResult
import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.PackageViewDescriptor
import org.jetbrains.kotlin.descriptors.impl.DeclarationDescriptorVisitorEmptyBodies
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import java.util.concurrent.ConcurrentHashMap

internal val blackList: Set<Name> =
  listOf("equals", "hashCode", "toString")
    .map(Name::identifier).toSet()

internal data class CacheId(val value: String)

internal val VirtualFile.metaCacheId: CacheId
  get() = CacheId(path)

class SyntheticDescriptorCache(
  val module: ModuleDescriptor,
  val descriptorCache: ConcurrentHashMap<FqName, DeclarationDescriptor> = ConcurrentHashMap()
) {
  companion object {
    fun fromAnalysis(file: KtFile, analysis: AnalysisResult): SyntheticDescriptorCache {
      val moduleDescriptor = analysis.moduleDescriptor
      val packageViewDescriptor = moduleDescriptor.getPackage(file.packageFqName)
      val cache = SyntheticDescriptorCache(moduleDescriptor)
      packageViewDescriptor.accept(
        MetaRecursiveVisitor(object : DeclarationDescriptorVisitorEmptyBodies<Unit, Unit>() {
          override fun visitDeclarationDescriptor(descriptor: DeclarationDescriptor?, data: Unit?) {
            descriptor?.let {
              if (descriptor.name !in blackList) {
                if (descriptor is CallableMemberDescriptor) { // constructors functions and properties
                  if (descriptor.ktFile()?.isMetaFile() == true && descriptor.kind != CallableMemberDescriptor.Kind.FAKE_OVERRIDE) {
                    println("Callable: Added to cache: ${descriptor.fqNameSafe}")
                    cache.descriptorCache[it.fqNameSafe] = it
                  }
                } else if (descriptor is ClassDescriptor) { // constructors functions and properties
                  if (descriptor.ktFile()?.isMetaFile() == true) {
                    println("Class: Added to cache: ${descriptor.fqNameSafe}")
                    cache.descriptorCache[it.fqNameSafe] = it
                  }
                } else if (descriptor is PackageViewDescriptor) { // constructors functions and properties
                  if (descriptor.ktFile()?.isMetaFile() == true) {
                    println("Package: Added to cache: ${descriptor.fqNameSafe}")
                    cache.descriptorCache[it.fqNameSafe] = it
                  }
                } else {
                  println("skipped synthetic cache entry: $descriptor: ${descriptor.name}")
                }
              }
            }
          }
        }), Unit)
      return cache
    }

  }
}