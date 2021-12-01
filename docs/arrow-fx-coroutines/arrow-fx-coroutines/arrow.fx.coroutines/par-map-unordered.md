//[arrow-fx-coroutines](../../index.md)/[arrow.fx.coroutines](index.md)/[parMapUnordered](par-map-unordered.md)

# parMapUnordered

[common]\

@FlowPreview

inline fun &lt;[A](par-map-unordered.md), [B](par-map-unordered.md)&gt; Flow&lt;[A](par-map-unordered.md)&gt;.[parMapUnordered](par-map-unordered.md)(concurrency: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) = DEFAULT_CONCURRENCY, crossinline transform: suspend ([A](par-map-unordered.md)) -&gt; [B](par-map-unordered.md)): Flow&lt;[B](par-map-unordered.md)&gt;

Like map, but will evaluate effects in parallel, emitting the results downstream. The number of concurrent effects is limited by [concurrency](par-map-unordered.md).

See [parMap](par-map.md) if retaining the original order of the stream is required.

import kotlinx.coroutines.delay\
import kotlinx.coroutines.flow.flowOf\
import kotlinx.coroutines.flow.toList\
import kotlinx.coroutines.flow.collect\
import arrow.fx.coroutines.parMapUnordered\
\
//sampleStart\
suspend fun main(): Unit {\
  flowOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)\
    .parMapUnordered { a -&gt;\
      delay(100)\
      a\
    }.toList() // [3, 5, 4, 6, 2, 8, 7, 1, 9, 10]\
}\
//sampleEnd<!--- KNIT example-flow-04.kt -->
