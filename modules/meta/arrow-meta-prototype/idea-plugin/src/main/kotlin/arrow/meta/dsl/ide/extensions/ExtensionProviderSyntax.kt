package arrow.meta.dsl.ide.extensions

import arrow.meta.dsl.platform.ideRegistry
import arrow.meta.phases.ExtensionPhase
import arrow.meta.plugin.idea.IdeMetaPlugin
import arrow.meta.plugin.idea.phases.editor.ExtensionProvider
import com.intellij.codeInsight.ContainerProvider
import com.intellij.lang.LanguageExtension
import com.intellij.openapi.extensions.BaseExtensionPointName
import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.openapi.extensions.LoadingOrder
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.diagnostics.Diagnostic
import org.jetbrains.kotlin.resolve.diagnostics.DiagnosticSuppressor

interface ExtensionProviderSyntax : ExtensionProvider {
  // Todo: Check LoadingOrder
  fun <E> IdeMetaPlugin.extensionProvider(
    EP_NAME: ExtensionPointName<E>,
    impl: E,
    loadingOrder: LoadingOrder = LoadingOrder.ANY
  ): ExtensionPhase =
    ideRegistry {
      addExtension(EP_NAME, impl, loadingOrder)
      println("ADDED another Extension: FOR ${EP_NAME.name}")
    }

  fun <E> IdeMetaPlugin.extensionProvider(
    LE: LanguageExtension<E>,
    impl: E
  ): ExtensionPhase =
    ideRegistry {
      addLanguageExtension(LE, impl)
      println("Adds LanguageExtension for ${LE.name}")
    }

  fun <E> IdeMetaPlugin.registerExtensionPoint(
    EP_NAME: BaseExtensionPointName,
    aClass: Class<E>
  ): ExtensionPhase =
    ideRegistry {
      registerExtension(EP_NAME, aClass)
      println("registered: ${EP_NAME.name}")
    }

  fun <E> IdeMetaPlugin.registerExtensionPoint(
    EP_NAME: ExtensionPointName<E>,
    aClass: Class<E>
  ): ExtensionPhase =
    ideRegistry {
      registerExtension(EP_NAME, aClass)
      println("registered: ${EP_NAME.name}")
    }

  fun IdeMetaPlugin.addContainerProvider(f: (PsiElement) -> PsiElement?): ExtensionPhase =
    extensionProvider(
      ContainerProvider.EP_NAME,
      ContainerProvider { f(it) }
    )

  /**
   * Check out [org.jetbrains.kotlin.resolve.checkers.PlatformDiagnosticSuppressor] for further improvements
   */
  fun IdeMetaPlugin.addDiagnosticSuppressor(
    isSuppressed: (diagnostic: Diagnostic) -> Boolean
  ): ExtensionPhase =
    extensionProvider(
      DiagnosticSuppressor.EP_NAME,
      object : DiagnosticSuppressor {
        override fun isSuppressed(diagnostic: Diagnostic): Boolean =
          isSuppressed(diagnostic)
      }
    )
}
