//[arrow-fx-coroutines](../../../index.md)/[arrow.fx.coroutines](../index.md)/[Resource](index.md)/[parZip](par-zip.md)

# parZip

[common]\
fun &lt;[B](par-zip.md), [C](par-zip.md)&gt; [parZip](par-zip.md)(fb: [Resource](index.md)&lt;[B](par-zip.md)&gt;, f: suspend ([A](index.md), [B](par-zip.md)) -&gt; [C](par-zip.md)): [Resource](index.md)&lt;[C](par-zip.md)&gt;

[common]\
fun &lt;[B](par-zip.md), [C](par-zip.md)&gt; [parZip](par-zip.md)(ctx: [CoroutineContext](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-coroutine-context/index.html) = Dispatchers.Default, fb: [Resource](index.md)&lt;[B](par-zip.md)&gt;, f: suspend ([A](index.md), [B](par-zip.md)) -&gt; [C](par-zip.md)): [Resource](index.md)&lt;[C](par-zip.md)&gt;

Composes two [Resource](index.md)s together by zipping them in parallel, by running both their acquire handlers in parallel, and both release handlers in parallel.

Useful in the case that starting a resource takes considerable computing resources or time.

import arrow.fx.coroutines.*\
import kotlinx.coroutines.delay\
\
class UserProcessor {\
  suspend fun start(): Unit { delay(750); println("Creating UserProcessor") }\
  fun shutdown(): Unit = println("Shutting down UserProcessor")\
  fun process(ds: DataSource): List&lt;String&gt; =\
   ds.users().map { "Processed $it" }\
}\
\
class DataSource {\
  suspend fun connect(): Unit { delay(1000); println("Connecting dataSource") }\
  fun users(): List&lt;String&gt; = listOf("User-1", "User-2", "User-3")\
  fun close(): Unit = println("Closed dataSource")\
}\
\
class Service(val db: DataSource, val userProcessor: UserProcessor) {\
  suspend fun processData(): List&lt;String&gt; = userProcessor.process(db)\
}\
\
//sampleStart\
val userProcessor = resource {\
  UserProcessor().also { it.start() }\
} release UserProcessor::shutdown\
\
val dataSource = resource {\
  DataSource().also { it.connect() }\
} release DataSource::close\
\
suspend fun main(): Unit {\
  userProcessor.parZip(dataSource) { userProcessor, ds -&gt;\
      Service(ds, userProcessor)\
    }.use { service -&gt; service.processData() }\
}\
//sampleEnd<!--- KNIT example-resource-07.kt -->
