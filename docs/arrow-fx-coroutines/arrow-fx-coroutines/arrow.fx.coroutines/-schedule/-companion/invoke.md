//[arrow-fx-coroutines](../../../../index.md)/[arrow.fx.coroutines](../../index.md)/[Schedule](../index.md)/[Companion](index.md)/[invoke](invoke.md)

# invoke

[common]\
operator fun &lt;[S](invoke.md), [A](invoke.md), [B](invoke.md)&gt; [invoke](invoke.md)(initial: suspend () -&gt; [S](invoke.md), update: suspend ([A](invoke.md), [S](invoke.md)) -&gt; [Schedule.Decision](../-decision/index.md)&lt;[S](invoke.md), [B](invoke.md)&gt;): [Schedule](../index.md)&lt;[A](invoke.md), [B](invoke.md)&gt;

Invoke constructor to manually define a schedule. If you need this, please consider adding it to Arrow or suggest a change to avoid using this manual method.
