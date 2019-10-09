package arrow.meta.plugin.idea.plugins.nothing

import arrow.meta.Plugin
import arrow.meta.invoke
import arrow.meta.plugin.idea.IdeMetaPlugin
import org.jetbrains.kotlin.psi.KtUserType
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

val IdeMetaPlugin.nothingIdePlugin: Plugin
  get() = "NothingIdePlugin" {
    meta(
      addLineMarkerProvider(
        icon = TODO(),
        message = "Nothing is impure"
      ) { it.safeAs<KtUserType>()?.referencedName?.run { this == "Nothing" } == true }
    )
  }
