//[arrow-core](../../../../index.md)/[arrow.core](../../index.md)/[Validated](../index.md)/[Companion](index.md)/[fromNullable](from-nullable.md)

# fromNullable

[common]\

@[JvmStatic](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.jvm/-jvm-static/index.html)

inline fun &lt;[E](from-nullable.md), [A](from-nullable.md)&gt; [fromNullable](from-nullable.md)(value: [A](from-nullable.md)?, ifNull: () -&gt; [E](from-nullable.md)): [Validated](../index.md)&lt;[E](from-nullable.md), [A](from-nullable.md)&gt;

Converts a nullable A? to a Validated&lt;E, A&gt;, where the provided ifNull output value is returned as [Invalid](../-invalid/index.md) when the specified value is null.
