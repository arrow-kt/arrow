package arrow.meta.dsl.ide.editor.liveTemplate

import arrow.meta.phases.ExtensionPhase
import arrow.meta.plugin.idea.IdeMetaPlugin
import com.intellij.codeInsight.template.TemplateContextType
import com.intellij.codeInsight.template.impl.DefaultLiveTemplatesProvider
import com.intellij.psi.PsiFile

interface LiveTemplateSyntax {
  fun IdeMetaPlugin.addLiveTemplateContext(
    id: String,
    presentableName: String,
    isInContext: (file: PsiFile, offset: Int) -> Boolean
  ): ExtensionPhase =
    extensionProvider(
      TemplateContextType.EP_NAME,
      object : TemplateContextType(id, presentableName) {
        override fun isInContext(file: PsiFile, offset: Int): Boolean =
          isInContext(file, offset)
      }
    )

  fun IdeMetaPlugin.addLiveTemplateProvider(
    defaultLiveTemplateFiles: Array<String>,
    hiddenLiveTemplateFiles: Array<String>?
  ): ExtensionPhase =
    extensionProvider(
      DefaultLiveTemplatesProvider.EP_NAME,
      object : DefaultLiveTemplatesProvider {
        override fun getDefaultLiveTemplateFiles(): Array<String> =
          defaultLiveTemplateFiles

        override fun getHiddenLiveTemplateFiles(): Array<String>? =
          hiddenLiveTemplateFiles
      }
    )
}
