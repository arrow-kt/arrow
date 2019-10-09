package arrow.meta.plugin.idea.phases.editor

import com.intellij.core.CoreApplicationEnvironment
import com.intellij.core.JavaCoreApplicationEnvironment
import com.intellij.lang.LanguageExtension
import com.intellij.openapi.Disposable
import com.intellij.openapi.extensions.BaseExtensionPointName
import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.openapi.extensions.Extensions
import com.intellij.openapi.extensions.LoadingOrder
import com.intellij.openapi.fileTypes.FileTypeExtension
import com.intellij.openapi.util.ClassExtension
import com.intellij.openapi.util.Disposer
import org.jetbrains.kotlin.idea.KotlinFileType
import org.jetbrains.kotlin.idea.KotlinLanguage

interface ExtensionProvider {
  val dispose: Disposable
    get() = Disposer.newDisposable()

  fun <E> addExtension(EP_NAME: ExtensionPointName<E>, impl: E, loadingOrder: LoadingOrder): Unit =
    Extensions.getRootArea().getExtensionPoint(EP_NAME).registerExtension(impl, loadingOrder, dispose)

  fun <E> addLanguageExtension(LE: LanguageExtension<E>, impl: E): Unit =
    LE.addExplicitExtension(KotlinLanguage.INSTANCE, impl)

  /**
   * Examples are here: [JavaCoreApplicationEnvironment] line 57, 58
   */
  fun <E> addFileTypeExtension(FE: FileTypeExtension<E>, impl: E): Unit =
    FE.addExplicitExtension(KotlinFileType.INSTANCE, impl)

  /**
   * Examples are here: [JavaCoreApplicationEnvironment] line 72 - 77
   * kotlinx.metadata.jvm.impl.JvmClassExtension is internal
   */
  fun <E> addClassExtension(CE: ClassExtension<E>, forClass: Class<*>, impl: E): Unit =
    CE.addExplicitExtension(forClass, impl)

  fun <E> registerExtension(EP_NAME: BaseExtensionPointName, aClass: Class<E>): Unit =
    CoreApplicationEnvironment.registerExtensionPoint(Extensions.getRootArea(), EP_NAME, aClass)

  fun <E> registerExtension(EP_NAME: ExtensionPointName<E>, aClass: Class<E>): Unit =
    CoreApplicationEnvironment.registerExtensionPoint(Extensions.getRootArea(), EP_NAME, aClass)
}
