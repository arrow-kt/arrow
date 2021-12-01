//[arrow-core](../../../index.md)/[arrow.core](../index.md)/[Ior](index.md)/[padNull](pad-null.md)

# padNull

[common]\
fun [padNull](pad-null.md)(): [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[A](index.md)?, [B](index.md)?&gt;

Return this [Ior](index.md) as [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html) of nullables]

Example:

import arrow.core.Ior\
\
//sampleStart\
val right = Ior.Right(12).padNull()         // Result: Pair(null, 12)\
val left = Ior.Left(12).padNull()           // Result: Pair(12, null)\
val both = Ior.Both("power", 12).padNull()  // Result: Pair("power", 12)\
//sampleEnd\
\
fun main() {\
  println("right = $right")\
  println("left = $left")\
  println("both = $both")\
}<!--- KNIT example-ior-17.kt -->
