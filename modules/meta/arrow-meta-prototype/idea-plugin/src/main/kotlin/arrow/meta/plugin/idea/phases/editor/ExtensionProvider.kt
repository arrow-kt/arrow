package arrow.meta.plugin.idea.phases.editor

import com.intellij.lang.LanguageExtension
import com.intellij.openapi.Disposable
import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.openapi.extensions.Extensions
import com.intellij.openapi.util.Disposer
import org.jetbrains.kotlin.idea.KotlinLanguage

interface ExtensionProvider {
  val dispose: Disposable
  // Todo: check LoadingOrder
  fun <E> addExtension(EP_NAME: ExtensionPointName<E>, impl: E): Unit? =
    Extensions.getRootArea().run {
      getExtensionPoint(EP_NAME).registerExtension(impl, dispose)
    }

  fun <E> addLanguageExtension(LE: LanguageExtension<E>, impl: E): Unit =
    LE.addExplicitExtension(KotlinLanguage.INSTANCE, impl)

  companion object : ExtensionProvider {
    override val dispose: Disposable
      get() = Disposer.newDisposable()
  }
}
