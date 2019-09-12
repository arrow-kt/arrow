package arrow.meta.plugin.idea

import com.intellij.openapi.Disposable
import com.intellij.openapi.vfs.AsyncFileListener
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.openapi.vfs.newvfs.events.VFileEvent
import org.jetbrains.kotlin.descriptors.ClassConstructorDescriptor
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.PackageFragmentDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.descriptors.SimpleFunctionDescriptor
import org.jetbrains.kotlin.idea.decompiler.classFile.DeserializerForClassfileDecompiler
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.descriptorUtil.classId
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.resolve.descriptorUtil.module
import org.jetbrains.kotlin.resolve.descriptorUtil.secondaryConstructors
import org.jetbrains.kotlin.resolve.extensions.SyntheticResolveExtension
import org.jetbrains.kotlin.resolve.lazy.LazyClassContext
import org.jetbrains.kotlin.resolve.lazy.declarations.ClassMemberDeclarationProvider
import org.jetbrains.kotlin.resolve.lazy.declarations.PackageMemberDeclarationProvider
import org.jetbrains.kotlin.resolve.scopes.MemberScope
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.TypeUtils
import java.util.*
import java.util.concurrent.ConcurrentHashMap

private val cache: ConcurrentHashMap<FqName, DeserializerForClassfileDecompiler> =
  ConcurrentHashMap()

class MetaSyntheticResolveExtension : SyntheticResolveExtension, AsyncFileListener, Disposable, AsyncFileListener.ChangeApplier {

  override fun dispose() {
    println("MetaSyntheticResolveExtension.dispose from virtual file manager")
    cache.clear()
  }

  init {
    virtualFileManager()?.run {
      println("MetaSyntheticResolveExtension.init $this")
      //addAsyncFileListener(this@MetaSyntheticResolveExtension, this@MetaSyntheticResolveExtension)
    }
  }

  private fun virtualFileManager(): VirtualFileManager? =
    currentProject()?.getComponent(VirtualFileManager::class.java)

  override fun prepareChange(events: MutableList<out VFileEvent>): AsyncFileListener.ChangeApplier? {
    println("MetaSyntheticResolveExtension.prepareChange: $events")
    events.map { fileEvent ->
      fileEvent.file?.let { virtualFile ->
        if (virtualFile.jvmClassFile()) {
          val packageDirectory = virtualFile.parent
          val deserializer = DeserializerForClassfileDecompiler(packageDirectory)
          val packageName = deserializer.directoryPackageFqName
          cache[packageName] = deserializer
          println("Added to cache [$packageName] = $deserializer, event: $fileEvent")
        }
      }
    }
    return this
  }

  private fun VirtualFile.jvmClassFile() = path.endsWith(".class")

  override fun afterVfsChange() {
    println("MetaSyntheticResolveExtension.afterVfsChange")
  }

  override fun beforeVfsChange() {
    println("MetaSyntheticResolveExtension.beforeVfsChange")
  }

  override fun addSyntheticSupertypes(
    thisDescriptor: ClassDescriptor,
    supertypes: MutableList<KotlinType>
  ) {
    println("START MetaSyntheticResolveExtension.addSyntheticSupertypes: $thisDescriptor $supertypes")
    val synth = thisDescriptor.meta()?.defaultType?.let(TypeUtils::getAllSupertypes) ?: emptySet()
    supertypes.addAll(synth)
    println("END MetaSyntheticResolveExtension.addSyntheticSupertypes: $thisDescriptor $supertypes")
  }

  override fun generateSyntheticClasses(
    thisDescriptor: ClassDescriptor,
    name: Name,
    ctx: LazyClassContext,
    declarationProvider: ClassMemberDeclarationProvider,
    result: MutableSet<ClassDescriptor>
  ) {
    println("START MetaSyntheticResolveExtension.generateSyntheticClasses: $thisDescriptor")
    val synth = thisDescriptor.metaMemberScope()?.getContributedDescriptors { true }?.filterIsInstance<ClassDescriptor>()
      ?: emptyList()
    result.addAll(synth)
    println("END MetaSyntheticResolveExtension.generateSyntheticClasses: $thisDescriptor $result")
  }

  override fun generateSyntheticClasses(
    thisDescriptor: PackageFragmentDescriptor,
    name: Name,
    ctx: LazyClassContext,
    declarationProvider: PackageMemberDeclarationProvider,
    result: MutableSet<ClassDescriptor>
  ) {
    println("START MetaSyntheticResolveExtension.generatePackageSyntheticClasses: $thisDescriptor")
    val synth = thisDescriptor.metaMemberScope()?.getContributedDescriptors { true }?.filterIsInstance<ClassDescriptor>()
      ?: emptyList()
    result.addAll(synth)
    println("END MetaSyntheticResolveExtension.generatePackageSyntheticClasses: $thisDescriptor $result")
  }

