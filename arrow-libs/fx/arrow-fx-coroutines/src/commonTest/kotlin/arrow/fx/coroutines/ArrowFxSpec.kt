package arrow.fx.coroutines

import io.kotest.core.spec.style.StringSpec

@Deprecated("Use Kotest", replaceWith = ReplaceWith("StringSpec", "io.kotest.core.spec.style.StringSpec"))
open class ArrowFxSpec: StringSpec {
  constructor(spec: StringSpec.() -> Unit = {}): super(spec)
  constructor(): super()
}
