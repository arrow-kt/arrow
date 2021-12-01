//[arrow-core](../../../index.md)/[arrow.core](../index.md)/[Option](index.md)/[findOrNull](find-or-null.md)

# findOrNull

[common]\
inline fun [findOrNull](find-or-null.md)(predicate: ([A](index.md)) -&gt; [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)): [A](index.md)?

Returns the $option's value if this option is nonempty '''and''' the predicate $p returns true when applied to this $option's value. Otherwise, returns null.

Example:

&lt;!--- KNIT example-option-25.kt --&gt;\
Some(12).exists { it 10 } // Result: 12\
Some(7).exists { it 10 }  // Result: null\
\
val none: Option&lt;Int&gt; = None\
none.exists { it 10 }      // Result: null<!--- KNIT example-option-26.kt -->