  override fun generateSyntheticMethods(
    thisDescriptor: ClassDescriptor,
    name: Name,
    bindingContext: BindingContext,
    fromSupertypes: List<SimpleFunctionDescriptor>,
    result: MutableCollection<SimpleFunctionDescriptor>
  ) {
    println("START MetaSyntheticResolveExtension.generateSyntheticMethods: $thisDescriptor")
    val synth = thisDescriptor.metaMemberScope()?.getContributedDescriptors { true }?.filterIsInstance<SimpleFunctionDescriptor>()
      ?: emptyList()
    result.addAll(synth)
    println("END MetaSyntheticResolveExtension.generateSyntheticMethods: $thisDescriptor $result")
  }

  override fun generateSyntheticProperties(
    thisDescriptor: ClassDescriptor,
    name: Name,
    bindingContext: BindingContext,
    fromSupertypes: ArrayList<PropertyDescriptor>,
    result: MutableSet<PropertyDescriptor>
  ) {
    println("START MetaSyntheticResolveExtension.generateSyntheticProperties: $thisDescriptor")
    val synth = thisDescriptor.metaMemberScope()?.getContributedDescriptors { true }?.filterIsInstance<PropertyDescriptor>()
      ?: emptyList()
    result.addAll(synth)
    println("END MetaSyntheticResolveExtension.generateSyntheticProperties: $thisDescriptor $result")
  }

  override fun generateSyntheticSecondaryConstructors(
    thisDescriptor: ClassDescriptor,
    bindingContext: BindingContext,
    result: MutableCollection<ClassConstructorDescriptor>
  ) {
    println("START MetaSyntheticResolveExtension.generateSyntheticSecondaryConstructors: $thisDescriptor")
    val synth = thisDescriptor.meta()?.secondaryConstructors ?: emptyList()
    result.addAll(synth)
    println("END MetaSyntheticResolveExtension.generateSyntheticSecondaryConstructors: $thisDescriptor $result")
  }

  override fun getSyntheticCompanionObjectNameIfNeeded(thisDescriptor: ClassDescriptor): Name? {
    println("START MetaSyntheticResolveExtension.getSyntheticCompanionObjectNameIfNeeded: $thisDescriptor")
    val synth = thisDescriptor.meta()?.companionObjectDescriptor?.name
    println("END MetaSyntheticResolveExtension.getSyntheticCompanionObjectNameIfNeeded: $thisDescriptor $synth")
    return synth
  }

  override fun getSyntheticFunctionNames(thisDescriptor: ClassDescriptor): List<Name> {
    println("START MetaSyntheticResolveExtension.getSyntheticFunctionNames: $thisDescriptor")
    val synth = thisDescriptor.metaMemberScope()?.getFunctionNames()?.toList() ?: emptyList()
    println("END MetaSyntheticResolveExtension.getSyntheticFunctionNames: $thisDescriptor $synth")
    return synth
  }

  override fun getSyntheticNestedClassNames(thisDescriptor: ClassDescriptor): List<Name> {
    println("START MetaSyntheticResolveExtension.getSyntheticNestedClassNames: $thisDescriptor")
    val synth = thisDescriptor.metaMemberScope()?.getClassifierNames()?.toList() ?: emptyList()
    println("END MetaSyntheticResolveExtension.getSyntheticNestedClassNames: $thisDescriptor $synth")
    return synth
  }
}

fun PackageFragmentDescriptor.metaDecompiler(): DeserializerForClassfileDecompiler? =
  cache[fqName]

fun ClassDescriptor.metaDecompiler(): DeserializerForClassfileDecompiler? =
  cache.values.firstOrNull { fqNameSafe.asString().startsWith(it.directoryPackageFqName.asString()) }

fun ClassDescriptor.isMeta(): Boolean =
  module.name.asString().startsWith("<special")

fun ClassDescriptor.meta(): ClassDescriptor? =
  if (isMeta()) null else classId?.let { classId -> metaDecompiler()?.resolveTopLevelClass(classId) }

fun PackageFragmentDescriptor.metaMemberScope(): MemberScope? =
  metaDecompiler()
    ?.resolveDeclarationsInFacade(fqName)
    ?.filterIsInstance<PackageFragmentDescriptor>()
    ?.firstOrNull()
    ?.getMemberScope()

fun ClassDescriptor.metaMemberScope(): MemberScope? =
  meta()?.unsubstitutedMemberScope
