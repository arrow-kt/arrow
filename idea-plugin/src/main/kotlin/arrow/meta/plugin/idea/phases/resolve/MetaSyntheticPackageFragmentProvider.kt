package arrow.meta.plugin.idea.phases.resolve

import arrow.meta.plugin.idea.IdeMetaPlugin
import arrow.meta.plugin.idea.phases.config.buildFolders
import arrow.meta.plugin.idea.phases.config.currentProject
import com.intellij.openapi.Disposable
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.AsyncFileListener
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.openapi.vfs.newvfs.events.VFileEvent
import com.intellij.psi.ClassFileViewProviderFactory
import com.intellij.psi.PsiManager
import org.jetbrains.kotlin.analyzer.ModuleInfo
import org.jetbrains.kotlin.caches.resolve.KotlinCacheService
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.descriptors.ClassifierDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.PackageFragmentProvider
import org.jetbrains.kotlin.descriptors.PackageFragmentProviderImpl
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.descriptors.SimpleFunctionDescriptor
import org.jetbrains.kotlin.descriptors.impl.PackageFragmentDescriptorImpl
import org.jetbrains.kotlin.idea.KotlinLanguage
import org.jetbrains.kotlin.idea.resolve.ResolutionFacade
import org.jetbrains.kotlin.incremental.components.LookupLocation
import org.jetbrains.kotlin.incremental.components.LookupTracker
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.stubs.elements.KtFileStubBuilder
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.jvm.extensions.PackageFragmentProviderExtension
import org.jetbrains.kotlin.resolve.lazy.BodyResolveMode
import org.jetbrains.kotlin.resolve.scopes.DescriptorKindFilter
import org.jetbrains.kotlin.resolve.scopes.MemberScope
import org.jetbrains.kotlin.storage.StorageManager
import org.jetbrains.kotlin.utils.Printer
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import java.util.regex.Pattern

private val metaPlugin = IdeMetaPlugin()

private val registered = AtomicBoolean(false)

class MetaSyntheticPackageFragmentProvider : PackageFragmentProviderExtension, AsyncFileListener, AsyncFileListener.ChangeApplier, Disposable {

  private val fileListenerInitialized: AtomicBoolean = AtomicBoolean(false)

  @Synchronized
  private fun Project.registerIdeStack(postInitialize : () -> Unit) {
    if (!registered.getAndSet(true)) {
      val project = currentProject()
      if (project != null) {
        val configuration = CompilerConfiguration()
        metaPlugin.registerMetaComponents(this, configuration)
        println("registerIdeProjectComponents DONE")
        postInitialize()
      } else {
        registered.set(false)
      }
    }
  }

  @Synchronized
  private fun initializeFileListener() {
    if (!fileListenerInitialized.get()) {
      currentProject()?.let { project ->
        val virtualFileManager = project.getComponent(VirtualFileManager::class.java)
        virtualFileManager?.addAsyncFileListener(this, this)
        fileListenerInitialized.set(true)
      }
    }
  }

  override fun getPackageFragmentProvider(
    project: Project,
    module: ModuleDescriptor,
    storageManager: StorageManager,
    trace: BindingTrace,
    moduleInfo: ModuleInfo?,
    lookupTracker: LookupTracker
  ): PackageFragmentProvider? {
    project.registerIdeStack {
      computeCache(project)
    }
    initializeFileListener()
    println("MetaSyntheticPackageFragmentProvider.getPackageFragmentProvider:, cache:\n $descriptorCache")
    return PackageFragmentProviderImpl(
      descriptorCache.keys().toList().map { packageName ->
        BuildCachePackageFragmentDescriptor(module, packageName)
      }
    )
  }

  inner class BuildCachePackageFragmentDescriptor(module: ModuleDescriptor, fqName: FqName) : PackageFragmentDescriptorImpl(module, fqName) {

    override fun getMemberScope(): MemberScope = scope

    private val scope = Scope()

    inner class Scope : MemberScope {
      override fun getClassifierNames(): Set<Name>? =
        descriptorCache[fqName]?.filterIsInstance<ClassifierDescriptor>()?.map { it.name }?.toSet()

      override fun getContributedClassifier(name: Name, location: LookupLocation): ClassifierDescriptor? =
        descriptorCache[fqName]?.filterIsInstance<ClassifierDescriptor>()?.firstOrNull { it.name == name }

      override fun getContributedDescriptors(kindFilter: DescriptorKindFilter, nameFilter: (Name) -> Boolean): Collection<DeclarationDescriptor> =
        descriptorCache[fqName]?.filter { it.name == name } ?: emptyList()

      override fun getContributedFunctions(name: Name, location: LookupLocation): Collection<SimpleFunctionDescriptor> =
        descriptorCache[fqName]?.filterIsInstance<SimpleFunctionDescriptor>() ?: emptyList()

      override fun getContributedVariables(name: Name, location: LookupLocation): Collection<PropertyDescriptor> =
        descriptorCache[fqName]?.filterIsInstance<PropertyDescriptor>() ?: emptyList()

      override fun getFunctionNames(): Set<Name> =
        descriptorCache[fqName]?.filterIsInstance<SimpleFunctionDescriptor>()?.map { it.name }?.toSet() ?: emptySet()

      override fun getVariableNames(): Set<Name> =
        descriptorCache[fqName]?.filterIsInstance<PropertyDescriptor>()?.map { it.name }?.toSet() ?: emptySet()

      override fun printScopeStructure(p: Printer) {
      }
    }
  }

