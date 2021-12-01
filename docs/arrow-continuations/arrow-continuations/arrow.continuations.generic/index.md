//[arrow-continuations](../../index.md)/[arrow.continuations.generic](index.md)

# Package arrow.continuations.generic

## Types

| Name | Summary |
|---|---|
| [AtomicRef](-atomic-ref/index.md) | [common, js, native, jvm]<br>[common, js, native]<br>class [AtomicRef](-atomic-ref/index.md)&lt;[V](-atomic-ref/index.md)&gt;(initialValue: [V](-atomic-ref/index.md))<br>[jvm]<br>typealias [AtomicRef](-atomic-ref/index.md) = [AtomicReference](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/atomic/AtomicReference.html)&lt;[V](-atomic-ref/index.md)&gt; |
| [ControlThrowable](-control-throwable/index.md) | [common, native]<br>[common, native]<br>open class [ControlThrowable](-control-throwable/index.md) : [Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html)<br>A [Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html) class intended for control flow. Instance of [ControlThrowable](-control-throwable/index.md) should **not** be caught, and arrow.core.NonFatal does not catch this [Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html). Thus by extension Either.catch and Validated.catch also don't catch [ControlThrowable](-control-throwable/index.md).<br>[js, jvm]<br>[js, jvm]<br>open class [ControlThrowable](-control-throwable/index.md) : [Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html)<br>A [Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html) class intended for control flow. Instance of ControlThrowable.kt should **not** be caught, and arrow.core.NonFatal does not catch this [Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html). Thus by extension Either.catch and Validated.catch also don't catch ControlThrowable.kt. |
| [DelimitedContinuation](-delimited-continuation/index.md) | [common]<br>interface [DelimitedContinuation](-delimited-continuation/index.md)&lt;[A](-delimited-continuation/index.md), [R](-delimited-continuation/index.md)&gt;<br>Base interface for a continuation |
| [DelimitedScope](-delimited-scope/index.md) | [common]<br>interface [DelimitedScope](-delimited-scope/index.md)&lt;[R](-delimited-scope/index.md)&gt;<br>Base interface for our scope. |
| [RestrictedScope](-restricted-scope/index.md) | [common]<br>interface [RestrictedScope](-restricted-scope/index.md)&lt;[R](-restricted-scope/index.md)&gt; : [DelimitedScope](-delimited-scope/index.md)&lt;[R](-restricted-scope/index.md)&gt; |
| [ShortCircuit](-short-circuit/index.md) | [common]<br>class [ShortCircuit](-short-circuit/index.md) : [ControlThrowable](-control-throwable/index.md) |
| [SuspendedScope](-suspended-scope/index.md) | [common]<br>interface [SuspendedScope](-suspended-scope/index.md)&lt;[R](-suspended-scope/index.md)&gt; : [DelimitedScope](-delimited-scope/index.md)&lt;[R](-suspended-scope/index.md)&gt; |

## Functions

| Name | Summary |
|---|---|
| [getAndUpdate](get-and-update.md) | [common]<br>inline fun &lt;[V](get-and-update.md)&gt; [AtomicRef](-atomic-ref/index.md)&lt;[V](get-and-update.md)&gt;.[getAndUpdate](get-and-update.md)(function: ([V](get-and-update.md)) -&gt; [V](get-and-update.md)): [V](get-and-update.md)<br>Updates variable atomically using the specified [function](get-and-update.md) of its value and returns its old value. |
| [loop](loop.md) | [common]<br>inline fun &lt;[V](loop.md)&gt; [AtomicRef](-atomic-ref/index.md)&lt;[V](loop.md)&gt;.[loop](loop.md)(action: ([V](loop.md)) -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)): [Nothing](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-nothing/index.html)<br>Infinite loop that reads this atomic variable and performs the specified [action](loop.md) on its value. |
| [update](update.md) | [common]<br>inline fun &lt;[V](update.md)&gt; [AtomicRef](-atomic-ref/index.md)&lt;[V](update.md)&gt;.[update](update.md)(function: ([V](update.md)) -&gt; [V](update.md)) |
| [updateAndGet](update-and-get.md) | [common]<br>inline fun &lt;[V](update-and-get.md)&gt; [AtomicRef](-atomic-ref/index.md)&lt;[V](update-and-get.md)&gt;.[updateAndGet](update-and-get.md)(function: ([V](update-and-get.md)) -&gt; [V](update-and-get.md)): [V](update-and-get.md)<br>Updates variable atomically using the specified [function](update-and-get.md) of its value and returns its new value. |
