package arrow.meta.plugin.idea

import arrow.meta.extensions.CompilerContext
import arrow.meta.extensions.MetaComponentRegistrar
import arrow.meta.qq.MetaAnalyzer
import arrow.meta.qq.Quote
import arrow.meta.qq.functionNames
import arrow.meta.qq.isMetaFile
import arrow.meta.qq.ktFile
import com.intellij.AppTopics
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.Document
import com.intellij.openapi.fileEditor.FileDocumentManagerListener
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.UserDataHolderBase
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.keyFMap.KeyFMap
import org.jetbrains.kotlin.analyzer.AnalysisResult
import org.jetbrains.kotlin.analyzer.ModuleInfo
import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.SimpleFunctionDescriptor
import org.jetbrains.kotlin.descriptors.impl.DeclarationDescriptorVisitorEmptyBodies
import org.jetbrains.kotlin.idea.caches.project.forcedModuleInfo
import org.jetbrains.kotlin.idea.caches.resolve.analyzeWithAllCompilerChecks
import org.jetbrains.kotlin.js.resolve.diagnostics.findPsi
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.resolve.lazy.LazyEntity
import org.jetbrains.kotlin.utils.addToStdlib.safeAs
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.collections.set

val subscribedToOnFileSave: AtomicBoolean = AtomicBoolean(false)

class SyntheticDescriptorCache(
  val descriptorCache: ConcurrentHashMap<FqName, DeclarationDescriptor> = ConcurrentHashMap()
) {
  companion object {
    fun fromAnalysis(file: KtFile, analysis: AnalysisResult): SyntheticDescriptorCache {
      val moduleDescriptor = analysis.moduleDescriptor
      val packageFragmentProvider = moduleDescriptor.getPackage(file.packageFqName)
      val cache = SyntheticDescriptorCache()
      packageFragmentProvider.accept(
        MetaRecursiveVisitor(object : DeclarationDescriptorVisitorEmptyBodies<Unit, Unit>() {
          override fun visitDeclarationDescriptor(descriptor: DeclarationDescriptor?, data: Unit?) {
            descriptor?.let {
              if (descriptor is CallableMemberDescriptor) {
                if (descriptor.ktFile()?.isMetaFile() == true && descriptor.kind != CallableMemberDescriptor.Kind.FAKE_OVERRIDE) {
                  println("Added to cache: ${descriptor.fqNameSafe}")
                  cache.descriptorCache[it.fqNameSafe] = it
                }
              }

            }
          }
        }), Unit)
      return cache
    }

  }
}


class MetaIdeAnalyzer : MetaAnalyzer {

  private val cache: ConcurrentHashMap<String, SyntheticDescriptorCache> = ConcurrentHashMap()

  private val FILE_KEY = Key.create<VirtualFile>("FILE_KEY")

  fun DeclarationDescriptor?.isGenerated(): Boolean =
    this?.findPsi()?.ktFile()?.name?.startsWith("_meta_") == true

  val DeclarationDescriptor?.syntheticCache: SyntheticDescriptorCache?
    get() = this?.let {
      if (!it.isGenerated()) {
        val fileName = it.ktFile()?.name
        fileName.let { name -> cache[name] }
      } else null
    }

  override fun metaSyntheticFunctionNames(thisDescriptor: ClassDescriptor): List<Name> =
    thisDescriptor.syntheticCache?.let {
      val compiledDescriptor = it.descriptorCache[thisDescriptor.fqNameSafe].safeAs<ClassDescriptor>()
      compiledDescriptor?.let {
        val originalNames = thisDescriptor.findPsi().safeAs<KtClassOrObject>()?.functionNames()?.toSet() ?: emptySet()
        val diff = it.unsubstitutedMemberScope.getFunctionNames().toList() - originalNames
        diff.toList()
      }
    } ?: emptyList()

  override fun metaSyntheticMethods(thisDescriptor: ClassDescriptor): List<SimpleFunctionDescriptor> =
    thisDescriptor.syntheticCache?.let {
      val compiledDescriptor = it.descriptorCache[thisDescriptor.fqNameSafe].safeAs<ClassDescriptor>()
      compiledDescriptor?.let {
        val compiledFunctions = it.unsubstitutedMemberScope.getContributedDescriptors { true }.filterIsInstance<SimpleFunctionDescriptor>()
        val originalFunctions = thisDescriptor.findPsi().safeAs<KtClassOrObject>()?.functionNames() ?: emptyList()
        compiledFunctions.filterNot { it.name in originalFunctions }
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

  override fun <P : KtElement, K : KtElement, S> MetaComponentRegistrar.subscribeToOnFileSave(
    quoteFactory: Quote.Factory<P, K, S>,
    match: K.() -> Boolean,
    map: S.(K) -> List<String>,
    transformation: (VirtualFile, Document) -> Pair<KtFile, AnalysisResult>): Unit {
    if (!subscribedToOnFileSave.get()) {
      val application = ApplicationManager.getApplication()
      application.messageBus.connect().subscribe<FileDocumentManagerListener>(
        AppTopics.FILE_DOCUMENT_SYNC,
        object : FileDocumentManagerListener {
          override fun beforeDocumentSaving(document: Document) {
            document.getFile()?.let { file ->
              val (ktFile, result) = transformation(file, document)
              println("Added cache transformation: $result")
              cache[file.name] = SyntheticDescriptorCache.fromAnalysis(ktFile, result)
            }
            println("MetaOnFileSaveComponent.beforeDocumentSaving: ${this@MetaIdeAnalyzer} $document")
          }
        })
      subscribedToOnFileSave.set(true)
    }
  }

}