  override fun dispose() {
    println("MetaSyntheticPackageFragmentProvider.dispose")
    descriptorCache.clear()
  }

  override fun beforeVfsChange() {
    println("MetaSyntheticPackageFragmentProvider.beforeVfsChange")
  }

  override fun afterVfsChange() {
    println("MetaSyntheticPackageFragmentProvider.afterVfsChange")
    currentProject()?.let { computeCache(it) }
    refreshingFiles.forEach {
      it.refresh(false, true) {
        println("Refreshed: $it")
      }
    }
    refreshingFiles.clear()
  }

  private val refreshingFiles: HashSet<VirtualFile> = hashSetOf()

  override fun prepareChange(events: MutableList<out VFileEvent>): AsyncFileListener.ChangeApplier? {
    refreshingFiles.addAll(events.mapNotNull { it.file })
    return this
  }

  private val descriptorCache: ConcurrentHashMap<FqName, List<DeclarationDescriptor>> = ConcurrentHashMap()

  @Synchronized
  private fun computeCache(project: Project): Unit {
    println("initializing new PackageFragmentProvider")
    descriptorCache.clear()
    val localFileSystem = LocalFileSystem.getInstance()
    val psiManager = project.getComponent(PsiManager::class.java)
    val buildFolders = project.buildFolders()
    val cacheService = KotlinCacheService.getInstance(project)
    val classFileViewProvider = ClassFileViewProviderFactory()
    val classFiles = buildFolders.packagedClasses(localFileSystem, classFileViewProvider, psiManager)
    if (classFiles.isNotEmpty()) {
      val resolutionFacade = cacheService.getResolutionFacade(classFiles.map { it.second })
      val resolvedDeclarations = classFiles.resolveClassesDeclarations(resolutionFacade)
      resolvedDeclarations.forEach { (packageName, declarations) ->
        val cachedDescriptors = descriptorCache[packageName] ?: emptyList()
        val leftovers = cachedDescriptors.filterNot { it in declarations }
        val newCachedPackageDescriptors = declarations + leftovers
        println("Adding to cache: $newCachedPackageDescriptors")
        descriptorCache[packageName] = newCachedPackageDescriptors
      }
    }
  }

  private fun List<Pair<FqName, KtFile>>.resolveClassesDeclarations(resolutionFacade: ResolutionFacade): List<Pair<FqName, List<DeclarationDescriptor>>> =
    flatMap { (packageName, file) ->
      listOf(packageName to file.declarations.resolveDeclarations(resolutionFacade))
    }

  private fun List<KtDeclaration>.resolveDeclarations(resolutionFacade: ResolutionFacade): List<DeclarationDescriptor> =
    map { ktDeclaration -> ktDeclaration.resolveDeclaration(resolutionFacade) }

  private fun KtDeclaration.resolveDeclaration(resolutionFacade: ResolutionFacade) =
    resolutionFacade.resolveToDescriptor(this, BodyResolveMode.PARTIAL_FOR_COMPLETION)

  private fun List<VirtualFile>.packagedClasses(localFileSystem: LocalFileSystem, classFileViewProvider: ClassFileViewProviderFactory, psiManager: PsiManager): List<Pair<FqName, KtFile>> =
    flatMap { buildFolder ->
      val buildRoot = File(buildFolder.path)
      val matchedFiles = arrayListOf<File>()
      FileUtil.collectMatchedFiles(buildRoot, Pattern.compile("(.*)\\.class$"), matchedFiles)
      matchedFiles
        .filterNot { it.name.endsWith("DefaultImpls.class") } //we don't want this synthetic files in the way
        .mapNotNull { file ->
          val maybeVirtualFile = localFileSystem.findFileByIoFile(file)
          maybeVirtualFile?.let { virtualFile ->
            val viewProvider = classFileViewProvider.createFileViewProvider(virtualFile, KotlinLanguage.INSTANCE, psiManager, true)
            val ktFile = KtFile(viewProvider, true)
            KtFileStubBuilder().buildStubTree(ktFile)
            val packageName = ktFile.packageFqName
            packageName to ktFile
          }
        }
    }

}