//[arrow-core](../../index.md)/[arrow.core](index.md)/[combine](combine.md)

# combine

[common]\
fun &lt;[A](combine.md), [T](combine.md)&gt; [Const](-const/index.md)&lt;[A](combine.md), [T](combine.md)&gt;.[combine](combine.md)(SG: [Semigroup](../arrow.typeclasses/-semigroup/index.md)&lt;[A](combine.md)&gt;, that: [Const](-const/index.md)&lt;[A](combine.md), [T](combine.md)&gt;): [Const](-const/index.md)&lt;[A](combine.md), [T](combine.md)&gt;

fun &lt;[A](combine.md), [B](combine.md)&gt; [Either](-either/index.md)&lt;[A](combine.md), [B](combine.md)&gt;.[combine](combine.md)(SGA: [Semigroup](../arrow.typeclasses/-semigroup/index.md)&lt;[A](combine.md)&gt;, SGB: [Semigroup](../arrow.typeclasses/-semigroup/index.md)&lt;[B](combine.md)&gt;, b: [Either](-either/index.md)&lt;[A](combine.md), [B](combine.md)&gt;): [Either](-either/index.md)&lt;[A](combine.md), [B](combine.md)&gt;

fun &lt;[A](combine.md), [B](combine.md)&gt; [Ior](-ior/index.md)&lt;[A](combine.md), [B](combine.md)&gt;.[combine](combine.md)(SA: [Semigroup](../arrow.typeclasses/-semigroup/index.md)&lt;[A](combine.md)&gt;, SB: [Semigroup](../arrow.typeclasses/-semigroup/index.md)&lt;[B](combine.md)&gt;, other: [Ior](-ior/index.md)&lt;[A](combine.md), [B](combine.md)&gt;): [Ior](-ior/index.md)&lt;[A](combine.md), [B](combine.md)&gt;

fun &lt;[A](combine.md)&gt; [Option](-option/index.md)&lt;[A](combine.md)&gt;.[combine](combine.md)(SGA: [Semigroup](../arrow.typeclasses/-semigroup/index.md)&lt;[A](combine.md)&gt;, b: [Option](-option/index.md)&lt;[A](combine.md)&gt;): [Option](-option/index.md)&lt;[A](combine.md)&gt;

fun &lt;[A](combine.md), [B](combine.md)&gt; [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[A](combine.md), [B](combine.md)&gt;.[combine](combine.md)(SA: [Semigroup](../arrow.typeclasses/-semigroup/index.md)&lt;[A](combine.md)&gt;, SB: [Semigroup](../arrow.typeclasses/-semigroup/index.md)&lt;[B](combine.md)&gt;, b: [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[A](combine.md), [B](combine.md)&gt;): [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[A](combine.md), [B](combine.md)&gt;

fun &lt;[E](combine.md), [A](combine.md)&gt; [Validated](-validated/index.md)&lt;[E](combine.md), [A](combine.md)&gt;.[combine](combine.md)(SE: [Semigroup](../arrow.typeclasses/-semigroup/index.md)&lt;[E](combine.md)&gt;, SA: [Semigroup](../arrow.typeclasses/-semigroup/index.md)&lt;[A](combine.md)&gt;, y: [Validated](-validated/index.md)&lt;[E](combine.md), [A](combine.md)&gt;): [Validated](-validated/index.md)&lt;[E](combine.md), [A](combine.md)&gt;

fun &lt;[K](combine.md), [A](combine.md)&gt; [Map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)&lt;[K](combine.md), [A](combine.md)&gt;.[combine](combine.md)(SG: [Semigroup](../arrow.typeclasses/-semigroup/index.md)&lt;[A](combine.md)&gt;, b: [Map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)&lt;[K](combine.md), [A](combine.md)&gt;): [Map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)&lt;[K](combine.md), [A](combine.md)&gt;
