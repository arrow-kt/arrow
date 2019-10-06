package arrow.meta.plugin.idea.phases.analysis

import arrow.meta.phases.CompilerContext
import arrow.meta.phases.analysis.MetaAnalyzer
import arrow.meta.plugin.idea.phases.config.currentProject
import arrow.meta.quotes.Quote
import arrow.meta.quotes.functionNames
import arrow.meta.quotes.ktClassOrObject
import arrow.meta.quotes.ktFile
import arrow.meta.quotes.nestedClassNames
import com.intellij.AppTopics
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.compiler.CompilationStatusListener
import com.intellij.openapi.compiler.CompileContext
import com.intellij.openapi.compiler.CompilerTopics
import com.intellij.openapi.editor.Document
import com.intellij.openapi.fileEditor.FileDocumentManagerListener
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerEvent
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.fileEditor.FileEditorProvider
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.UserDataHolderBase
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.keyFMap.KeyFMap
import org.jetbrains.kotlin.analyzer.AnalysisResult
import org.jetbrains.kotlin.analyzer.ModuleInfo
import org.jetbrains.kotlin.container.get
import org.jetbrains.kotlin.context.SimpleGlobalContext
import org.jetbrains.kotlin.context.withModule
import org.jetbrains.kotlin.context.withProject
import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.PackageFragmentDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.descriptors.SimpleFunctionDescriptor
import org.jetbrains.kotlin.frontend.di.createContainerForBodyResolve
import org.jetbrains.kotlin.idea.caches.project.forcedModuleInfo
import org.jetbrains.kotlin.idea.caches.resolve.analyzeWithAllCompilerChecks
import org.jetbrains.kotlin.idea.core.util.toPsiFile
import org.jetbrains.kotlin.idea.project.IdeaModuleStructureOracle
import org.jetbrains.kotlin.idea.project.findAnalyzerServices
import org.jetbrains.kotlin.idea.project.languageVersionSettings
import org.jetbrains.kotlin.js.resolve.diagnostics.findPsi
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.platform.CommonPlatforms
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.BodyResolver
import org.jetbrains.kotlin.resolve.StatementFilter
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.resolve.descriptorUtil.module
import org.jetbrains.kotlin.resolve.lazy.ResolveSession
import org.jetbrains.kotlin.resolve.lazy.declarations.ClassMemberDeclarationProvider
import org.jetbrains.kotlin.resolve.lazy.declarations.PackageMemberDeclarationProvider
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.TypeUtils
import org.jetbrains.kotlin.utils.addToStdlib.safeAs
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.collections.set

private val subscribedToEditorHooks: AtomicBoolean = AtomicBoolean(false)

class MetaIdeAnalyzer : MetaAnalyzer {

  private val cache: ConcurrentHashMap<CacheId, SyntheticDescriptorCache> = ConcurrentHashMap()

  private val FILE_KEY = Key.create<VirtualFile>("FILE_KEY")

  private fun DeclarationDescriptor?.isGenerated(): Boolean =
    this?.findPsi()?.containingFile?.name?.startsWith("_meta_") == true

  val DeclarationDescriptor?.syntheticCache: SyntheticDescriptorCache?
    get() = this?.let {
      if (!it.isGenerated()) {
        val file: VirtualFile? = it.findPsi()?.containingFile?.virtualFile
        file?.let { cache[it.metaCacheId] }
      } else null
    }

  override fun metaPackageFragments(
    module: ModuleDescriptor,
    fqName: FqName
  ): List<PackageFragmentDescriptor> =
    cache.values.firstOrNull {
      it.module.name == module.name
    }?.module?.getPackage(fqName)?.fragments ?: emptyList()


  override fun metaSubPackagesOf(
    module: ModuleDescriptor,
    fqName: FqName,
    nameFilter: (Name) -> Boolean
  ): Collection<FqName> =
    cache.values.firstOrNull {
      it.module.name == module.name
    }?.module?.getSubPackagesOf(fqName, nameFilter) ?: emptyList()

