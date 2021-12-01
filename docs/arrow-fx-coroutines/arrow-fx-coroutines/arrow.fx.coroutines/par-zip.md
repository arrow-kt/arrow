//[arrow-fx-coroutines](../../index.md)/[arrow.fx.coroutines](index.md)/[parZip](par-zip.md)

# parZip

[common]\
inline suspend fun &lt;[A](par-zip.md), [B](par-zip.md), [C](par-zip.md)&gt; [parZip](par-zip.md)(crossinline fa: suspend CoroutineScope.() -&gt; [A](par-zip.md), crossinline fb: suspend CoroutineScope.() -&gt; [B](par-zip.md), crossinline f: suspend CoroutineScope.([A](par-zip.md), [B](par-zip.md)) -&gt; [C](par-zip.md)): [C](par-zip.md)

Runs [fa](par-zip.md), [fb](par-zip.md) in parallel on Dispatchers.Default and combines their results using the provided function.

import arrow.fx.coroutines.*\
\
suspend fun main(): Unit {\
  //sampleStart\
  val result = parZip(\
    { "First one is on ${Thread.currentThread().name}" },\
    { "Second one is on ${Thread.currentThread().name}" }\
  ) { a, b -&gt;\
      "$a\n$b"\
    }\
  //sampleEnd\
 println(result)\
}<!--- KNIT example-parzip-01.kt -->

## See also

common

| | |
|---|---|
| [parZip](par-zip.md) | for a function that can run on any [CoroutineContext](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-coroutine-context/index.html) |

## Parameters

common

| | |
|---|---|
| fa | value to parallel map |
| fb | value to parallel map |
| f | function to map/combine value [A](par-zip.md) and [B](par-zip.md) |

[common]\
inline suspend fun &lt;[A](par-zip.md), [B](par-zip.md), [C](par-zip.md)&gt; [parZip](par-zip.md)(ctx: [CoroutineContext](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-coroutine-context/index.html) = EmptyCoroutineContext, crossinline fa: suspend CoroutineScope.() -&gt; [A](par-zip.md), crossinline fb: suspend CoroutineScope.() -&gt; [B](par-zip.md), crossinline f: suspend CoroutineScope.([A](par-zip.md), [B](par-zip.md)) -&gt; [C](par-zip.md)): [C](par-zip.md)

Runs [fa](par-zip.md), [fb](par-zip.md) in parallel on [ctx](par-zip.md) and combines their results using the provided function.

Coroutine context is inherited from a CoroutineScope, additional context elements can be specified with [ctx](par-zip.md) argument. If the combined context does not have any dispatcher nor any other [ContinuationInterceptor](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-continuation-interceptor/index.html), then Dispatchers.Default is used. **WARNING** If the combined context has a single threaded [ContinuationInterceptor](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-continuation-interceptor/index.html), this function will not run [fa](par-zip.md), [fb](par-zip.md) in parallel.

import arrow.fx.coroutines.*\
import kotlinx.coroutines.Dispatchers\
\
suspend fun main(): Unit {\
  //sampleStart\
  val result = parZip(\
    Dispatchers.IO,\
    { "First one is on ${Thread.currentThread().name}" },\
    { "Second one is on ${Thread.currentThread().name}" }\
  ) { a, b -&gt;\
      "$a\n$b"\
    }\
  //sampleEnd\
 println(result)\
}<!--- KNIT example-parzip-02.kt -->

## See also

common

| | |
|---|---|
| [parZip](par-zip.md) | for a function that ensures operations run in parallel on the Dispatchers.Default. |

## Parameters

common

| | |
|---|---|
| fa | value to parallel map |
| fb | value to parallel map |
| f | function to map/combine value [A](par-zip.md) and [B](par-zip.md) |

[common]\
inline suspend fun &lt;[A](par-zip.md), [B](par-zip.md), [C](par-zip.md), [D](par-zip.md)&gt; [parZip](par-zip.md)(crossinline fa: suspend CoroutineScope.() -&gt; [A](par-zip.md), crossinline fb: suspend CoroutineScope.() -&gt; [B](par-zip.md), crossinline fc: suspend CoroutineScope.() -&gt; [C](par-zip.md), crossinline f: suspend CoroutineScope.([A](par-zip.md), [B](par-zip.md), [C](par-zip.md)) -&gt; [D](par-zip.md)): [D](par-zip.md)

Runs [fa](par-zip.md), [fb](par-zip.md), [fc](par-zip.md) in parallel on Dispatchers.Default and combines their results using the provided function.

