package arrow.meta.dsl.ide.editor.inspection

import arrow.meta.phases.ExtensionPhase
import arrow.meta.plugin.idea.IdeMetaPlugin
import com.intellij.codeHighlighting.HighlightDisplayLevel
import com.intellij.codeInspection.InspectionEP
import com.intellij.codeInspection.InspectionSuppressor
import com.intellij.codeInspection.LanguageInspectionSuppressors
import com.intellij.codeInspection.LocalInspectionEP
import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.SuppressQuickFix
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.idea.inspections.AbstractKotlinInspection
import org.jetbrains.kotlin.psi.KtElement

/**
 * TODO: @param inspection should also be used with #addApplicableInspection.
 * More General Inspections can be build with [AbstractKotlinInspection] e.g.: [org.jetbrains.kotlin.idea.inspections.RedundantSuspendModifierInspection]
 */
interface InspectionSyntax {
  fun IdeMetaPlugin.addInspection(
    inspection: LocalInspectionTool,
    shortName: String,
    defaultLevel: HighlightDisplayLevel = HighlightDisplayLevel(HighlightSeverity.INFORMATION),
    defaultShortName: String? = null,
    groupDisplayName: String? = null,
    defaultGroupDisplayName: String? = null,
    displayName: String? = null,
    defaultDisplayName: String? = null,
    groupPath: Array<String>? = null
  ): ExtensionPhase =
    extensionProvider(
      InspectionEP.GLOBAL_INSPECTION,
      object : InspectionEP() {
        override fun getDefaultLevel(): HighlightDisplayLevel =
          defaultLevel

        override fun getGroupPath(): Array<String>? =
          groupPath

        override fun getDisplayName(): String? =
          displayName

        override fun getDefaultDisplayName(): String? =
          defaultDisplayName

        override fun getInstance(): Any =
          inspection

        override fun getDefaultGroupDisplayName(): String? =
          defaultGroupDisplayName

        override fun getShortName(): String =
          shortName

        override fun getDefaultShortName(): String? =
          defaultShortName

        override fun getGroupDisplayName(): String? =
          groupDisplayName
      }
    )

  /**
   * [LocalInspectionEP.LOCAL_INSPECTION] or [LocalInspectionEP.GLOBAL_INSPECTION]
   */
  @Suppress("UNCHECKED_CAST")
  fun <K : KtElement> IdeMetaPlugin.addApplicableInspection(
    defaultFixText: String,
    kClass: Class<K> = KtElement::class.java as Class<K>,
    highlightingRange: (element: K) -> TextRange? = { null },
    inspectionText: (element: K) -> String,
    applyTo: (element: K, project: Project, editor: Editor?) -> Unit,
    isApplicable: (element: K) -> Boolean
  ): ExtensionPhase = TODO("Adapt ExtensionProvider to Subtypes OR this is used solely for [QuickFixContributor]")
  /*extensionProvider(
    TODO(),
    object : AbstractApplicabilityBasedInspection<K>(kClass) {
      override val defaultFixText: String
        get() = defaultFixText

      override fun applyTo(element: K, project: Project, editor: Editor?) =
        applyTo(element, project, editor)

      override fun inspectionText(element: K): String =
        inspectionText(element)

      override fun isApplicable(element: K): Boolean =
        isApplicable(element)

      override fun inspectionHighlightRangeInElement(element: K): TextRange? =
        highlightingRange(element)
    }
  )*/

  fun IdeMetaPlugin.addInspectionSuppressor(
    suppressFor: (element: PsiElement, toolId: String) -> Boolean,
    suppressAction: (element: PsiElement?, toolId: String) -> Array<SuppressQuickFix>
  ): ExtensionPhase =
    extensionProvider(
      LanguageInspectionSuppressors.INSTANCE,
      object : InspectionSuppressor {
        override fun getSuppressActions(element: PsiElement?, toolId: String): Array<SuppressQuickFix> =
          suppressAction(element, toolId)

        override fun isSuppressedFor(element: PsiElement, toolId: String): Boolean =
          suppressFor(element, toolId)
      }
    )
}