  override fun metaSyntheticFunctionNames(thisDescriptor: ClassDescriptor): List<Name> =
    thisDescriptor.syntheticCache?.let {
      val compiledDescriptor = it.descriptorCache[thisDescriptor.fqNameSafe].safeAs<ClassDescriptor>()
      compiledDescriptor?.let { classDescriptor ->
        val originalNames = thisDescriptor.findPsi().safeAs<KtClassOrObject>()?.functionNames()?.toSet() ?: emptySet()
        val diff = classDescriptor.unsubstitutedMemberScope.getFunctionNames().toList() - originalNames
        diff - blackList
      }
    } ?: emptyList()

  override fun metaSyntheticNestedClassNames(thisDescriptor: ClassDescriptor): List<Name> =
    thisDescriptor.syntheticCache?.let {
      val compiledDescriptor = it.descriptorCache[thisDescriptor.fqNameSafe].safeAs<ClassDescriptor>()
      compiledDescriptor?.let { classDescriptor ->
        val originalNames = thisDescriptor.ktClassOrObject()?.nestedClassNames()?.toSet()?.map(Name::identifier) ?: emptyList()
        val compiledNames = classDescriptor.ktClassOrObject()?.nestedClassNames()?.toSet()?.map(Name::identifier) ?: emptyList()
        val diff = compiledNames - originalNames
        diff
      }
    } ?: emptyList()

  override fun metaSyntheticMethods(name: Name, thisDescriptor: ClassDescriptor): List<SimpleFunctionDescriptor> =
    thisDescriptor.syntheticCache?.let {
      val compiledDescriptor = it.descriptorCache[thisDescriptor.fqNameSafe].safeAs<ClassDescriptor>()
      compiledDescriptor?.let {
        val compiledFunctions = it.unsubstitutedMemberScope.getContributedDescriptors { true }.filterIsInstance<SimpleFunctionDescriptor>()
        val originalFunctions = thisDescriptor.unsubstitutedMemberScope.getFunctionNames()
        compiledFunctions.filter { cf ->
          cf.name == name && cf.name !in originalFunctions && cf.name !in blackList
        }.map { fn ->
          fn.copy(
            thisDescriptor,
            fn.modality,
            fn.visibility,
            CallableMemberDescriptor.Kind.SYNTHESIZED,
            true
          )
        }
      }
    } ?: emptyList()

  override fun metaSyntheticProperties(name: Name, thisDescriptor: ClassDescriptor): List<PropertyDescriptor> =
    thisDescriptor.syntheticCache?.let {
      val compiledDescriptor = it.descriptorCache[thisDescriptor.fqNameSafe].safeAs<ClassDescriptor>()
      compiledDescriptor?.let {
        val compiledProperties = it.unsubstitutedMemberScope.getContributedDescriptors { true }.filterIsInstance<PropertyDescriptor>()
        val originalProperties = thisDescriptor.unsubstitutedMemberScope.getVariableNames()
        compiledProperties.filter { cf ->
          cf.name == name && cf.name !in originalProperties && cf.name !in blackList
        }.mapNotNull { fn ->
          fn.copy(
            thisDescriptor,
            fn.modality,
            fn.visibility,
            CallableMemberDescriptor.Kind.SYNTHESIZED,
            true
          ).safeAs<PropertyDescriptor>()
        }
      }
    } ?: emptyList()

  override fun metaSyntheticSupertypes(classDescriptor: ClassDescriptor): List<KotlinType> =
    classDescriptor.syntheticCache?.let {
      it.descriptorCache[classDescriptor.fqNameSafe].safeAs<ClassDescriptor>()?.let { compiled ->
        val superTypes = TypeUtils.getAllSupertypes(compiled.defaultType)
          .filter { tpe -> tpe != classDescriptor.module.builtIns.anyType }
        superTypes
      }
    } ?: emptyList()