import arrow.fx.coroutines.*\
\
suspend fun main(): Unit {\
  //sampleStart\
  val result = parZip(\
    { "First one is on ${Thread.currentThread().name}" },\
    { "Second one is on ${Thread.currentThread().name}" },\
    { "Third one is on ${Thread.currentThread().name}" }\
  ) { a, b, c -&gt;\
      "$a\n$b\n$c"\
    }\
  //sampleEnd\
 println(result)\
}<!--- KNIT example-parzip-03.kt -->

## See also

common

| | |
|---|---|
| [parZip](par-zip.md) | for a function that can run on any [CoroutineContext](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-coroutine-context/index.html). |

## Parameters

common

| | |
|---|---|
| fa | value to parallel map |
| fb | value to parallel map |
| fc | value to parallel map |
| f | function to map/combine value [A](par-zip.md), [B](par-zip.md) and [C](par-zip.md) |

[common]\
inline suspend fun &lt;[A](par-zip.md), [B](par-zip.md), [C](par-zip.md), [D](par-zip.md)&gt; [parZip](par-zip.md)(ctx: [CoroutineContext](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-coroutine-context/index.html) = EmptyCoroutineContext, crossinline fa: suspend CoroutineScope.() -&gt; [A](par-zip.md), crossinline fb: suspend CoroutineScope.() -&gt; [B](par-zip.md), crossinline fc: suspend CoroutineScope.() -&gt; [C](par-zip.md), crossinline f: suspend CoroutineScope.([A](par-zip.md), [B](par-zip.md), [C](par-zip.md)) -&gt; [D](par-zip.md)): [D](par-zip.md)

Runs [fa](par-zip.md), [fb](par-zip.md), [fc](par-zip.md) in parallel on [ctx](par-zip.md) and combines their results using the provided function.

Coroutine context is inherited from a CoroutineScope, additional context elements can be specified with [ctx](par-zip.md) argument. If the combined context does not have any dispatcher nor any other [ContinuationInterceptor](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-continuation-interceptor/index.html), then Dispatchers.Default is used. **WARNING** If the combined context has a single threaded [ContinuationInterceptor](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-continuation-interceptor/index.html), this function will not run [fa](par-zip.md), [fb](par-zip.md)&[fc](par-zip.md) in parallel.

import arrow.fx.coroutines.*\
import kotlinx.coroutines.Dispatchers\
\
suspend fun main(): Unit {\
  //sampleStart\
  val result = parZip(\
    Dispatchers.IO,\
    { "First one is on ${Thread.currentThread().name}" },\
    { "Second one is on ${Thread.currentThread().name}" },\
    { "Third one is on ${Thread.currentThread().name}" }\
  ) { a, b, c -&gt;\
      "$a\n$b\n$c"\
    }\
  //sampleEnd\
 println(result)\
}<!--- KNIT example-parzip-04.kt -->

## See also

common

| | |
|---|---|
| [parZip](par-zip.md) | for a function that ensures operations run in parallel on the Dispatchers.Default. |

## Parameters

common

| | |
|---|---|
| fa | value to parallel map |
| fb | value to parallel map |
| fc | value to parallel map |
| f | function to map/combine value [A](par-zip.md), [B](par-zip.md) and [C](par-zip.md). |

[common]\
inline suspend fun &lt;[A](par-zip.md), [B](par-zip.md), [C](par-zip.md), [D](par-zip.md), [E](par-zip.md)&gt; [parZip](par-zip.md)(crossinline fa: suspend CoroutineScope.() -&gt; [A](par-zip.md), crossinline fb: suspend CoroutineScope.() -&gt; [B](par-zip.md), crossinline fc: suspend CoroutineScope.() -&gt; [C](par-zip.md), crossinline fd: suspend CoroutineScope.() -&gt; [D](par-zip.md), crossinline f: suspend CoroutineScope.([A](par-zip.md), [B](par-zip.md), [C](par-zip.md), [D](par-zip.md)) -&gt; [E](par-zip.md)): [E](par-zip.md)

Runs [fa](par-zip.md), [fb](par-zip.md), [fc](par-zip.md), [fd](par-zip.md) in parallel on Dispatchers.Default and combines their results using the provided function.

import arrow.fx.coroutines.*\
\
suspend fun main(): Unit {\
  //sampleStart\
  val result = parZip(\
    { "First one is on ${Thread.currentThread().name}" },\
    { "Second one is on ${Thread.currentThread().name}" },\
    { "Third one is on ${Thread.currentThread().name}" },\
    { "Fourth one is on ${Thread.currentThread().name}" }\
  ) { a, b, c, d -&gt;\
      "$a\n$b\n$c\n$d"\
    }\
  //sampleEnd\
 println(result)\
}<!--- KNIT example-parzip-05.kt -->

## See also

common

