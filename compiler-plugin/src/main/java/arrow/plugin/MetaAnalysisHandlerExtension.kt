package arrow.plugin

import org.jetbrains.kotlin.analyzer.AnalysisResult
import org.jetbrains.kotlin.com.intellij.openapi.project.Project
import org.jetbrains.kotlin.container.ComponentProvider
import org.jetbrains.kotlin.container.get
import org.jetbrains.kotlin.context.ProjectContext
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.diagnostics.reportFromPlugin
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.jvm.extensions.AnalysisHandlerExtension
import org.jetbrains.kotlin.resolve.lazy.ResolveSession

class MetaAnalysisHandlerExtension : AnalysisHandlerExtension {

  override fun doAnalysis(
    project: Project,
    module: ModuleDescriptor,
    projectContext: ProjectContext,
    files: Collection<KtFile>,
    bindingTrace: BindingTrace,
    componentProvider: ComponentProvider
  ): AnalysisResult? {
    println("AnalysisHandlerExtension.doAnalysis")
//    val resolveSession: ResolveSession = componentProvider.get()
//    for (file in files) {
//      for (declaration in file.declarations) {
//        println("Analyzing declaration: $declaration")
//        val classDescriptor: ClassDescriptor = resolveSession.resolveToDescriptor(declaration) as? ClassDescriptor ?: continue
//        classDescriptor.
//        bindingTrace.reportFromPlugin(
//          ARROW_ANALISYS_DEBUG.on(declaration),
//          ArrowDefaultErrorMessages
//        )
//      }
//    }
    return null
  }
}
