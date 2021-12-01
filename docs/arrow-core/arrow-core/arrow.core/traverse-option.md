//[arrow-core](../../index.md)/[arrow.core](index.md)/[traverseOption](traverse-option.md)

# traverseOption

[common]\
inline fun &lt;[A](traverse-option.md), [B](traverse-option.md)&gt; [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;[A](traverse-option.md)&gt;.[traverseOption](traverse-option.md)(f: ([A](traverse-option.md)) -&gt; [Option](-option/index.md)&lt;[B](traverse-option.md)&gt;): [Option](-option/index.md)&lt;[List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[B](traverse-option.md)&gt;&gt;

inline fun &lt;[A](traverse-option.md), [B](traverse-option.md)&gt; [NonEmptyList](-non-empty-list/index.md)&lt;[A](traverse-option.md)&gt;.[traverseOption](traverse-option.md)(f: ([A](traverse-option.md)) -&gt; [Option](-option/index.md)&lt;[B](traverse-option.md)&gt;): [Option](-option/index.md)&lt;[NonEmptyList](-non-empty-list/index.md)&lt;[B](traverse-option.md)&gt;&gt;

fun &lt;[A](traverse-option.md), [B](traverse-option.md)&gt; [Sequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/-sequence/index.html)&lt;[A](traverse-option.md)&gt;.[traverseOption](traverse-option.md)(f: ([A](traverse-option.md)) -&gt; [Option](-option/index.md)&lt;[B](traverse-option.md)&gt;): [Option](-option/index.md)&lt;[Sequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/-sequence/index.html)&lt;[B](traverse-option.md)&gt;&gt;

inline fun &lt;[K](traverse-option.md), [A](traverse-option.md), [B](traverse-option.md)&gt; [Map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)&lt;[K](traverse-option.md), [A](traverse-option.md)&gt;.[traverseOption](traverse-option.md)(f: ([A](traverse-option.md)) -&gt; [Option](-option/index.md)&lt;[B](traverse-option.md)&gt;): [Option](-option/index.md)&lt;[Map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)&lt;[K](traverse-option.md), [B](traverse-option.md)&gt;&gt;
