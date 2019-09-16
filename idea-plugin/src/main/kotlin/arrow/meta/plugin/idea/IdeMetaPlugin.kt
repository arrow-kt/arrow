package arrow.meta.plugin.idea

import arrow.meta.MetaPlugin
import arrow.meta.extensions.CompilerContext
import arrow.meta.extensions.ExtensionPhase
import arrow.meta.plugin.idea.typeclasses.typeClassesIde
import arrow.meta.utils.ide
import com.intellij.codeInsight.highlighting.HighlightErrorFilter
import com.intellij.openapi.extensions.Extensions
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiErrorElement
import org.jetbrains.kotlin.container.useImpl
import org.jetbrains.kotlin.name.Name

open class IdeMetaPlugin : MetaPlugin() {
  override fun intercept(): List<Pair<Name, List<ExtensionPhase>>> {
    return super.intercept() + listOf(typeClassesIde)
  }

  override fun CompilerContext.registerIdeExclusivePhase(currentPhase: ExtensionPhase): Unit {
    currentProject()?.let { project ->
      if (currentPhase is IdeExtensionPhase.EditorHighlightErrorFilter)
        registerHighlighter(project, currentPhase)
    }
  }

  override fun registerMetaAnalyzer(): ExtensionPhase =
    ide {
      storageComponent(
        registerModuleComponents = { container, moduleDescriptor ->
          println("Registering meta analyzer")
          container.useImpl<MetaIdeAnalyzer>()
          //
        },
        check = { declaration, descriptor, context ->
        }
      )
    } ?: ExtensionPhase.Empty


  private fun CompilerContext.registerHighlighter(
    project: Project,
    phase: IdeExtensionPhase.EditorHighlightErrorFilter
  ) {
    Extensions.getArea(project).getExtensionPoint(HighlightErrorFilter.EP_NAME).registerExtension(
      object : HighlightErrorFilter() {
        override fun shouldHighlightErrorElement(element: PsiErrorElement): Boolean =
          phase.run { this@registerHighlighter.shouldHighlightErrorElement(element) }
      })
  }
}