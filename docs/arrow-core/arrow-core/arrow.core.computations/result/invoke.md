//[arrow-core](../../../index.md)/[arrow.core.computations](../index.md)/[result](index.md)/[invoke](invoke.md)

# invoke

[common]\
inline operator fun &lt;[A](invoke.md)&gt; [invoke](invoke.md)(block: [ResultEffect](../-result-effect/index.md).() -&gt; [A](invoke.md)): [Result](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-result/index.html)&lt;[A](invoke.md)&gt;

Provides a computation block for [Result](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-result/index.html) which is build on top of Kotlin's Result Std operations.

import arrow.core.*\
\
fun main() {\
  result { // We can safely use assertion based operation inside blocks\
    kotlin.require(false) { "Boom" }\
  } // Result.Failure(IllegalArgumentException("Boom"))\
\
  result {\
    Result.failure(RuntimeException("Boom"))\
      .recover { 1 }\
      .bind()\
  } // Result.Success(1)\
\
  result {\
    val x = Result.success(1).bind()\
    val y = Result.success(x + 1).bind()\
    x + y\
  } // Result.Success(3)\
}<!--- KNIT example-result-computations-01.kt -->
