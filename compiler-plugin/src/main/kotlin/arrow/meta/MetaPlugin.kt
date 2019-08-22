package arrow.meta

import arrow.meta.comprehensions.comprehensions
import arrow.meta.extensions.ExtensionPhase
import arrow.meta.extensions.MetaComponentRegistrar
import arrow.meta.higherkind.higherKindedTypes
import arrow.meta.typeclasses.typeClasses
import kotlin.contracts.ExperimentalContracts

class MetaPlugin : MetaComponentRegistrar {
  @ExperimentalContracts
  override fun intercept(): List<ExtensionPhase> =
    higherKindedTypes +
      typeClasses +
      comprehensions
}