//[arrow-fx-coroutines](../../../index.md)/[arrow.fx.coroutines](../index.md)/[Race3](index.md)

# Race3

[common]\
sealed class [Race3](index.md)&lt;out [A](index.md), out [B](index.md), out [C](index.md)&gt;

## Types

| Name | Summary |
|---|---|
| [First](-first/index.md) | [common]<br>data class [First](-first/index.md)&lt;[A](-first/index.md)&gt;(winner: [A](-first/index.md)) : [Race3](index.md)&lt;[A](-first/index.md), [Nothing](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-nothing/index.html), [Nothing](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-nothing/index.html)&gt; |
| [Second](-second/index.md) | [common]<br>data class [Second](-second/index.md)&lt;[B](-second/index.md)&gt;(winner: [B](-second/index.md)) : [Race3](index.md)&lt;[Nothing](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-nothing/index.html), [B](-second/index.md), [Nothing](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-nothing/index.html)&gt; |
| [Third](-third/index.md) | [common]<br>data class [Third](-third/index.md)&lt;[C](-third/index.md)&gt;(winner: [C](-third/index.md)) : [Race3](index.md)&lt;[Nothing](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-nothing/index.html), [Nothing](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-nothing/index.html), [C](-third/index.md)&gt; |

## Functions

| Name | Summary |
|---|---|
| [fold](fold.md) | [common]<br>inline fun &lt;[D](fold.md)&gt; [fold](fold.md)(ifA: ([A](index.md)) -&gt; [D](fold.md), ifB: ([B](index.md)) -&gt; [D](fold.md), ifC: ([C](index.md)) -&gt; [D](fold.md)): [D](fold.md) |

## Inheritors

| Name |
|---|
| [Race3](-first/index.md) |
| [Race3](-second/index.md) |
| [Race3](-third/index.md) |
