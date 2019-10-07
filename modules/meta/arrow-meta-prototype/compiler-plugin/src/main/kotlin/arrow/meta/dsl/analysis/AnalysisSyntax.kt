package arrow.meta.dsl.analysis

import arrow.meta.dsl.platform.cli
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.analysis.AnalysisHandler
import arrow.meta.phases.analysis.CollectAdditionalSources
import arrow.meta.phases.analysis.ExtraImports
import arrow.meta.phases.analysis.PreprocessedVirtualFileFactory
import arrow.meta.internal.Noop
import arrow.meta.phases.ExtensionPhase
import org.jetbrains.kotlin.analyzer.AnalysisResult
import org.jetbrains.kotlin.com.intellij.openapi.project.Project
import org.jetbrains.kotlin.com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.kotlin.com.intellij.testFramework.LightVirtualFile
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.container.ComponentProvider
import org.jetbrains.kotlin.context.ProjectContext
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.diagnostics.Diagnostic
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtImportInfo
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.BindingTraceContext
import org.jetbrains.kotlin.resolve.diagnostics.MutableDiagnosticsWithSuppression
import java.util.ArrayList

interface AnalysisSyntax {
  fun additionalSources(
    collectAdditionalSourcesAndUpdateConfiguration: CompilerContext.(knownSources: Collection<KtFile>, configuration: CompilerConfiguration, project: Project) -> Collection<KtFile>
  ): CollectAdditionalSources =
    object : CollectAdditionalSources {
      override fun CompilerContext.collectAdditionalSourcesAndUpdateConfiguration(knownSources: Collection<KtFile>, configuration: CompilerConfiguration, project: Project): Collection<KtFile> =
        collectAdditionalSourcesAndUpdateConfiguration(knownSources, configuration, project)
    }

  fun analysys(
    doAnalysis: CompilerContext.(project: Project, module: ModuleDescriptor, projectContext: ProjectContext, files: Collection<KtFile>, bindingTrace: BindingTrace, componentProvider: ComponentProvider) -> AnalysisResult?,
    analysisCompleted: CompilerContext.(project: Project, module: ModuleDescriptor, bindingTrace: BindingTrace, files: Collection<KtFile>) -> AnalysisResult?
  ): AnalysisHandler =
    object : AnalysisHandler {
      override fun CompilerContext.doAnalysis(
        project: Project,
        module: ModuleDescriptor,
        projectContext: ProjectContext,
        files: Collection<KtFile>,
        bindingTrace: BindingTrace,
        componentProvider: ComponentProvider
      ): AnalysisResult? {
        ctx.module = module
        ctx.files = files
        ctx.componentProvider = componentProvider
        return doAnalysis(project, module, projectContext, files, bindingTrace, componentProvider)
      }

      override fun CompilerContext.analysisCompleted(
        project: Project,
        module: ModuleDescriptor,
        bindingTrace: BindingTrace,
        files: Collection<KtFile>
      ): AnalysisResult? =
        analysisCompleted(project, module, bindingTrace, files)
    }

  fun preprocessedVirtualFileFactory(
    createPreprocessedFile: CompilerContext.(file: VirtualFile?) -> VirtualFile?,
    createPreprocessedLightFile: CompilerContext.(file: LightVirtualFile?) -> LightVirtualFile? = Noop.nullable2()
  ): PreprocessedVirtualFileFactory =
    object : PreprocessedVirtualFileFactory {
      override fun CompilerContext.isPassThrough(): Boolean = false

      override fun CompilerContext.createPreprocessedFile(file: VirtualFile?): VirtualFile? =
        createPreprocessedFile(file)

      override fun CompilerContext.createPreprocessedLightFile(file: LightVirtualFile?): LightVirtualFile? =
        createPreprocessedLightFile(file)
    }

  fun extraImports(extraImports: CompilerContext.(ktFile: KtFile) -> Collection<KtImportInfo>): ExtraImports =
    object : ExtraImports {
      override fun CompilerContext.extraImports(ktFile: KtFile): Collection<KtImportInfo> =
        extraImports(ktFile)
    }

  @Suppress("UNCHECKED_CAST")
  fun suppressDiagnostic(f: (Diagnostic) -> Boolean): ExtensionPhase =
    cli {
      analysys(
        doAnalysis = { project, module, projectContext, files, bindingTrace, componentProvider ->
          null
        },
        analysisCompleted = { project, module, bindingTrace, files ->
          val diagnostics: MutableDiagnosticsWithSuppression =
          BindingTraceContext::class.java.getDeclaredField("mutableDiagnostics").also { it.isAccessible = true }.get(bindingTrace) as MutableDiagnosticsWithSuppression
          val mutableDiagnostics = diagnostics.getOwnDiagnostics() as ArrayList<Diagnostic>
          mutableDiagnostics.removeIf(f)
          null
        }
      )
    } ?: ExtensionPhase.Empty
}
