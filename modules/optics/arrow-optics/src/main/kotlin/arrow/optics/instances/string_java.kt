package java_lang

import arrow.optics.instances.StringFilterIndexInstance
import arrow.optics.instances.StringIndexInstance

interface StringFilterIndexInstance {
  companion object {
    operator fun invoke() = StringFilterIndexInstance
  }
}

interface StringIndexInstance {
  companion object {
    operator fun invoke() = StringIndexInstance
  }
}
