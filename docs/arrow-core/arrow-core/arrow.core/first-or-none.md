//[arrow-core](../../index.md)/[arrow.core](index.md)/[firstOrNone](first-or-none.md)

# firstOrNone

[common]\
fun &lt;[T](first-or-none.md)&gt; [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;[T](first-or-none.md)&gt;.[firstOrNone](first-or-none.md)(): [Option](-option/index.md)&lt;[T](first-or-none.md)&gt;

Returns the first element as [Some(element)](-some/index.md), or [None](-none/index.md) if the iterable is empty.

[common]\
inline fun &lt;[T](first-or-none.md)&gt; [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;[T](first-or-none.md)&gt;.[firstOrNone](first-or-none.md)(predicate: ([T](first-or-none.md)) -&gt; [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)): [Option](-option/index.md)&lt;[T](first-or-none.md)&gt;

Returns the first element as [Some(element)](-some/index.md) matching the given [predicate](first-or-none.md), or [None](-none/index.md) if element was not found.
