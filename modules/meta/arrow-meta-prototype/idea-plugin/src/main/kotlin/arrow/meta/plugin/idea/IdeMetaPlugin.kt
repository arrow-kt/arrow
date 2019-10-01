package arrow.meta.plugin.idea

import arrow.meta.MetaPlugin
import arrow.meta.dsl.ide.IdeSyntax
import arrow.meta.phases.ExtensionPhase
import arrow.meta.plugin.idea.internal.registry.IdeInternalRegistry
import org.jetbrains.kotlin.idea.KotlinIcons
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtThrowExpression

class IdeMetaPlugin : MetaPlugin(), IdeInternalRegistry, IdeSyntax {
  override fun intercept(): List<Pair<Name, List<ExtensionPhase>>> {
    return super.intercept() + icon
  }
}

// TODO: still WIP
val IdeMetaPlugin.init: Pair<Name, List<ExtensionPhase>>
  get() = Name.identifier("Initial Extension Registry") to
    meta(
      addLocalInspectionToolToIdeRegistry()
    )

val IdeMetaPlugin.icon: Pair<Name, List<ExtensionPhase>>
  get() = Name.identifier("ImpureLineMarker") to
    meta(
      addLineMarkerProvider(
        icon = KotlinIcons.SUSPEND_CALL,
        matchOn = {
          it is KtThrowExpression
        },
        message = "KtThrow LineMarker Example"
      )
      /*addInspection(
        inspection = applicableInspection(
          "TestInspection",
          KtNamedFunction::class.java,
          { f: KtNamedFunction -> f.textRange },
          isApplicable = { f -> f.name == "foo" },
          inspectionHighlightType = { _ -> ProblemHighlightType.ERROR },
          inspectionText = { "WHAT" },
          applyTo = { element, project, editor ->
            element.addModifier(KtTokens.SUSPEND_KEYWORD)
          }
        ),
        shortName = "Test"
      )*/
    )
