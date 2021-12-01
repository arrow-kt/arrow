//[arrow-meta](../../../index.md)/[arrow.meta](../index.md)/[Either](index.md)

# Either

[jvm]\
sealed class [Either](index.md)&lt;out [A](index.md), out [B](index.md)&gt;

## Types

| Name | Summary |
|---|---|
| [Left](-left/index.md) | [jvm]<br>data class [Left](-left/index.md)&lt;out [A](-left/index.md)&gt;(a: [A](-left/index.md)) : [Either](index.md)&lt;[A](-left/index.md), [Nothing](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-nothing/index.html)&gt; |
| [Right](-right/index.md) | [jvm]<br>data class [Right](-right/index.md)&lt;out [B](-right/index.md)&gt;(b: [B](-right/index.md)) : [Either](index.md)&lt;[Nothing](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-nothing/index.html), [B](-right/index.md)&gt; |

## Functions

| Name | Summary |
|---|---|
| [fold](fold.md) | [jvm]<br>inline fun &lt;[C](fold.md)&gt; [fold](fold.md)(ifLeft: ([A](index.md)) -&gt; [C](fold.md), ifRight: ([B](index.md)) -&gt; [C](fold.md)): [C](fold.md) |
| [map](map.md) | [jvm]<br>fun &lt;[C](map.md)&gt; [map](map.md)(f: ([B](index.md)) -&gt; [C](map.md)): [Either](index.md)&lt;[A](index.md), [C](map.md)&gt; |

## Inheritors

| Name |
|---|
| [Either](-left/index.md) |
| [Either](-right/index.md) |

## Extensions

| Name | Summary |
|---|---|
| [flatMap](../flat-map.md) | [jvm]<br>inline fun &lt;[A](../flat-map.md), [B](../flat-map.md), [C](../flat-map.md)&gt; [Either](index.md)&lt;[A](../flat-map.md), [B](../flat-map.md)&gt;.[flatMap](../flat-map.md)(f: ([B](../flat-map.md)) -&gt; [Either](index.md)&lt;[A](../flat-map.md), [C](../flat-map.md)&gt;): [Either](index.md)&lt;[A](../flat-map.md), [C](../flat-map.md)&gt; |
