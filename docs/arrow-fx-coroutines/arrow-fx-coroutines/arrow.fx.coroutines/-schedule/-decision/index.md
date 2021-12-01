//[arrow-fx-coroutines](../../../../index.md)/[arrow.fx.coroutines](../../index.md)/[Schedule](../index.md)/[Decision](index.md)

# Decision

[common]\
data class [Decision](index.md)&lt;out [A](index.md), out [B](index.md)&gt;(cont: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html), delayInNanos: [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html), state: [A](index.md), finish: [Eval](../../../../../arrow-core/arrow-core/arrow.core/-eval/index.md)&lt;[B](index.md)&gt;)

A single decision. Contains the decision to continue, the delay, the new state and the (lazy) result of a Schedule.

## Constructors

| | |
|---|---|
| [Decision](-decision.md) | [common]<br>fun &lt;out [A](index.md), out [B](index.md)&gt; [Decision](-decision.md)(cont: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html), delayInNanos: [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html), state: [A](index.md), finish: [Eval](../../../../../arrow-core/arrow-core/arrow.core/-eval/index.md)&lt;[B](index.md)&gt;) |

## Types

| Name | Summary |
|---|---|
| [Companion](-companion/index.md) | [common]<br>object [Companion](-companion/index.md) |

## Functions

| Name | Summary |
|---|---|
| [bimap](bimap.md) | [common]<br>fun &lt;[C](bimap.md), [D](bimap.md)&gt; [bimap](bimap.md)(f: ([A](index.md)) -&gt; [C](bimap.md), g: ([B](index.md)) -&gt; [D](bimap.md)): [Schedule.Decision](index.md)&lt;[C](bimap.md), [D](bimap.md)&gt; |
| [combine](combine.md) | [common]<br>@[ExperimentalTime](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.time/-experimental-time/index.html)<br>fun &lt;[C](combine.md), [D](combine.md), [E](combine.md)&gt; [combine](combine.md)(other: [Schedule.Decision](index.md)&lt;[C](combine.md), [D](combine.md)&gt;, f: ([Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html), [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)) -&gt; [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html), g: ([Duration](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.time/-duration/index.html), [Duration](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.time/-duration/index.html)) -&gt; [Duration](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.time/-duration/index.html), zip: ([B](index.md), [D](combine.md)) -&gt; [E](combine.md)): [Schedule.Decision](index.md)&lt;[Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[A](index.md), [C](combine.md)&gt;, [E](combine.md)&gt; |
| [combineNanos](combine-nanos.md) | [common]<br>fun &lt;[C](combine-nanos.md), [D](combine-nanos.md), [E](combine-nanos.md)&gt; [combineNanos](combine-nanos.md)(other: [Schedule.Decision](index.md)&lt;[C](combine-nanos.md), [D](combine-nanos.md)&gt;, f: ([Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html), [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)) -&gt; [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html), g: ([Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html), [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)) -&gt; [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html), zip: ([B](index.md), [D](combine-nanos.md)) -&gt; [E](combine-nanos.md)): [Schedule.Decision](index.md)&lt;[Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[A](index.md), [C](combine-nanos.md)&gt;, [E](combine-nanos.md)&gt; |
| [equals](equals.md) | [common]<br>open operator override fun [equals](equals.md)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [map](map.md) | [common]<br>fun &lt;[D](map.md)&gt; [map](map.md)(g: ([B](index.md)) -&gt; [D](map.md)): [Schedule.Decision](index.md)&lt;[A](index.md), [D](map.md)&gt; |
| [mapLeft](map-left.md) | [common]<br>fun &lt;[C](map-left.md)&gt; [mapLeft](map-left.md)(f: ([A](index.md)) -&gt; [C](map-left.md)): [Schedule.Decision](index.md)&lt;[C](map-left.md), [B](index.md)&gt; |
| [not](not.md) | [common]<br>operator fun [not](not.md)(): [Schedule.Decision](index.md)&lt;[A](index.md), [B](index.md)&gt; |

## Properties

| Name | Summary |
|---|---|
| [cont](cont.md) | [common]<br>val [cont](cont.md): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [delayInNanos](delay-in-nanos.md) | [common]<br>val [delayInNanos](delay-in-nanos.md): [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html) |
| [duration](duration.md) | [common]<br>@[ExperimentalTime](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.time/-experimental-time/index.html)<br>val [duration](duration.md): [Duration](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.time/-duration/index.html) |
| [finish](finish.md) | [common]<br>val [finish](finish.md): [Eval](../../../../../arrow-core/arrow-core/arrow.core/-eval/index.md)&lt;[B](index.md)&gt; |
| [state](state.md) | [common]<br>val [state](state.md): [A](index.md) |
