//[arrow-core](../../../index.md)/[arrow.core](../index.md)/[Validated](index.md)/[tapInvalid](tap-invalid.md)

# tapInvalid

[common]\
inline fun [tapInvalid](tap-invalid.md)(f: ([E](index.md)) -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)): [Validated](index.md)&lt;[E](index.md), [A](index.md)&gt;

The given function is applied as a fire and forget effect if this is Invalid. When applied the result is ignored and the original Validated value is returned

Example:

import arrow.core.Validated\
Validated.Valid(12).tapInvalid { println("flower") } // Result: Valid(12)\
Validated.Invalid(12).tapInvalid { println("flower") }  // Result: prints "flower" and returns: Invalid(12)<!--- KNIT example-validated-18.kt -->
