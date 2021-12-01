//[arrow-meta](../../../../index.md)/[arrow.meta](../../index.md)/[Either](../index.md)/[Right](index.md)

# Right

[jvm]\
data class [Right](index.md)&lt;out [B](index.md)&gt;(b: [B](index.md)) : [Either](../index.md)&lt;[Nothing](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-nothing/index.html), [B](index.md)&gt;

## Functions

| Name | Summary |
|---|---|
| [fold](index.md#-123914220%2FFunctions%2F-35121544) | [jvm]<br>inline fun &lt;[C](index.md#-123914220%2FFunctions%2F-35121544)&gt; [fold](index.md#-123914220%2FFunctions%2F-35121544)(ifLeft: ([Nothing](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-nothing/index.html)) -&gt; [C](index.md#-123914220%2FFunctions%2F-35121544), ifRight: ([B](index.md)) -&gt; [C](index.md#-123914220%2FFunctions%2F-35121544)): [C](index.md#-123914220%2FFunctions%2F-35121544) |
| [map](../map.md) | [jvm]<br>fun &lt;[C](../map.md)&gt; [map](../map.md)(f: ([B](index.md)) -&gt; [C](../map.md)): [Either](../index.md)&lt;[Nothing](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-nothing/index.html), [C](../map.md)&gt; |

## Properties

| Name | Summary |
|---|---|
| [b](b.md) | [jvm]<br>val [b](b.md): [B](index.md) |
