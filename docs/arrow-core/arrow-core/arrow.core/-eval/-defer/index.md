//[arrow-core](../../../../index.md)/[arrow.core](../../index.md)/[Eval](../index.md)/[Defer](index.md)

# Defer

[common]\
data class [Defer](index.md)&lt;out [A](index.md)&gt;(thunk: () -&gt; [Eval](../index.md)&lt;[A](index.md)&gt;) : [Eval](../index.md)&lt;[A](index.md)&gt; 

Defer is a type of Eval<A> that is used to defer computations which produce Eval<A>.

Users should not instantiate Defer instances themselves. Instead, they will be automatically created when needed.

## Constructors

| | |
|---|---|
| [Defer](-defer.md) | [common]<br>fun &lt;out [A](index.md)&gt; [Defer](-defer.md)(thunk: () -&gt; [Eval](../index.md)&lt;[A](index.md)&gt;) |

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
| [thunk](thunk.md) | [common]<br>val [thunk](thunk.md): () -&gt; [Eval](../index.md)&lt;[A](index.md)&gt; |
