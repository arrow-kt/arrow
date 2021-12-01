//[arrow-core](../../../../index.md)/[arrow.core](../../index.md)/[Eval](../index.md)/[FlatMap](index.md)

# FlatMap

[common]\
abstract class [FlatMap](index.md)&lt;out [A](index.md)&gt; : [Eval](../index.md)&lt;[A](index.md)&gt; 

FlatMap is a type of Eval<A> that is used to chain computations involving .map and .flatMap. Along with Eval#flatMap. It implements the trampoline that guarantees stack-safety.

Users should not instantiate FlatMap instances themselves. Instead, they will be automatically created when needed.

Unlike a traditional trampoline, the internal workings of the trampoline are not exposed. This allows a slightly more efficient implementation of the .value method.

## Constructors

| | |
|---|---|
| [FlatMap](-flat-map.md) | [common]<br>fun [FlatMap](-flat-map.md)() |

## Functions

| Name | Summary |
|---|---|
| [coflatMap](../coflat-map.md) | [common]<br>inline fun &lt;[B](../coflat-map.md)&gt; [coflatMap](../coflat-map.md)(crossinline f: ([Eval](../index.md)&lt;[A](index.md)&gt;) -&gt; [B](../coflat-map.md)): [Eval](../index.md)&lt;[B](../coflat-map.md)&gt; |
| [flatMap](../flat-map.md) | [common]<br>fun &lt;[B](../flat-map.md)&gt; [flatMap](../flat-map.md)(f: ([A](index.md)) -&gt; [Eval](../index.md)&lt;[B](../flat-map.md)&gt;): [Eval](../index.md)&lt;[B](../flat-map.md)&gt; |
| [map](../map.md) | [common]<br>inline fun &lt;[B](../map.md)&gt; [map](../map.md)(crossinline f: ([A](index.md)) -&gt; [B](../map.md)): [Eval](../index.md)&lt;[B](../map.md)&gt; |
| [memoize](memoize.md) | [common]<br>open override fun [memoize](memoize.md)(): [Eval](../index.md)&lt;[A](index.md)&gt; |
| [run](run.md) | [common]<br>abstract fun &lt;[S](run.md)&gt; [run](run.md)(s: [S](run.md)): [Eval](../index.md)&lt;[A](index.md)&gt; |
| [start](start.md) | [common]<br>abstract fun &lt;[S](start.md)&gt; [start](start.md)(): [Eval](../index.md)&lt;[S](start.md)&gt; |
| [toString](to-string.md) | [common]<br>open override fun [toString](to-string.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [value](value.md) | [common]<br>open override fun [value](value.md)(): [A](index.md) |
