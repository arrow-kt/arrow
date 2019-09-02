package arrow.meta.plugin.idea

import arrow.meta.qq.KtFileInterceptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiFile
import com.intellij.psi.impl.PsiManagerImpl
import com.intellij.psi.impl.file.impl.FileManagerImpl
import com.jetbrains.rd.util.AtomicReference
import com.jetbrains.rd.util.putUnique
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.utils.addToStdlib.cast
import java.util.concurrent.ConcurrentHashMap

private var transformation : AtomicReference<((KtFile) -> KtFile)?> = AtomicReference(null)

class MetaPsiManager(
  delegate: PsiManagerImpl
) : PsiManagerImpl(
  delegate.project,
  delegate.fileManager.cast<FileManagerImpl>()["myFileDocumentManager"],
  null,
  delegate.fileManager.cast<FileManagerImpl>()["myFileIndex"],
  delegate["myMessageBus"],
  delegate.modificationTracker
), KtFileInterceptor {

  override fun initialized(): Boolean =
    transformation.get() != null

  override fun accept(f: (KtFile) -> KtFile) {
    transformation.getAndSet(f)
  }

  override fun findViewProvider(file: VirtualFile): FileViewProvider? {
    println("MetaPsiManager.findViewProvider: $file")
    return super.findViewProvider(file)
  }

  override fun findFile(file: VirtualFile): PsiFile? {
    val result = super.findFile(file)
    if (result != null && result is KtFile && !syntheticKtFiles.containsKey(file)) {
      val transformedFile = transformation.get()?.invoke(result)
      if (transformedFile != null) {
        println("MetaPsiManager.findFile added synthetic file: $transformedFile")
        syntheticKtFiles[file] = result to transformedFile
      }
    }
    return result
  }

  companion object {
    private val syntheticKtFiles: ConcurrentHashMap<VirtualFile, Pair<KtFile, KtFile>> =
      ConcurrentHashMap()
  }

}

inline fun <reified A> Project.replaceComponent(f: (A) -> A): Unit {
  val componentAdapter =
    picoContainer.getComponentAdapterOfType(A::class.java)
      ?: picoContainer.getComponentAdapter(A::class.java)
  val facade = componentAdapter.getComponentInstance(picoContainer) as A
  val newInstance: A = f(facade)
  val field = componentAdapter.javaClass.getDeclaredField("myInitializedComponentInstance")
  field.isAccessible = true
  field.set(componentAdapter, newInstance)
}

@Suppress("UNCHECKED_CAST")
inline operator fun <reified A, B> A.get(field: String): B =
  A::class.java.getDeclaredField(field).also { it.isAccessible = true }.get(this) as B