| | |
|---|---|
| [parZip](par-zip.md) | for a function that can run on any [CoroutineContext](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-coroutine-context/index.html). |

## Parameters

common

| | |
|---|---|
| fa | value to parallel map |
| fb | value to parallel map |
| fc | value to parallel map |
| fd | value to parallel map |
| f | function to map/combine value [A](par-zip.md), [B](par-zip.md), [C](par-zip.md) and [D](par-zip.md) |

[common]\
inline suspend fun &lt;[A](par-zip.md), [B](par-zip.md), [C](par-zip.md), [D](par-zip.md), [E](par-zip.md)&gt; [parZip](par-zip.md)(ctx: [CoroutineContext](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-coroutine-context/index.html) = EmptyCoroutineContext, crossinline fa: suspend CoroutineScope.() -&gt; [A](par-zip.md), crossinline fb: suspend CoroutineScope.() -&gt; [B](par-zip.md), crossinline fc: suspend CoroutineScope.() -&gt; [C](par-zip.md), crossinline fd: suspend CoroutineScope.() -&gt; [D](par-zip.md), crossinline f: suspend CoroutineScope.([A](par-zip.md), [B](par-zip.md), [C](par-zip.md), [D](par-zip.md)) -&gt; [E](par-zip.md)): [E](par-zip.md)

Runs [fa](par-zip.md), [fb](par-zip.md), [fc](par-zip.md), [fd](par-zip.md) in parallel on [ctx](par-zip.md) and combines their results using the provided function.

Coroutine context is inherited from a CoroutineScope, additional context elements can be specified with [ctx](par-zip.md) argument. If the combined context does not have any dispatcher nor any other [ContinuationInterceptor](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-continuation-interceptor/index.html), then Dispatchers.Default is used. **WARNING** If the combined context has a single threaded [ContinuationInterceptor](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-continuation-interceptor/index.html), this function will not run [fa](par-zip.md), [fb](par-zip.md), [fc](par-zip.md)&[fd](par-zip.md) in parallel.

import arrow.fx.coroutines.*\
import kotlinx.coroutines.Dispatchers\
\
suspend fun main(): Unit {\
  //sampleStart\
  val result = parZip(\
    Dispatchers.IO,\
    { "First one is on ${Thread.currentThread().name}" },\
    { "Second one is on ${Thread.currentThread().name}" },\
    { "Third one is on ${Thread.currentThread().name}" },\
    { "Fourth one is on ${Thread.currentThread().name}" }\
  ) { a, b, c, d -&gt;\
      "$a\n$b\n$c\n$d"\
    }\
  //sampleEnd\
 println(result)\
}<!--- KNIT example-parzip-06.kt -->

## See also

common

| | |
|---|---|
| [parZip](par-zip.md) | for a function that ensures operations run in parallel on the Dispatchers.Default. |

## Parameters

common

| | |
|---|---|
| fa | value to parallel map |
| fb | value to parallel map |
| fc | value to parallel map |
| fd | value to parallel map |
| f | function to map/combine value [A](par-zip.md), [B](par-zip.md), [C](par-zip.md) and [D](par-zip.md). |

[common]\
inline suspend fun &lt;[A](par-zip.md), [B](par-zip.md), [C](par-zip.md), [D](par-zip.md), [E](par-zip.md), [F](par-zip.md)&gt; [parZip](par-zip.md)(crossinline fa: suspend CoroutineScope.() -&gt; [A](par-zip.md), crossinline fb: suspend CoroutineScope.() -&gt; [B](par-zip.md), crossinline fc: suspend CoroutineScope.() -&gt; [C](par-zip.md), crossinline fd: suspend CoroutineScope.() -&gt; [D](par-zip.md), crossinline fe: suspend CoroutineScope.() -&gt; [E](par-zip.md), crossinline f: suspend CoroutineScope.([A](par-zip.md), [B](par-zip.md), [C](par-zip.md), [D](par-zip.md), [E](par-zip.md)) -&gt; [F](par-zip.md)): [F](par-zip.md)

Runs [fa](par-zip.md), [fb](par-zip.md), [fc](par-zip.md), [fd](par-zip.md), [fe](par-zip.md) in parallel on Dispatchers.Default and combines their results using the provided function.

import arrow.fx.coroutines.*\
\
suspend fun main(): Unit {\
  //sampleStart\
  val result = parZip(\
    { "First one is on ${Thread.currentThread().name}" },\
    { "Second one is on ${Thread.currentThread().name}" },\
    { "Third one is on ${Thread.currentThread().name}" },\
    { "Fourth one is on ${Thread.currentThread().name}" },\
    { "Fifth one is on ${Thread.currentThread().name}" }\
  ) { a, b, c, d, e -&gt;\
      "$a\n$b\n$c\n$d\n$e"\
    }\
  //sampleEnd\
 println(result)\
}<!--- KNIT example-parzip-07.kt -->

