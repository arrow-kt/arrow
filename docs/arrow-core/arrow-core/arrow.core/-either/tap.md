//[arrow-core](../../../index.md)/[arrow.core](../index.md)/[Either](index.md)/[tap](tap.md)

# tap

[common]\
inline fun [tap](tap.md)(f: ([B](index.md)) -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)): [Either](index.md)&lt;[A](index.md), [B](index.md)&gt;

The given function is applied as a fire and forget effect if this is a [Right](-right/index.md). When applied the result is ignored and the original Either value is returned

Example:

&lt;!--- KNIT example-either-44.kt --&gt;\
Right(12).tap { println("flower") } // Result: prints "flower" and returns: Right(12)\
Left(12).tap { println("flower") }  // Result: Left(12)<!--- KNIT example-either-45.kt -->
