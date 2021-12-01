//[arrow-core](../../../index.md)/[arrow.core](../index.md)/[Ior](index.md)/[swap](swap.md)

# swap

[common]\
fun [swap](swap.md)(): [Ior](index.md)&lt;[B](index.md), [A](index.md)&gt;

If this is a [Left](-left/index.md), then return the left value in [Right](-right/index.md) or vice versa, when this is [Both](-both/index.md) , left and right values are swap

Example:

&lt;!--- KNIT example-ior-15.kt --&gt;\
Left("left").swap()   // Result: Right("left")\
Right("right").swap() // Result: Left("right")\
Both("left", "right").swap() // Result: Both("right", "left")<!--- KNIT example-ior-16.kt -->