## See also

common

| | |
|---|---|
| [parZip](par-zip.md) | for a function that can run on any [CoroutineContext](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-coroutine-context/index.html). |

## Parameters

common

| | |
|---|---|
| fa | value to parallel map |
| fb | value to parallel map |
| fc | value to parallel map |
| fd | value to parallel map |
| fe | value to parallel map |
| f | function to map/combine value [A](par-zip.md), [B](par-zip.md), [C](par-zip.md), [D](par-zip.md) and [E](par-zip.md) |

[common]\
inline suspend fun &lt;[A](par-zip.md), [B](par-zip.md), [C](par-zip.md), [D](par-zip.md), [E](par-zip.md), [F](par-zip.md)&gt; [parZip](par-zip.md)(ctx: [CoroutineContext](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-coroutine-context/index.html) = EmptyCoroutineContext, crossinline fa: suspend CoroutineScope.() -&gt; [A](par-zip.md), crossinline fb: suspend CoroutineScope.() -&gt; [B](par-zip.md), crossinline fc: suspend CoroutineScope.() -&gt; [C](par-zip.md), crossinline fd: suspend CoroutineScope.() -&gt; [D](par-zip.md), crossinline fe: suspend CoroutineScope.() -&gt; [E](par-zip.md), crossinline f: suspend CoroutineScope.([A](par-zip.md), [B](par-zip.md), [C](par-zip.md), [D](par-zip.md), [E](par-zip.md)) -&gt; [F](par-zip.md)): [F](par-zip.md)

Runs [fa](par-zip.md), [fb](par-zip.md), [fc](par-zip.md), [fd](par-zip.md), [fe](par-zip.md) in parallel on [ctx](par-zip.md) and combines their results using the provided function.

Coroutine context is inherited from a CoroutineScope, additional context elements can be specified with [ctx](par-zip.md) argument. If the combined context does not have any dispatcher nor any other [ContinuationInterceptor](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-continuation-interceptor/index.html), then Dispatchers.Default is used. **WARNING** If the combined context has a single threaded [ContinuationInterceptor](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-continuation-interceptor/index.html), this function will not run [fa](par-zip.md), [fb](par-zip.md), [fc](par-zip.md), [fd](par-zip.md)&[fe](par-zip.md) in parallel.

import arrow.fx.coroutines.*\
import kotlinx.coroutines.Dispatchers\
\
suspend fun main(): Unit {\
  //sampleStart\
  val result = parZip(\
    Dispatchers.IO,\
    { "First one is on ${Thread.currentThread().name}" },\
    { "Second one is on ${Thread.currentThread().name}" },\
    { "Third one is on ${Thread.currentThread().name}" },\
    { "Fourth one is on ${Thread.currentThread().name}" },\
    { "Fifth one is on ${Thread.currentThread().name}" }\
  ) { a, b, c, d, e -&gt;\
      "$a\n$b\n$c\n$d\n$e"\
    }\
  //sampleEnd\
 println(result)\
}<!--- KNIT example-parzip-08.kt -->

## See also

common

| | |
|---|---|
| [parZip](par-zip.md) | for a function that ensures operations run in parallel on the Dispatchers.Default. |

## Parameters

common

| | |
|---|---|
| fa | value to parallel map |
| fb | value to parallel map |
| fc | value to parallel map |
| fd | value to parallel map |
| fe | value to parallel map |
| f | function to map/combine value [A](par-zip.md), [B](par-zip.md), [C](par-zip.md), [D](par-zip.md), and [E](par-zip.md). |

[common]\
inline suspend fun &lt;[A](par-zip.md), [B](par-zip.md), [C](par-zip.md), [D](par-zip.md), [E](par-zip.md), [F](par-zip.md), [G](par-zip.md)&gt; [parZip](par-zip.md)(crossinline fa: suspend CoroutineScope.() -&gt; [A](par-zip.md), crossinline fb: suspend CoroutineScope.() -&gt; [B](par-zip.md), crossinline fc: suspend CoroutineScope.() -&gt; [C](par-zip.md), crossinline fd: suspend CoroutineScope.() -&gt; [D](par-zip.md), crossinline fe: suspend CoroutineScope.() -&gt; [E](par-zip.md), crossinline ff: suspend CoroutineScope.() -&gt; [F](par-zip.md), crossinline f: suspend CoroutineScope.([A](par-zip.md), [B](par-zip.md), [C](par-zip.md), [D](par-zip.md), [E](par-zip.md), [F](par-zip.md)) -&gt; [G](par-zip.md)): [G](par-zip.md)

