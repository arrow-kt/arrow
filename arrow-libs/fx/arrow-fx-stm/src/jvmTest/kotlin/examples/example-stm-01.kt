// This file was automatically generated from STM.kt by Knit tool. Do not edit.
package arrow.fx.stm.examples.exampleStm01

import arrow.fx.stm.atomically
import arrow.fx.stm.TVar
import arrow.fx.stm.STM

fun STM.transfer(from: TVar<Int>, to: TVar<Int>, amount: Int): Unit {
  withdraw(from, amount)
  deposit(to, amount)
}

fun STM.deposit(acc: TVar<Int>, amount: Int): Unit {
  val current = acc.read()
  acc.write(current + amount)
  // or the shorthand acc.modify { it + amount }
}

fun STM.withdraw(acc: TVar<Int>, amount: Int): Unit {
  val current = acc.read()
  if (current - amount >= 0) acc.write(current - amount)
  else throw IllegalStateException("Not enough money in the account!")
}

suspend fun main() {
  val acc1 = TVar.new(500)
  val acc2 = TVar.new(300)
  println("Balance account 1: ${acc1.unsafeRead()}")
  println("Balance account 2: ${acc2.unsafeRead()}")
  println("Performing transaction")
  atomically { transfer(acc1, acc2, 50) }
  println("Balance account 1: ${acc1.unsafeRead()}")
  println("Balance account 2: ${acc2.unsafeRead()}")
}
