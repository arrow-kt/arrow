package arrow.meta.plugin.idea.phases.resolve

import arrow.meta.plugin.idea.phases.config.buildFolders
import arrow.meta.quotes.get
import arrow.meta.quotes.ktClassOrObject
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.IndexNotReadyException
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.intellij.psi.impl.PsiManagerImpl
import org.jetbrains.kotlin.analyzer.ModuleInfo
import org.jetbrains.kotlin.caches.resolve.KotlinCacheService
import org.jetbrains.kotlin.daemon.common.findWithTransform
import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor
import org.jetbrains.kotlin.descriptors.ClassConstructorDescriptor
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.ClassifierDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.PackageFragmentDescriptor
import org.jetbrains.kotlin.descriptors.PackageFragmentProvider
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.descriptors.SimpleFunctionDescriptor
import org.jetbrains.kotlin.descriptors.SourceElement
import org.jetbrains.kotlin.descriptors.Visibilities
import org.jetbrains.kotlin.descriptors.impl.PackageFragmentDescriptorImpl
import org.jetbrains.kotlin.idea.KotlinLanguage
import org.jetbrains.kotlin.idea.caches.resolve.analyzeAndGetResult
import org.jetbrains.kotlin.idea.resolve.ResolutionFacade
import org.jetbrains.kotlin.idea.stubindex.resolve.StubBasedPackageMemberDeclarationProvider
import org.jetbrains.kotlin.incremental.components.LookupLocation
import org.jetbrains.kotlin.incremental.components.LookupTracker
import org.jetbrains.kotlin.incremental.components.NoLookupLocation
import org.jetbrains.kotlin.js.resolve.diagnostics.findPsi
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtClassBody
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtModifierList
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtPrimaryConstructor
import org.jetbrains.kotlin.psi.KtPureClassOrObject
import org.jetbrains.kotlin.psi.KtSecondaryConstructor
import org.jetbrains.kotlin.psi.KtSuperTypeListEntry
import org.jetbrains.kotlin.psi.stubs.elements.KtFileStubBuilder
import org.jetbrains.kotlin.psi.synthetics.SyntheticClassOrObjectDescriptor
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameOrNull
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.resolve.extensions.SyntheticResolveExtension
import org.jetbrains.kotlin.resolve.jvm.extensions.PackageFragmentProviderExtension
import org.jetbrains.kotlin.resolve.lazy.BodyResolveMode
import org.jetbrains.kotlin.resolve.lazy.LazyClassContext
import org.jetbrains.kotlin.resolve.lazy.LazyEntity
import org.jetbrains.kotlin.resolve.lazy.declarations.ClassMemberDeclarationProvider
import org.jetbrains.kotlin.resolve.lazy.declarations.DeclarationProvider
import org.jetbrains.kotlin.resolve.lazy.declarations.PackageMemberDeclarationProvider
import org.jetbrains.kotlin.resolve.lazy.descriptors.LazyClassDescriptor
import org.jetbrains.kotlin.resolve.scopes.DescriptorKindFilter
import org.jetbrains.kotlin.resolve.scopes.MemberScope
import org.jetbrains.kotlin.storage.StorageManager
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.isError
import org.jetbrains.kotlin.utils.Printer
import org.jetbrains.kotlin.utils.addToStdlib.safeAs
import java.io.File
import java.util.ArrayList
import java.util.concurrent.ConcurrentHashMap
import java.util.regex.Pattern

