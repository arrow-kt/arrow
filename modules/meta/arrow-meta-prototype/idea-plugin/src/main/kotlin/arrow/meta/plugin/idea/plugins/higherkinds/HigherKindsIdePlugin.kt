package arrow.meta.plugin.idea.plugins.higherkinds

import arrow.meta.Plugin
import arrow.meta.invoke
import arrow.meta.plugin.idea.IdeMetaPlugin
import arrow.meta.plugin.idea.resources.ArrowIcons
import arrow.meta.plugins.higherkind.isHigherKindedType
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

val IdeMetaPlugin.higherKindsIdePlugin: Plugin
  get() = "HigherKindsIdePlugin" {
    meta(
      addLineMarkerProvider(
        icon = ArrowIcons.HKT,
        message = "HKT",
        matchOn = { it.safeAs<KtClass>()?.let(::isHigherKindedType) == true }
      )
    )
  }
