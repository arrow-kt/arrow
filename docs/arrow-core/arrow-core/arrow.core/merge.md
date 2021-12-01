//[arrow-core](../../index.md)/[arrow.core](index.md)/[merge](merge.md)

# merge

[common]\
inline fun &lt;[A](merge.md)&gt; [Either](-either/index.md)&lt;[A](merge.md), [A](merge.md)&gt;.[merge](merge.md)(): [A](merge.md)

Returns the value from this [Right](-either/-right/index.md) or [Left](-either/-left/index.md).

Example:

&lt;!--- KNIT example-either-62.kt --&gt;\
Right(12).merge() // Result: 12\
Left(12).merge() // Result: 12<!--- KNIT example-either-63.kt -->
