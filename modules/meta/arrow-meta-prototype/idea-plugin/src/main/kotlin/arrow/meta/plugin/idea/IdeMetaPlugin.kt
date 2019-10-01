package arrow.meta.plugin.idea

import arrow.meta.MetaPlugin
import arrow.meta.phases.ExtensionPhase
import arrow.meta.plugin.idea.internal.registry.IdeInternalRegistry
import org.jetbrains.kotlin.name.Name

class IdeMetaPlugin : MetaPlugin(), IdeInternalRegistry
