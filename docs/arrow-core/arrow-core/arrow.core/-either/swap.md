//[arrow-core](../../../index.md)/[arrow.core](../index.md)/[Either](index.md)/[swap](swap.md)

# swap

[common]\
fun [swap](swap.md)(): [Either](index.md)&lt;[B](index.md), [A](index.md)&gt;

If this is a [Left](-left/index.md), then return the left value in [Right](-right/index.md) or vice versa.

Example:

&lt;!--- KNIT example-either-36.kt --&gt;\
Left("left").swap()   // Result: Right("left")\
Right("right").swap() // Result: Left("right")<!--- KNIT example-either-37.kt -->
