//[arrow-continuations](../../../index.md)/[arrow.continuations.generic](../index.md)/[AtomicRef](index.md)

# AtomicRef

[common, js, native]\
class [AtomicRef](index.md)&lt;[V](index.md)&gt;(initialValue: [V](index.md))

[jvm]\
typealias [AtomicRef](index.md) = [AtomicReference](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/atomic/AtomicReference.html)&lt;[V](index.md)&gt;

## Functions

| Name | Summary |
|---|---|
| [compareAndSet](compare-and-set.md) | [common, js, native]<br>[common, js, native]<br>fun [compareAndSet](compare-and-set.md)(expected: [V](index.md), new: [V](index.md)): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Compare current value with expected and set to new if they're the same. Note, 'compare' is checking the actual object id, not 'equals'. |
| [get](get.md) | [common, js, native]<br>[common, js, native]<br>fun [get](get.md)(): [V](index.md) |
| [getAndSet](get-and-set.md) | [common, js, native]<br>[common, js, native]<br>fun [getAndSet](get-and-set.md)(value: [V](index.md)): [V](index.md) |
| [set](set.md) | [common, js, native]<br>[common, js, native]<br>fun [set](set.md)(value: [V](index.md)) |

## Extensions

| Name | Summary |
|---|---|
| [getAndUpdate](../get-and-update.md) | [common]<br>inline fun &lt;[V](../get-and-update.md)&gt; [AtomicRef](index.md)&lt;[V](../get-and-update.md)&gt;.[getAndUpdate](../get-and-update.md)(function: ([V](../get-and-update.md)) -&gt; [V](../get-and-update.md)): [V](../get-and-update.md)<br>Updates variable atomically using the specified [function](../get-and-update.md) of its value and returns its old value. |
| [loop](../loop.md) | [common]<br>inline fun &lt;[V](../loop.md)&gt; [AtomicRef](index.md)&lt;[V](../loop.md)&gt;.[loop](../loop.md)(action: ([V](../loop.md)) -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)): [Nothing](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-nothing/index.html)<br>Infinite loop that reads this atomic variable and performs the specified [action](../loop.md) on its value. |
| [update](../update.md) | [common]<br>inline fun &lt;[V](../update.md)&gt; [AtomicRef](index.md)&lt;[V](../update.md)&gt;.[update](../update.md)(function: ([V](../update.md)) -&gt; [V](../update.md)) |
| [updateAndGet](../update-and-get.md) | [common]<br>inline fun &lt;[V](../update-and-get.md)&gt; [AtomicRef](index.md)&lt;[V](../update-and-get.md)&gt;.[updateAndGet](../update-and-get.md)(function: ([V](../update-and-get.md)) -&gt; [V](../update-and-get.md)): [V](../update-and-get.md)<br>Updates variable atomically using the specified [function](../update-and-get.md) of its value and returns its new value. |
