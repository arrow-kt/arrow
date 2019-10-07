package arrow.meta

import arrow.meta.phases.CompilerContext
import arrow.meta.plugins.comprehensions.comprehensions
import arrow.meta.plugins.higherkind.higherKindedTypes
import arrow.meta.plugins.optics.lenses
import arrow.meta.plugins.typeclasses.typeClasses
import kotlin.contracts.ExperimentalContracts

open class MetaPlugin : Meta {
  @ExperimentalContracts
  override fun intercept(ctx: CompilerContext): List<Plugin> =
    listOf(
      higherKindedTypes,
      typeClasses,
      comprehensions,
      lenses
    )
}