Runs [fa](par-zip.md), [fb](par-zip.md), [fc](par-zip.md), [fd](par-zip.md), [fe](par-zip.md), [ff](par-zip.md) in parallel on Dispatchers.Default and combines their results using the provided function.

import arrow.fx.coroutines.*\
\
suspend fun main(): Unit {\
  //sampleStart\
  val result = parZip(\
    { "First one is on ${Thread.currentThread().name}" },\
    { "Second one is on ${Thread.currentThread().name}" },\
    { "Third one is on ${Thread.currentThread().name}" },\
    { "Fourth one is on ${Thread.currentThread().name}" },\
    { "Fifth one is on ${Thread.currentThread().name}" },\
    { "Sixth one is on ${Thread.currentThread().name}" }\
  ) { a, b, c, d, e, f -&gt;\
      "$a\n$b\n$c\n$d\n$e\n$f"\
    }\
  //sampleEnd\
 println(result)\
}<!--- KNIT example-parzip-09.kt -->

## See also

common

| | |
|---|---|
| [parZip](par-zip.md) | for a function that can run on any [CoroutineContext](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-coroutine-context/index.html). |

## Parameters

common

| | |
|---|---|
| fa | value to parallel map |
| fb | value to parallel map |
| fc | value to parallel map |
| fd | value to parallel map |
| fe | value to parallel map |
| ff | value to parallel map |
| f | function to map/combine value [A](par-zip.md), [B](par-zip.md), [C](par-zip.md), [D](par-zip.md), [E](par-zip.md) and [F](par-zip.md) |

[common]\
inline suspend fun &lt;[A](par-zip.md), [B](par-zip.md), [C](par-zip.md), [D](par-zip.md), [E](par-zip.md), [F](par-zip.md), [G](par-zip.md)&gt; [parZip](par-zip.md)(ctx: [CoroutineContext](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-coroutine-context/index.html) = EmptyCoroutineContext, crossinline fa: suspend CoroutineScope.() -&gt; [A](par-zip.md), crossinline fb: suspend CoroutineScope.() -&gt; [B](par-zip.md), crossinline fc: suspend CoroutineScope.() -&gt; [C](par-zip.md), crossinline fd: suspend CoroutineScope.() -&gt; [D](par-zip.md), crossinline fe: suspend CoroutineScope.() -&gt; [E](par-zip.md), crossinline ff: suspend CoroutineScope.() -&gt; [F](par-zip.md), crossinline f: suspend CoroutineScope.([A](par-zip.md), [B](par-zip.md), [C](par-zip.md), [D](par-zip.md), [E](par-zip.md), [F](par-zip.md)) -&gt; [G](par-zip.md)): [G](par-zip.md)

Runs [fa](par-zip.md), [fb](par-zip.md), [fc](par-zip.md), [fd](par-zip.md), [fe](par-zip.md), [ff](par-zip.md) in parallel on [ctx](par-zip.md) and combines their results using the provided function.

Coroutine context is inherited from a CoroutineScope, additional context elements can be specified with [ctx](par-zip.md) argument. If the combined context does not have any dispatcher nor any other [ContinuationInterceptor](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-continuation-interceptor/index.html), then Dispatchers.Default is used. **WARNING** If the combined context has a single threaded [ContinuationInterceptor](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-continuation-interceptor/index.html), this function will not run [fa](par-zip.md), [fb](par-zip.md), [fc](par-zip.md), [fd](par-zip.md), [fe](par-zip.md)&[ff](par-zip.md) in parallel.

import arrow.fx.coroutines.*\
import kotlinx.coroutines.Dispatchers\
\
suspend fun main(): Unit {\
  //sampleStart\
  val result = parZip(\
    Dispatchers.IO,\
    { "First one is on ${Thread.currentThread().name}" },\
    { "Second one is on ${Thread.currentThread().name}" },\
    { "Third one is on ${Thread.currentThread().name}" },\
    { "Fourth one is on ${Thread.currentThread().name}" },\
    { "Fifth one is on ${Thread.currentThread().name}" },\
    { "Sixth one is on ${Thread.currentThread().name}" }\
  ) { a, b, c, d, e, g -&gt;\
      "$a\n$b\n$c\n$d\n$e\n$g"\
    }\
  //sampleEnd\
 println(result)\
}<!--- KNIT example-parzip-10.kt -->

## See also

common

| | |
|---|---|
| [parZip](par-zip.md) | for a function that ensures operations run in parallel on the Dispatchers.Default. |

## Parameters

common

