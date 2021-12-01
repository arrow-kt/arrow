//[arrow-core](../../index.md)/[arrow.core](index.md)/[lastOrNone](last-or-none.md)

# lastOrNone

[common]\
fun &lt;[T](last-or-none.md)&gt; [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;[T](last-or-none.md)&gt;.[lastOrNone](last-or-none.md)(): [Option](-option/index.md)&lt;[T](last-or-none.md)&gt;

Returns the last element as [Some(element)](-some/index.md), or [None](-none/index.md) if the iterable is empty.

[common]\
inline fun &lt;[T](last-or-none.md)&gt; [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;[T](last-or-none.md)&gt;.[lastOrNone](last-or-none.md)(predicate: ([T](last-or-none.md)) -&gt; [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)): [Option](-option/index.md)&lt;[T](last-or-none.md)&gt;

Returns the last element as [Some(element)](-some/index.md) matching the given [predicate](last-or-none.md), or [None](-none/index.md) if no such element was found.
