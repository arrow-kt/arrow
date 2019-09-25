package arrow.meta.dsl.ide.extensions

import arrow.meta.dsl.platform.ide
import arrow.meta.phases.ExtensionPhase
import arrow.meta.plugin.idea.IdeMetaPlugin
import arrow.meta.plugin.idea.phases.editor.ExtensionProvider
import com.intellij.codeInsight.ContainerProvider
import com.intellij.lang.LanguageExtension
import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.openapi.extensions.LoadingOrder
import com.intellij.psi.PsiElement

interface ExtensionProviderSyntax : ExtensionProvider {
  fun <E> IdeMetaPlugin.extensionProvider(
    EP_NAME: ExtensionPointName<E>,
    impl: E,
    loadingOrder: LoadingOrder = LoadingOrder.ANY
  ): ExtensionPhase =
    ide {
      addExtension(EP_NAME, impl, loadingOrder)?.run {
        // println("ADDED another Extension: FOR ${EP_NAME.name}")
        ExtensionPhase.Empty
      }
    } ?: ExtensionPhase.Empty

  fun <E> IdeMetaPlugin.extensionProvider(
    LE: LanguageExtension<E>,
    impl: E
  ): ExtensionPhase =
    ide {
      addLanguageExtension(LE, impl)
      // println("Adds LanguageExtension for ${LE.name}")
      ExtensionPhase.Empty
    } ?: ExtensionPhase.Empty

  fun IdeMetaPlugin.addContainerProvider(f: (PsiElement) -> PsiElement?): ExtensionPhase =
    extensionProvider(
      ContainerProvider.EP_NAME,
      ContainerProvider { f(it) }
    )
}
