//[arrow-core](../../../index.md)/[arrow.core](../index.md)/[Option](index.md)/[tapNone](tap-none.md)

# tapNone

[common]\
inline fun [tapNone](tap-none.md)(f: () -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)): [Option](index.md)&lt;[A](index.md)&gt;

The given function is applied as a fire and forget effect if this is a None. When applied the result is ignored and the original None value is returned

Example:

&lt;!--- KNIT example-option-19.kt --&gt;\
Some(12).tapNone { println("flower") } // Result: Some(12)\
none&lt;Int&gt;().tapNone { println("flower") }  // Result: prints "flower" and returns: None<!--- KNIT example-option-20.kt -->
