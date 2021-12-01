//[arrow-core](../../../index.md)/[arrow.core](../index.md)/[Validated](index.md)/[bimap](bimap.md)

# bimap

[common]\
inline fun &lt;[EE](bimap.md), [B](bimap.md)&gt; [bimap](bimap.md)(fe: ([E](index.md)) -&gt; [EE](bimap.md), fa: ([A](index.md)) -&gt; [B](bimap.md)): [Validated](index.md)&lt;[EE](bimap.md), [B](bimap.md)&gt;

From arrow.typeclasses.Bifunctor, maps both types of this Validated.

Apply a function to an Invalid or Valid value, returning a new Invalid or Valid value respectively.
