package arrow.meta.typeclasses

import arrow.meta.extensions.ExtensionPhase
import arrow.meta.extensions.MetaComponentRegistrar
import arrow.meta.qq.classOrObject

val MetaComponentRegistrar.typeClasses: List<ExtensionPhase>
  get() =
    meta(
      classOrObject({
        true
      }) { ktClass ->
        println("intercepted ${ktClass.name}")
        emptyList()
      }
    )