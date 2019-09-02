package arrow.meta

import arrow.meta.dummy.dummy
import arrow.meta.extensions.ExtensionPhase
import arrow.meta.extensions.MetaComponentRegistrar
import org.jetbrains.kotlin.name.Name

class MetaPlugin : MetaComponentRegistrar {
  override fun intercept(): List<Pair<Name, List<ExtensionPhase>>> =
    listOf(dummy) //, higherKindedTypes, typeClasses, comprehensions)
}