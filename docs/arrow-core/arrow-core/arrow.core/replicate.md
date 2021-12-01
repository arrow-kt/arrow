//[arrow-core](../../index.md)/[arrow.core](index.md)/[replicate](replicate.md)

# replicate

[common]\
fun &lt;[A](replicate.md), [B](replicate.md)&gt; [Either](-either/index.md)&lt;[A](replicate.md), [B](replicate.md)&gt;.[replicate](replicate.md)(n: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), MB: [Monoid](../arrow.typeclasses/-monoid/index.md)&lt;[B](replicate.md)&gt;): [Either](-either/index.md)&lt;[A](replicate.md), [B](replicate.md)&gt;

fun &lt;[A](replicate.md)&gt; [Eval](-eval/index.md)&lt;[A](replicate.md)&gt;.[replicate](replicate.md)(n: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [Eval](-eval/index.md)&lt;[List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[A](replicate.md)&gt;&gt;

fun &lt;[A](replicate.md)&gt; [Eval](-eval/index.md)&lt;[A](replicate.md)&gt;.[replicate](replicate.md)(n: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), MA: [Monoid](../arrow.typeclasses/-monoid/index.md)&lt;[A](replicate.md)&gt;): [Eval](-eval/index.md)&lt;[A](replicate.md)&gt;

fun &lt;[A](replicate.md), [B](replicate.md)&gt; [Ior](-ior/index.md)&lt;[A](replicate.md), [B](replicate.md)&gt;.[replicate](replicate.md)(SA: [Semigroup](../arrow.typeclasses/-semigroup/index.md)&lt;[A](replicate.md)&gt;, n: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [Ior](-ior/index.md)&lt;[A](replicate.md), [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[B](replicate.md)&gt;&gt;

fun &lt;[A](replicate.md), [B](replicate.md)&gt; [Ior](-ior/index.md)&lt;[A](replicate.md), [B](replicate.md)&gt;.[replicate](replicate.md)(SA: [Semigroup](../arrow.typeclasses/-semigroup/index.md)&lt;[A](replicate.md)&gt;, n: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), MB: [Monoid](../arrow.typeclasses/-monoid/index.md)&lt;[B](replicate.md)&gt;): [Ior](-ior/index.md)&lt;[A](replicate.md), [B](replicate.md)&gt;

fun &lt;[A](replicate.md)&gt; [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;[A](replicate.md)&gt;.[replicate](replicate.md)(n: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[A](replicate.md)&gt;&gt;

fun &lt;[A](replicate.md)&gt; [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;[A](replicate.md)&gt;.[replicate](replicate.md)(n: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), MA: [Monoid](../arrow.typeclasses/-monoid/index.md)&lt;[A](replicate.md)&gt;): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[A](replicate.md)&gt;

fun &lt;[A](replicate.md)&gt; [Option](-option/index.md)&lt;[A](replicate.md)&gt;.[replicate](replicate.md)(n: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), MA: [Monoid](../arrow.typeclasses/-monoid/index.md)&lt;[A](replicate.md)&gt;): [Option](-option/index.md)&lt;[A](replicate.md)&gt;

fun &lt;[A](replicate.md)&gt; [Sequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/-sequence/index.html)&lt;[A](replicate.md)&gt;.[replicate](replicate.md)(n: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [Sequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/-sequence/index.html)&lt;[Sequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/-sequence/index.html)&lt;[A](replicate.md)&gt;&gt;

fun &lt;[A](replicate.md)&gt; [Sequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/-sequence/index.html)&lt;[A](replicate.md)&gt;.[replicate](replicate.md)(n: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), MA: [Monoid](../arrow.typeclasses/-monoid/index.md)&lt;[A](replicate.md)&gt;): [Sequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/-sequence/index.html)&lt;[A](replicate.md)&gt;

fun &lt;[E](replicate.md), [A](replicate.md)&gt; [Validated](-validated/index.md)&lt;[E](replicate.md), [A](replicate.md)&gt;.[replicate](replicate.md)(SE: [Semigroup](../arrow.typeclasses/-semigroup/index.md)&lt;[E](replicate.md)&gt;, n: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [Validated](-validated/index.md)&lt;[E](replicate.md), [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[A](replicate.md)&gt;&gt;

fun &lt;[E](replicate.md), [A](replicate.md)&gt; [Validated](-validated/index.md)&lt;[E](replicate.md), [A](replicate.md)&gt;.[replicate](replicate.md)(SE: [Semigroup](../arrow.typeclasses/-semigroup/index.md)&lt;[E](replicate.md)&gt;, n: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), MA: [Monoid](../arrow.typeclasses/-monoid/index.md)&lt;[A](replicate.md)&gt;): [Validated](-validated/index.md)&lt;[E](replicate.md), [A](replicate.md)&gt;
