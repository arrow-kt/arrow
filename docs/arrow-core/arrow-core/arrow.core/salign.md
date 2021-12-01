//[arrow-core](../../index.md)/[arrow.core](index.md)/[salign](salign.md)

# salign

[common]\
fun &lt;[A](salign.md)&gt; [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;[A](salign.md)&gt;.[salign](salign.md)(SG: [Semigroup](../arrow.typeclasses/-semigroup/index.md)&lt;[A](salign.md)&gt;, other: [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;[A](salign.md)&gt;): [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;[A](salign.md)&gt;

fun &lt;[A](salign.md)&gt; [Sequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/-sequence/index.html)&lt;[A](salign.md)&gt;.[salign](salign.md)(SG: [Semigroup](../arrow.typeclasses/-semigroup/index.md)&lt;[A](salign.md)&gt;, other: [Sequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/-sequence/index.html)&lt;[A](salign.md)&gt;): [Sequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/-sequence/index.html)&lt;[A](salign.md)&gt;

aligns two structures and combine them with the given [Semigroup.combine](../arrow.typeclasses/-semigroup/combine.md)

[common]\
fun &lt;[A](salign.md)&gt; [Option](-option/index.md)&lt;[A](salign.md)&gt;.[salign](salign.md)(SA: [Semigroup](../arrow.typeclasses/-semigroup/index.md)&lt;[A](salign.md)&gt;, b: [Option](-option/index.md)&lt;[A](salign.md)&gt;): [Option](-option/index.md)&lt;[A](salign.md)&gt;

[common]\
fun &lt;[K](salign.md), [A](salign.md)&gt; [Map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)&lt;[K](salign.md), [A](salign.md)&gt;.[salign](salign.md)(SG: [Semigroup](../arrow.typeclasses/-semigroup/index.md)&lt;[A](salign.md)&gt;, other: [Map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)&lt;[K](salign.md), [A](salign.md)&gt;): [Map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)&lt;[K](salign.md), [A](salign.md)&gt;

aligns two structures and combine them with the given Semigroups '+'
