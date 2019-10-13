package arrow.meta.plugin.idea.plugins.comprehensions

import arrow.meta.Plugin
import arrow.meta.invoke
import arrow.meta.plugin.idea.IdeMetaPlugin
import arrow.meta.plugin.idea.resources.ArrowIcons
import arrow.meta.plugins.comprehensions.isBinding
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.utils.addToStdlib.safeAs
import kotlin.contracts.ExperimentalContracts

@ExperimentalContracts
val IdeMetaPlugin.comprehensionsIdePlugin: Plugin
  get() = "ComprehensionsIdePlugin" {
    meta(
      addLineMarkerProvider(
        icon = ArrowIcons.BIND,
        message = "Bind",
        matchOn = { it.safeAs<KtExpression>()?.isBinding() == true }
      )
    )
  }
