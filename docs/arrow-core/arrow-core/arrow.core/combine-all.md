//[arrow-core](../../index.md)/[arrow.core](index.md)/[combineAll](combine-all.md)

# combineAll

[common]\
fun &lt;[A](combine-all.md), [B](combine-all.md)&gt; [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;[Either](-either/index.md)&lt;[A](combine-all.md), [B](combine-all.md)&gt;&gt;.[combineAll](combine-all.md)(MA: [Monoid](../arrow.typeclasses/-monoid/index.md)&lt;[A](combine-all.md)&gt;, MB: [Monoid](../arrow.typeclasses/-monoid/index.md)&lt;[B](combine-all.md)&gt;): [Either](-either/index.md)&lt;[A](combine-all.md), [B](combine-all.md)&gt;

fun &lt;[A](combine-all.md)&gt; [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;[A](combine-all.md)&gt;.[combineAll](combine-all.md)(MA: [Monoid](../arrow.typeclasses/-monoid/index.md)&lt;[A](combine-all.md)&gt;): [A](combine-all.md)

fun &lt;[A](combine-all.md)&gt; [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;[Option](-option/index.md)&lt;[A](combine-all.md)&gt;&gt;.[combineAll](combine-all.md)(MA: [Monoid](../arrow.typeclasses/-monoid/index.md)&lt;[A](combine-all.md)&gt;): [Option](-option/index.md)&lt;[A](combine-all.md)&gt;

fun &lt;[A](combine-all.md)&gt; [Option](-option/index.md)&lt;[A](combine-all.md)&gt;.[combineAll](combine-all.md)(MA: [Monoid](../arrow.typeclasses/-monoid/index.md)&lt;[A](combine-all.md)&gt;): [A](combine-all.md)

fun &lt;[A](combine-all.md)&gt; [Sequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/-sequence/index.html)&lt;[A](combine-all.md)&gt;.[combineAll](combine-all.md)(MA: [Monoid](../arrow.typeclasses/-monoid/index.md)&lt;[A](combine-all.md)&gt;): [A](combine-all.md)

fun &lt;[E](combine-all.md), [A](combine-all.md)&gt; [Validated](-validated/index.md)&lt;[E](combine-all.md), [A](combine-all.md)&gt;.[combineAll](combine-all.md)(MA: [Monoid](../arrow.typeclasses/-monoid/index.md)&lt;[A](combine-all.md)&gt;): [A](combine-all.md)

fun &lt;[K](combine-all.md), [A](combine-all.md)&gt; [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;[Map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)&lt;[K](combine-all.md), [A](combine-all.md)&gt;&gt;.[combineAll](combine-all.md)(SG: [Semigroup](../arrow.typeclasses/-semigroup/index.md)&lt;[A](combine-all.md)&gt;): [Map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)&lt;[K](combine-all.md), [A](combine-all.md)&gt;
