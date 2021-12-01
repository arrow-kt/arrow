//[arrow-core](../../index.md)/[arrow.core](index.md)/[redeemWith](redeem-with.md)

# redeemWith

[common]\
inline fun &lt;[A](redeem-with.md), [B](redeem-with.md), [C](redeem-with.md), [D](redeem-with.md)&gt; [Either](-either/index.md)&lt;[A](redeem-with.md), [B](redeem-with.md)&gt;.[redeemWith](redeem-with.md)(fa: ([A](redeem-with.md)) -&gt; [Either](-either/index.md)&lt;[C](redeem-with.md), [D](redeem-with.md)&gt;, fb: ([B](redeem-with.md)) -&gt; [Either](-either/index.md)&lt;[C](redeem-with.md), [D](redeem-with.md)&gt;): [Either](-either/index.md)&lt;[C](redeem-with.md), [D](redeem-with.md)&gt;

inline fun &lt;[A](redeem-with.md), [B](redeem-with.md)&gt; [Option](-option/index.md)&lt;[A](redeem-with.md)&gt;.[redeemWith](redeem-with.md)(fe: ([Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)) -&gt; [Option](-option/index.md)&lt;[B](redeem-with.md)&gt;, fb: ([A](redeem-with.md)) -&gt; [Option](-option/index.md)&lt;[B](redeem-with.md)&gt;): [Option](-option/index.md)&lt;[B](redeem-with.md)&gt;

[common]\
inline fun &lt;[A](redeem-with.md), [B](redeem-with.md)&gt; [Result](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-result/index.html)&lt;[A](redeem-with.md)&gt;.[redeemWith](redeem-with.md)(handleErrorWith: (throwable: [Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html)) -&gt; [Result](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-result/index.html)&lt;[B](redeem-with.md)&gt;, transform: ([A](redeem-with.md)) -&gt; [Result](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-result/index.html)&lt;[B](redeem-with.md)&gt;): [Result](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-result/index.html)&lt;[B](redeem-with.md)&gt;

Compose both:

<ul><li>a [transform](redeem-with.md) operation on the success value [A](redeem-with.md) into [B](redeem-with.md) whilst flattening [Result](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-result/index.html).</li><li>a recovering [transform](redeem-with.md) operation on the failure value [Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html) whilst flattening [Result](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-result/index.html).</li></ul>

Combining the powers of [flatMap](flat-map.md) and [handleErrorWith](redeem-with.md).
