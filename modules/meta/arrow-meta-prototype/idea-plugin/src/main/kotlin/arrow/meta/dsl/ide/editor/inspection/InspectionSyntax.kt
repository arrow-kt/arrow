package arrow.meta.dsl.ide.editor.inspection

import arrow.meta.internal.Noop
import arrow.meta.phases.ExtensionPhase
import arrow.meta.plugin.idea.IdeMetaPlugin
import com.intellij.codeHighlighting.HighlightDisplayLevel
import com.intellij.codeInspection.InspectionEP
import com.intellij.codeInspection.InspectionProfileEntry
import com.intellij.codeInspection.InspectionSuppressor
import com.intellij.codeInspection.LanguageInspectionSuppressors
import com.intellij.codeInspection.LocalInspectionEP
import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.SuppressQuickFix
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.idea.inspections.AbstractApplicabilityBasedInspection
import org.jetbrains.kotlin.idea.inspections.AbstractKotlinInspection
import org.jetbrains.kotlin.psi.KtElement

/**
 * TODO: @param inspection should also be used with #applicableInspection.
 * todo: registration is wip
 * More General Inspections can be build with [AbstractKotlinInspection] e.g.: [org.jetbrains.kotlin.idea.inspections.RedundantSuspendModifierInspection]
 */
interface InspectionSyntax : InspectionUtilitySyntax {
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
    highlightingRange: (element: K) -> TextRange? = Noop.nullable1(),
    inspectionText: (element: K) -> String,
    applyTo: (element: K, project: Project, editor: Editor?) -> Unit,
    isApplicable: (element: K) -> Boolean,
    inspectionHighlightType: (element: K) -> ProblemHighlightType =
      { _ -> ProblemHighlightType.GENERIC_ERROR_OR_WARNING }
  ): ExtensionPhase = TODO("Adapt ExtensionProvider to Subtypes OR this is used solely for [QuickFixContributor]")
    /*extensionProvider(
      LocalInspectionEP.GLOBAL_INSPECTION,
      object : LocalInspectionEP() {
        override fun instantiateTool(): InspectionProfileEntry =
          applicableInspection(defaultFixText, kClass, highlightingRange, inspectionText, applyTo, isApplicable, inspectionHighlightType)
      }
    )*/

  /*fun IdeMetaPlugin.addLocalInspection(
  ): ExtensionPhase =
    registerExtensionPoint(EP_NAME, LocalInspectionTool::class.java).run {
      extensionProvider(
        EP_NAME,
        object : LocalInspectionTool() {
          override fun getShortName(): String = "Test"
        }
      )
    }*/


  fun IdeMetaPlugin.addInspectionSuppressor(
    suppressFor: (element: PsiElement, toolId: String) -> Boolean,
    suppressAction: (element: PsiElement?, toolId: String) -> Array<SuppressQuickFix>
  ): ExtensionPhase =
    extensionProvider(
      LanguageInspectionSuppressors.INSTANCE,
      inspectionSuppressor(suppressFor, suppressAction)
    )

  @Suppress("UNCHECKED_CAST")
  fun <K : KtElement> InspectionSyntax.applicableInspection(
    defaultFixText: String,
    kClass: Class<K> = KtElement::class.java as Class<K>,
    highlightingRange: (element: K) -> TextRange? = Noop.nullable1(),
    inspectionText: (element: K) -> String,
    applyTo: (element: K, project: Project, editor: Editor?) -> Unit,
    isApplicable: (element: K) -> Boolean,
    inspectionHighlightType: (element: K) -> ProblemHighlightType =
      { _ -> ProblemHighlightType.GENERIC_ERROR_OR_WARNING }
  ): AbstractApplicabilityBasedInspection<K> =
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

      override fun inspectionHighlightType(element: K): ProblemHighlightType =
        inspectionHighlightType(element)
    }


  fun InspectionUtilitySyntax.inspectionSuppressor(
    suppressFor: (element: PsiElement, toolId: String) -> Boolean,
    suppressAction: (element: PsiElement?, toolId: String) -> Array<SuppressQuickFix>
  ): InspectionSuppressor =
    object : InspectionSuppressor {
      override fun getSuppressActions(element: PsiElement?, toolId: String): Array<SuppressQuickFix> =
        suppressAction(element, toolId)

      override fun isSuppressedFor(element: PsiElement, toolId: String): Boolean =
        suppressFor(element, toolId)
    }
}
