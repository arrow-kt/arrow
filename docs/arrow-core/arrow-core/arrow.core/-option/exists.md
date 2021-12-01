//[arrow-core](../../../index.md)/[arrow.core](../index.md)/[Option](index.md)/[exists](exists.md)

# exists

[common]\
inline fun [exists](exists.md)(predicate: ([A](index.md)) -&gt; [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)

Returns true if this option is nonempty '''and''' the predicate $p returns true when applied to this $option's value. Otherwise, returns false.

Example:

&lt;!--- KNIT example-option-23.kt --&gt;\
Some(12).exists { it 10 } // Result: true\
Some(7).exists { it 10 }  // Result: false\
\
val none: Option&lt;Int&gt; = None\
none.exists { it 10 }      // Result: false<!--- KNIT example-option-24.kt -->

## Parameters

common

| | |
|---|---|
| predicate | the predicate to test |