| | |
|---|---|
| fa | value to parallel map |
| fb | value to parallel map |
| fc | value to parallel map |
| fd | value to parallel map |
| fe | value to parallel map |
| ff | value to parallel map |
| f | function to map/combine value [A](par-zip.md), [B](par-zip.md), [C](par-zip.md), [D](par-zip.md), [E](par-zip.md) and [F](par-zip.md) |

[common]\
inline suspend fun &lt;[A](par-zip.md), [B](par-zip.md), [C](par-zip.md), [D](par-zip.md), [E](par-zip.md), [F](par-zip.md), [G](par-zip.md), [H](par-zip.md)&gt; [parZip](par-zip.md)(crossinline fa: suspend CoroutineScope.() -&gt; [A](par-zip.md), crossinline fb: suspend CoroutineScope.() -&gt; [B](par-zip.md), crossinline fc: suspend CoroutineScope.() -&gt; [C](par-zip.md), crossinline fd: suspend CoroutineScope.() -&gt; [D](par-zip.md), crossinline fe: suspend CoroutineScope.() -&gt; [E](par-zip.md), crossinline ff: suspend CoroutineScope.() -&gt; [F](par-zip.md), crossinline fg: suspend CoroutineScope.() -&gt; [G](par-zip.md), crossinline f: suspend CoroutineScope.([A](par-zip.md), [B](par-zip.md), [C](par-zip.md), [D](par-zip.md), [E](par-zip.md), [F](par-zip.md), [G](par-zip.md)) -&gt; [H](par-zip.md)): [H](par-zip.md)

Runs [fa](par-zip.md), [fb](par-zip.md), [fc](par-zip.md), [fd](par-zip.md), [fe](par-zip.md), [ff](par-zip.md), [fg](par-zip.md) in parallel on Dispatchers.Default and combines their results using the provided function.

import arrow.fx.coroutines.*\
\
suspend fun main(): Unit {\
  //sampleStart\
  val result = parZip(\
    { "First one is on ${Thread.currentThread().name}" },\
    { "Second one is on ${Thread.currentThread().name}" },\
    { "Third one is on ${Thread.currentThread().name}" },\
    { "Fourth one is on ${Thread.currentThread().name}" },\
    { "Fifth one is on ${Thread.currentThread().name}" },\
    { "Sixth one is on ${Thread.currentThread().name}" },\
    { "Seventh one is on ${Thread.currentThread().name}" }\
  ) { a, b, c, d, e, g, h -&gt;\
      "$a\n$b\n$c\n$d\n$e\n$g\n$h"\
    }\
  //sampleEnd\
 println(result)\
}<!--- KNIT example-parzip-11.kt -->

## See also

common

| | |
|---|---|
| [parZip](par-zip.md) | for a function that can run on any [CoroutineContext](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-coroutine-context/index.html). |

## Parameters

common

| | |
|---|---|
| fa | value to parallel map |
| fb | value to parallel map |
| fc | value to parallel map |
| fd | value to parallel map |
| fe | value to parallel map |
| ff | value to parallel map |
| fg | value to parallel map |
| f | function to map/combine value [A](par-zip.md), [B](par-zip.md), [C](par-zip.md), [D](par-zip.md), [E](par-zip.md), [F](par-zip.md) and [G](par-zip.md) |

[common]\
inline suspend fun &lt;[A](par-zip.md), [B](par-zip.md), [C](par-zip.md), [D](par-zip.md), [E](par-zip.md), [F](par-zip.md), [G](par-zip.md), [H](par-zip.md)&gt; [parZip](par-zip.md)(ctx: [CoroutineContext](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-coroutine-context/index.html) = EmptyCoroutineContext, crossinline fa: suspend CoroutineScope.() -&gt; [A](par-zip.md), crossinline fb: suspend CoroutineScope.() -&gt; [B](par-zip.md), crossinline fc: suspend CoroutineScope.() -&gt; [C](par-zip.md), crossinline fd: suspend CoroutineScope.() -&gt; [D](par-zip.md), crossinline fe: suspend CoroutineScope.() -&gt; [E](par-zip.md), crossinline ff: suspend CoroutineScope.() -&gt; [F](par-zip.md), crossinline fg: suspend CoroutineScope.() -&gt; [G](par-zip.md), crossinline f: suspend CoroutineScope.([A](par-zip.md), [B](par-zip.md), [C](par-zip.md), [D](par-zip.md), [E](par-zip.md), [F](par-zip.md), [G](par-zip.md)) -&gt; [H](par-zip.md)): [H](par-zip.md)

Runs [fa](par-zip.md), [fb](par-zip.md), [fc](par-zip.md), [fd](par-zip.md), [fe](par-zip.md), [ff](par-zip.md), [fg](par-zip.md) in parallel on [ctx](par-zip.md) and combines their results using the provided function.

