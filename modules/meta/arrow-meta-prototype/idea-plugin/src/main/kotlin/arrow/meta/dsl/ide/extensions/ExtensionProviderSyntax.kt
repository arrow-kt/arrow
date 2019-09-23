package arrow.meta.dsl.ide.extensions

import arrow.meta.dsl.platform.ide
import arrow.meta.phases.ExtensionPhase
import arrow.meta.plugin.idea.IdeMetaPlugin
import arrow.meta.plugin.idea.phases.editor.ExtensionProvider
import com.intellij.codeInsight.ContainerProvider
import com.intellij.lang.LanguageExtension
import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.psi.PsiElement

interface ExtensionProviderSyntax {
  // TODO: Test impl
  fun <E> IdeMetaPlugin.extensionProvider(
    EP_NAME: ExtensionPointName<E>,
    impl: E
  ): ExtensionPhase =
    // StorageComponent: Incosistent Container exception
    ide {
      packageFragmentProvider { project, module, storageManager, trace, moduleInfo, lookupTracker ->
        //analyzer?.run {
        ExtensionProvider.addExtension(EP_NAME, impl)
        println("ADDED another Extension: FOR ${EP_NAME.name}")
        //}
        null
      }
    } ?: ExtensionPhase.Empty

  // TODO: Test impl
  fun <E> IdeMetaPlugin.extensionProvider(
    LE: LanguageExtension<E>,
    impl: E
  ): ExtensionPhase =
    ide {
      storageComponent(
        registerModuleComponents = { container, moduleDescriptor ->
          analyzer?.run {
            ExtensionProvider.addLanguageExtension(LE, impl)
          }
        },
        check = { _, _, _ ->
        }
      )
    } ?: ExtensionPhase.Empty

  fun IdeMetaPlugin.addContainerProvider(f: (PsiElement) -> PsiElement?): ExtensionPhase =
    extensionProvider(
      ContainerProvider.EP_NAME,
      ContainerProvider { f(it) }
    )
}