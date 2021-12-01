//[arrow-core](../../index.md)/[arrow.core](index.md)/[filterOrElse](filter-or-else.md)

# filterOrElse

[common]\
inline fun &lt;[A](filter-or-else.md), [B](filter-or-else.md)&gt; [Either](-either/index.md)&lt;[A](filter-or-else.md), [B](filter-or-else.md)&gt;.[filterOrElse](filter-or-else.md)(predicate: ([B](filter-or-else.md)) -&gt; [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html), default: () -&gt; [A](filter-or-else.md)): [Either](-either/index.md)&lt;[A](filter-or-else.md), [B](filter-or-else.md)&gt;

Returns [Right](-either/-right/index.md) with the existing value of [Right](-either/-right/index.md) if this is a [Right](-either/-right/index.md) and the given predicate holds for the right value.<br>

Returns Left(default) if this is a [Right](-either/-right/index.md) and the given predicate does not hold for the right value.<br>

Returns [Left](-either/-left/index.md) with the existing value of [Left](-either/-left/index.md) if this is a [Left](-either/-left/index.md).<br>

Example:

&lt;!--- KNIT example-either-59.kt --&gt;\
Right(12).filterOrElse({ it 10 }, { -1 }) // Result: Right(12)\
Right(7).filterOrElse({ it 10 }, { -1 })  // Result: Left(-1)\
\
val left: Either&lt;Int, Int&gt; = Left(12)\
left.filterOrElse({ it 10 }, { -1 })      // Result: Left(12)<!--- KNIT example-either-60.kt -->
