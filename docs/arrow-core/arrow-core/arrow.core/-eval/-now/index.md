//[arrow-core](../../../../index.md)/[arrow.core](../../index.md)/[Eval](../index.md)/[Now](index.md)

# Now

[common]\
data class [Now](index.md)&lt;out [A](index.md)&gt;(value: [A](index.md)) : [Eval](../index.md)&lt;[A](index.md)&gt; 

Construct an eager Eval<A> instance. In some sense it is equivalent to using a val.

This type should be used when an A value is already in hand, or when the computation to produce an A value is pure and very fast.

## Constructors

| | |
|---|---|
| [Now](-now.md) | [common]<br>fun &lt;out [A](index.md)&gt; [Now](-now.md)(value: [A](index.md)) |

## Types

| Name | Summary |
|---|---|
| [Companion](-companion/index.md) | [common]<br>object [Companion](-companion/index.md) |

## Functions

| Name | Summary |
|---|---|
| [coflatMap](../coflat-map.md) | [common]<br>inline fun &lt;[B](../coflat-map.md)&gt; [coflatMap](../coflat-map.md)(crossinline f: ([Eval](../index.md)&lt;[A](index.md)&gt;) -&gt; [B](../coflat-map.md)): [Eval](../index.md)&lt;[B](../coflat-map.md)&gt; |
| [flatMap](../flat-map.md) | [common]<br>fun &lt;[B](../flat-map.md)&gt; [flatMap](../flat-map.md)(f: ([A](index.md)) -&gt; [Eval](../index.md)&lt;[B](../flat-map.md)&gt;): [Eval](../index.md)&lt;[B](../flat-map.md)&gt; |
| [map](../map.md) | [common]<br>inline fun &lt;[B](../map.md)&gt; [map](../map.md)(crossinline f: ([A](index.md)) -&gt; [B](../map.md)): [Eval](../index.md)&lt;[B](../map.md)&gt; |
| [memoize](memoize.md) | [common]<br>open override fun [memoize](memoize.md)(): [Eval](../index.md)&lt;[A](index.md)&gt; |
| [toString](to-string.md) | [common]<br>open override fun [toString](to-string.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [value](value.md) | [common]<br>open override fun [value](value.md)(): [A](index.md) |

## Properties

| Name | Summary |
|---|---|
| [value](value.md) | [common]<br>val [value](value.md): [A](index.md) |
