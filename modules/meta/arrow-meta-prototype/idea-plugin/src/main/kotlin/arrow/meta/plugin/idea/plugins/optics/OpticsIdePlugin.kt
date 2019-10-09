package arrow.meta.plugin.idea.plugins.optics

import arrow.meta.Plugin
import arrow.meta.invoke
import arrow.meta.plugin.idea.IdeMetaPlugin
import arrow.meta.plugins.optics.isProductType
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

val IdeMetaPlugin.opticsIdePlugin: Plugin
  get() = "OpticsIdePlugin" {
    meta(
      addLineMarkerProvider(
        icon = TODO(),
        message = "Optics",
        matchOn = { it.safeAs<KtClass>()?.let(::isProductType) == true }
      )
    )
  }
