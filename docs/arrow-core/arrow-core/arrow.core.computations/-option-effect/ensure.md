//[arrow-core](../../../index.md)/[arrow.core.computations](../index.md)/[OptionEffect](index.md)/[ensure](ensure.md)

# ensure

[common]\
open suspend fun [ensure](ensure.md)(value: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html))

Ensure check if the [value](ensure.md) is true, and if it is it allows the option { } binding to continue. In case it is false, then it short-circuits the binding and returns [None](../../arrow.core/-none/index.md).

import arrow.core.computations.option\
\
//sampleStart\
suspend fun main() {\
  option&lt;Int&gt; {\
    ensure(true)\
    println("ensure(true) passes")\
    ensure(false)\
    1\
  }\
//sampleEnd\
  .let(::println)\
}\
// println: "ensure(true) passes"\
// res: None<!--- KNIT example-option-computations-01.kt -->
