//[arrow-fx-stm](../../index.md)/[arrow.fx.stm](index.md)

# Package arrow.fx.stm

## Types

| Name | Summary |
|---|---|
| [STM](-s-t-m/index.md) | [common]<br>interface [STM](-s-t-m/index.md)<br>Software transactional memory, or STM, is an abstraction for concurrent state modification. With [STM](-s-t-m/index.md) one can write code that concurrently accesses state and that can easily be composed without exposing details of how it ensures safety guarantees. Programs running within an [STM](-s-t-m/index.md) transaction will neither deadlock nor have race-conditions. |
| [TArray](-t-array/index.md) | [common]<br>data class [TArray](-t-array/index.md)&lt;[A](-t-array/index.md)&gt;<br>A [TArray](-t-array/index.md) is an array of transactional variables. |
| [TMap](-t-map/index.md) | [common]<br>data class [TMap](-t-map/index.md)&lt;[K](-t-map/index.md), [V](-t-map/index.md)&gt;<br>A [TMap](-t-map/index.md) is a concurrent transactional implementation of a key value hashmap. |
| [TMVar](-t-m-var/index.md) | [common]<br>data class [TMVar](-t-m-var/index.md)&lt;[A](-t-m-var/index.md)&gt;<br>A [TMVar](-t-m-var/index.md) is a mutable reference that can either be empty or hold a value. |
| [TQueue](-t-queue/index.md) | [common]<br>data class [TQueue](-t-queue/index.md)&lt;[A](-t-queue/index.md)&gt;<br>A [TQueue](-t-queue/index.md) is a transactional unbounded queue which can be written to and read from concurrently. |
| [TSemaphore](-t-semaphore/index.md) | [common]<br>data class [TSemaphore](-t-semaphore/index.md)<br>[TSemaphore](-t-semaphore/index.md) is the transactional Semaphore. |
| [TSet](-t-set/index.md) | [common]<br>data class [TSet](-t-set/index.md)&lt;[A](-t-set/index.md)&gt;<br>A [TSet](-t-set/index.md) is a concurrent transactional implementation of a hashset. |
| [TVar](-t-var/index.md) | [common]<br>class [TVar](-t-var/index.md)&lt;[A](-t-var/index.md)&gt;<br>A [TVar](-t-var/index.md) is a mutable reference that can only be (safely) accessed inside a [STM](-s-t-m/index.md) transaction. |

## Functions

| Name | Summary |
|---|---|
| [atomically](atomically.md) | [common]<br>suspend fun &lt;[A](atomically.md)&gt; [atomically](atomically.md)(f: [STM](-s-t-m/index.md).() -&gt; [A](atomically.md)): [A](atomically.md)<br>Run a transaction to completion. |
| [check](check.md) | [common]<br>fun [STM](-s-t-m/index.md).[check](check.md)(b: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html))<br>Retry if [b](check.md) is false otherwise does nothing. |
| [newEmptyTMVar](new-empty-t-m-var.md) | [common]<br>fun &lt;[A](new-empty-t-m-var.md)&gt; [STM](-s-t-m/index.md).[newEmptyTMVar](new-empty-t-m-var.md)(): [TMVar](-t-m-var/index.md)&lt;[A](new-empty-t-m-var.md)&gt; |
| [newTArray](new-t-array.md) | [common]<br>fun &lt;[A](new-t-array.md)&gt; [STM](-s-t-m/index.md).[newTArray](new-t-array.md)(vararg arr: [A](new-t-array.md)): [TArray](-t-array/index.md)&lt;[A](new-t-array.md)&gt;<br>fun &lt;[A](new-t-array.md)&gt; [STM](-s-t-m/index.md).[newTArray](new-t-array.md)(xs: [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;[A](new-t-array.md)&gt;): [TArray](-t-array/index.md)&lt;[A](new-t-array.md)&gt;<br>fun &lt;[A](new-t-array.md)&gt; [STM](-s-t-m/index.md).[newTArray](new-t-array.md)(size: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), a: [A](new-t-array.md)): [TArray](-t-array/index.md)&lt;[A](new-t-array.md)&gt;<br>fun &lt;[A](new-t-array.md)&gt; [STM](-s-t-m/index.md).[newTArray](new-t-array.md)(size: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), f: ([Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)) -&gt; [A](new-t-array.md)): [TArray](-t-array/index.md)&lt;[A](new-t-array.md)&gt; |
| [newTMap](new-t-map.md) | [common]<br>fun &lt;[K](new-t-map.md), [V](new-t-map.md)&gt; [STM](-s-t-m/index.md).[newTMap](new-t-map.md)(): [TMap](-t-map/index.md)&lt;[K](new-t-map.md), [V](new-t-map.md)&gt;<br>fun &lt;[K](new-t-map.md), [V](new-t-map.md)&gt; [STM](-s-t-m/index.md).[newTMap](new-t-map.md)(fn: ([K](new-t-map.md)) -&gt; [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [TMap](-t-map/index.md)&lt;[K](new-t-map.md), [V](new-t-map.md)&gt; |
| [newTMVar](new-t-m-var.md) | [common]<br>fun &lt;[A](new-t-m-var.md)&gt; [STM](-s-t-m/index.md).[newTMVar](new-t-m-var.md)(a: [A](new-t-m-var.md)): [TMVar](-t-m-var/index.md)&lt;[A](new-t-m-var.md)&gt; |
| [newTQueue](new-t-queue.md) | [common]<br>fun &lt;[A](new-t-queue.md)&gt; [STM](-s-t-m/index.md).[newTQueue](new-t-queue.md)(): [TQueue](-t-queue/index.md)&lt;[A](new-t-queue.md)&gt; |
| [newTSem](new-t-sem.md) | [common]<br>fun [STM](-s-t-m/index.md).[newTSem](new-t-sem.md)(initial: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [TSemaphore](-t-semaphore/index.md) |
| [newTSet](new-t-set.md) | [common]<br>fun &lt;[A](new-t-set.md)&gt; [STM](-s-t-m/index.md).[newTSet](new-t-set.md)(): [TSet](-t-set/index.md)&lt;[A](new-t-set.md)&gt;<br>fun &lt;[A](new-t-set.md)&gt; [STM](-s-t-m/index.md).[newTSet](new-t-set.md)(fn: ([A](new-t-set.md)) -&gt; [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [TSet](-t-set/index.md)&lt;[A](new-t-set.md)&gt; |
| [stm](stm.md) | [common]<br>inline fun &lt;[A](stm.md)&gt; [stm](stm.md)(noinline f: [STM](-s-t-m/index.md).() -&gt; [A](stm.md)): [STM](-s-t-m/index.md).() -&gt; [A](stm.md)<br>Helper to create stm blocks that can be run with [STM.orElse](-s-t-m/or-else.md) |
