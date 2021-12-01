//[arrow-core](../../../index.md)/[arrow.core](../index.md)/[Either](index.md)/[map](map.md)

# map

[common]\
inline fun &lt;[C](map.md)&gt; [map](map.md)(f: ([B](index.md)) -&gt; [C](map.md)): [Either](index.md)&lt;[A](index.md), [C](map.md)&gt;

The given function is applied if this is a [Right](-right/index.md).

Example:

&lt;!--- KNIT example-either-38.kt --&gt;\
Right(12).map { "flower" } // Result: Right("flower")\
Left(12).map { "flower" }  // Result: Left(12)<!--- KNIT example-either-39.kt -->
