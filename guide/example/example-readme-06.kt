// This file was automatically generated from README.md by Knit tool. Do not edit.
package example.exampleReadme06

import arrow.cont
import arrow.fx.coroutines.parTraverse
import kotlinx.coroutines.delay

suspend fun parTraverse() = cont<String, List<Int>> {
 (0..100).parTraverse { index -> // running tasks
   if(index == 50) shift<Int>("error")
   else index.also { delay(1_000_000) } // Cancelled by shift
 }
}.fold(::println, ::println) // "error"
