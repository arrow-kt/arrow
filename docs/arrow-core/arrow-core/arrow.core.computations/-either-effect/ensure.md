//[arrow-core](../../../index.md)/[arrow.core.computations](../index.md)/[EitherEffect](index.md)/[ensure](ensure.md)

# ensure

[common]\
open suspend fun [ensure](ensure.md)(value: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html), orLeft: () -&gt; [E](index.md))

Ensure check if the [value](ensure.md) is true, and if it is it allows the either { } binding to continue. In case it is false, then it short-circuits the binding and returns the provided value by [orLeft](ensure.md) inside an [Either.Left](../../arrow.core/-either/-left/index.md).

import arrow.core.computations.either\
\
//sampleStart\
suspend fun main() {\
  either&lt;String, Int&gt; {\
    ensure(true) { "" }\
    println("ensure(true) passes")\
    ensure(false) { "failed" }\
    1\
  }\
//sampleEnd\
  .let(::println)\
}\
// println: "ensure(true) passes"\
// res: Either.Left("failed")<!--- KNIT example-either-computations-01.kt -->
