//[arrow-fx-coroutines](../../../../index.md)/[arrow.fx.coroutines](../../index.md)/[Schedule](../index.md)/[Companion](index.md)/[delayed](delayed.md)

# delayed

[common]\

@[JvmName](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.jvm/-jvm-name/index.html)(name = "delayedNanos")

fun &lt;[A](delayed.md)&gt; [delayed](delayed.md)(delaySchedule: [Schedule](../index.md)&lt;[A](delayed.md), [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)&gt;): [Schedule](../index.md)&lt;[A](delayed.md), [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)&gt;

Creates a Schedule that uses another Schedule to generate the delay of this schedule. Continues for as long as [delaySchedule](delayed.md) continues and adds the output of [delaySchedule](delayed.md) to the delay that [delaySchedule](delayed.md) produced. Also returns the full delay as output.

The Schedule [delaySchedule](delayed.md) is should specify the delay in nanoseconds.

A common use case is to define a unfolding schedule and use the result to change the delay. For an example see the implementation of [spaced](spaced.md), [linear](linear.md), [fibonacci](fibonacci.md) or [exponential](exponential.md)

[common]\

@[ExperimentalTime](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.time/-experimental-time/index.html)

@[JvmName](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.jvm/-jvm-name/index.html)(name = "delayedDuration")

fun &lt;[A](delayed.md)&gt; [delayed](delayed.md)(delaySchedule: [Schedule](../index.md)&lt;[A](delayed.md), [Duration](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.time/-duration/index.html)&gt;): [Schedule](../index.md)&lt;[A](delayed.md), [Duration](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.time/-duration/index.html)&gt;

Creates a Schedule that uses another Schedule to generate the delay of this schedule. Continues for as long as [delaySchedule](delayed.md) continues and adds the output of [delaySchedule](delayed.md) to the delay that [delaySchedule](delayed.md) produced. Also returns the full delay as output.

A common use case is to define a unfolding schedule and use the result to change the delay. For an example see the implementation of [spaced](spaced.md), [linear](linear.md), [fibonacci](fibonacci.md) or [exponential](exponential.md)
