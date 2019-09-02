package arrow.meta.dummy

import arrow.meta.extensions.ExtensionPhase
import arrow.meta.extensions.MetaComponentRegistrar
import arrow.meta.qq.classOrObject
import org.jetbrains.kotlin.name.Name

val MetaComponentRegistrar.dummy: Pair<Name, List<ExtensionPhase>>
  get() =
    Name.identifier("Dummy") to
      meta(
        classOrObject({ true }) { c ->
          println("Processing Dummy: ${c.name}")
          listOfNotNull(
            """
              |interface SynthSuperType
              |
            """,
            """
              |$modality $visibility $kind $name<$typeParameters>($valueParameters): java.lang.Serializable {
              |  $body
              |  fun test(): Unit = println("Boom!")
              |  class ZZ
              |  class YY
              |  companion object Factory {
              |    fun test(): Unit = println("Boom!")
              |  }
              |}
              |"""
          )
        }
      )
