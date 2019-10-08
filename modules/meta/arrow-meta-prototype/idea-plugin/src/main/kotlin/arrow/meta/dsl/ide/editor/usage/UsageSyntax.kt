package arrow.meta.dsl.ide.editor.usage

import arrow.meta.internal.Noop
import arrow.meta.phases.ExtensionPhase
import arrow.meta.plugin.idea.IdeMetaPlugin
import com.intellij.find.findUsages.AbstractFindUsagesDialog
import com.intellij.find.findUsages.FindUsagesHandler
import com.intellij.find.findUsages.FindUsagesHandlerFactory
import com.intellij.find.findUsages.FindUsagesOptions
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.SearchScope
import com.intellij.usageView.UsageInfo
import com.intellij.usages.Usage
import com.intellij.usages.UsageTarget
import com.intellij.usages.impl.rules.UsageType
import com.intellij.usages.impl.rules.UsageTypeProvider
import com.intellij.usages.rules.ImportFilteringRule
import com.intellij.util.Processor

interface UsageSyntax {
  /**
   * A filtering rule that allows you to exclude imports from actions such as "find usages"
   */
  fun IdeMetaPlugin.addImportFilteringRule(
    isVisible: (usage: Usage, targets: Array<out UsageTarget>) -> Boolean
  ): ExtensionPhase =
    extensionProvider(
      ImportFilteringRule.EP_NAME,
      object : ImportFilteringRule() {
        override fun isVisible(usage: Usage, targets: Array<out UsageTarget>): Boolean {
          return isVisible(usage, targets)
        }

        // TODO("fix 'isVisible' overrides nothing. Though, direct impl. builds fine")
        @Deprecated("Deprecated Ide API from In", ReplaceWith("isVisible(usage, targets)", "com.intellij.usages.UsageTarget"))
        override fun isVisible(usage: Usage): Boolean {
          return super.isVisible(usage)
        }
      }
    )

  fun IdeMetaPlugin.addUsageTypeProvider(
    f: (psiElement: PsiElement) -> UsageType?
  ): ExtensionPhase =
    extensionProvider(
      UsageTypeProvider.EP_NAME,
      UsageTypeProvider { if (it != null) f(it) else null }
    )

  fun IdeMetaPlugin.addFindUsagesHandlerFactory(
    findUsagesHandler: (element: PsiElement, forHighlightUsages: Boolean) -> FindUsagesHandler?,
    canFindUsages: (element: PsiElement) -> Boolean
  ): ExtensionPhase =
    extensionProvider(
      FindUsagesHandlerFactory.EP_NAME,
      object : FindUsagesHandlerFactory() {
        override fun createFindUsagesHandler(element: PsiElement, forHighlightUsages: Boolean): FindUsagesHandler? =
          findUsagesHandler(element, forHighlightUsages)

        override fun canFindUsages(element: PsiElement): Boolean =
          canFindUsages(element)
      }
    )

  /**
   * Users may appreciate no calls to super
   * @param findUsagesDialog if ([AbstractFindUsagesDialog] == null) super method is called
   * @param findReferencesToHighlight if [MutableCollection<PsiReference>].isEmpty super method is called
   * @param primaryElements if isEmpty super method is called
   * @param secondaryElements if isEmpty super method is called
   * @param processUsagesInText if [Boolean] == null super method is called
   */
  fun UsageSyntax.findUsagesHandler(
    element: PsiElement,
    processElementUsages: (element: PsiElement, processor: Processor<UsageInfo>, options: FindUsagesOptions) -> Boolean = Noop.boolean3True,
    findReferencesToHighlight: (target: PsiElement, searchScope: SearchScope) -> MutableCollection<PsiReference> =
      { _, _ -> emptyList<PsiReference>().toMutableList() },
    primaryElements: Array<PsiElement> = PsiElement.EMPTY_ARRAY,
    secondaryElements: Array<PsiElement> = PsiElement.EMPTY_ARRAY,
    findUsagesDialog: (isSingleFile: Boolean, toShowInNewTab: Boolean, mustOpenInNewTab: Boolean) -> AbstractFindUsagesDialog? = Noop.nullable3(),
    processUsagesInText: (element: PsiElement, processor: Processor<UsageInfo>, searchScope: GlobalSearchScope) -> Boolean? = Noop.nullable3()
  ): FindUsagesHandler =
    object : FindUsagesHandler(element) {
      override fun processElementUsages(element: PsiElement, processor: Processor<UsageInfo>, options: FindUsagesOptions): Boolean =
        processElementUsages(element, processor, options)

      override fun findReferencesToHighlight(target: PsiElement, searchScope: SearchScope): MutableCollection<PsiReference> =
        findReferencesToHighlight(target, searchScope).let { if (it.isEmpty()) super.findReferencesToHighlight(target, searchScope) else it }

      override fun getPrimaryElements(): Array<PsiElement> =
        primaryElements.run { if (isEmpty()) super.getPrimaryElements() else this }

      override fun getSecondaryElements(): Array<PsiElement> =
        secondaryElements.run { if (isEmpty()) super.getSecondaryElements() else this }

      override fun getFindUsagesDialog(isSingleFile: Boolean, toShowInNewTab: Boolean, mustOpenInNewTab: Boolean): AbstractFindUsagesDialog =
        findUsagesDialog(isSingleFile, toShowInNewTab, mustOpenInNewTab).run {
          this ?: super.getFindUsagesDialog(isSingleFile, toShowInNewTab, mustOpenInNewTab)
        }

      override fun processUsagesInText(element: PsiElement, processor: Processor<UsageInfo>, searchScope: GlobalSearchScope): Boolean =
        processUsagesInText(element, processor, searchScope).run {
          this ?: super.processUsagesInText(element, processor, searchScope)
        }
    }
}
