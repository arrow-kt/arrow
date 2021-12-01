//[arrow-fx-coroutines](../../index.md)/[arrow.fx.coroutines](index.md)/[parMap](par-map.md)

# parMap

[common]\

@FlowPreview

@ExperimentalCoroutinesApi

inline fun &lt;[A](par-map.md), [B](par-map.md)&gt; Flow&lt;[A](par-map.md)&gt;.[parMap](par-map.md)(concurrency: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) = DEFAULT_CONCURRENCY, crossinline transform: suspend CoroutineScope.([A](par-map.md)) -&gt; [B](par-map.md)): Flow&lt;[B](par-map.md)&gt;

Like map, but will evaluate [transform](par-map.md) in parallel, emitting the results downstream in **the same order as the input stream**. The number of concurrent effects is limited by [concurrency](par-map.md).

If [concurrency](par-map.md) is more than 1, then inner flows are be collected by this operator concurrently. With concurrency == 1 this operator is identical to map.

Applications of flowOn, buffer, and produceIn after this operator are fused with its concurrent merging so that only one properly configured channel is used for execution of merging logic.

See [parMapUnordered](par-map-unordered.md) if there is no requirement to retain the order of the original stream.

import kotlinx.coroutines.delay\
import kotlinx.coroutines.flow.flowOf\
import kotlinx.coroutines.flow.toList\
import kotlinx.coroutines.flow.collect\
import arrow.fx.coroutines.parMap\
\
//sampleStart\
suspend fun main(): Unit {\
  flowOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)\
    .parMap { a -&gt;\
      delay(100)\
      a\
    }.toList() // [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]\
}\
//sampleEnd<!--- KNIT example-flow-02.kt -->

The upstream source runs concurrently with downstream parMap, and thus the upstream concurrently runs, "prefetching", the next element. i.e.

 import arrow.fx.coroutines.*\
\
 suspend fun main(): Unit {\
 //sampleStart\
 val source = flowOf(1, 2, 3, 4)\
 source.parMap(concurrency= 2) {\
     println("Processing $it")\
     never&lt;Unit&gt;()\
   }.collect()\
//sampleEnd\
}<!--- KNIT example-flow-03.kt -->

1, 2, 3 will be emitted from source but only "Processing 1" & "Processing 2" will get printed.
