// This file was automatically generated from Resource.kt by Knit tool. Do not edit.
package arrow.fx.coroutines.examples.exampleResource07

import arrow.fx.coroutines.install
import arrow.fx.coroutines.resource
import arrow.fx.coroutines.resourceScope

val resource = resource {
  install({ 42.also { println("Getting expensive resource") } }) { r, exitCase ->
    println("Releasing expensive resource: $r, exit: $exitCase")
  }
}

suspend fun main(): Unit = resourceScope {
  val res = resource.bind()
  println("Expensive resource under use! $res")
}
