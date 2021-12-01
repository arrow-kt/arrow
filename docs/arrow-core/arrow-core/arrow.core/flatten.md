//[arrow-core](../../index.md)/[arrow.core](index.md)/[flatten](flatten.md)

# flatten

[common]\
fun &lt;[A](flatten.md), [B](flatten.md)&gt; [Either](-either/index.md)&lt;[A](flatten.md), [Either](-either/index.md)&lt;[A](flatten.md), [B](flatten.md)&gt;&gt;.[flatten](flatten.md)(): [Either](-either/index.md)&lt;[A](flatten.md), [B](flatten.md)&gt;

inline fun &lt;[A](flatten.md), [B](flatten.md)&gt; [Ior](-ior/index.md)&lt;[A](flatten.md), [Ior](-ior/index.md)&lt;[A](flatten.md), [B](flatten.md)&gt;&gt;.[flatten](flatten.md)(SA: [Semigroup](../arrow.typeclasses/-semigroup/index.md)&lt;[A](flatten.md)&gt;): [Ior](-ior/index.md)&lt;[A](flatten.md), [B](flatten.md)&gt;

fun &lt;[A](flatten.md)&gt; [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;[Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;[A](flatten.md)&gt;&gt;.[flatten](flatten.md)(): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[A](flatten.md)&gt;

fun &lt;[A](flatten.md)&gt; [NonEmptyList](-non-empty-list/index.md)&lt;[NonEmptyList](-non-empty-list/index.md)&lt;[A](flatten.md)&gt;&gt;.[flatten](flatten.md)(): [NonEmptyList](-non-empty-list/index.md)&lt;[A](flatten.md)&gt;

fun &lt;[A](flatten.md)&gt; [Option](-option/index.md)&lt;[Option](-option/index.md)&lt;[A](flatten.md)&gt;&gt;.[flatten](flatten.md)(): [Option](-option/index.md)&lt;[A](flatten.md)&gt;

fun &lt;[A](flatten.md)&gt; [Sequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/-sequence/index.html)&lt;[Sequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/-sequence/index.html)&lt;[A](flatten.md)&gt;&gt;.[flatten](flatten.md)(): [Sequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/-sequence/index.html)&lt;[A](flatten.md)&gt;
