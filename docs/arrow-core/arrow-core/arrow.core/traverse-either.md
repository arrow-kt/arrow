//[arrow-core](../../index.md)/[arrow.core](index.md)/[traverseEither](traverse-either.md)

# traverseEither

[common]\
inline fun &lt;[E](traverse-either.md), [A](traverse-either.md), [B](traverse-either.md)&gt; [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;[A](traverse-either.md)&gt;.[traverseEither](traverse-either.md)(f: ([A](traverse-either.md)) -&gt; [Either](-either/index.md)&lt;[E](traverse-either.md), [B](traverse-either.md)&gt;): [Either](-either/index.md)&lt;[E](traverse-either.md), [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[B](traverse-either.md)&gt;&gt;

inline fun &lt;[E](traverse-either.md), [A](traverse-either.md), [B](traverse-either.md)&gt; [NonEmptyList](-non-empty-list/index.md)&lt;[A](traverse-either.md)&gt;.[traverseEither](traverse-either.md)(f: ([A](traverse-either.md)) -&gt; [Either](-either/index.md)&lt;[E](traverse-either.md), [B](traverse-either.md)&gt;): [Either](-either/index.md)&lt;[E](traverse-either.md), [NonEmptyList](-non-empty-list/index.md)&lt;[B](traverse-either.md)&gt;&gt;

fun &lt;[E](traverse-either.md), [A](traverse-either.md), [B](traverse-either.md)&gt; [Sequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/-sequence/index.html)&lt;[A](traverse-either.md)&gt;.[traverseEither](traverse-either.md)(f: ([A](traverse-either.md)) -&gt; [Either](-either/index.md)&lt;[E](traverse-either.md), [B](traverse-either.md)&gt;): [Either](-either/index.md)&lt;[E](traverse-either.md), [Sequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/-sequence/index.html)&lt;[B](traverse-either.md)&gt;&gt;

inline fun &lt;[K](traverse-either.md), [E](traverse-either.md), [A](traverse-either.md), [B](traverse-either.md)&gt; [Map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)&lt;[K](traverse-either.md), [A](traverse-either.md)&gt;.[traverseEither](traverse-either.md)(f: ([A](traverse-either.md)) -&gt; [Either](-either/index.md)&lt;[E](traverse-either.md), [B](traverse-either.md)&gt;): [Either](-either/index.md)&lt;[E](traverse-either.md), [Map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)&lt;[K](traverse-either.md), [B](traverse-either.md)&gt;&gt;
