//[arrow-core](../../../index.md)/[arrow.core](../index.md)/[Ior](index.md)/[leftOrNull](left-or-null.md)

# leftOrNull

[common]\
fun [leftOrNull](left-or-null.md)(): [A](index.md)?

Returns the [Left](-left/index.md) value or A if this is [Left](-left/index.md) or [Both](-both/index.md) and null if this is a [Right](-right/index.md).

Example:

import arrow.core.Ior\
\
//sampleStart\
val right = Ior.Right(12).leftOrNull()         // Result: null\
val left = Ior.Left(12).leftOrNull()           // Result: 12\
val both = Ior.Both(12, "power").leftOrNull()  // Result: 12\
//sampleEnd\
\
fun main() {\
  println("right = $right")\
  println("left = $left")\
  println("both = $both")\
}<!--- KNIT example-ior-21.kt -->
