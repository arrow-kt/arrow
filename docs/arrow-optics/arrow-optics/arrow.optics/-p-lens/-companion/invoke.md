//[arrow-optics](../../../../index.md)/[arrow.optics](../../index.md)/[PLens](../index.md)/[Companion](index.md)/[invoke](invoke.md)

# invoke

[common]\
operator fun &lt;[S](invoke.md), [T](invoke.md), [A](invoke.md), [B](invoke.md)&gt; [invoke](invoke.md)(get: ([S](invoke.md)) -&gt; [A](invoke.md), set: ([S](invoke.md), [B](invoke.md)) -&gt; [T](invoke.md)): [PLens](../index.md)&lt;[S](invoke.md), [T](invoke.md), [A](invoke.md), [B](invoke.md)&gt;

Invoke operator overload to create a [PLens](../index.md) of type S with target A. Can also be used to construct [Lens](../../index.md#-141055921%2FClasslikes%2F-617900156)
