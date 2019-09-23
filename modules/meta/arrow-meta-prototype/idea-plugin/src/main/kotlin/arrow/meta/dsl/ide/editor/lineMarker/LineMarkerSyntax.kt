package arrow.meta.dsl.ide.editor.lineMarker

import arrow.meta.phases.ExtensionPhase
import arrow.meta.plugin.idea.IdeMetaPlugin
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.codeInsight.daemon.LineMarkerProviders
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.progress.ProgressManager
import com.intellij.psi.PsiElement
import javax.swing.Icon

interface LineMarkerSyntax {
  /**
   * This technique adds an LineMarker on the specified PsiElement similar to the Recursive Kotlin Icon [org.jetbrains.kotlin.idea.highlighter.KotlinRecursiveCallLineMarkerProvider]
   * or Suspended Icon [org.jetbrains.kotlin.idea.highlighter.KotlinSuspendCallLineMarkerProvider]
   */
  fun IdeMetaPlugin.addLineMarkerProvider(
    icon: Icon,
    matchOn: (element: PsiElement) -> Boolean,
    slowLineMarker: (element: PsiElement) -> LineMarkerInfo<PsiElement>?,
    element: PsiElement,
    message: String,
    placed: GutterIconRenderer.Alignment = GutterIconRenderer.Alignment.LEFT
  ): ExtensionPhase =
    addLineMarkerProvider(matchOn, slowLineMarker)

  /**
   * TODO: Add more Techniques such as the one from Elm
   */
  fun IdeMetaPlugin.addLineMarkerProvider(
    matchOn: (element: PsiElement) -> Boolean,
    slowLineMarker: (element: PsiElement) -> LineMarkerInfo<PsiElement>?,
    lineMarkerInfo: (element: PsiElement) -> LineMarkerInfo<PsiElement>? = { _ -> null }
  ): ExtensionPhase =
    extensionProvider(
      LineMarkerProviders.INSTANCE,
      object : LineMarkerProvider {
        override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<PsiElement>? =
          lineMarkerInfo(element)

        override fun collectSlowLineMarkers(elements: MutableList<PsiElement>, result: MutableCollection<LineMarkerInfo<PsiElement>>) {
          for (element in elements) {
            ProgressManager.checkCanceled()
            if (matchOn(element)) {
              slowLineMarker(element)?.let { result.add(it) }
            }
          }
        }
      }
    )

  fun LineMarkerSyntax.addLineMarkerInfo(
    icon: Icon,
    element: PsiElement,
    message: String,
    placed: GutterIconRenderer.Alignment = GutterIconRenderer.Alignment.LEFT
    // nav: GutterIconNavigationHandler<*>? = null TODO
  ): LineMarkerInfo<PsiElement> =
    object : LineMarkerInfo<PsiElement>(
      element,
      element.textRange,
      icon,
      { message },
      null,
      placed
    ) {
      override fun createGutterRenderer(): GutterIconRenderer =
        object : LineMarkerInfo.LineMarkerGutterIconRenderer<PsiElement>(this) {
          override fun getClickAction(): AnAction? = null // to place breakpoint on mouse click
        }
    }
}
