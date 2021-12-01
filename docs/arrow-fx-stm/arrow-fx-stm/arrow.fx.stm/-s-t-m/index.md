//[arrow-fx-stm](../../../index.md)/[arrow.fx.stm](../index.md)/[STM](index.md)

# STM

[common]\
interface [STM](index.md)

# Consistent and safe concurrent state updates

Software transactional memory, or STM, is an abstraction for concurrent state modification. With [STM](index.md) one can write code that concurrently accesses state and that can easily be composed without exposing details of how it ensures safety guarantees. Programs running within an [STM](index.md) transaction will neither deadlock nor have race-conditions.

The api of [STM](index.md) is based on the haskell package [stm](https://hackage.haskell.org/package/stm) and the implementation is based on the GHC implementation for fine-grained locks.

The base building blocks of [STM](index.md) are [TVar](../-t-var/index.md)'s and the primitives [retry](retry.md), [orElse](or-else.md) and [catch](catch.md).

##  STM Datastructures

There are several datastructures built on top of [TVar](../-t-var/index.md)'s already provided out of the box:

<ul><li>[TQueue](../-t-queue/index.md): A transactional mutable queue</li><li>[TMVar](../-t-m-var/index.md): A mutable transactional variable that may be empty</li><li>[TSet](../-t-set/index.md), [TMap](../-t-map/index.md): Transactional Set and Map</li><li>[TArray](../-t-array/index.md): Array of [TVar](../-t-var/index.md)'s</li><li>[TSemaphore](../-t-semaphore/index.md): Transactional semaphore</li><li>[TVar](../-t-var/index.md): A transactional mutable variable</li></ul>

All of these structures (excluding [TVar](../-t-var/index.md)) are built upon [TVar](../-t-var/index.md)'s and the [STM](index.md) primitives and implementing other datastructures with [STM](index.md) can be done by composing the existing structures.

##  Reading and writing to concurrent state:

In order to modify transactional datastructures we have to be inside the [STM](index.md) context. This is achieved either by defining our functions with [STM](index.md) as the receiver or using [stm](../stm.md) to create lambda functions with [STM](index.md) as the receiver.

Running a transaction is then done using [atomically](../atomically.md):

import arrow.fx.stm.atomically\
import arrow.fx.stm.TVar\
import arrow.fx.stm.STM\
\
//sampleStart\
fun STM.transfer(from: TVar&lt;Int&gt;, to: TVar&lt;Int&gt;, amount: Int): Unit {\
  withdraw(from, amount)\
  deposit(to, amount)\
}\
\
fun STM.deposit(acc: TVar&lt;Int&gt;, amount: Int): Unit {\
  val current = acc.read()\
  acc.write(current + amount)\
  // or the shorthand acc.modify { it + amount }\
}\
\
fun STM.withdraw(acc: TVar&lt;Int&gt;, amount: Int): Unit {\
  val current = acc.read()\
  if (current - amount &gt;= 0) acc.write(current + amount)\
  else throw IllegalStateException("Not enough money in the account!")\
}\
//sampleEnd\
\
suspend fun main() {\
  val acc1 = TVar.new(500)\
  val acc2 = TVar.new(300)\
  println("Balance account 1: ${acc1.unsafeRead()}")\
  println("Balance account 2: ${acc2.unsafeRead()}")\
  println("Performing transaction")\
  atomically { transfer(acc1, acc2, 50) }\
  println("Balance account 1: ${acc1.unsafeRead()}")\
  println("Balance account 2: ${acc2.unsafeRead()}")\
}<!--- KNIT example-stm-01.kt -->

This example shows a banking service moving money from one account to the other with [STM](index.md). Should the first account not have enough money we throw an exception. This code is guaranteed to never deadlock and to never produce an invalid state by committing after the read state has changed concurrently.

Note: A transaction that sees an invalid state (a [TVar](../-t-var/index.md) that was read has been changed concurrently) will restart and try again. This usually means we rerun the function entirely, therefore it is recommended to keep transactions small and to never use code that has side-effects inside. However no kotlin interface can actually keep you from doing side effects inside STM. Using side-effects such as access to resources, logging or network access comes with severe disadvantages:

<ul><li>Transactions may be aborted at any time so accessing resources may never trigger finalizers</li><li>Transactions may rerun an arbitrary amount of times before finishing and thus all effects will rerun.</li></ul>

##  Retrying manually

It is sometimes beneficial to manually abort the current transaction if, for example, an invalid state has been read. E.g. a [TQueue](../-t-queue/index.md) had no elements to read. The aborted transaction will automatically restart once any previously accessed variable has changed.

This is achieved by the primitive [retry](retry.md):

import arrow.fx.stm.atomically\
import arrow.fx.stm.TVar\
import arrow.fx.stm.STM\
import kotlinx.coroutines.runBlocking\
import kotlinx.coroutines.async\
import kotlinx.coroutines.delay\
\
//sampleStart\
fun STM.transfer(from: TVar&lt;Int&gt;, to: TVar&lt;Int&gt;, amount: Int): Unit {\
  withdraw(from, amount)\
  deposit(to, amount)\
}\
\
fun STM.deposit(acc: TVar&lt;Int&gt;, amount: Int): Unit {\
  val current = acc.read()\
  acc.write(current + amount)\
  // or the shorthand acc.modify { it + amount }\
}\
\
fun STM.withdraw(acc: TVar&lt;Int&gt;, amount: Int): Unit {\
  val current = acc.read()\
  if (current - amount &gt;= 0) acc.write(current + amount)\
  else retry() // we now retry if there is not enough money in the account\
  // this can also be achieved by using `check(current - amount &gt;= 0); acc.write(it + amount)`\
}\
//sampleEnd\
\
fun main(): Unit = runBlocking {\
  val acc1 = TVar.new(0)\
  val acc2 = TVar.new(300)\
  println("Balance account 1: ${acc1.unsafeRead()}")\
  println("Balance account 2: ${acc2.unsafeRead()}")\
  async {\
    println("Sending money - Searching")\
    delay(2000)\
    println("Sending money - Found some")\
    atomically { acc1.write(100_000_000) }\
  }\
  println("Performing transaction")\
  atomically {\
    println("Trying to transfer")\
    transfer(acc1, acc2, 50)\
  }\
  println("Balance account 1: ${acc1.unsafeRead()}")\
  println("Balance account 2: ${acc2.unsafeRead()}")\
}<!--- KNIT example-stm-02.kt -->

Here in this (silly) example we changed withdraw to use [retry](retry.md) and thus wait until enough money is in the account, which after a few seconds just happens to be the case.

[retry](retry.md) can be used to implement a lot of complex transactions and many datastructures like [TMVar](../-t-m-var/index.md) or [TQueue](../-t-queue/index.md) use to to great effect.

##  Branching with [orElse](or-else.md)

[orElse](or-else.md) is another important primitive which allows a user to detect if a branch called [retry](retry.md) and then use a fallback instead. If the fallback retries as well the whole transaction retries.

import kotlinx.coroutines.runBlocking\
import arrow.fx.stm.atomically\
import arrow.fx.stm.TVar\
import arrow.fx.stm.STM\
import arrow.fx.stm.stm\
\
//sampleStart\
fun STM.transaction(v: TVar&lt;Int&gt;): Int? =\
  stm {\
    val result = v.read()\
    check(result in 0..10)\
    result\
  } orElse { null }\
//sampleEnd\
\
fun main(): Unit = runBlocking {\
  val v = TVar.new(100)\
  println("Value is ${v.unsafeRead()}")\
  atomically { transaction(v) }\
    .also { println("Transaction returned $it") }\
  println("Set value to 5")\
  println("Value is ${v.unsafeRead()}")\
  atomically { v.write(5) }\
  atomically { transaction(v) }\
    .also { println("Transaction returned $it") }\
}<!--- KNIT example-stm-03.kt -->

This example uses [stm](../stm.md) which is a helper just like the stdlib function [suspend](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/index.html) to ease use of an infix function like [orElse](or-else.md). In this transaction, when the value inside the variable is not in the correct range, the transaction retries (due to [check](../check.md) calling [retry](retry.md)). If it is in the correct range it simply returns the value. [orElse](or-else.md) here intercepts a call to [retry](retry.md) and executes the alternative which simply returns null.

##  Exceptions

Throwing inside [STM](index.md) will let the exception bubble up to either a [catch](catch.md) handler or to [atomically](../atomically.md) which will rethrow it.

Note: Using try {...} catch (e: Exception) {...} is not encouraged because any state change inside try will not be undone when an exception occurs! The recommended way of catching exceptions is to use [catch](catch.md) which properly rolls back the transaction!

Further reading:

<ul><li>[Composable memory transactions, by Tim Harris, Simon Marlow, Simon Peyton Jones, and Maurice Herlihy, in ACM Conference on Principles and Practice of Parallel Programming 2005.](https://www.microsoft.com/en-us/research/publication/composable-memory-transactions/)</li></ul>

## Functions

| Name | Summary |
|---|---|
| [acquire](acquire.md) | [common]<br>open fun [TSemaphore](../-t-semaphore/index.md).[acquire](acquire.md)()<br>Acquire 1 permit from a [TSemaphore](../-t-semaphore/index.md).<br>[common]<br>open fun [TSemaphore](../-t-semaphore/index.md).[acquire](acquire.md)(n: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html))<br>Acquire [n](acquire.md) permit from a [TSemaphore](../-t-semaphore/index.md). |
| [available](available.md) | [common]<br>open fun [TSemaphore](../-t-semaphore/index.md).[available](available.md)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)<br>Returns the currently available number of permits in a [TSemaphore](../-t-semaphore/index.md). |
| [catch](catch.md) | [common]<br>abstract fun &lt;[A](catch.md)&gt; [catch](catch.md)(f: [STM](index.md).() -&gt; [A](catch.md), onError: [STM](index.md).([Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html)) -&gt; [A](catch.md)): [A](catch.md)<br>Run [f](catch.md) and handle any exception thrown with [onError](catch.md). |
| [flush](flush.md) | [common]<br>open fun &lt;[A](flush.md)&gt; [TQueue](../-t-queue/index.md)&lt;[A](flush.md)&gt;.[flush](flush.md)(): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[A](flush.md)&gt;<br>Drains all entries of a [TQueue](../-t-queue/index.md) into a single list. |
| [fold](fold.md) | [common]<br>open fun &lt;[A](fold.md), [B](fold.md)&gt; [TArray](../-t-array/index.md)&lt;[A](fold.md)&gt;.[fold](fold.md)(init: [B](fold.md), f: ([B](fold.md), [A](fold.md)) -&gt; [B](fold.md)): [B](fold.md)<br>Fold a [TArray](../-t-array/index.md) to a single value. |
| [get](get.md) | [common]<br>open operator fun &lt;[A](get.md)&gt; [TArray](../-t-array/index.md)&lt;[A](get.md)&gt;.[get](get.md)(i: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [A](get.md)<br>Read a variable from the [TArray](../-t-array/index.md).<br>[common]<br>open operator fun &lt;[K](get.md), [V](get.md)&gt; [TMap](../-t-map/index.md)&lt;[K](get.md), [V](get.md)&gt;.[get](get.md)(k: [K](get.md)): [V](get.md)?<br>Alias of [STM.lookup](lookup.md) |
| [insert](insert.md) | [common]<br>open fun &lt;[A](insert.md)&gt; [TSet](../-t-set/index.md)&lt;[A](insert.md)&gt;.[insert](insert.md)(a: [A](insert.md))<br>Adds an element to the set.<br>[common]<br>open fun &lt;[K](insert.md), [V](insert.md)&gt; [TMap](../-t-map/index.md)&lt;[K](insert.md), [V](insert.md)&gt;.[insert](insert.md)(k: [K](insert.md), v: [V](insert.md))<br>Add a key value pair to the map |
| [isEmpty](is-empty.md) | [common]<br>open fun &lt;[A](is-empty.md)&gt; [TMVar](../-t-m-var/index.md)&lt;[A](is-empty.md)&gt;.[isEmpty](is-empty.md)(): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Check if a [TMVar](../-t-m-var/index.md) is empty. This function never retries.<br>[common]<br>open fun &lt;[A](is-empty.md)&gt; [TQueue](../-t-queue/index.md)&lt;[A](is-empty.md)&gt;.[isEmpty](is-empty.md)(): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Check if a [TQueue](../-t-queue/index.md) is empty. |
| [isNotEmpty](is-not-empty.md) | [common]<br>open fun &lt;[A](is-not-empty.md)&gt; [TMVar](../-t-m-var/index.md)&lt;[A](is-not-empty.md)&gt;.[isNotEmpty](is-not-empty.md)(): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Check if a [TMVar](../-t-m-var/index.md) is not empty. This function never retries.<br>[common]<br>open fun &lt;[A](is-not-empty.md)&gt; [TQueue](../-t-queue/index.md)&lt;[A](is-not-empty.md)&gt;.[isNotEmpty](is-not-empty.md)(): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Check if a [TQueue](../-t-queue/index.md) is not empty. |
| [lookup](lookup.md) | [common]<br>open fun &lt;[K](lookup.md), [V](lookup.md)&gt; [TMap](../-t-map/index.md)&lt;[K](lookup.md), [V](lookup.md)&gt;.[lookup](lookup.md)(k: [K](lookup.md)): [V](lookup.md)?<br>Lookup a value at the specific key [k](lookup.md) |
| [member](member.md) | [common]<br>open fun &lt;[K](member.md), [V](member.md)&gt; [TMap](../-t-map/index.md)&lt;[K](member.md), [V](member.md)&gt;.[member](member.md)(k: [K](member.md)): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Check if a key [k](member.md) is in the map<br>[common]<br>open fun &lt;[A](member.md)&gt; [TSet](../-t-set/index.md)&lt;[A](member.md)&gt;.[member](member.md)(a: [A](member.md)): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Check if an element is already in the set |
| [modify](modify.md) | [common]<br>open fun &lt;[A](modify.md)&gt; [TVar](../-t-var/index.md)&lt;[A](modify.md)&gt;.[modify](modify.md)(f: ([A](modify.md)) -&gt; [A](modify.md))<br>Modify the value of a [TVar](../-t-var/index.md) |
| [newTVar](new-t-var.md) | [common]<br>open fun &lt;[A](new-t-var.md)&gt; [newTVar](new-t-var.md)(a: [A](new-t-var.md)): [TVar](../-t-var/index.md)&lt;[A](new-t-var.md)&gt;<br>Create a new [TVar](../-t-var/index.md) inside a transaction, because [TVar.new](../-t-var/-companion/new.md) is not possible inside [STM](index.md) transactions. |
| [orElse](or-else.md) | [common]<br>abstract infix fun &lt;[A](or-else.md)&gt; [STM](index.md).() -&gt; [A](or-else.md).[orElse](or-else.md)(other: [STM](index.md).() -&gt; [A](or-else.md)): [A](or-else.md)<br>Run the given transaction and fallback to the other one if the first one calls [retry](retry.md). |
| [peek](peek.md) | [common]<br>open fun &lt;[A](peek.md)&gt; [TQueue](../-t-queue/index.md)&lt;[A](peek.md)&gt;.[peek](peek.md)(): [A](peek.md)<br>Read the front element of a [TQueue](../-t-queue/index.md) without removing it. |
| [plusAssign](plus-assign.md) | [common]<br>open operator fun &lt;[K](plus-assign.md), [V](plus-assign.md)&gt; [TMap](../-t-map/index.md)&lt;[K](plus-assign.md), [V](plus-assign.md)&gt;.[plusAssign](plus-assign.md)(kv: [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[K](plus-assign.md), [V](plus-assign.md)&gt;)<br>Add a key value pair to the map<br>[common]<br>open operator fun &lt;[A](plus-assign.md)&gt; [TQueue](../-t-queue/index.md)&lt;[A](plus-assign.md)&gt;.[plusAssign](plus-assign.md)(a: [A](plus-assign.md))<br>Append an element to the [TQueue](../-t-queue/index.md). Alias for [STM.write](write.md).<br>[common]<br>open operator fun &lt;[A](plus-assign.md)&gt; [TSet](../-t-set/index.md)&lt;[A](plus-assign.md)&gt;.[plusAssign](plus-assign.md)(a: [A](plus-assign.md))<br>Adds an element to the set. Alias of [STM.insert](insert.md). |
| [put](put.md) | [common]<br>open fun &lt;[A](put.md)&gt; [TMVar](../-t-m-var/index.md)&lt;[A](put.md)&gt;.[put](put.md)(a: [A](put.md))<br>Put a value into an empty [TMVar](../-t-m-var/index.md). |
| [read](read.md) | [common]<br>open fun &lt;[A](read.md)&gt; [TMVar](../-t-m-var/index.md)&lt;[A](read.md)&gt;.[read](read.md)(): [A](read.md)<br>Read a value from a [TMVar](../-t-m-var/index.md) without removing it.<br>[common]<br>open fun &lt;[A](read.md)&gt; [TQueue](../-t-queue/index.md)&lt;[A](read.md)&gt;.[read](read.md)(): [A](read.md)<br>Remove the front element from the [TQueue](../-t-queue/index.md) or retry if the [TQueue](../-t-queue/index.md) is empty.<br>[common]<br>abstract fun &lt;[A](read.md)&gt; [TVar](../-t-var/index.md)&lt;[A](read.md)&gt;.[read](read.md)(): [A](read.md)<br>Read the value from a [TVar](../-t-var/index.md). |
| [release](release.md) | [common]<br>open fun [TSemaphore](../-t-semaphore/index.md).[release](release.md)()<br>Release a permit back to the [TSemaphore](../-t-semaphore/index.md).<br>[common]<br>open fun [TSemaphore](../-t-semaphore/index.md).[release](release.md)(n: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html))<br>Release [n](release.md) permits back to the [TSemaphore](../-t-semaphore/index.md). |
| [remove](remove.md) | [common]<br>open fun &lt;[K](remove.md), [V](remove.md)&gt; [TMap](../-t-map/index.md)&lt;[K](remove.md), [V](remove.md)&gt;.[remove](remove.md)(k: [K](remove.md))<br>Remove a key value pair from a map<br>[common]<br>open fun &lt;[A](remove.md)&gt; [TSet](../-t-set/index.md)&lt;[A](remove.md)&gt;.[remove](remove.md)(a: [A](remove.md))<br>Remove an element from the set. |
| [removeAll](remove-all.md) | [common]<br>open fun &lt;[A](remove-all.md)&gt; [TQueue](../-t-queue/index.md)&lt;[A](remove-all.md)&gt;.[removeAll](remove-all.md)(pred: ([A](remove-all.md)) -&gt; [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html))<br>Filter a [TQueue](../-t-queue/index.md), removing all elements for which [pred](remove-all.md) returns false. |
| [retry](retry.md) | [common]<br>abstract fun [retry](retry.md)(): [Nothing](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-nothing/index.html)<br>Abort and retry the current transaction. |
| [set](set.md) | [common]<br>open operator fun &lt;[A](set.md)&gt; [TArray](../-t-array/index.md)&lt;[A](set.md)&gt;.[set](set.md)(i: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), a: [A](set.md))<br>Set a variable in the [TArray](../-t-array/index.md).<br>[common]<br>open operator fun &lt;[K](set.md), [V](set.md)&gt; [TMap](../-t-map/index.md)&lt;[K](set.md), [V](set.md)&gt;.[set](set.md)(k: [K](set.md), v: [V](set.md))<br>Alias for [STM.insert](insert.md) |
| [size](size.md) | [common]<br>open fun &lt;[A](size.md)&gt; [TQueue](../-t-queue/index.md)&lt;[A](size.md)&gt;.[size](size.md)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)<br>Return the current number of elements in a [TQueue](../-t-queue/index.md) |
| [swap](swap.md) | [common]<br>open fun &lt;[A](swap.md)&gt; [TMVar](../-t-m-var/index.md)&lt;[A](swap.md)&gt;.[swap](swap.md)(a: [A](swap.md)): [A](swap.md)<br>Swap the content of a [TMVar](../-t-m-var/index.md) or retry if it is empty.<br>[common]<br>open fun &lt;[A](swap.md)&gt; [TVar](../-t-var/index.md)&lt;[A](swap.md)&gt;.[swap](swap.md)(a: [A](swap.md)): [A](swap.md)<br>Swap the content of the [TVar](../-t-var/index.md) |
| [take](take.md) | [common]<br>open fun &lt;[A](take.md)&gt; [TMVar](../-t-m-var/index.md)&lt;[A](take.md)&gt;.[take](take.md)(): [A](take.md)<br>Read the value from a [TMVar](../-t-m-var/index.md) and empty it. |
| [transform](transform.md) | [common]<br>open fun &lt;[A](transform.md)&gt; [TArray](../-t-array/index.md)&lt;[A](transform.md)&gt;.[transform](transform.md)(f: ([A](transform.md)) -&gt; [A](transform.md))<br>Modify each element in a [TArray](../-t-array/index.md) by applying [f](transform.md). |
| [tryAcquire](try-acquire.md) | [common]<br>open fun [TSemaphore](../-t-semaphore/index.md).[tryAcquire](try-acquire.md)(): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>open fun [TSemaphore](../-t-semaphore/index.md).[tryAcquire](try-acquire.md)(n: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Like [TSemaphore.acquire](acquire.md) except that it returns whether or not acquisition was successful. |
| [tryPeek](try-peek.md) | [common]<br>open fun &lt;[A](try-peek.md)&gt; [TQueue](../-t-queue/index.md)&lt;[A](try-peek.md)&gt;.[tryPeek](try-peek.md)(): [A](try-peek.md)?<br>Same as [TQueue.peek](peek.md) except it returns null if the [TQueue](../-t-queue/index.md) is empty. |
| [tryPut](try-put.md) | [common]<br>open fun &lt;[A](try-put.md)&gt; [TMVar](../-t-m-var/index.md)&lt;[A](try-put.md)&gt;.[tryPut](try-put.md)(a: [A](try-put.md)): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Same as [TMVar.put](put.md) except that it returns true or false if was successful or it retried. |
| [tryRead](try-read.md) | [common]<br>open fun &lt;[A](try-read.md)&gt; [TMVar](../-t-m-var/index.md)&lt;[A](try-read.md)&gt;.[tryRead](try-read.md)(): [A](try-read.md)?<br>Same as [TMVar.read](read.md) except that it returns null if the [TMVar](../-t-m-var/index.md) is empty and thus never retries.<br>[common]<br>open fun &lt;[A](try-read.md)&gt; [TQueue](../-t-queue/index.md)&lt;[A](try-read.md)&gt;.[tryRead](try-read.md)(): [A](try-read.md)?<br>Same as [TQueue.read](read.md) except it returns null if the [TQueue](../-t-queue/index.md) is empty. |
| [tryTake](try-take.md) | [common]<br>open fun &lt;[A](try-take.md)&gt; [TMVar](../-t-m-var/index.md)&lt;[A](try-take.md)&gt;.[tryTake](try-take.md)(): [A](try-take.md)?<br>Same as [TMVar.take](take.md) except it returns null if the [TMVar](../-t-m-var/index.md) is empty and thus never retries. |
| [update](update.md) | [common]<br>open fun &lt;[K](update.md), [V](update.md)&gt; [TMap](../-t-map/index.md)&lt;[K](update.md), [V](update.md)&gt;.[update](update.md)(k: [K](update.md), fn: ([V](update.md)) -&gt; [V](update.md))<br>Update a value at a key if it exists. |
| [write](write.md) | [common]<br>open fun &lt;[A](write.md)&gt; [TQueue](../-t-queue/index.md)&lt;[A](write.md)&gt;.[write](write.md)(a: [A](write.md))<br>Append an element to the [TQueue](../-t-queue/index.md).<br>[common]<br>abstract fun &lt;[A](write.md)&gt; [TVar](../-t-var/index.md)&lt;[A](write.md)&gt;.[write](write.md)(a: [A](write.md))<br>Set the value of a [TVar](../-t-var/index.md). |
| [writeFront](write-front.md) | [common]<br>open fun &lt;[A](write-front.md)&gt; [TQueue](../-t-queue/index.md)&lt;[A](write-front.md)&gt;.[writeFront](write-front.md)(a: [A](write-front.md))<br>Prepend an element to the [TQueue](../-t-queue/index.md). |

## Extensions

| Name | Summary |
|---|---|
| [check](../check.md) | [common]<br>fun [STM](index.md).[check](../check.md)(b: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html))<br>Retry if [b](../check.md) is false otherwise does nothing. |
| [newEmptyTMVar](../new-empty-t-m-var.md) | [common]<br>fun &lt;[A](../new-empty-t-m-var.md)&gt; [STM](index.md).[newEmptyTMVar](../new-empty-t-m-var.md)(): [TMVar](../-t-m-var/index.md)&lt;[A](../new-empty-t-m-var.md)&gt; |
| [newTArray](../new-t-array.md) | [common]<br>fun &lt;[A](../new-t-array.md)&gt; [STM](index.md).[newTArray](../new-t-array.md)(size: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), f: ([Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)) -&gt; [A](../new-t-array.md)): [TArray](../-t-array/index.md)&lt;[A](../new-t-array.md)&gt;<br>fun &lt;[A](../new-t-array.md)&gt; [STM](index.md).[newTArray](../new-t-array.md)(size: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), a: [A](../new-t-array.md)): [TArray](../-t-array/index.md)&lt;[A](../new-t-array.md)&gt;<br>fun &lt;[A](../new-t-array.md)&gt; [STM](index.md).[newTArray](../new-t-array.md)(vararg arr: [A](../new-t-array.md)): [TArray](../-t-array/index.md)&lt;[A](../new-t-array.md)&gt;<br>fun &lt;[A](../new-t-array.md)&gt; [STM](index.md).[newTArray](../new-t-array.md)(xs: [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;[A](../new-t-array.md)&gt;): [TArray](../-t-array/index.md)&lt;[A](../new-t-array.md)&gt; |
| [newTMap](../new-t-map.md) | [common]<br>fun &lt;[K](../new-t-map.md), [V](../new-t-map.md)&gt; [STM](index.md).[newTMap](../new-t-map.md)(fn: ([K](../new-t-map.md)) -&gt; [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [TMap](../-t-map/index.md)&lt;[K](../new-t-map.md), [V](../new-t-map.md)&gt;<br>fun &lt;[K](../new-t-map.md), [V](../new-t-map.md)&gt; [STM](index.md).[newTMap](../new-t-map.md)(): [TMap](../-t-map/index.md)&lt;[K](../new-t-map.md), [V](../new-t-map.md)&gt; |
| [newTMVar](../new-t-m-var.md) | [common]<br>fun &lt;[A](../new-t-m-var.md)&gt; [STM](index.md).[newTMVar](../new-t-m-var.md)(a: [A](../new-t-m-var.md)): [TMVar](../-t-m-var/index.md)&lt;[A](../new-t-m-var.md)&gt; |
| [newTQueue](../new-t-queue.md) | [common]<br>fun &lt;[A](../new-t-queue.md)&gt; [STM](index.md).[newTQueue](../new-t-queue.md)(): [TQueue](../-t-queue/index.md)&lt;[A](../new-t-queue.md)&gt; |
| [newTSem](../new-t-sem.md) | [common]<br>fun [STM](index.md).[newTSem](../new-t-sem.md)(initial: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [TSemaphore](../-t-semaphore/index.md) |
| [newTSet](../new-t-set.md) | [common]<br>fun &lt;[A](../new-t-set.md)&gt; [STM](index.md).[newTSet](../new-t-set.md)(fn: ([A](../new-t-set.md)) -&gt; [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [TSet](../-t-set/index.md)&lt;[A](../new-t-set.md)&gt;<br>fun &lt;[A](../new-t-set.md)&gt; [STM](index.md).[newTSet](../new-t-set.md)(): [TSet](../-t-set/index.md)&lt;[A](../new-t-set.md)&gt; |
