//[arrow-core](../../../index.md)/[arrow.core](../index.md)/[Either](index.md)/[mapLeft](map-left.md)

# mapLeft

[common]\
inline fun &lt;[C](map-left.md)&gt; [mapLeft](map-left.md)(f: ([A](index.md)) -&gt; [C](map-left.md)): [Either](index.md)&lt;[C](map-left.md), [B](index.md)&gt;

The given function is applied if this is a [Left](-left/index.md).

Example:

&lt;!--- KNIT example-either-40.kt --&gt;\
Right(12).mapLeft { "flower" } // Result: Right(12)\
Left(12).mapLeft { "flower" }  // Result: Left("flower")<!--- KNIT example-either-41.kt -->
