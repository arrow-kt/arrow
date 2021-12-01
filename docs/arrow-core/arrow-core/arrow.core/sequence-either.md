//[arrow-core](../../index.md)/[arrow.core](index.md)/[sequenceEither](sequence-either.md)

# sequenceEither

[common]\
fun &lt;[A](sequence-either.md), [B](sequence-either.md), [C](sequence-either.md)&gt; [Ior](-ior/index.md)&lt;[A](sequence-either.md), [Either](-either/index.md)&lt;[B](sequence-either.md), [C](sequence-either.md)&gt;&gt;.[sequenceEither](sequence-either.md)(): [Either](-either/index.md)&lt;[B](sequence-either.md), [Ior](-ior/index.md)&lt;[A](sequence-either.md), [C](sequence-either.md)&gt;&gt;

fun &lt;[E](sequence-either.md), [A](sequence-either.md)&gt; [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;[Either](-either/index.md)&lt;[E](sequence-either.md), [A](sequence-either.md)&gt;&gt;.[sequenceEither](sequence-either.md)(): [Either](-either/index.md)&lt;[E](sequence-either.md), [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[A](sequence-either.md)&gt;&gt;

fun &lt;[E](sequence-either.md), [A](sequence-either.md)&gt; [NonEmptyList](-non-empty-list/index.md)&lt;[Either](-either/index.md)&lt;[E](sequence-either.md), [A](sequence-either.md)&gt;&gt;.[sequenceEither](sequence-either.md)(): [Either](-either/index.md)&lt;[E](sequence-either.md), [NonEmptyList](-non-empty-list/index.md)&lt;[A](sequence-either.md)&gt;&gt;

fun &lt;[A](sequence-either.md), [B](sequence-either.md)&gt; [Option](-option/index.md)&lt;[Either](-either/index.md)&lt;[A](sequence-either.md), [B](sequence-either.md)&gt;&gt;.[sequenceEither](sequence-either.md)(): [Either](-either/index.md)&lt;[A](sequence-either.md), [Option](-option/index.md)&lt;[B](sequence-either.md)&gt;&gt;

fun &lt;[E](sequence-either.md), [A](sequence-either.md)&gt; [Sequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/-sequence/index.html)&lt;[Either](-either/index.md)&lt;[E](sequence-either.md), [A](sequence-either.md)&gt;&gt;.[sequenceEither](sequence-either.md)(): [Either](-either/index.md)&lt;[E](sequence-either.md), [Sequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/-sequence/index.html)&lt;[A](sequence-either.md)&gt;&gt;

fun &lt;[E](sequence-either.md), [A](sequence-either.md), [B](sequence-either.md)&gt; [Validated](-validated/index.md)&lt;[A](sequence-either.md), [Either](-either/index.md)&lt;[E](sequence-either.md), [B](sequence-either.md)&gt;&gt;.[sequenceEither](sequence-either.md)(): [Either](-either/index.md)&lt;[E](sequence-either.md), [Validated](-validated/index.md)&lt;[A](sequence-either.md), [B](sequence-either.md)&gt;&gt;

fun &lt;[K](sequence-either.md), [E](sequence-either.md), [A](sequence-either.md)&gt; [Map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)&lt;[K](sequence-either.md), [Either](-either/index.md)&lt;[E](sequence-either.md), [A](sequence-either.md)&gt;&gt;.[sequenceEither](sequence-either.md)(): [Either](-either/index.md)&lt;[E](sequence-either.md), [Map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)&lt;[K](sequence-either.md), [A](sequence-either.md)&gt;&gt;
