//[arrow-fx-coroutines](../../../../index.md)/[arrow.fx.coroutines](../../index.md)/[Schedule](../index.md)/[Companion](index.md)/[unfoldLazy](unfold-lazy.md)

# unfoldLazy

[common]\
fun &lt;[I](unfold-lazy.md), [A](unfold-lazy.md)&gt; [unfoldLazy](unfold-lazy.md)(c: suspend () -&gt; [A](unfold-lazy.md), f: suspend ([A](unfold-lazy.md)) -&gt; [A](unfold-lazy.md)): [Schedule](../index.md)&lt;[I](unfold-lazy.md), [A](unfold-lazy.md)&gt;

Creates a schedule that unfolds effectfully using a seed value [c](unfold-lazy.md) and a unfold function [f](unfold-lazy.md). This keeps the current state (the current seed) as State and runs the unfold function on every call to update. This schedule always continues without delay and returns the current state.
