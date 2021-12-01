//[arrow-core](../../index.md)/[arrow.core.computations](index.md)/[ensureNotNull](ensure-not-null.md)

# ensureNotNull

[common]\
suspend fun &lt;[E](ensure-not-null.md), [B](ensure-not-null.md) : [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)&gt; [EitherEffect](-either-effect/index.md)&lt;[E](ensure-not-null.md), *&gt;.[ensureNotNull](ensure-not-null.md)(value: [B](ensure-not-null.md)?, orLeft: () -&gt; [E](ensure-not-null.md)): [B](ensure-not-null.md)

Ensures that [value](ensure-not-null.md) is not null. When the value is not null, then it will be returned as non null and the check value is now smart-checked to non-null. Otherwise, if the [value](ensure-not-null.md) is null then the [either](either/index.md) binding will short-circuit with [orLeft](ensure-not-null.md) inside of [Either.Left](../arrow.core/-either/-left/index.md).

import arrow.core.computations.either\
import arrow.core.computations.ensureNotNull\
\
//sampleStart\
suspend fun main() {\
  either&lt;String, Int&gt; {\
    val x: Int? = 1\
    ensureNotNull(x) { "passes" }\
    println(x)\
    ensureNotNull(null) { "failed" }\
  }\
//sampleEnd\
  .let(::println)\
}\
// println: "1"\
// res: Either.Left("failed")<!--- KNIT example-either-computations-02.kt -->

[common]\
suspend fun &lt;[B](ensure-not-null.md) : [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)&gt; [NullableEffect](-nullable-effect/index.md)&lt;*&gt;.[ensureNotNull](ensure-not-null.md)(value: [B](ensure-not-null.md)?): [B](ensure-not-null.md)

Ensures that [value](ensure-not-null.md) is not null. When the value is not null, then it will be returned as non null and the check value is now smart-checked to non-null. Otherwise, if the [value](ensure-not-null.md) is null then the [option](option/index.md) binding will short-circuit with [None](../arrow.core/-none/index.md).

import arrow.core.computations.nullable\
\
//sampleStart\
suspend fun main() {\
  nullable&lt;Int&gt; {\
    val x: Int? = 1\
    ensureNotNull(x)\
    println(x)\
    ensureNotNull(null)\
  }\
//sampleEnd\
  .let(::println)\
}\
// println: "1"\
// res: null<!--- KNIT example-nullable-computations-02.kt -->

[common]\
suspend fun &lt;[B](ensure-not-null.md) : [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)&gt; [OptionEffect](-option-effect/index.md)&lt;*&gt;.[ensureNotNull](ensure-not-null.md)(value: [B](ensure-not-null.md)?): [B](ensure-not-null.md)

Ensures that [value](ensure-not-null.md) is not null. When the value is not null, then it will be returned as non null and the check value is now smart-checked to non-null. Otherwise, if the [value](ensure-not-null.md) is null then the [option](option/index.md) binding will short-circuit with [None](../arrow.core/-none/index.md).

import arrow.core.computations.option\
\
//sampleStart\
suspend fun main() {\
  option&lt;Int&gt; {\
    val x: Int? = 1\
    ensureNotNull(x)\
    println(x)\
    ensureNotNull(null)\
  }\
//sampleEnd\
  .let(::println)\
}\
// println: "1"\
// res: None<!--- KNIT example-option-computations-02.kt -->
