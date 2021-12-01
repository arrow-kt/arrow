//[arrow-core](../../../index.md)/[arrow.core](../index.md)/[Either](index.md)/[tapLeft](tap-left.md)

# tapLeft

[common]\
inline fun [tapLeft](tap-left.md)(f: ([A](index.md)) -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)): [Either](index.md)&lt;[A](index.md), [B](index.md)&gt;

The given function is applied as a fire and forget effect if this is a [Left](-left/index.md). When applied the result is ignored and the original Either value is returned

Example:

&lt;!--- KNIT example-either-42.kt --&gt;\
Right(12).tapLeft { println("flower") } // Result: Right(12)\
Left(12).tapLeft { println("flower") }  // Result: prints "flower" and returns: Left(12)<!--- KNIT example-either-43.kt -->
