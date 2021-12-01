//[arrow-fx-stm](../../../index.md)/[arrow.fx.stm](../index.md)/[TMVar](index.md)

# TMVar

[common]\
data class [TMVar](index.md)&lt;[A](index.md)&gt;

A [TMVar](index.md) is a mutable reference that can either be empty or hold a value.

The main use for [TMVar](index.md) is as a synchronization primitive as it can be used to force other transactions to wait until a [TMVar](index.md) is full.

##  Creating a [TMVar](index.md):

As usual with [STM](../-s-t-m/index.md) types there are two equal sets of operators for creating them, one that can be used inside and one for use outside of transactions:

<ul><li>[TMVar.new](-companion/new.md) and [STM.newTMVar](../new-t-m-var.md) create a new filled [TMVar](index.md)</li><li>[TMVar.empty](-companion/empty.md) and [STM.newEmptyTMVar](../new-empty-t-m-var.md) create an empty [TMVar](index.md)</li></ul>

##  Reading the content of a [TMVar](index.md)

Taking the value out of a [TMVar](index.md):

import arrow.fx.stm.TMVar\
import arrow.fx.stm.atomically\
\
suspend fun main() {\
  //sampleStart\
  val tmvar = TMVar.new(10)\
  val result = atomically {\
    tmvar.take()\
  }\
  //sampleEnd\
  println("Result $result")\
  println("New value ${atomically { tmvar.tryTake() } }")\
}<!--- KNIT example-tmvar-01.kt -->

Should the [TMVar](index.md) be empty at the time of calling [STM.take](../-s-t-m/take.md), it will call [STM.retry](../-s-t-m/retry.md), suspend and wait for another transaction to put a value back.

This behaviour can be avoided by using [STM.tryTake](../-s-t-m/try-take.md) instead:

import arrow.fx.stm.TMVar\
import arrow.fx.stm.atomically\
\
suspend fun main() {\
  //sampleStart\
  val tmvar = TMVar.empty&lt;Int&gt;()\
  val result = atomically {\
    tmvar.tryTake()\
  }\
  //sampleEnd\
  println("Result $result")\
  println("New value ${atomically { tmvar.tryTake() } }")\
}<!--- KNIT example-tmvar-02.kt -->

Another effect of using either [STM.take](../-s-t-m/take.md) or [STM.tryTake](../-s-t-m/try-take.md) is that the [TMVar](index.md) will be empty after a successful call. Alternatively if you just want to read without emptying you can use either [STM.read](../-s-t-m/read.md) or [STM.tryRead](../-s-t-m/try-read.md) which will not remove the value. As with [STM.take](../-s-t-m/read.md) will fail and retry the transaction should the [TMVar](index.md) be empty, whereas [STM.tryTake](../-s-t-m/try-take.md) and [STM.tryRead](../-s-t-m/try-read.md) will return null instead.

import arrow.fx.stm.TMVar\
import arrow.fx.stm.atomically\
\
suspend fun main() {\
  //sampleStart\
  val tmvar = TMVar.new(30)\
  val result = atomically {\
    tmvar.read()\
  }\
  //sampleEnd\
  println("Result $result")\
  println("New value ${atomically { tmvar.tryTake() } }")\
}<!--- KNIT example-tmvar-03.kt -->import arrow.fx.stm.TMVar\
import arrow.fx.stm.atomically\
\
suspend fun main() {\
  //sampleStart\
  val tmvar = TMVar.empty&lt;Int&gt;()\
  val result = atomically {\
    tmvar.tryRead()\
  }\
  //sampleEnd\
  println("Result $result")\
}<!--- KNIT example-tmvar-04.kt -->

##  Setting the value of a [TMVar](index.md):

Changing the value of an empty [TMVar](index.md):

import arrow.fx.stm.TMVar\
import arrow.fx.stm.atomically\
\
suspend fun main() {\
  //sampleStart\
  val tmvar = TMVar.empty&lt;Int&gt;()\
  atomically {\
    tmvar.put(20)\
  }\
  //sampleEnd\
  println("New value ${atomically { tmvar.tryTake() } }")\
}<!--- KNIT example-tmvar-05.kt -->

Should the [TMVar](index.md) be full, [STM.put](../-s-t-m/put.md) will call [STM.retry](../-s-t-m/retry.md) and wait for another transaction to empty the [TMVar](index.md) again. This can be avoided by using [STM.tryPut](../-s-t-m/try-put.md) instead. [STM.tryPut](../-s-t-m/try-put.md) returns whether or not the operation was successful.

import arrow.fx.stm.TMVar\
import arrow.fx.stm.atomically\
\
suspend fun main() {\
  //sampleStart\
  val tmvar = TMVar.new(20)\
  val result = atomically {\
    tmvar.tryPut(30)\
  }\
  //sampleEnd\
  println("Result $result")\
  println("New value ${atomically { tmvar.tryTake() } }")\
}<!--- KNIT example-tmvar-06.kt -->

Another common pattern is to swap the value of a [TMVar](index.md), returning the old value and setting a new one:

import arrow.fx.stm.TMVar\
import arrow.fx.stm.atomically\
\
suspend fun main() {\
  //sampleStart\
  val tmvar = TMVar.new(30)\
  val result = atomically {\
    tmvar.swap(40)\
  }\
  //sampleEnd\
  println("Result $result")\
  println("New value ${atomically { tmvar.tryTake() } }")\
}<!--- KNIT example-tmvar-07.kt -->

##  Checking if a [TMVar](index.md) is empty:

Checking if a [TMVar](index.md) is empty or not can be done by either using [STM.isEmpty](../-s-t-m/is-empty.md) or [STM.isNotEmpty](../-s-t-m/is-not-empty.md):

import arrow.fx.stm.TMVar\
import arrow.fx.stm.atomically\
\
suspend fun main() {\
  //sampleStart\
  val tmvar = TMVar.empty&lt;Int&gt;()\
  val result = atomically {\
    tmvar.isEmpty()\
  }\
  //sampleEnd\
  println("Result $result")\
}<!--- KNIT example-tmvar-08.kt -->

Because the state of a transaction is constant there can never be a race condition between checking if a TMVar is empty and subsequent reads in the *same* transaction.

## Types

| Name | Summary |
|---|---|
| [Companion](-companion/index.md) | [common]<br>object [Companion](-companion/index.md) |
