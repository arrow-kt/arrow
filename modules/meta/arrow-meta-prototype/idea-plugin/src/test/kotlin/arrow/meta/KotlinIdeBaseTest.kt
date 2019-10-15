package arrow.meta

import com.intellij.psi.PsiElement
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixture4TestCase
import org.jetbrains.kotlin.idea.KotlinFileType
import org.junit.Assert

open class KotlinIdeBaseTest : LightPlatformCodeInsightFixture4TestCase() {
  /**
   * Parses the code defined by parameter 'code' and calls 'block' for each
   * PsiElement marked by 'caretMarker`.
   */
  internal fun withEachCaret(code: String, caretMarker: String = "<CARET>", block: (PsiElement) -> Unit) {
    val offsets = mutableListOf<Int>()

    val codeBuilder = StringBuilder(code)
    while (codeBuilder.contains(caretMarker)) {
      val offset = codeBuilder.indexOf(caretMarker)
      offsets += offset
      codeBuilder.delete(offset, offset + caretMarker.length)
    }

    offsets.forEach { offset ->
      // reparse file for each offset to allow side-effects in tests
      // and to keep myFixture up-to-date
      val psiFile = myFixture.configureByText(KotlinFileType.INSTANCE, codeBuilder.toString())
      Assert.assertNotNull(psiFile)

      val element = psiFile.findElementAt(offset)
      Assert.assertNotNull(element)

      block(element!!)
    }
  }
}
