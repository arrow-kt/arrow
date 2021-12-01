//[arrow-core](../../../index.md)/[arrow.core](../index.md)/[Ior](index.md)/[orNull](or-null.md)

# orNull

[common]\
fun [orNull](or-null.md)(): [B](index.md)?

Returns the [Right](-right/index.md) value or B if this is [Right](-right/index.md) or [Both](-both/index.md) and null if this is a [Left](-left/index.md).

Example:

import arrow.core.Ior\
\
//sampleStart\
val right = Ior.Right(12).orNull()         // Result: 12\
val left = Ior.Left(12).orNull()           // Result: null\
val both = Ior.Both(12, "power").orNull()  // Result: "power"\
//sampleEnd\
fun main() {\
  println("right = $right")\
  println("left = $left")\
  println("both = $both")\
}<!--- KNIT example-ior-20.kt -->
