package arrow.meta.qq

import arrow.meta.extensions.MetaComponentRegistrar
import org.jetbrains.kotlin.analyzer.AnalysisResult
import org.jetbrains.kotlin.analyzer.ModuleInfo
import org.jetbrains.kotlin.com.intellij.openapi.editor.Document
import org.jetbrains.kotlin.com.intellij.openapi.project.Project
import org.jetbrains.kotlin.com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.SimpleFunctionDescriptor
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.platform.TargetPlatform
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtFile

interface MetaAnalyzer {

  fun <P : KtElement, K : KtElement, S> MetaComponentRegistrar.subscribeToOnFileSave(
    quoteFactory: Quote.Factory<P, K, S>,
    match: K.() -> Boolean,
    map: S.(K) -> List<String>,
    transformation: (VirtualFile, Document) -> Pair<KtFile, AnalysisResult>
  )

  fun KtFile.metaAnalysys(moduleInfo: ModuleInfo? = null): AnalysisResult
  fun metaSyntheticFunctionNames(thisDescriptor: ClassDescriptor): List<Name>
  fun metaSyntheticMethods(name: Name, thisDescriptor: ClassDescriptor): List<SimpleFunctionDescriptor>
}
