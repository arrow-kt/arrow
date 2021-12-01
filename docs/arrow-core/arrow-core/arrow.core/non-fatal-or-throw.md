//[arrow-core](../../index.md)/[arrow.core](index.md)/[nonFatalOrThrow](non-fatal-or-throw.md)

# nonFatalOrThrow

[common]\
fun [Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html).[nonFatalOrThrow](non-fatal-or-throw.md)(): [Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html)

Returns the Throwable if NonFatal and throws it otherwise.

#### Return

the Throwable this if NonFatal

import arrow.*\
import arrow.core.*\
\
fun unsafeFunction(i: Int): String =\
   when (i) {\
        1 -&gt; throw IllegalArgumentException("Non-Fatal")\
        2 -&gt; throw OutOfMemoryError("Fatal")\
        else -&gt; "Hello"\
   }\
\
fun main(args: Array&lt;String&gt;) {\
  val nonFatal: Either&lt;Throwable, String&gt; =\
  //sampleStart\
  try {\
     Either.Right(unsafeFunction(1))\
  } catch (t: Throwable) {\
      Either.Left(t.nonFatalOrThrow())\
  }\
  //sampleEnd\
  println(nonFatal)\
}<!--- KNIT example-nonfatalorthrow-01.kt -->

## Throws

| | |
|---|---|
| [kotlin.Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html) | the Throwable this if Fatal |
