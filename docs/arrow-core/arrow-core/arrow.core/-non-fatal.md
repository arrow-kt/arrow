//[arrow-core](../../index.md)/[arrow.core](index.md)/[NonFatal](-non-fatal.md)

# NonFatal

[common]\
fun [NonFatal](-non-fatal.md)(t: [Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html)): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)

Extractor of non-fatal Throwable. Will not match fatal errors like VirtualMachineError (for example, OutOfMemoryError and StackOverflowError, subclasses of VirtualMachineError), ThreadDeath, LinkageError, InterruptedException. This will also not match [CancellationException](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines.cancellation/-cancellation-exception/index.html) since that's a fatal exception in Kotlin for cancellation purposes.

Checks whether the passed [t](-non-fatal.md) Throwable is NonFatal.

#### Return

true if the provided Throwable is to be considered non-fatal, or false if it is to be considered fatal

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
    if (NonFatal(t)) {\
        Either.Left(t)\
    } else {\
        throw t\
    }\
  }\
  //sampleEnd\
  println(nonFatal)\
}<!--- KNIT example-nonfatal-01.kt -->

## Parameters

common

| | |
|---|---|
| t | the Throwable to check |

js

| | |
|---|---|
| t | the Throwable to check |

jvm

| | |
|---|---|
| t | the Throwable to check |

native

| | |
|---|---|
| t | the Throwable to check |

[js, jvm, native]\
[js, jvm, native]\
fun [NonFatal](-non-fatal.md)(t: [Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html)): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)

## Parameters

common

| | |
|---|---|
| t | the Throwable to check |

js

| | |
|---|---|
| t | the Throwable to check |

jvm

| | |
|---|---|
| t | the Throwable to check |

native

| | |
|---|---|
| t | the Throwable to check |
