// This file was automatically generated from Resource.kt by Knit tool. Do not edit.
package arrow.fx.coroutines.examples.exampleResource08

import arrow.fx.coroutines.*

suspend fun acquireResource(): Int = 42.also { println("Getting expensive resource") }
suspend fun releaseResource(r: Int, exitCase: ExitCase): Unit = println("Releasing expensive resource: $r, exit: $exitCase")

suspend fun main(): Unit {
  val resource = Resource(::acquireResource, ::releaseResource)
  resource.use {
    println("Expensive resource under use! $it")
  }
}
