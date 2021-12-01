//[arrow-core](../../../../index.md)/[arrow.core](../../index.md)/[Validated](../index.md)/[Companion](index.md)/[fromOption](from-option.md)

# fromOption

[common]\

@[JvmStatic](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.jvm/-jvm-static/index.html)

inline fun &lt;[E](from-option.md), [A](from-option.md)&gt; [fromOption](from-option.md)(o: [Option](../../-option/index.md)&lt;[A](from-option.md)&gt;, ifNone: () -&gt; [E](from-option.md)): [Validated](../index.md)&lt;[E](from-option.md), [A](from-option.md)&gt;

Converts an Option&lt;A&gt; to a Validated&lt;E, A&gt;, where the provided ifNone output value is returned as [Invalid](../-invalid/index.md) when the specified Option is None.
