//[arrow-optics](../../../../index.md)/[arrow.optics](../../index.md)/[PPrism](../index.md)/[Companion](index.md)/[invoke](invoke.md)

# invoke

[common]\
operator fun &lt;[S](invoke.md), [T](invoke.md), [A](invoke.md), [B](invoke.md)&gt; [invoke](invoke.md)(getOrModify: ([S](invoke.md)) -&gt; [Either](../../../../../arrow-core/arrow-core/arrow.core/-either/index.md)&lt;[T](invoke.md), [A](invoke.md)&gt;, reverseGet: ([B](invoke.md)) -&gt; [T](invoke.md)): [PPrism](../index.md)&lt;[S](invoke.md), [T](invoke.md), [A](invoke.md), [B](invoke.md)&gt;

Invoke operator overload to create a [PPrism](../index.md) of type S with focus A. Can also be used to construct [Prism](../../index.md#1394331700%2FClasslikes%2F-617900156)
