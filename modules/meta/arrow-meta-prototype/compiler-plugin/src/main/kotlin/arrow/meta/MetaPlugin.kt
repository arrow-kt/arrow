package arrow.meta

import arrow.meta.phases.ExtensionPhase
import arrow.meta.plugins.comprehensions.comprehensions
import arrow.meta.plugins.dummy.dummy
import arrow.meta.plugins.higherkind.higherKindedTypes
import arrow.meta.plugins.typeclasses.typeClasses
import org.jetbrains.kotlin.name.Name

open class MetaPlugin : MetaComponentRegistrar {
  override fun intercept(): List<Pair<Name, List<ExtensionPhase>>> =
    listOf(higherKindedTypes, typeClasses, comprehensions) //, higherKindedTypes, typeClasses, comprehensions)
}
