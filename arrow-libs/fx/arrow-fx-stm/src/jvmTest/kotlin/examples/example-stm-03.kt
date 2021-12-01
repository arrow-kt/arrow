// This file was automatically generated from STM.kt by Knit tool. Do not edit.
package arrow.fx.stm.examples.exampleStm03

import kotlinx.coroutines.runBlocking
import arrow.fx.stm.atomically
import arrow.fx.stm.TVar
import arrow.fx.stm.STM
import arrow.fx.stm.stm

fun STM.transaction(v: TVar<Int>): Int? =
  stm {
    val result = v.read()
    check(result in 0..10)
    result
  } orElse { null }

fun main(): Unit = runBlocking {
  val v = TVar.new(100)
  println("Value is ${v.unsafeRead()}")
  atomically { transaction(v) }
    .also { println("Transaction returned $it") }
  println("Set value to 5")
  println("Value is ${v.unsafeRead()}")
  atomically { v.write(5) }
  atomically { transaction(v) }
    .also { println("Transaction returned $it") }
}
