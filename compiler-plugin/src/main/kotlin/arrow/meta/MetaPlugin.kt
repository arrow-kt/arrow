package arrow.meta

import arrow.meta.dummy.dummy
import arrow.meta.extensions.ExtensionPhase
import arrow.meta.extensions.MetaComponentRegistrar
import arrow.meta.higherkind.higherKindedTypes
import arrow.meta.typeclasses.typeClasses
import org.jetbrains.kotlin.name.Name

class MetaPlugin : MetaComponentRegistrar {
  override fun intercept(): List<Pair<Name, List<ExtensionPhase>>> =
    listOf(higherKindedTypes, typeClasses) //, higherKindedTypes, typeClasses, comprehensions)
}