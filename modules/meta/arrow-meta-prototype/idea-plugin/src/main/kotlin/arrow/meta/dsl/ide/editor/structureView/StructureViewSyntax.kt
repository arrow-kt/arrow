package arrow.meta.dsl.ide.editor.structureView

import arrow.meta.internal.Noop
import arrow.meta.phases.ExtensionPhase
import arrow.meta.plugin.idea.IdeMetaPlugin
import com.intellij.ide.structureView.StructureViewBuilder
import com.intellij.ide.structureView.StructureViewModel
import com.intellij.ide.structureView.StructureViewModelBase
import com.intellij.ide.structureView.StructureViewTreeElement
import com.intellij.ide.structureView.TreeBasedStructureViewBuilder
import com.intellij.ide.structureView.impl.common.PsiTreeElementBase
import com.intellij.lang.LanguageStructureViewBuilder
import com.intellij.lang.PsiStructureViewFactory
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.ui.Queryable
import com.intellij.psi.PsiFile
import org.jetbrains.kotlin.idea.structureView.KotlinStructureViewElement
import org.jetbrains.kotlin.idea.structureView.KotlinStructureViewModel
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtEnumEntry
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

interface StructureViewSyntax {

  /**
   * Convenient function to construct a StructureView
   */
  fun IdeMetaPlugin.addStructureViewForKtFile(
    putInfo: (info: MutableMap<String, String>) -> Unit,
    childrenBase: (file: KtFile) -> MutableCollection<StructureViewTreeElement> =
      { KotlinStructureViewElement(it).childrenBase.toMutableList() },
    presentableText: (file: KtFile) -> String? =
      { KotlinStructureViewElement(it).presentableText },
    isAlwaysShowsPlus: (element: StructureViewTreeElement?) -> Boolean =
      { it?.value.run { (this is KtClassOrObject && this !is KtEnumEntry) || this is KtFile } },
    isAlwaysLeaf: (element: StructureViewTreeElement?) -> Boolean = Noop.boolean1False
  ): ExtensionPhase =
    extensionProvider(
      LanguageStructureViewBuilder.INSTANCE,
      structureViewFactory { psiFile: PsiFile ->
        psiFile.safeAs<KtFile>()?.run {
          treeBasedStructureViewBuilder { editor: Editor? ->
            structureViewModel(
              this,
              editor,
              { treeElementBase(this, childrenBase, presentableText, putInfo) },
              isAlwaysShowsPlus, isAlwaysLeaf, putInfo
            )
          }
        }
      }
    )

  /**
   * Room for improvement: the function parameters should also adapt to matchOn
   */
  fun IdeMetaPlugin.addStructureView(
    matchOn: (psiFile: PsiFile) -> Boolean,
    putInfo: (info: MutableMap<String, String>) -> Unit,
    childrenBase: (file: PsiFile) -> MutableCollection<StructureViewTreeElement> =
      { KotlinStructureViewElement(it).childrenBase.toMutableList() },
    presentableText: (file: PsiFile) -> String? =
      { KotlinStructureViewElement(it).presentableText },
    isAlwaysShowsPlus: (element: StructureViewTreeElement?) -> Boolean =
      { it?.value.run { (this is KtClassOrObject && this !is KtEnumEntry) || this is KtFile } },
    isAlwaysLeaf: (element: StructureViewTreeElement?) -> Boolean = Noop.boolean1False
  ): ExtensionPhase =
    extensionProvider(
      LanguageStructureViewBuilder.INSTANCE,
      structureViewFactory { psiFile: PsiFile ->
        if (matchOn(psiFile))
          treeBasedStructureViewBuilder { editor: Editor? ->
            structureViewModel(
              psiFile,
              editor,
              { treeElementBase(psiFile, childrenBase, presentableText, putInfo) },
              isAlwaysShowsPlus, isAlwaysLeaf, putInfo
            )
          }
        else
          null
      }
    )

  /**
   * Use [LanguageStructureViewBuilder]
   */
  fun StructureViewSyntax.structureViewFactory(
    structureViewBuilder: (psiFile: PsiFile) -> StructureViewBuilder?
  ): PsiStructureViewFactory =
    PsiStructureViewFactory { psiFile -> structureViewBuilder(psiFile) }

  /**
   * Standard impl for [StructureViewBuilder]
   */
  fun StructureViewSyntax.treeBasedStructureViewBuilder(
    structureViewModel: (editor: Editor?) -> StructureViewModel
  ): StructureViewBuilder =
    object : TreeBasedStructureViewBuilder() {
      override fun createStructureViewModel(editor: Editor?): StructureViewModel =
        structureViewModel(editor)
    }

  /**
   * Closest impl to Kotlin's StructureViewModel
   * Default's from [KotlinStructureViewModel]
   * Can also be abstracted to PsiFile's
   */
  fun StructureViewSyntax.structureViewModel(
    psiFile: PsiFile,
    editor: Editor?,
    treeElementBase: (file: PsiFile) -> StructureViewTreeElement =
      { KotlinStructureViewElement(it, false) },
    isAlwaysShowsPlus: (element: StructureViewTreeElement?) -> Boolean =
      { it?.value.run { (this is KtClassOrObject && this !is KtEnumEntry) || this is KtFile } },
    isAlwaysLeaf: (element: StructureViewTreeElement?) -> Boolean = Noop.boolean1False,
    putInfo: (info: MutableMap<String, String>) -> Unit
  ): StructureViewModel =
    object : StructureViewModelBase(psiFile, editor, treeElementBase(psiFile)), StructureViewModel.ElementInfoProvider, Queryable {
      override fun isAlwaysShowsPlus(element: StructureViewTreeElement?): Boolean =
        isAlwaysShowsPlus(element)

      override fun isAlwaysLeaf(element: StructureViewTreeElement?): Boolean =
        isAlwaysLeaf(element)

      override fun putInfo(info: MutableMap<String, String>) =
        putInfo(info)
    }

  fun <F : PsiFile> StructureViewSyntax.treeElementBase(
    psiFile: F,
    childrenBase: (file: F) -> MutableCollection<StructureViewTreeElement> =
      { KotlinStructureViewElement(it).childrenBase.toMutableList() },
    presentableText: (file: F) -> String? =
      { KotlinStructureViewElement(it).presentableText },
    putInfo: (info: MutableMap<String, String>) -> Unit
  ): StructureViewTreeElement =
    object : PsiTreeElementBase<F>(psiFile), Queryable {
      override fun getChildrenBase(): MutableCollection<StructureViewTreeElement> =
        childrenBase(psiFile)

      override fun getPresentableText(): String? =
        presentableText(psiFile)

      override fun putInfo(info: MutableMap<String, String>) =
        putInfo(info)
    }
}
