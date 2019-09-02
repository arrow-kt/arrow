package arrow.meta.qq

import org.jetbrains.kotlin.psi.KtFile

interface KtFileInterceptor {
  fun initialized() : Boolean
  fun accept(f: (KtFile) -> KtFile): Unit
}