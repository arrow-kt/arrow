// This file was automatically generated from STM.kt by Knit tool. Do not edit.
package arrow.fx.stm.examples.exampleStm02

import arrow.fx.stm.atomically
import arrow.fx.stm.TVar
import arrow.fx.stm.STM
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.async
import kotlinx.coroutines.delay

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
  else retry() // we now retry if there is not enough money in the account
  // this can also be achieved by using `check(current - amount >= 0); acc.write(it + amount)`
}

fun main(): Unit = runBlocking {
  val acc1 = TVar.new(0)
  val acc2 = TVar.new(300)
  println("Balance account 1: ${acc1.unsafeRead()}")
  println("Balance account 2: ${acc2.unsafeRead()}")
  async {
    println("Sending money - Searching")
    delay(2000)
    println("Sending money - Found some")
    atomically { acc1.write(100_000_000) }
  }
  println("Performing transaction")
  atomically {
    println("Trying to transfer")
    transfer(acc1, acc2, 50)
  }
  println("Balance account 1: ${acc1.unsafeRead()}")
  println("Balance account 2: ${acc2.unsafeRead()}")
}
