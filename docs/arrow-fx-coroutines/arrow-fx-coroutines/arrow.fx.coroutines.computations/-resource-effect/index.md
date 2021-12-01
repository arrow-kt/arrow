//[arrow-fx-coroutines](../../../index.md)/[arrow.fx.coroutines.computations](../index.md)/[ResourceEffect](index.md)

# ResourceEffect

[common]\
interface [ResourceEffect](index.md)

Computation block for the [Resource](../../arrow.fx.coroutines/-resource/index.md) type. The [Resource](../../arrow.fx.coroutines/-resource/index.md) allows us to describe resources as immutable values, and compose them together in simple ways. This way you can split the logic of what a Resource is and how it should be closed from how you use them.

<ul><li>#  Using and composing Resource</li></ul>

import arrow.fx.coroutines.computations.resource\
import arrow.fx.coroutines.release\
\
class UserProcessor {\
  fun start(): Unit = println("Creating UserProcessor")\
  fun shutdown(): Unit = println("Shutting down UserProcessor")\
  fun process(ds: DataSource): List&lt;String&gt; =\
   ds.users().map { "Processed $it" }\
}\
\
class DataSource {\
  fun connect(): Unit = println("Connecting dataSource")\
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
  UserProcessor().also(UserProcessor::start)\
} release UserProcessor::shutdown\
\
val dataSource = resource {\
  DataSource().also { it.connect() }\
} release DataSource::close\
\
suspend fun main(): Unit {\
  resource&lt;Service&gt; {\
    Service(dataSource.bind(), userProcessor.bind())\
  }.use { service -&gt; service.processData() }\
}\
//sampleEnd<!--- KNIT example-resource-computations-01.kt -->

## Functions

| Name | Summary |
|---|---|
| [bind](bind.md) | [common]<br>abstract suspend fun &lt;[A](bind.md)&gt; [Resource](../../arrow.fx.coroutines/-resource/index.md)&lt;[A](bind.md)&gt;.[bind](bind.md)(): [A](bind.md) |
