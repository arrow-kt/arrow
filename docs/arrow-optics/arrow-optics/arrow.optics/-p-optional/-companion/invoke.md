//[arrow-optics](../../../../index.md)/[arrow.optics](../../index.md)/[POptional](../index.md)/[Companion](index.md)/[invoke](invoke.md)

# invoke

[common]\
operator fun &lt;[S](invoke.md), [T](invoke.md), [A](invoke.md), [B](invoke.md)&gt; [invoke](invoke.md)(getOrModify: ([S](invoke.md)) -&gt; [Either](../../../../../arrow-core/arrow-core/arrow.core/-either/index.md)&lt;[T](invoke.md), [A](invoke.md)&gt;, set: ([S](invoke.md), [B](invoke.md)) -&gt; [T](invoke.md)): [POptional](../index.md)&lt;[S](invoke.md), [T](invoke.md), [A](invoke.md), [B](invoke.md)&gt;

Invoke operator overload to create a [POptional](../index.md) of type S with focus A. Can also be used to construct [Optional](../../index.md#-1955528147%2FClasslikes%2F-617900156)
