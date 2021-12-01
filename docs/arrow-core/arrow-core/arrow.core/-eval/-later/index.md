//[arrow-core](../../../../index.md)/[arrow.core](../../index.md)/[Eval](../index.md)/[Later](index.md)

# Later

[common]\
data class [Later](index.md)&lt;out [A](index.md)&gt;(f: () -&gt; [A](index.md)) : [Eval](../index.md)&lt;[A](index.md)&gt; 

Construct a lazy Eval<A> instance.

This type should be used for most "lazy" values. In some sense it is equivalent to using a lazy val.

When caching is not required or desired (e.g. if the value produced may be large) prefer Always. When there is no computation necessary, prefer Now.

Once Later has been evaluated, the closure (and any values captured by the closure) will not be retained, and will be available for garbage collection.

## Constructors

| | |
|---|---|
| [Later](-later.md) | [common]<br>fun &lt;out [A](index.md)&gt; [Later](-later.md)(f: () -&gt; [A](index.md)) |

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
