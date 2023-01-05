// This file was automatically generated from Effect.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEffectGuide14

import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import arrow.core.continuations.effect
import kotlin.time.Duration.Companion.seconds

fun main(): Unit = runBlocking {

 effect<String, Int> {
   launch {
     delay(3.seconds)
     shift("error")
   }
   1
 }.fold(::println, ::println)
}
