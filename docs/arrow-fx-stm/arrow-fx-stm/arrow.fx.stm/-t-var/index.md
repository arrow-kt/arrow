//[arrow-fx-stm](../../../index.md)/[arrow.fx.stm](../index.md)/[TVar](index.md)

# TVar

[common]\
class [TVar](index.md)&lt;[A](index.md)&gt;

A [TVar](index.md) is a mutable reference that can only be (safely) accessed inside a [STM](../-s-t-m/index.md) transaction.

##  Creating a [TVar](index.md)

There are two ways of creating [TVar](index.md)'s:

<ul><li>[STM.newTVar](../-s-t-m/new-t-var.md) to create a [TVar](index.md) inside a transaction</li><li>[TVar.new](-companion/new.md) to create a top-level [TVar](index.md) outside of a transaction</li></ul>

Strictly speaking [TVar.new](-companion/new.md) is not necessary as it can be defined as atomically { newTVar(v) } however [TVar.new](-companion/new.md) is much faster because it avoids creating a (pointless) transaction. [STM.newTVar](../-s-t-m/new-t-var.md) should be used inside transactions because it is not possible to use [TVar.new](-companion/new.md) inside [STM](../-s-t-m/index.md) due to suspend.

##  Reading a value from a [TVar](index.md)

One-off reading from a [TVar](index.md) outside of a transaction can be done by using [TVar.unsafeRead](unsafe-read.md). Despite the name using this method is only unsafe if the read value (or a derivative) is then used inside another transaction which may cause race conditions again. However the benefit of using this over atomically { tvar.read() } is that it avoids creating a transaction and is thus much faster.

import arrow.fx.stm.TVar\
\
suspend fun main() {\
  //sampleStart\
  val tvar = TVar.new(10)\
  val result = tvar.unsafeRead()\
  //sampleEnd\
  println(result)\
}<!--- KNIT example-tvar-01.kt -->

Reading from a [TVar](index.md) inside a transaction is done by using [STM.read](../-s-t-m/read.md).

import arrow.fx.stm.TVar\
import arrow.fx.stm.atomically\
\
suspend fun main() {\
  //sampleStart\
  val tvar = TVar.new(10)\
  val result = atomically {\
    tvar.read()\
  }\
  //sampleEnd\
  println(result)\
}<!--- KNIT example-tvar-02.kt -->

Checking the validity of a transaction is done by checking the contents of all accessed [TVar](index.md)'s before locking the [TVar](index.md)'s that have been written to and then checking only the [TVar](index.md)'s that have only been read not modified again. To keep transactions as fast as possible it is key to keep the number of accessed [TVar](index.md)'s small.

Another important thing to remember is that only writes will ever lock a [TVar](index.md) and only those that need to be changed. This means that so long as transactions access disjoint sets of variables or a transaction is read only, they may run in parallel.

##  Modifying the value inside the [TVar](index.md)

Writing a new value to the [TVar](index.md):

import arrow.fx.stm.TVar\
import arrow.fx.stm.atomically\
\
suspend fun main() {\
  //sampleStart\
  val tvar = TVar.new(10)\
  val result = atomically {\
    tvar.write(20)\
  }\
  //sampleEnd\
  println(result)\
}<!--- KNIT example-tvar-03.kt -->

Modifying the value based on the initial value:

import arrow.fx.stm.TVar\
import arrow.fx.stm.atomically\
\
suspend fun main() {\
  //sampleStart\
  val tvar = TVar.new(10)\
  val result = atomically {\
    tvar.modify { it * 2 }\
  }\
  //sampleEnd\
  println(result)\
}<!--- KNIT example-tvar-04.kt -->

Writing a new value to the [TVar](index.md) and returning the initial value:

import arrow.fx.stm.TVar\
import arrow.fx.stm.atomically\
\
suspend fun main() {\
  //sampleStart\
  val tvar = TVar.new(10)\
  val result = atomically {\
    tvar.swap(20)\
  }\
  //sampleEnd\
  println("Result $result")\
  println("New value ${tvar.unsafeRead()}")\
}<!--- KNIT example-tvar-05.kt -->

## Types

| Name | Summary |
|---|---|
| [Companion](-companion/index.md) | [common]<br>object [Companion](-companion/index.md) |

## Functions

| Name | Summary |
|---|---|
| [equals](equals.md) | [common]<br>open operator override fun [equals](equals.md)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [hashCode](hash-code.md) | [common]<br>open override fun [hashCode](hash-code.md)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [unsafeRead](unsafe-read.md) | [common]<br>suspend fun [unsafeRead](unsafe-read.md)(): [A](index.md)<br>Read the value of a [TVar](index.md). This has no consistency guarantees for subsequent reads and writes since it is outside of a stm transaction. |
