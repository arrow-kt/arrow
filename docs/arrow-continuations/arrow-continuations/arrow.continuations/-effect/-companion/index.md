//[arrow-continuations](../../../../index.md)/[arrow.continuations](../../index.md)/[Effect](../index.md)/[Companion](index.md)

# Companion

[common]\
object [Companion](index.md)

## Functions

| Name | Summary |
|---|---|
| [restricted](restricted.md) | [common]<br>inline fun &lt;[Eff](restricted.md) : [Effect](../index.md)&lt;*&gt;, [F](restricted.md), [A](restricted.md)&gt; [restricted](restricted.md)(crossinline eff: ([DelimitedScope](../../../arrow.continuations.generic/-delimited-scope/index.md)&lt;[F](restricted.md)&gt;) -&gt; [Eff](restricted.md), crossinline just: ([A](restricted.md)) -&gt; [F](restricted.md), crossinline f: suspend [Eff](restricted.md).() -&gt; [A](restricted.md)): [F](restricted.md) |
| [suspended](suspended.md) | [common]<br>inline suspend fun &lt;[Eff](suspended.md) : [Effect](../index.md)&lt;*&gt;, [F](suspended.md), [A](suspended.md)&gt; [suspended](suspended.md)(crossinline eff: ([DelimitedScope](../../../arrow.continuations.generic/-delimited-scope/index.md)&lt;[F](suspended.md)&gt;) -&gt; [Eff](suspended.md), crossinline just: ([A](suspended.md)) -&gt; [F](suspended.md), crossinline f: suspend [Eff](suspended.md).() -&gt; [A](suspended.md)): [F](suspended.md) |
