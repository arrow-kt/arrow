//[arrow-core](../../../../index.md)/[arrow.core](../../index.md)/[Ior](../index.md)/[Both](index.md)/[isBoth](is-both.md)

# isBoth

[common]\
open override val [isBoth](is-both.md): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)

Returns true if this is a [Both](index.md), false otherwise.

Example:

&lt;!--- KNIT example-ior-05.kt --&gt;\
Left("tulip").isBoth           // Result: false\
Right("venus fly-trap").isBoth // Result: false\
Both("venus", "fly-trap").isBoth // Result: true<!--- KNIT example-ior-06.kt -->
