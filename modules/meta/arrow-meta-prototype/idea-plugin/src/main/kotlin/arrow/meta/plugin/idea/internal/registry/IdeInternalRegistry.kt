package arrow.meta.plugin.idea.internal.registry

import arrow.meta.MetaComponentRegistrar
import arrow.meta.dsl.platform.ide
import arrow.meta.internal.registry.InternalRegistry
import arrow.meta.phases.ExtensionPhase
import arrow.meta.plugin.idea.phases.analysis.MetaIdeAnalyzer
import org.jetbrains.kotlin.container.useImpl
import org.jetbrains.kotlin.name.Name

internal interface IdeInternalRegistry : InternalRegistry {

  override fun registerMetaAnalyzer(): ExtensionPhase =
    ide {
      storageComponent(
        registerModuleComponents = { container, moduleDescriptor ->
          //println("Registering meta analyzer")
          container.useImpl<MetaIdeAnalyzer>()
          //
        },
        check = { declaration, descriptor, context ->
        }
      )
    } ?: ExtensionPhase.Empty

}
