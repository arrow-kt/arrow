package arrow.fx.coroutines

import io.kotest.core.spec.style.StringSpec

open class ArrowFxSpec: StringSpec {
  public constructor(spec: StringSpec.() -> Unit = {}): super(spec)
  public constructor(): super()
}