  override fun metaCompanionObjectNameIfNeeded(classDescriptor: ClassDescriptor): Name? =
    classDescriptor.syntheticCache?.let {
      val compiledDescriptor = it.descriptorCache[classDescriptor.fqNameSafe].safeAs<ClassDescriptor>()
      compiledDescriptor?.let { c ->
        c.ktClassOrObject()?.companionObjects?.firstOrNull()?.nameAsSafeName
      }
    }

  override fun metaSyntheticPackageClasses(name: Name, packageDescriptor: PackageFragmentDescriptor, declarationProvider: PackageMemberDeclarationProvider): List<ClassDescriptor> =
    packageDescriptor.syntheticCache?.let {
      val compiledDescriptor = it.descriptorCache[packageDescriptor.fqNameSafe].safeAs<PackageFragmentDescriptor>()
      compiledDescriptor?.let {
        val compiledClasses = it.getMemberScope().getContributedDescriptors { true }.filterIsInstance<ClassDescriptor>()
        val originalClasses = packageDescriptor.getMemberScope().getClassifierNames() ?: emptySet()
        compiledClasses.filter { cf ->
          cf.name == name && cf.name !in originalClasses
        }
      }
    } ?: emptyList()

  override fun metaSyntheticClasses(name: Name, classDescriptor: ClassDescriptor, declarationProvider: ClassMemberDeclarationProvider): List<ClassDescriptor> =
    classDescriptor.syntheticCache?.let {
      val compiledDescriptor = it.descriptorCache[classDescriptor.fqNameSafe].safeAs<ClassDescriptor>()
      compiledDescriptor?.let {
        val compiledClasses = it.unsubstitutedMemberScope.getContributedDescriptors { true }.filterIsInstance<ClassDescriptor>()
        val originalClasses = classDescriptor.unsubstitutedMemberScope.getClassifierNames() ?: emptySet()
        compiledClasses.filter { cf ->
          cf.name == name && cf.name !in originalClasses
        }
      }
    } ?: emptyList()

  private fun Document.getFile(): VirtualFile? {
    val userMapField =
      UserDataHolderBase::class.java.getDeclaredField("myUserMap")
        .also { it.isAccessible = true }
    val userData: KeyFMap = userMapField.get(this) as KeyFMap
    return userData.keys.find { it.toString() == FILE_KEY.toString() }?.let {
      userData[it] as VirtualFile?
    }
  }

  override fun KtFile.metaAnalysys(moduleInfo: ModuleInfo?): AnalysisResult {
    moduleInfo?.let { forcedModuleInfo = it }
    return analyzeWithAllCompilerChecks()
  }

