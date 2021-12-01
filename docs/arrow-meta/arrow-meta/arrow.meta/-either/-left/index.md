//[arrow-meta](../../../../index.md)/[arrow.meta](../../index.md)/[Either](../index.md)/[Left](index.md)

# Left

[jvm]\
data class [Left](index.md)&lt;out [A](index.md)&gt;(a: [A](index.md)) : [Either](../index.md)&lt;[A](index.md), [Nothing](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-nothing/index.html)&gt;

## Functions

| Name | Summary |
|---|---|
| [fold](index.md#1556445838%2FFunctions%2F-35121544) | [jvm]<br>inline fun &lt;[C](index.md#1556445838%2FFunctions%2F-35121544)&gt; [fold](index.md#1556445838%2FFunctions%2F-35121544)(ifLeft: ([A](index.md)) -&gt; [C](index.md#1556445838%2FFunctions%2F-35121544), ifRight: ([Nothing](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-nothing/index.html)) -&gt; [C](index.md#1556445838%2FFunctions%2F-35121544)): [C](index.md#1556445838%2FFunctions%2F-35121544) |
| [map](index.md#1088427248%2FFunctions%2F-35121544) | [jvm]<br>fun &lt;[C](index.md#1088427248%2FFunctions%2F-35121544)&gt; [map](index.md#1088427248%2FFunctions%2F-35121544)(f: ([Nothing](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-nothing/index.html)) -&gt; [C](index.md#1088427248%2FFunctions%2F-35121544)): [Either](../index.md)&lt;[A](index.md), [C](index.md#1088427248%2FFunctions%2F-35121544)&gt; |

## Properties

| Name | Summary |
|---|---|
| [a](a.md) | [jvm]<br>val [a](a.md): [A](index.md) |