Coroutine context is inherited from a CoroutineScope, additional context elements can be specified with [ctx](par-zip.md) argument. If the combined context does not have any dispatcher nor any other [ContinuationInterceptor](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-continuation-interceptor/index.html), then Dispatchers.Default is used. **WARNING** If the combined context has a single threaded [ContinuationInterceptor](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-continuation-interceptor/index.html), this function will not run [fa](par-zip.md), [fb](par-zip.md), [fc](par-zip.md), [fd](par-zip.md), [fe](par-zip.md), [ff](par-zip.md)&[fg](par-zip.md) in parallel.

import arrow.fx.coroutines.*\
import kotlinx.coroutines.Dispatchers\
\
suspend fun main(): Unit {\
  //sampleStart\
  val result = parZip(\
    Dispatchers.IO,\
    { "First one is on ${Thread.currentThread().name}" },\
    { "Second one is on ${Thread.currentThread().name}" },\
    { "Third one is on ${Thread.currentThread().name}" },\
    { "Fourth one is on ${Thread.currentThread().name}" },\
    { "Fifth one is on ${Thread.currentThread().name}" },\
    { "Sixth one is on ${Thread.currentThread().name}" },\
    { "Seventh one is on ${Thread.currentThread().name}" }\
  ) { a, b, c, d, e, f, g -&gt;\
      "$a\n$b\n$c\n$d\n$e\n$f\n$g"\
    }\
  //sampleEnd\
 println(result)\
}<!--- KNIT example-parzip-12.kt -->

## See also

common

| | |
|---|---|
| [parZip](par-zip.md) | for a function that ensures operations run in parallel on the Dispatchers.Default. |

## Parameters

common

| | |
|---|---|
| fa | value to parallel map |
| fb | value to parallel map |
| fc | value to parallel map |
| fd | value to parallel map |
| fe | value to parallel map |
| ff | value to parallel map |
| fg | value to parallel map |
| f | function to map/combine value [A](par-zip.md), [B](par-zip.md), [C](par-zip.md), [D](par-zip.md), [E](par-zip.md), [F](par-zip.md) and [G](par-zip.md) |

[common]\
inline suspend fun &lt;[A](par-zip.md), [B](par-zip.md), [C](par-zip.md), [D](par-zip.md), [E](par-zip.md), [F](par-zip.md), [G](par-zip.md), [H](par-zip.md), [I](par-zip.md)&gt; [parZip](par-zip.md)(crossinline fa: suspend CoroutineScope.() -&gt; [A](par-zip.md), crossinline fb: suspend CoroutineScope.() -&gt; [B](par-zip.md), crossinline fc: suspend CoroutineScope.() -&gt; [C](par-zip.md), crossinline fd: suspend CoroutineScope.() -&gt; [D](par-zip.md), crossinline fe: suspend CoroutineScope.() -&gt; [E](par-zip.md), crossinline ff: suspend CoroutineScope.() -&gt; [F](par-zip.md), crossinline fg: suspend CoroutineScope.() -&gt; [G](par-zip.md), crossinline fh: suspend CoroutineScope.() -&gt; [H](par-zip.md), crossinline f: suspend CoroutineScope.([A](par-zip.md), [B](par-zip.md), [C](par-zip.md), [D](par-zip.md), [E](par-zip.md), [F](par-zip.md), [G](par-zip.md), [H](par-zip.md)) -&gt; [I](par-zip.md)): [I](par-zip.md)

Runs [fa](par-zip.md), [fb](par-zip.md), [fc](par-zip.md), [fd](par-zip.md), [fe](par-zip.md), [ff](par-zip.md), [fg](par-zip.md), [fh](par-zip.md) in parallel on Dispatchers.Default and combines their results using the provided function.

import arrow.fx.coroutines.*\
\
suspend fun main(): Unit {\
  //sampleStart\
  val result = parZip(\
    { "First one is on ${Thread.currentThread().name}" },\
    { "Second one is on ${Thread.currentThread().name}" },\
    { "Third one is on ${Thread.currentThread().name}" },\
    { "Fourth one is on ${Thread.currentThread().name}" },\
    { "Fifth one is on ${Thread.currentThread().name}" },\
    { "Sixth one is on ${Thread.currentThread().name}" },\
    { "Seventh one is on ${Thread.currentThread().name}" },\
    { "Eighth one is on ${Thread.currentThread().name}" }\
  ) { a, b, c, d, e, f, g, h -&gt;\
      "$a\n$b\n$c\n$d\n$e\n$f\n$g\n$h"\
    }\
  //sampleEnd\
 println(result)\
}<!--- KNIT example-parzip-13.kt -->

