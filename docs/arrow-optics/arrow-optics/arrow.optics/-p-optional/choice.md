//[arrow-optics](../../../index.md)/[arrow.optics](../index.md)/[POptional](index.md)/[choice](choice.md)

# choice

[common]\
open infix fun &lt;[S1](choice.md), [T1](choice.md)&gt; [choice](choice.md)(other: [POptional](index.md)&lt;[S1](choice.md), [T1](choice.md), [A](index.md), [B](index.md)&gt;): [POptional](index.md)&lt;[Either](../../../../arrow-core/arrow-core/arrow.core/-either/index.md)&lt;[S](index.md), [S1](choice.md)&gt;, [Either](../../../../arrow-core/arrow-core/arrow.core/-either/index.md)&lt;[T](index.md), [T1](choice.md)&gt;, [A](index.md), [B](index.md)&gt;

Join two [POptional](index.md) with the same focus [B](index.md)
