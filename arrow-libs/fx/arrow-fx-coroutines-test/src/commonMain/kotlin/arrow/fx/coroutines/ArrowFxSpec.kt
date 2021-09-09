package arrow.fx.coroutines

import arrow.core.test.UnitSpec

/** Simple overwritten Kotest StringSpec (UnitSpec) to reduce stress on tests. */
public abstract class ArrowFxSpec(
  iterations: Int = 250,
  spec: ArrowFxSpec.() -> Unit = {}
) : UnitSpec(iterations) {
  init {
    spec()
  }
}
