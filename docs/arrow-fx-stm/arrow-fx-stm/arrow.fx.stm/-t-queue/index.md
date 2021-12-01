//[arrow-fx-stm](../../../index.md)/[arrow.fx.stm](../index.md)/[TQueue](index.md)

# TQueue

[common]\
data class [TQueue](index.md)&lt;[A](index.md)&gt;

A [TQueue](index.md) is a transactional unbounded queue which can be written to and read from concurrently.

The implementation uses two [TVar](../-t-var/index.md)'s containing lists. One for read and one for write access. Due to the semantics of [STM](../-s-t-m/index.md) this means a write to the queue will never invalidate or block a read and vice versa, making highly concurrent use possible.

In practice, if the read variable is empty, the two must swap contents but this operation is infrequent and thus can be ignored.

##  Creating a [TQueue](index.md)

Creating an empty queue can be done by using either [STM.newTQueue](../new-t-queue.md) or [TQueue.new](-companion/new.md) depending on whether or not you are in a transaction or not.

##  Writing to the [TQueue](index.md)

Writing to the end of the queue is done by using [STM.write](../-s-t-m/write.md):

import arrow.fx.stm.TQueue\
import arrow.fx.stm.atomically\
\
suspend fun main() {\
  //sampleStart\
  val tq = TQueue.new&lt;Int&gt;()\
  atomically {\
    tq.write(2)\
    // or alternatively\
    tq += 4\
  }\
  //sampleEnd\
  println("Items in queue ${atomically { tq.flush() }}")\
}<!--- KNIT example-tqueue-01.kt -->

It is also possible to write to the front of the queue, but since that accesses the read variable it can lead to worse overall performance:

import arrow.fx.stm.TQueue\
import arrow.fx.stm.atomically\
\
suspend fun main() {\
  //sampleStart\
  val tq = TQueue.new&lt;Int&gt;()\
  atomically {\
    tq.write(1)\
    tq.writeFront(2)\
  }\
  //sampleEnd\
  println("Items in queue ${atomically { tq.flush() }}")\
}<!--- KNIT example-tqueue-02.kt -->

##  Reading items from a [TQueue](index.md)

There are several different ways to read from a [TQueue](index.md), the most common one being [STM.read](../-s-t-m/read.md):

import arrow.fx.stm.TQueue\
import arrow.fx.stm.atomically\
\
suspend fun main() {\
  //sampleStart\
  val tq = TQueue.new&lt;Int&gt;()\
  val result = atomically {\
    tq.write(2)\
    tq.read()\
  }\
  //sampleEnd\
  println("Result $result")\
  println("Items in queue ${atomically { tq.flush() }}")\
}<!--- KNIT example-tqueue-03.kt -->

Should the queue be empty calling [STM.read](../-s-t-m/read.md) will cause the transaction to retry and thus wait for items to be added to the queue. This can be avoided using [STM.tryRead](../-s-t-m/try-read.md) instead:

import arrow.fx.stm.TQueue\
import arrow.fx.stm.atomically\
\
suspend fun main() {\
  //sampleStart\
  val tq = TQueue.new&lt;Int&gt;()\
  val result = atomically {\
    tq.tryRead()\
  }\
  //sampleEnd\
  println("Result $result")\
  println("Items in queue ${atomically { tq.flush() }}")\
}<!--- KNIT example-tqueue-04.kt -->

[STM.read](../-s-t-m/read.md) also removes the read item from the queue. Alternatively [STM.peek](../-s-t-m/peek.md) will leave the queue unchanged on a read:

import arrow.fx.stm.TQueue\
import arrow.fx.stm.atomically\
\
suspend fun main() {\
  //sampleStart\
  val tq = TQueue.new&lt;Int&gt;()\
  val result = atomically {\
    tq.write(2)\
\
    tq.peek()\
  }\
  //sampleEnd\
  println("Result $result")\
  println("Items in queue ${atomically { tq.flush() }}")\
}<!--- KNIT example-tqueue-05.kt -->

As with [STM.read](../-s-t-m/peek.md) will retry should the queue be empty. The alternative [STM.tryPeek](../-s-t-m/try-peek.md) is there to avoid that:

import arrow.fx.stm.TQueue\
import arrow.fx.stm.atomically\
\
suspend fun main() {\
  //sampleStart\
  val tq = TQueue.new&lt;Int&gt;()\
  val result = atomically {\
    tq.tryPeek()\
  }\
  //sampleEnd\
  println("Result $result")\
  println("Items in queue ${atomically { tq.flush() }}")\
}<!--- KNIT example-tqueue-06.kt -->

It is also possible to read the entire list in one go using [STM.flush](../-s-t-m/flush.md):

import arrow.fx.stm.TQueue\
import arrow.fx.stm.atomically\
\
suspend fun main() {\
  //sampleStart\
  val tq = TQueue.new&lt;Int&gt;()\
  val result = atomically {\
    tq.write(2)\
    tq.write(4)\
\
    tq.flush()\
  }\
  //sampleEnd\
  println("Result $result")\
  println("Items in queue ${atomically { tq.flush() }}")\
}<!--- KNIT example-tqueue-07.kt -->

##  Checking a queues size

Checking if a queue is empty can be done by using either [STM.isEmpty](../-s-t-m/is-empty.md) or [STM.isNotEmpty](../-s-t-m/is-not-empty.md):

import arrow.fx.stm.TQueue\
import arrow.fx.stm.atomically\
\
suspend fun main() {\
  //sampleStart\
  val tq = TQueue.new&lt;Int&gt;()\
  val result = atomically {\
    tq.isEmpty()\
  }\
  //sampleEnd\
  println("Result $result")\
}<!--- KNIT example-tqueue-08.kt -->

Retrieving the actual size of a list can be done using [STM.size](../-s-t-m/size.md):

import arrow.fx.stm.TQueue\
import arrow.fx.stm.atomically\
\
suspend fun main() {\
  //sampleStart\
  val tq = TQueue.new&lt;Int&gt;()\
  val result = atomically {\
    tq.size()\
  }\
  //sampleEnd\
  println("Result $result")\
}<!--- KNIT example-tqueue-09.kt -->

All three of these methods have to access both the write and read end of a [TQueue](index.md) and thus can increase contention. Use them sparingly!

##  Removing elements from a [TQueue](index.md)

It is also possible to remove elements from a [TQueue](index.md) using [STM.removeAll](../-s-t-m/remove-all.md):

import arrow.fx.stm.TQueue\
import arrow.fx.stm.atomically\
\
suspend fun main() {\
  //sampleStart\
  val tq = TQueue.new&lt;Int&gt;()\
  atomically {\
    tq.write(0)\
    tq.removeAll { it != 0 }\
  }\
  //sampleEnd\
  println("Items in queue ${atomically { tq.flush() }}")\
}<!--- KNIT example-tqueue-10.kt -->

This method also access both ends of the queue and thus should be used infrequently to avoid contention.

## Types

| Name | Summary |
|---|---|
| [Companion](-companion/index.md) | [common]<br>object [Companion](-companion/index.md) |
