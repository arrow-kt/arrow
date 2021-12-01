//[arrow-core](../../../index.md)/[arrow.core](../index.md)/[Option](index.md)/[tap](tap.md)

# tap

[common]\
inline fun [tap](tap.md)(f: ([A](index.md)) -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)): [Option](index.md)&lt;[A](index.md)&gt;

The given function is applied as a fire and forget effect if this is a some. When applied the result is ignored and the original Some value is returned

Example:

&lt;!--- KNIT example-option-21.kt --&gt;\
Some(12).tap { println("flower") } // Result: prints "flower" and returns: Some(12)\
none&lt;Int&gt;().tap { println("flower") }  // Result: None<!--- KNIT example-option-22.kt -->
