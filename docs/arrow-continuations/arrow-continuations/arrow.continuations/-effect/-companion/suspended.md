//[arrow-continuations](../../../../index.md)/[arrow.continuations](../../index.md)/[Effect](../index.md)/[Companion](index.md)/[suspended](suspended.md)

# suspended

[common]\
inline suspend fun &lt;[Eff](suspended.md) : [Effect](../index.md)&lt;*&gt;, [F](suspended.md), [A](suspended.md)&gt; [suspended](suspended.md)(crossinline eff: ([DelimitedScope](../../../arrow.continuations.generic/-delimited-scope/index.md)&lt;[F](suspended.md)&gt;) -&gt; [Eff](suspended.md), crossinline just: ([A](suspended.md)) -&gt; [F](suspended.md), crossinline f: suspend [Eff](suspended.md).() -&gt; [A](suspended.md)): [F](suspended.md)
