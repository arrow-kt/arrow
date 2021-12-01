//[arrow-core](../../index.md)/[arrow.core](index.md)/[leftIfNull](left-if-null.md)

# leftIfNull

[common]\
inline fun &lt;[A](left-if-null.md), [B](left-if-null.md)&gt; [Either](-either/index.md)&lt;[A](left-if-null.md), [B](left-if-null.md)?&gt;.[leftIfNull](left-if-null.md)(default: () -&gt; [A](left-if-null.md)): [Either](-either/index.md)&lt;[A](left-if-null.md), [B](left-if-null.md)&gt;

Returns [Right](-either/-right/index.md) with the existing value of [Right](-either/-right/index.md) if this is an [Right](-either/-right/index.md) with a non-null value. The returned Either.Right type is not nullable.

Returns Left(default()) if this is an [Right](-either/-right/index.md) and the existing value is null

Returns [Left](-either/-left/index.md) with the existing value of [Left](-either/-left/index.md) if this is an [Left](-either/-left/index.md).

Example:

&lt;!--- KNIT example-either-64.kt --&gt;\
Right(12).leftIfNull({ -1 })   // Result: Right(12)\
Right(null).leftIfNull({ -1 }) // Result: Left(-1)\
\
Left(12).leftIfNull({ -1 })    // Result: Left(12)<!--- KNIT example-either-65.kt -->
