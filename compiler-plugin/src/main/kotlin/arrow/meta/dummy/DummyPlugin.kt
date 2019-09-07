package arrow.meta.dummy

import arrow.meta.extensions.ExtensionPhase
import arrow.meta.extensions.MetaComponentRegistrar
import arrow.meta.qq.classOrObject
import org.jetbrains.kotlin.name.Name

val MetaComponentRegistrar.dummy: Pair<Name, List<ExtensionPhase>>
  get() =
    Name.identifier("Dummy") to
      meta(
        classOrObject({ name == "TestClass" }) { c ->
          println("Processing Dummy: ${c.name}")
          listOfNotNull(
            """
              |interface SynthSuperType {
              |  fun fromSynthSuperType(): Unit = println("fromSynthSuperType")
              |}
            """,
            """
              |$modality $visibility $kind $name<$typeParameters>($valueParameters): SynthSuperType {
              |  $body
              |  fun test2(): String = "Boom!"
              |  fun test(): Unit = println(test2())
              |  
              |  val xx: Int = 1
              |  val yy: Double = 0.0
              |  
              |  class ZZ
              |  class XX
              |  
              |  companion object Factory
              |}
              |"""
          )
        }
      )
