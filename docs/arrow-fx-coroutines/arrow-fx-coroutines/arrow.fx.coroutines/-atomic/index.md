//[arrow-fx-coroutines](../../../index.md)/[arrow.fx.coroutines](../index.md)/[Atomic](index.md)

# Atomic

[common]\
interface [Atomic](index.md)&lt;[A](index.md)&gt;

An [Atomic](index.md) with an initial value of [A](index.md).

[Atomic](index.md) wraps atomic, so that you can also use it on a top-level function or pass it around. In other languages this data type is also known as Ref, IORef or Concurrent safe Reference. So in case you don't need to pass around an atomic reference, or use it in top-level functions it's advised to use atomic from Atomic Fu directly.

import arrow.fx.coroutines.*\
\
suspend fun main() {\
  val count = Atomic(0)\
\
  (0 until 20_000).parTraverse {\
    count.update(Int::inc)\
  }\
  println(count.get())\
}<!--- KNIT example-atomic-01.kt -->

[Atomic](index.md) also offers some other interesting operators such as [modify](modify.md), [tryUpdate](try-update.md), [access](access.md)&[lens](lens.md).

## Types

| Name | Summary |
|---|---|
| [Companion](-companion/index.md) | [common]<br>object [Companion](-companion/index.md) |

## Functions

| Name | Summary |
|---|---|
| [access](access.md) | [common]<br>abstract suspend fun [access](access.md)(): [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[A](index.md), suspend ([A](index.md)) -&gt; [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)&gt;<br>Obtains a snapshot of the current value, and a setter for updating it. |
| [get](get.md) | [common]<br>abstract suspend fun [get](get.md)(): [A](index.md)<br>Obtains the current value. Since [AtomicRef](../../../../arrow-continuations/arrow-continuations/arrow.continuations.generic/-atomic-ref/index.md) is always guaranteed to have a value, the returned action completes immediately after being bound. |
| [getAndSet](get-and-set.md) | [common]<br>abstract suspend fun [getAndSet](get-and-set.md)(a: [A](index.md)): [A](index.md)<br>Replaces the current value with [a](get-and-set.md), returning the *old* value. |
| [getAndUpdate](get-and-update.md) | [common]<br>abstract suspend fun [getAndUpdate](get-and-update.md)(f: ([A](index.md)) -&gt; [A](index.md)): [A](index.md)<br>Modifies the current value using the supplied update function and returns the *old* value. |
| [lens](lens.md) | [common]<br>open fun &lt;[B](lens.md)&gt; [lens](lens.md)(get: ([A](index.md)) -&gt; [B](lens.md), set: ([A](index.md), [B](lens.md)) -&gt; [A](index.md)): [Atomic](index.md)&lt;[B](lens.md)&gt;<br>Creates an [AtomicRef](../../../../arrow-continuations/arrow-continuations/arrow.continuations.generic/-atomic-ref/index.md) for [B](lens.md) based on provided a [get](lens.md) and [set](lens.md) operation. |
| [modify](modify.md) | [common]<br>abstract suspend fun &lt;[B](modify.md)&gt; [modify](modify.md)(f: ([A](index.md)) -&gt; [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[A](index.md), [B](modify.md)&gt;): [B](modify.md)<br>Modify allows to inspect the state [A](index.md) of the [AtomicRef](../../../../arrow-continuations/arrow-continuations/arrow.continuations.generic/-atomic-ref/index.md), update it and extract a different state [B](modify.md). |
| [modifyGet](modify-get.md) | [common]<br>abstract suspend fun &lt;[B](modify-get.md)&gt; [modifyGet](modify-get.md)(f: ([A](index.md)) -&gt; [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[A](index.md), [B](modify-get.md)&gt;): [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[A](index.md), [B](modify-get.md)&gt;<br>ModifyGet allows to inspect state [A](index.md), update it and extract a different state [B](modify-get.md). In contrast to [modify](modify.md), it returns a [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html) of the updated state [A](index.md) and the extracted state [B](modify-get.md). |
| [set](set.md) | [common]<br>abstract suspend fun [set](set.md)(a: [A](index.md))<br>Sets the current value to [a](set.md). The returned action completes after the reference has been successfully set. |
| [setAndGet](set-and-get.md) | [common]<br>abstract suspend fun [setAndGet](set-and-get.md)(a: [A](index.md)): [A](index.md)<br>Replaces the current value with [a](set-and-get.md), returning the *new* value. |
| [tryModify](try-modify.md) | [common]<br>abstract suspend fun &lt;[B](try-modify.md)&gt; [tryModify](try-modify.md)(f: ([A](index.md)) -&gt; [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[A](index.md), [B](try-modify.md)&gt;): [B](try-modify.md)?<br>Attempts to inspect the state, uptade it, and extract a different state. |
| [tryUpdate](try-update.md) | [common]<br>abstract suspend fun [tryUpdate](try-update.md)(f: ([A](index.md)) -&gt; [A](index.md)): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Attempts to modify the current value once, in contrast to [update](update.md) which calls [f](try-update.md) until it succeeds. |
| [update](update.md) | [common]<br>abstract suspend fun [update](update.md)(f: ([A](index.md)) -&gt; [A](index.md))<br>Updates the current value using the supplied function [f](update.md). |
| [updateAndGet](update-and-get.md) | [common]<br>abstract suspend fun [updateAndGet](update-and-get.md)(f: ([A](index.md)) -&gt; [A](index.md)): [A](index.md)<br>Modifies the current value using the supplied update function and returns the *new* value. |
