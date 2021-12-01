//[arrow-core](../../../index.md)/[arrow.core](../index.md)/[Either](index.md)/[exists](exists.md)

# exists

[common]\
inline fun [exists](exists.md)(predicate: ([B](index.md)) -&gt; [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)

Returns false if [Left](-left/index.md) or returns the result of the application of the given predicate to the [Right](-right/index.md) value.

Example:

&lt;!--- KNIT example-either-46.kt --&gt;\
Right(12).exists { it 10 } // Result: true\
Right(7).exists { it 10 }  // Result: false\
\
val left: Either&lt;Int, Int&gt; = Left(12)\
left.exists { it 10 }      // Result: false<!--- KNIT example-either-47.kt -->
