//[arrow-core](../../../../index.md)/[arrow.core](../../index.md)/[Ior](../index.md)/[Left](index.md)/[isLeft](is-left.md)

# isLeft

[common]\
open override val [isLeft](is-left.md): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)

Returns true if this is a [Left](index.md), false otherwise.

Example:

&lt;!--- KNIT example-ior-03.kt --&gt;\
Left("tulip").isLeft           // Result: true\
Right("venus fly-trap").isLeft // Result: false\
Both("venus", "fly-trap").isLeft // Result: false<!--- KNIT example-ior-04.kt -->
