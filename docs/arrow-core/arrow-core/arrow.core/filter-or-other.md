//[arrow-core](../../index.md)/[arrow.core](index.md)/[filterOrOther](filter-or-other.md)

# filterOrOther

[common]\
inline fun &lt;[A](filter-or-other.md), [B](filter-or-other.md)&gt; [Either](-either/index.md)&lt;[A](filter-or-other.md), [B](filter-or-other.md)&gt;.[filterOrOther](filter-or-other.md)(predicate: ([B](filter-or-other.md)) -&gt; [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html), default: ([B](filter-or-other.md)) -&gt; [A](filter-or-other.md)): [Either](-either/index.md)&lt;[A](filter-or-other.md), [B](filter-or-other.md)&gt;

Returns [Right](-either/-right/index.md) with the existing value of [Right](-either/-right/index.md) if this is a [Right](-either/-right/index.md) and the given predicate holds for the right value.<br>

Returns Left(default({right})) if this is a [Right](-either/-right/index.md) and the given predicate does not hold for the right value. Useful for error handling where 'default' returns a message with context on why the value did not pass the filter<br>

Returns [Left](-either/-left/index.md) with the existing value of [Left](-either/-left/index.md) if this is a [Left](-either/-left/index.md).<br>

Example:

import arrow.core.*\
\
suspend fun main(): Unit {\
  //sampleStart\
  Either.Right(7).filterOrOther({ it == 10 }, { "Value '$it' is not equal to 10" })\
    .let(::println) // Either.Left(Value '7' is not equal to 10")\
\
  Either.Right(10).filterOrOther({ it == 10 }, { "Value '$it' is not equal to 10" })\
    .let(::println) // Either.Right(10)\
\
  Either.Left(12).filterOrOther({ str: String -&gt; str.contains("impossible") }, { -1 })\
    .let(::println) // Either.Left(12)\
  //sampleEnd\
}<!--- KNIT example-either-61.kt -->
