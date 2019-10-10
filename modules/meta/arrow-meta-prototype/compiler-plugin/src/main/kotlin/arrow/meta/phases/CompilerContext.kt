package arrow.meta.phases

import arrow.meta.phases.analysis.MetaAnalyzer
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.com.intellij.openapi.project.Project
import org.jetbrains.kotlin.container.ComponentProvider
import org.jetbrains.kotlin.container.get
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtPsiFactory

class CompilerContext(
  val project: Project,
  val messageCollector: MessageCollector?
) {

  val ktPsiElementFactory: KtPsiFactory = KtPsiFactory(project, false)
  val ctx: CompilerContext = this
  lateinit var module: ModuleDescriptor
  lateinit var files: Collection<KtFile>
  lateinit var componentProvider: ComponentProvider
  private lateinit var metaAnalyzerField: MetaAnalyzer

  val analyzer: MetaAnalyzer?
    get() = when {
      ::metaAnalyzerField.isInitialized -> metaAnalyzerField
      ::componentProvider.isInitialized -> {
        //TODO sometimes we get in here before the DI container has finished composing and it blows up
        metaAnalyzerField = componentProvider.get()
        metaAnalyzerField
      }
      else -> null
    }
}