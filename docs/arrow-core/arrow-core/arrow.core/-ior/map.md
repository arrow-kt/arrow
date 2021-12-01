//[arrow-core](../../../index.md)/[arrow.core](../index.md)/[Ior](index.md)/[map](map.md)

# map

[common]\
inline fun &lt;[D](map.md)&gt; [map](map.md)(f: ([B](index.md)) -&gt; [D](map.md)): [Ior](index.md)&lt;[A](index.md), [D](map.md)&gt;

The given function is applied if this is a [Right](-right/index.md) or [Both](-both/index.md) to B.

Example:

&lt;!--- KNIT example-ior-09.kt --&gt;\
Ior.Right(12).map { "flower" } // Result: Right("flower")\
Ior.Left(12).map { "flower" }  // Result: Left(12)\
Ior.Both(12, "power").map { "flower $it" }  // Result: Both(12, "flower power")<!--- KNIT example-ior-10.kt -->
