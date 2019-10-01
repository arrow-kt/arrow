package arrow.meta.plugins.dummy

import arrow.meta.phases.ExtensionPhase
import arrow.meta.MetaComponentRegistrar
import arrow.meta.quotes.classOrObject
import arrow.meta.quotes.func
import org.jetbrains.kotlin.name.Name

val MetaComponentRegistrar.dummy: Pair<Name, List<ExtensionPhase>>
  get() =
    Name.identifier("Dummy") to
      meta(
        classOrObject({ name?.startsWith("Test") == true }) { c ->
          listOfNotNull(
            """
              |class Test {
              |  fun test(): Unit = TODO()
              |}
              """
          )
        }
      )
