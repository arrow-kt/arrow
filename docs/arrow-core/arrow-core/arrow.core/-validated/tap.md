//[arrow-core](../../../index.md)/[arrow.core](../index.md)/[Validated](index.md)/[tap](tap.md)

# tap

[common]\
inline fun [tap](tap.md)(f: ([A](index.md)) -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)): [Validated](index.md)&lt;[E](index.md), [A](index.md)&gt;

The given function is applied as a fire and forget effect if this is Valid. When applied the result is ignored and the original Validated value is returned

Example:

import arrow.core.Validated\
\
Validated.Valid(12).tap { println("flower") } // Result: prints "flower" and returns: Valid(12)\
Validated.Invalid(12).tap { println("flower") }  // Result: Invalid(12)<!--- KNIT example-validated-19.kt -->
