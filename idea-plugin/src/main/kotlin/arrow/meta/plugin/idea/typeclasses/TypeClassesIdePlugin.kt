package arrow.meta.plugin.idea.typeclasses

import arrow.meta.phases.ExtensionPhase
import arrow.meta.plugin.idea.IdeMetaPlugin
import arrow.meta.plugin.idea.highlight
import org.jetbrains.kotlin.name.Name

val IdeMetaPlugin.typeClassesIde: Pair<Name, List<ExtensionPhase>>
  get() =
    Name.identifier("typeClassesIde") to
      meta(
        highlight(
          shouldHighlightErrorElement = { psiErrorElement ->
            println("typeClassesIde.shouldHighlightErrorElement: ${psiErrorElement.text}")
            true
          }
        )
      )