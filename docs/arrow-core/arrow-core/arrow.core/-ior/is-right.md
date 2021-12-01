//[arrow-core](../../../index.md)/[arrow.core](../index.md)/[Ior](index.md)/[isRight](is-right.md)

# isRight

[common]\
abstract val [isRight](is-right.md): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)

Returns true if this is a [Right](-right/index.md), false otherwise.

Example:

&lt;!--- KNIT example-ior-01.kt --&gt;\
Left("tulip").isRight           // Result: false\
Right("venus fly-trap").isRight // Result: true\
Both("venus", "fly-trap").isRight // Result: false<!--- KNIT example-ior-02.kt -->
