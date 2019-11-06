package arrow.meta.plugin.idea.test

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixture4TestCase
import org.jetbrains.kotlin.idea.KotlinFileType

abstract class IdeBaseTestSyntax : LightPlatformCodeInsightFixture4TestCase() {
  /**
   * Parses the code defined by parameter 'code' and calls 'block' for each
   * PsiElement marked by 'caretMarker`.
   */
  fun <R> String.withEachCaret(matchOn: String = "<caret>", f: (PsiElement) -> R): Unit =
    StringBuilder(this).run {
      matchFold(emptyList<Int>(), matchOn) { list, index ->
        list + index
      }.let { result: List<Int> ->
        result.forEach { index ->
          // reparse file for each offset to allow side-effects in tests
          // and to keep myFixture up-to-date
          val psiFile: PsiFile? = myFixture.configureByText(KotlinFileType.INSTANCE, toString())
          psiFile?.findElementAt(index)?.let { psiElement ->
            f(psiElement)
          }
        }
      }
    }
}

fun <R> StringBuilder.matchFold(
  acc: R,
  matchOn: String,
  f: (acc: R, index: Int) -> R): R =
  if (contains(matchOn))
    indexOf(matchOn).let { i ->
      delete(i, i + matchOn.length).matchFold(f(acc, i), matchOn, f)
    }
  else
    acc
