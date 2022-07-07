package arrow.fx.coroutines

import arrow.core.test.UnitSpec
import arrow.core.test.concurrency.deprecateArrowTestModules

/** Simple overwritten Kotest StringSpec (UnitSpec) to reduce stress on tests. */
@Deprecated(deprecateArrowTestModules)
public abstract class ArrowFxSpec(
  iterations: Int = 250,
  spec: ArrowFxSpec.() -> Unit = {}
) : UnitSpec(iterations) {
  init {
    spec()
  }
}
