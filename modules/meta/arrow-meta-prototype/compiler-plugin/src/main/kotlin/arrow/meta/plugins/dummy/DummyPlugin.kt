package arrow.meta.plugins.dummy

import arrow.meta.Meta
import arrow.meta.Plugin
import arrow.meta.invoke
import arrow.meta.quotes.Transform
import arrow.meta.quotes.classOrObject

val Meta.dummy: Plugin
  get() =
    "Dummy" {
      meta(
        classOrObject({ name?.startsWith("Test") == true }) { c ->
          Transform.replace(
            replacing = c,
            newDeclaration =
              """|class Test {
                 |  fun test(): Unit = TODO()
                 |}
                 |""".`class`.synthetic
          )
        }
      )
    }
