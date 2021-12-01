//[arrow-core](../../../index.md)/[arrow.core](../index.md)/[Either](index.md)/[orNull](or-null.md)

# orNull

[common]\
fun [orNull](or-null.md)(): [B](index.md)?

Returns the right value if it exists, otherwise null

Example:

import arrow.core.Either.Left\
import arrow.core.Either.Right\
\
//sampleStart\
val right = Right(12).orNull() // Result: 12\
val left = Left(12).orNull()   // Result: null\
//sampleEnd\
fun main() {\
  println("right = $right")\
  println("left = $left")\
}<!--- KNIT example-either-48.kt -->
