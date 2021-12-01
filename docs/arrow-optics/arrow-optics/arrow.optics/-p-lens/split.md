//[arrow-optics](../../../index.md)/[arrow.optics](../index.md)/[PLens](index.md)/[split](split.md)

# split

[common]\
open infix fun &lt;[S1](split.md), [T1](split.md), [A1](split.md), [B1](split.md)&gt; [split](split.md)(other: [PLens](index.md)&lt;[S1](split.md), [T1](split.md), [A1](split.md), [B1](split.md)&gt;): [PLens](index.md)&lt;[Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[S](index.md), [S1](split.md)&gt;, [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[T](index.md), [T1](split.md)&gt;, [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[A](index.md), [A1](split.md)&gt;, [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[B](index.md), [B1](split.md)&gt;&gt;

Pair two disjoint [PLens](index.md)