class MetaSyntheticPackageFragmentProvider(val project: Project) :
  PackageFragmentProviderExtension,
  SyntheticResolveExtension,
  Disposable {

  companion object {
    fun getInstance(project: Project): MetaSyntheticPackageFragmentProvider {
      val first = PackageFragmentProviderExtension.getInstances(project)
        .filterIsInstance<MetaSyntheticPackageFragmentProvider>()
        .firstOrNull()
      return first ?: throw IllegalStateException("fragment provider must not be null")
    }

    fun measureTimeMillis(block: () -> Unit): Long {
      val start = System.currentTimeMillis()
      try {
        block()
      } finally {
        return System.currentTimeMillis() - start
      }
    }
  }

  /**
   * For now, there's one cache per project. For example, refreshing the cache when a project
   * is opened must not clear the data of another project.
   */
  private val descriptorCache: ConcurrentHashMap<FqName, List<DeclarationDescriptor>> = ConcurrentHashMap()

  override fun getPackageFragmentProvider(
    project: Project,
    module: ModuleDescriptor,
    storageManager: StorageManager,
    trace: BindingTrace,
    moduleInfo: ModuleInfo?,
    lookupTracker: LookupTracker
  ): PackageFragmentProvider? {
    return DescriptorCachePackageFragmentProvider(module)
  }

  inner class DescriptorCachePackageFragmentProvider(val module: ModuleDescriptor) : PackageFragmentProvider {
    override fun getPackageFragments(fqName: FqName): List<PackageFragmentDescriptor> =
      descriptorCache.keys().toList().map { packageName ->
        BuildCachePackageFragmentDescriptor(module, packageName)
      }

    override fun getSubPackagesOf(fqName: FqName, nameFilter: (Name) -> Boolean): Collection<FqName> =
      getPackageFragments(fqName).map { it.fqName }
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
    LOG.info("MetaSyntheticPackageFragmentProvider.dispose")
    descriptorCache.clear()
  }

  /**
   * Triggers a cache refresh. It's called in a background thread
   * and only when indexing is not in progress.
   * If indexing is in progress when this is called, then the refresh will be
   * delayed until indexing finished.
   */
  internal fun computeCacheAsync() {
    ApplicationManager.getApplication().executeOnPooledThread {
      DumbService.getInstance(project).runReadActionInSmartMode {
        computeCache()
      }
    }
  }

  @Synchronized
  private fun computeCache() {
    assert(ApplicationManager.getApplication().isReadAccessAllowed)

    measureTimeMillis {
      LOG.debug("initializing new PackageFragmentProvider")
      descriptorCache.clear()

      val localFileSystem = LocalFileSystem.getInstance()
      val psiManager = project.getComponent(PsiManager::class.java)
      val buildFolders = project.buildFolders()
      val classFiles = buildFolders.packagedClasses(localFileSystem, psiManager)

      LOG.debug("build folders: $buildFolders")

      if (classFiles.isNotEmpty()) {
        val cacheService = KotlinCacheService.getInstance(project)
        val resolutionFacade = cacheService.getResolutionFacade(classFiles.map { it.second })
        val resolvedDeclarations = classFiles.resolveClassesDeclarations(resolutionFacade)
        resolvedDeclarations.forEach { (packageName, declarations) ->
          val cachedDescriptors = descriptorCache[packageName] ?: emptyList()
          val leftovers = cachedDescriptors.filterNot { it in declarations }
          val newCachedPackageDescriptors = declarations + leftovers
          val synthDescriptors = newCachedPackageDescriptors.filter { it.isMetaSynthetic() }
          if (synthDescriptors.isNotEmpty()) {
            synthDescriptors.forEach { synthDescriptor ->
              try {
                if (synthDescriptor is LazyEntity) synthDescriptor.forceResolveAllContents()
              } catch (e: IndexNotReadyException) {
                LOG.warn("Index wasn't ready to resolve: ${synthDescriptor.name}")
              }
            }
            descriptorCache[packageName] = synthDescriptors
          }
        }
      }

      if (LOG.isDebugEnabled) {
        LOG.debug("MetaSyntheticPackageFragmentProvider.getPackageFragmentProvider:, cache:\n ${descriptorCache.toList().joinToString {
          "${it.first} : ${it.second.size}"
        }}")
      }
    }.let {
      LOG.info("computeCache() took $it ms")
    }
  }

  private fun List<LazyClassDescriptor>.toSynthetic(declarationProvider: DeclarationProvider): List<ClassDescriptor> =
    map { it.synthetic(declarationProvider) }

  private fun List<SimpleFunctionDescriptor>.toSynthetic(): List<SimpleFunctionDescriptor> =
    map { it.synthetic() }

  private fun SimpleFunctionDescriptor.synthetic(): SimpleFunctionDescriptor =
    copy(
      containingDeclaration,
      modality,
      if (visibility == Visibilities.INHERITED) Visibilities.PUBLIC else visibility,
      CallableMemberDescriptor.Kind.SYNTHESIZED,
      true
    )

  private fun LazyClassDescriptor.synthetic(declarationProvider: DeclarationProvider): SyntheticClassOrObjectDescriptor {
    val ktDeclaration = this.ktClassOrObject()
    return SyntheticClassOrObjectDescriptor(
      c = this["c"],
      parentClassOrObject = when (declarationProvider) {
        is ClassMemberDeclarationProvider -> declarationProvider.correspondingClassOrObject
        is StubBasedPackageMemberDeclarationProvider -> declarationProvider.getPackageFiles().firstOrNull {
          it.declarations.any { declaration -> declaration == ktDeclaration }
        }?.ktPureClassOrObject()
        is PackageMemberDeclarationProvider -> declarationProvider.getPackageFiles().firstOrNull {
          it.declarations.any { declaration -> declaration == ktDeclaration }
        }?.ktPureClassOrObject()
        else -> null
      } ?: TODO("Unknown declaration provider $declarationProvider"),
      containingDeclaration = containingDeclaration,
      name = name,
      source = SourceElement.NO_SOURCE,
      outerScope = scopeForClassHeaderResolution,
      modality = if (modality == Modality.SEALED) Modality.ABSTRACT else modality,
      visibility = if (visibility == Visibilities.INHERITED) Visibilities.PUBLIC else visibility,
      annotations = annotations,
      constructorVisibility = Visibilities.PUBLIC,
      kind = kind,
      isCompanionObject = isCompanionObject
    ).also {
      it.initialize(declaredTypeParameters)
    }
  }

  private fun KtFile.ktPureClassOrObject(): KtPureClassOrObject =
    object : KtPureClassOrObject {
      override fun hasExplicitPrimaryConstructor(): Boolean = false
      override fun getParent(): PsiElement = this@ktPureClassOrObject.psiOrParent
      override fun getName(): String? = this@ktPureClassOrObject.name
      override fun getPrimaryConstructorParameters(): List<KtParameter> = emptyList()
      override fun getSecondaryConstructors(): List<KtSecondaryConstructor> = emptyList()
      override fun hasPrimaryConstructor(): Boolean = false
      override fun getContainingKtFile(): KtFile = this@ktPureClassOrObject
      override fun getPrimaryConstructor(): KtPrimaryConstructor? = null
      override fun getSuperTypeListEntries(): List<KtSuperTypeListEntry> = emptyList()
      override fun getPsiOrParent(): KtElement = this@ktPureClassOrObject.psiOrParent
      override fun getPrimaryConstructorModifierList(): KtModifierList? = null
      override fun isLocal(): Boolean = false
      override fun getCompanionObjects(): List<KtObjectDeclaration> = emptyList()
      override fun getDeclarations(): List<KtDeclaration> = this@ktPureClassOrObject.declarations
      override fun getBody(): KtClassBody? = null
    }

  private fun DeclarationDescriptor.isMetaSynthetic(): Boolean =
    annotations.findAnnotation(FqName("arrow.synthetic")) != null

  private fun List<Pair<FqName, KtFile>>.resolveClassesDeclarations(resolutionFacade: ResolutionFacade): List<Pair<FqName, List<DeclarationDescriptor>>> =
    flatMap { (packageName, file) ->
      file.analyzeAndGetResult().let {
        it.bindingContext.diagnostics.all().forEach { diagnostic ->
          LOG.debug("$file : $diagnostic")
        }
      }
      listOf(packageName to file.declarations.resolveDeclarations(resolutionFacade))
    }

  private fun List<KtDeclaration>.resolveDeclarations(resolutionFacade: ResolutionFacade): List<DeclarationDescriptor> =
    map { ktDeclaration -> ktDeclaration.resolveDeclaration(resolutionFacade) }

  private fun KtDeclaration.resolveDeclaration(resolutionFacade: ResolutionFacade): DeclarationDescriptor =
    resolutionFacade.resolveToDescriptor(this, BodyResolveMode.FULL)

  private fun List<VirtualFile>.packagedClasses(localFileSystem: LocalFileSystem, psiManager: PsiManager): List<Pair<FqName, KtFile>> =
    flatMap { buildFolder ->
      val buildRoot = File(buildFolder.path)
      val matchedFiles = arrayListOf<File>()
      FileUtil.collectMatchedFiles(buildRoot, Pattern.compile("(.*)\\.class$"), matchedFiles)
      matchedFiles
        .filterNot { it.name.endsWith("DefaultImpls.class") } //we don't want this synthetic files in the way
        .mapNotNull { file ->
          val maybeVirtualFile = localFileSystem.findFileByIoFile(file)
          maybeVirtualFile?.let { virtualFile ->
            psiManager as PsiManagerImpl
            val viewProvider = psiManager.fileManager.createFileViewProvider(virtualFile, true)
            //classFileViewProvider.createFileViewProvider(virtualFile, KotlinLanguage.INSTANCE, psiManager, true)
            if (viewProvider.hasLanguage(KotlinLanguage.INSTANCE)) {
              val ktFile = KtFile(viewProvider, true)
              KtFileStubBuilder().buildStubTree(ktFile)
              val packageName = ktFile.packageFqName
              packageName to ktFile
            } else null
          }
        }
    }

  override fun generateSyntheticClasses(thisDescriptor: PackageFragmentDescriptor, name: Name, ctx: LazyClassContext, declarationProvider: PackageMemberDeclarationProvider, result: MutableSet<ClassDescriptor>) {
    if (!thisDescriptor.isMetaSynthetic()) {
      result.replaceWithSynthetics(thisDescriptor.fqName, declarationProvider)
      descriptorCache[thisDescriptor.fqName]?.filterIsInstance<LazyClassDescriptor>()?.filter { !it.isMetaSynthetic() }?.let {
        LOG.debug("generatePackageSyntheticClasses: ${thisDescriptor.fqName}: $it, $name, result: $result")
        result.addAll(it.toSynthetic(declarationProvider))
      }
    }
  }

  private fun MutableSet<ClassDescriptor>.replaceWithSynthetics(packageName: FqName, declarationProvider: DeclarationProvider): Unit {
    val replacements = map { existing ->
      val synthDescriptors = descriptorCache[packageName]?.filterIsInstance<LazyClassDescriptor>() ?: emptyList()
      val replacement = synthDescriptors.find { synth -> synth.fqNameOrNull() == existing.fqNameOrNull() }
      existing to replacement
    }.toMap()
    removeIf { replacements[it] != null }
    addAll(replacements.values.filterNotNull().toSynthetic(declarationProvider))
  }

  private fun MutableCollection<SimpleFunctionDescriptor>.replaceWithSynthetics(packageName: FqName): Unit {
    val replacements = mapNotNull { existing ->
      val synthDescriptors = descriptorCache[packageName]?.filterIsInstance<LazyClassDescriptor>() ?: emptyList()
      val replacement = synthDescriptors.findWithTransform { synth ->
        val fn = synth.unsubstitutedMemberScope.getContributedFunctions(existing.name, NoLookupLocation.FROM_BACKEND).firstOrNull()
        (fn != null) to fn
      }
      existing to replacement
    }.toMap()
    removeIf { replacements[it] != null }
    addAll(replacements.values.filterNotNull())
    val allSynths = map { it.synthetic() }
    clear()
    addAll(allSynths)
  }


  override fun generateSyntheticClasses(thisDescriptor: ClassDescriptor, name: Name, ctx: LazyClassContext, declarationProvider: ClassMemberDeclarationProvider, result: MutableSet<ClassDescriptor>) {
    if (!thisDescriptor.isMetaSynthetic()) {
      thisDescriptor.findPsi().safeAs<KtClassOrObject>()?.containingKtFile?.packageFqName?.let { packageName ->
        result.replaceWithSynthetics(packageName, declarationProvider)
        descriptorCache[packageName]?.filterIsInstance<LazyClassDescriptor>()?.find { it.fqNameSafe == thisDescriptor.fqNameSafe }?.let { classDescriptor ->
          val synthNestedClasses = classDescriptor.unsubstitutedMemberScope.getContributedDescriptors { true }.filter { it.isMetaSynthetic() }.filterIsInstance<LazyClassDescriptor>()
          LOG.debug("generateNestedSyntheticClasses: ${thisDescriptor.name}: $synthNestedClasses")
          result.addAll(synthNestedClasses.toSynthetic(declarationProvider))
        }
      }
    }
  }

  override fun generateSyntheticMethods(thisDescriptor: ClassDescriptor, name: Name, bindingContext: BindingContext, fromSupertypes: List<SimpleFunctionDescriptor>, result: MutableCollection<SimpleFunctionDescriptor>) {
    if (!thisDescriptor.isMetaSynthetic()) {
      thisDescriptor.findPsi().safeAs<KtClassOrObject>()?.containingKtFile?.packageFqName?.let { packageName ->
        //result.replaceWithSynthetics(packageName)
        descriptorCache[packageName]?.filterIsInstance<ClassDescriptor>()?.find { it.fqNameSafe == thisDescriptor.fqNameSafe }?.let { classDescriptor ->
          val synthMemberFunctions = classDescriptor.unsubstitutedMemberScope.getContributedDescriptors { true }.filter { it.isMetaSynthetic() }.filterIsInstance<SimpleFunctionDescriptor>()
          LOG.debug("generateSyntheticMethods: ${thisDescriptor.name}: $synthMemberFunctions")
          result.addAll(synthMemberFunctions.toSynthetic())
        }
      }
    }
  }

  override fun generateSyntheticProperties(thisDescriptor: ClassDescriptor, name: Name, bindingContext: BindingContext, fromSupertypes: ArrayList<PropertyDescriptor>, result: MutableSet<PropertyDescriptor>) {
    if (!thisDescriptor.isMetaSynthetic()) {
      thisDescriptor.findPsi().safeAs<KtClassOrObject>()?.containingKtFile?.packageFqName?.let { packageName ->
        descriptorCache[packageName]?.filterIsInstance<ClassDescriptor>()?.find { it.fqNameSafe == thisDescriptor.fqNameSafe }?.let { classDescriptor ->
          val syntMemberProperties = classDescriptor.unsubstitutedMemberScope.getContributedDescriptors { true }.filter { it.isMetaSynthetic() }.filterIsInstance<PropertyDescriptor>()
          LOG.debug("generateSyntheticProperties: ${thisDescriptor.name}: $syntMemberProperties")
          result.addAll(syntMemberProperties)
        }
      }
    }
  }

  override fun generateSyntheticSecondaryConstructors(thisDescriptor: ClassDescriptor, bindingContext: BindingContext, result: MutableCollection<ClassConstructorDescriptor>) {
    if (!thisDescriptor.isMetaSynthetic()) {
      thisDescriptor.findPsi().safeAs<KtClassOrObject>()?.containingKtFile?.packageFqName?.let { packageName ->
        descriptorCache[packageName]?.filterIsInstance<ClassDescriptor>()?.find { it.fqNameSafe == thisDescriptor.fqNameSafe }?.let { classDescriptor ->
          val synthSecConstructors = classDescriptor.unsubstitutedMemberScope.getContributedDescriptors { true }.filterIsInstance<ClassConstructorDescriptor>().filter { !it.isPrimary && it.isMetaSynthetic() }
          LOG.debug("generateSyntheticSecondaryConstructors: ${thisDescriptor.name}: $synthSecConstructors")
          result.addAll(synthSecConstructors)
        }
      }
    }
  }

  override fun getSyntheticCompanionObjectNameIfNeeded(thisDescriptor: ClassDescriptor): Name? =
    if (!thisDescriptor.isMetaSynthetic()) {
      thisDescriptor.findPsi().safeAs<KtClassOrObject>()?.containingKtFile?.packageFqName?.let { packageName ->
        descriptorCache[packageName]?.filterIsInstance<ClassDescriptor>()?.find { it.fqNameSafe == thisDescriptor.fqNameSafe }?.let { classDescriptor ->
          val name = classDescriptor.companionObjectDescriptor?.name
          LOG.debug("getSyntheticCompanionObjectNameIfNeeded: ${thisDescriptor.name}: $name")
          name
        }
      }
    } else null

  override fun getSyntheticFunctionNames(thisDescriptor: ClassDescriptor): List<Name> =
    if (!thisDescriptor.isMetaSynthetic()) {
      thisDescriptor.findPsi().safeAs<KtClassOrObject>()?.containingKtFile?.packageFqName?.let { packageName ->
        descriptorCache[packageName]?.filterIsInstance<ClassDescriptor>()?.find { it.fqNameSafe == thisDescriptor.fqNameSafe }?.let { classDescriptor ->
          val synthFunctionNames = classDescriptor.unsubstitutedMemberScope.getContributedDescriptors { true }.filterIsInstance<SimpleFunctionDescriptor>().filter { it.isMetaSynthetic() }.map { it.name }
          LOG.debug("getSyntheticFunctionNames: ${thisDescriptor.name}: $synthFunctionNames")
          synthFunctionNames
        }
      } ?: emptyList()
    } else emptyList()


  override fun getSyntheticNestedClassNames(thisDescriptor: ClassDescriptor): List<Name> =
    if (!thisDescriptor.isMetaSynthetic()) {
      thisDescriptor.findPsi().safeAs<KtClassOrObject>()?.containingKtFile?.packageFqName?.let { packageName ->
        descriptorCache[packageName]?.filterIsInstance<ClassDescriptor>()?.find { it.fqNameSafe == thisDescriptor.fqNameSafe }?.let { classDescriptor ->
          val synthClassNames = classDescriptor.unsubstitutedMemberScope.getContributedDescriptors { true }.filterIsInstance<ClassDescriptor>().filter { it.isMetaSynthetic() }.map { it.name }
          LOG.debug("getSyntheticFunctionNames: ${thisDescriptor.name}: $synthClassNames")
          synthClassNames
        }
      } ?: emptyList()
    } else emptyList()

  override fun addSyntheticSupertypes(thisDescriptor: ClassDescriptor, supertypes: MutableList<KotlinType>) {
    if (!thisDescriptor.isMetaSynthetic()) {
      thisDescriptor.ktClassOrObject()?.containingKtFile?.packageFqName?.let { packageName ->
        descriptorCache[packageName]?.let { declarations ->
          declarations.filterIsInstance<ClassDescriptor>().find {
            thisDescriptor.fqNameSafe == it.fqNameSafe
          }?.typeConstructor?.supertypes?.let { collection ->
            //supertypes.clear()
            val result = collection.filterNot { it.isError }
            LOG.debug("Found synth supertypes: $result")
            supertypes.addAll(result)
          }
        }
      }
    }
  }
}

