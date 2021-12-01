//[arrow-core](../../index.md)/[arrow.core](index.md)/[combineK](combine-k.md)

# combineK

[common]\
fun &lt;[A](combine-k.md), [B](combine-k.md)&gt; [Either](-either/index.md)&lt;[A](combine-k.md), [B](combine-k.md)&gt;.[combineK](combine-k.md)(y: [Either](-either/index.md)&lt;[A](combine-k.md), [B](combine-k.md)&gt;): [Either](-either/index.md)&lt;[A](combine-k.md), [B](combine-k.md)&gt;

fun &lt;[E](combine-k.md), [A](combine-k.md)&gt; [Validated](-validated/index.md)&lt;[E](combine-k.md), [A](combine-k.md)&gt;.[combineK](combine-k.md)(SE: [Semigroup](../arrow.typeclasses/-semigroup/index.md)&lt;[E](combine-k.md)&gt;, y: [Validated](-validated/index.md)&lt;[E](combine-k.md), [A](combine-k.md)&gt;): [Validated](-validated/index.md)&lt;[E](combine-k.md), [A](combine-k.md)&gt;
