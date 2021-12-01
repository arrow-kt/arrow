//[arrow-core](../../../index.md)/[arrow.core](../index.md)/[Ior](index.md)/[mapLeft](map-left.md)

# mapLeft

[common]\
inline fun &lt;[C](map-left.md)&gt; [mapLeft](map-left.md)(fa: ([A](index.md)) -&gt; [C](map-left.md)): [Ior](index.md)&lt;[C](map-left.md), [B](index.md)&gt;

The given function is applied if this is a [Left](-left/index.md) or [Both](-both/index.md) to A.

Example:

&lt;!--- KNIT example-ior-13.kt --&gt;\
Ior.Right(12).map { "flower" } // Result: Right(12)\
Ior.Left(12).map { "flower" }  // Result: Left("power")\
Ior.Both(12, "power").map { "flower $it" }  // Result: Both("flower 12", "power")<!--- KNIT example-ior-14.kt -->
