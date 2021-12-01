//[arrow-continuations](../../../../index.md)/[arrow.continuations](../../index.md)/[Effect](../index.md)/[Companion](index.md)/[restricted](restricted.md)

# restricted

[common]\
inline fun &lt;[Eff](restricted.md) : [Effect](../index.md)&lt;*&gt;, [F](restricted.md), [A](restricted.md)&gt; [restricted](restricted.md)(crossinline eff: ([DelimitedScope](../../../arrow.continuations.generic/-delimited-scope/index.md)&lt;[F](restricted.md)&gt;) -&gt; [Eff](restricted.md), crossinline just: ([A](restricted.md)) -&gt; [F](restricted.md), crossinline f: suspend [Eff](restricted.md).() -&gt; [A](restricted.md)): [F](restricted.md)
