package arrow.meta.phases.analysis

import arrow.meta.phases.CompilerContext
import arrow.meta.phases.ExtensionPhase
import org.jetbrains.kotlin.com.intellij.openapi.project.Project
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.psi.KtFile

interface CollectAdditionalSources : ExtensionPhase {
  fun CompilerContext.collectAdditionalSourcesAndUpdateConfiguration(
    knownSources: Collection<KtFile>,
    configuration: CompilerConfiguration,
    project: Project
  ): Collection<KtFile>
}