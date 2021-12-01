//[arrow-core](../../index.md)/[arrow.core](index.md)/[singleOrNone](single-or-none.md)

# singleOrNone

[common]\
fun &lt;[T](single-or-none.md)&gt; [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;[T](single-or-none.md)&gt;.[singleOrNone](single-or-none.md)(): [Option](-option/index.md)&lt;[T](single-or-none.md)&gt;

Returns single element as [Some(element)](-some/index.md), or [None](-none/index.md) if the iterable is empty or has more than one element.

[common]\
inline fun &lt;[T](single-or-none.md)&gt; [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;[T](single-or-none.md)&gt;.[singleOrNone](single-or-none.md)(predicate: ([T](single-or-none.md)) -&gt; [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)): [Option](-option/index.md)&lt;[T](single-or-none.md)&gt;

Returns the single element as [Some(element)](-some/index.md) matching the given [predicate](single-or-none.md), or [None](-none/index.md) if element was not found or more than one element was found.
