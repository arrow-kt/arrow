//[arrow-core](../../../index.md)/[arrow.core](../index.md)/[Option](index.md)/[align](align.md)

# align

[common]\
infix fun &lt;[B](align.md)&gt; [align](align.md)(b: [Option](index.md)&lt;[B](align.md)&gt;): [Option](index.md)&lt;[Ior](../-ior/index.md)&lt;[A](index.md), [B](align.md)&gt;&gt;

Align two options (this on the left and [b](align.md) on the right) as one Option of [Ior](../-ior/index.md).

[common]\
inline fun &lt;[B](align.md), [C](align.md)&gt; [align](align.md)(b: [Option](index.md)&lt;[B](align.md)&gt;, f: ([Ior](../-ior/index.md)&lt;[A](index.md), [B](align.md)&gt;) -&gt; [C](align.md)): [Option](index.md)&lt;[C](align.md)&gt;

Align two options (this on the left and [b](align.md) on the right) as one Option of [Ior](../-ior/index.md), and then, if it's not [None](../-none/index.md), map it using [f](align.md).
