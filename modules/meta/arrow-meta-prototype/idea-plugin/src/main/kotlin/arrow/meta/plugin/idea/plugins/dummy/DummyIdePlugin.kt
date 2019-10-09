package arrow.meta.plugin.idea.plugins.dummy

import arrow.meta.Plugin
import arrow.meta.invoke
import arrow.meta.plugin.idea.IdeMetaPlugin
import org.jetbrains.kotlin.idea.KotlinIcons
import org.jetbrains.kotlin.psi.KtThrowExpression

/**
 * Unrelated to [arrow.meta.plugins.dummy.dummy]
 */
val IdeMetaPlugin.dummyIdePlugin: Plugin
  get() = "DummyIdePlugin" {
    meta(
      addLineMarkerProvider(
        icon = KotlinIcons.SUSPEND_CALL,
        matchOn = {
          it is KtThrowExpression
        },
        message = "KtThrow LineMarker Example"
      )
    )
  }
