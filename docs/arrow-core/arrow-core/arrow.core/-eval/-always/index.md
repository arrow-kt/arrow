//[arrow-core](../../../../index.md)/[arrow.core](../../index.md)/[Eval](../index.md)/[Always](index.md)

# Always

[common]\
data class [Always](index.md)&lt;out [A](index.md)&gt;(f: () -&gt; [A](index.md)) : [Eval](../index.md)&lt;[A](index.md)&gt; 

Construct a lazy Eval<A> instance.

This type can be used for "lazy" values. In some sense it is equivalent to using a Function0 value.

This type will evaluate the computation every time the value is required. It should be avoided except when laziness is required and caching must be avoided. Generally, prefer Later.

## Constructors

| | |
|---|---|
| [Always](-always.md) | [common]<br>fun &lt;out [A](index.md)&gt; [Always](-always.md)(f: () -&gt; [A](index.md)) |

## Functions

| Name | Summary |
|---|---|
| [coflatMap](../coflat-map.md) | [common]<br>inline fun &lt;[B](../coflat-map.md)&gt; [coflatMap](../coflat-map.md)(crossinline f: ([Eval](../index.md)&lt;[A](index.md)&gt;) -&gt; [B](../coflat-map.md)): [Eval](../index.md)&lt;[B](../coflat-map.md)&gt; |
| [flatMap](../flat-map.md) | [common]<br>fun &lt;[B](../flat-map.md)&gt; [flatMap](../flat-map.md)(f: ([A](index.md)) -&gt; [Eval](../index.md)&lt;[B](../flat-map.md)&gt;): [Eval](../index.md)&lt;[B](../flat-map.md)&gt; |
| [map](../map.md) | [common]<br>inline fun &lt;[B](../map.md)&gt; [map](../map.md)(crossinline f: ([A](index.md)) -&gt; [B](../map.md)): [Eval](../index.md)&lt;[B](../map.md)&gt; |
| [memoize](memoize.md) | [common]<br>open override fun [memoize](memoize.md)(): [Eval](../index.md)&lt;[A](index.md)&gt; |
| [toString](to-string.md) | [common]<br>open override fun [toString](to-string.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [value](value.md) | [common]<br>open override fun [value](value.md)(): [A](index.md) |