## See also

common

| | |
|---|---|
| [parZip](par-zip.md) | for a function that can run on any [CoroutineContext](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-coroutine-context/index.html). |

## Parameters

common

| | |
|---|---|
| fa | value to parallel map |
| fb | value to parallel map |
| fc | value to parallel map |
| fd | value to parallel map |
| fe | value to parallel map |
| ff | value to parallel map |
| fg | value to parallel map |
| fh | value to parallel map |
| f | function to map/combine value [A](par-zip.md), [B](par-zip.md), [C](par-zip.md), [D](par-zip.md), [E](par-zip.md), [F](par-zip.md), [G](par-zip.md) and [H](par-zip.md) |

[common]\
inline suspend fun &lt;[A](par-zip.md), [B](par-zip.md), [C](par-zip.md), [D](par-zip.md), [E](par-zip.md), [F](par-zip.md), [G](par-zip.md), [H](par-zip.md), [I](par-zip.md)&gt; [parZip](par-zip.md)(ctx: [CoroutineContext](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-coroutine-context/index.html) = EmptyCoroutineContext, crossinline fa: suspend CoroutineScope.() -&gt; [A](par-zip.md), crossinline fb: suspend CoroutineScope.() -&gt; [B](par-zip.md), crossinline fc: suspend CoroutineScope.() -&gt; [C](par-zip.md), crossinline fd: suspend CoroutineScope.() -&gt; [D](par-zip.md), crossinline fe: suspend CoroutineScope.() -&gt; [E](par-zip.md), crossinline ff: suspend CoroutineScope.() -&gt; [F](par-zip.md), crossinline fg: suspend CoroutineScope.() -&gt; [G](par-zip.md), crossinline fh: suspend CoroutineScope.() -&gt; [H](par-zip.md), crossinline f: suspend CoroutineScope.([A](par-zip.md), [B](par-zip.md), [C](par-zip.md), [D](par-zip.md), [E](par-zip.md), [F](par-zip.md), [G](par-zip.md), [H](par-zip.md)) -&gt; [I](par-zip.md)): [I](par-zip.md)

Runs [fa](par-zip.md), [fb](par-zip.md), [fc](par-zip.md), [fd](par-zip.md), [fe](par-zip.md), [ff](par-zip.md), [fg](par-zip.md), [fh](par-zip.md) in parallel on [ctx](par-zip.md) and combines their results using the provided function.

Coroutine context is inherited from a CoroutineScope, additional context elements can be specified with [ctx](par-zip.md) argument. If the combined context does not have any dispatcher nor any other [ContinuationInterceptor](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-continuation-interceptor/index.html), then Dispatchers.Default is used. **WARNING** If the combined context has a single threaded [ContinuationInterceptor](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-continuation-interceptor/index.html), this function will not run [fa](par-zip.md), [fb](par-zip.md), [fc](par-zip.md), [fd](par-zip.md), [fe](par-zip.md), [ff](par-zip.md)&[fg](par-zip.md) in parallel.

import arrow.fx.coroutines.*\
import kotlinx.coroutines.Dispatchers\
\
suspend fun main(): Unit {\
  //sampleStart\
  val result = parZip(\
    Dispatchers.IO,\
    { "First one is on ${Thread.currentThread().name}" },\
    { "Second one is on ${Thread.currentThread().name}" },\
    { "Third one is on ${Thread.currentThread().name}" },\
    { "Fourth one is on ${Thread.currentThread().name}" },\
    { "Fifth one is on ${Thread.currentThread().name}" },\
    { "Sixth one is on ${Thread.currentThread().name}" },\
    { "Seventh one is on ${Thread.currentThread().name}" }\
  ) { a, b, c, d, e, f, g -&gt;\
      "$a\n$b\n$c\n$d\n$e\n$f\n$g"\
    }\
  //sampleEnd\
 println(result)\
}<!--- KNIT example-parzip-14.kt -->

## See also

common

| | |
|---|---|
| [parZip](par-zip.md) | for a function that ensures operations run in parallel on the Dispatchers.Default. |

## Parameters

common

| | |
|---|---|
| fa | value to parallel map |
| fb | value to parallel map |
| fc | value to parallel map |
| fd | value to parallel map |
| fe | value to parallel map |
| ff | value to parallel map |
| fg | value to parallel map |
| fh | value to parallel map |
| f | function to map/combine value [A](par-zip.md), [B](par-zip.md), [C](par-zip.md), [D](par-zip.md), [E](par-zip.md), [F](par-zip.md), [G](par-zip.md) and [H](par-zip.md) |
