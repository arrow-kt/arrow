//[arrow-optics](../../../index.md)/[arrow.optics](../index.md)/[PSetter](index.md)/[choice](choice.md)

# choice

[common]\
open infix fun &lt;[U](choice.md), [V](choice.md)&gt; [choice](choice.md)(other: [PSetter](index.md)&lt;[U](choice.md), [V](choice.md), [A](index.md), [B](index.md)&gt;): [PSetter](index.md)&lt;[Either](../../../../arrow-core/arrow-core/arrow.core/-either/index.md)&lt;[S](index.md), [U](choice.md)&gt;, [Either](../../../../arrow-core/arrow-core/arrow.core/-either/index.md)&lt;[T](index.md), [V](choice.md)&gt;, [A](index.md), [B](index.md)&gt;

Join two [PSetter](index.md) with the same target
