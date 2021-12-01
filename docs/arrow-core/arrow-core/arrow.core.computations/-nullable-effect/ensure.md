//[arrow-core](../../../index.md)/[arrow.core.computations](../index.md)/[NullableEffect](index.md)/[ensure](ensure.md)

# ensure

[common]\
open suspend fun [ensure](ensure.md)(value: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html))

Ensure check if the [value](ensure.md) is true, and if it is it allows the nullable { } binding to continue. In case it is false, then it short-circuits the binding and returns null.

import arrow.core.computations.nullable\
\
//sampleStart\
suspend fun main() {\
  nullable&lt;Int&gt; {\
    ensure(true)\
    println("ensure(true) passes")\
    ensure(false)\
    1\
  }\
//sampleEnd\
  .let(::println)\
}\
// println: "ensure(true) passes"\
// res: null<!--- KNIT example-nullable-computations-01.kt -->