  override fun <P : KtElement, K : KtElement, S> CompilerContext.subscribeToEditorHooks(
    project: Project,
    quoteFactory: Quote.Factory<P, K, S>,
    match: K.() -> Boolean,
    map: S.(K) -> List<String>,
    transformation: (VirtualFile, Document) -> Pair<KtFile, AnalysisResult>?): Unit {
    if (!subscribedToEditorHooks.get()) {
      val application = ApplicationManager.getApplication()
      val projectBus = currentProject()?.messageBus?.connect()
      val connection = application.messageBus.connect()
      projectBus?.subscribe<FileEditorManagerListener>(
        FileEditorManagerListener.FILE_EDITOR_MANAGER,
        object : FileEditorManagerListener {
          override fun selectionChanged(event: FileEditorManagerEvent) {
            println("FileEditorManagerListener.selectionChanged: ${this@MetaIdeAnalyzer} $event")
            super.selectionChanged(event)
          }

          override fun fileOpened(source: FileEditorManager, file: VirtualFile) {
            println("FileEditorManagerListener.fileOpened: ${this@MetaIdeAnalyzer} $file")
            file.document(project)?.let {
              println("FileEditorManagerListener.fileOpened: populateSyntheticCache $it")
              populateSyntheticCache(it, transformation)
            }
            super.fileOpened(source, file)
          }

          override fun fileOpenedSync(source: FileEditorManager, file: VirtualFile, editors: com.intellij.openapi.util.Pair<Array<FileEditor>, Array<FileEditorProvider>>) {
            println("FileEditorManagerListener.fileOpenedSync: ${this@MetaIdeAnalyzer} $file")
            super.fileOpenedSync(source, file, editors)
          }

          override fun fileClosed(source: FileEditorManager, file: VirtualFile) {
            println("FileEditorManagerListener.fileClosed: ${this@MetaIdeAnalyzer}, removing cache for $file")
            cache.remove(file.metaCacheId)
            super.fileClosed(source, file)
          }
        }
      )
      projectBus?.subscribe<CompilationStatusListener>(
        CompilerTopics.COMPILATION_STATUS,
        object : CompilationStatusListener {
          override fun compilationFinished(aborted: Boolean, errors: Int, warnings: Int, compileContext: CompileContext) {
            println("CompilationStatusListener.compilationFinished: ${this@MetaIdeAnalyzer} errors: $errors, context: $compileContext")
            super.compilationFinished(aborted, errors, warnings, compileContext)
          }

          override fun automakeCompilationFinished(errors: Int, warnings: Int, compileContext: CompileContext) {
            println("CompilationStatusListener.automakeCompilationFinished: ${this@MetaIdeAnalyzer} errors: $errors, context: $compileContext")
            super.automakeCompilationFinished(errors, warnings, compileContext)
          }

          override fun fileGenerated(outputRoot: String, relativePath: String) {
            println("CompilationStatusListener.fileGenerated: ${this@MetaIdeAnalyzer} $outputRoot $relativePath")
            super.fileGenerated(outputRoot, relativePath)
          }
        }
      )
      connection.subscribe<FileDocumentManagerListener>(
        AppTopics.FILE_DOCUMENT_SYNC,
        object : FileDocumentManagerListener {
          override fun fileContentReloaded(file: VirtualFile, document: Document) {
            println("MetaOnFileSaveComponent.fileContentReloaded: ${this@MetaIdeAnalyzer} $file")
            super.fileContentReloaded(file, document)
          }

          override fun fileContentLoaded(file: VirtualFile, document: Document) {
            println("MetaOnFileSaveComponent.fileContentLoaded: ${this@MetaIdeAnalyzer} $file")
            super.fileContentLoaded(file, document)
          }

          override fun beforeDocumentSaving(document: Document) {
            populateSyntheticCache(document, transformation)
            println("MetaOnFileSaveComponent.beforeDocumentSaving: ${this@MetaIdeAnalyzer} $document")
          }
        })
      subscribedToEditorHooks.set(true)
    }
  }

  override fun populateSyntheticCache(document: Document, transformation: (VirtualFile, Document) -> Pair<KtFile, AnalysisResult>?) {
    document.getFile()?.let { file ->
      transformation(file, document)?.let { (ktFile, result) ->
        cache[file.metaCacheId] = SyntheticDescriptorCache.fromAnalysis(ktFile, result)
        println("Added cache transformation: cache[${file.name}] $result")
      }
    }
  }

  override fun createBodyResolver(
    resolveSession: ResolveSession,
    trace: BindingTrace,
    file: KtFile,
    statementFilter: StatementFilter
  ): BodyResolver {
    val globalContext = SimpleGlobalContext(resolveSession.storageManager, resolveSession.exceptionTracker)
    val module = resolveSession.moduleDescriptor
    return createContainerForBodyResolve(
      globalContext.withProject(file.project).withModule(module),
      trace,
      CommonPlatforms.defaultCommonPlatform,
      statementFilter,
      CommonPlatforms.defaultCommonPlatform.findAnalyzerServices,
      file.languageVersionSettings,
      IdeaModuleStructureOracle()
    ).get()
  }
}

fun VirtualFile.document(project: Project): Document? =
  toPsiFile(project)?.viewProvider?.document


