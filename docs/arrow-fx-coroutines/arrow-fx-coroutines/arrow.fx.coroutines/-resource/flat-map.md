//[arrow-fx-coroutines](../../../index.md)/[arrow.fx.coroutines](../index.md)/[Resource](index.md)/[flatMap](flat-map.md)

# flatMap

[common]\
fun &lt;[B](flat-map.md)&gt; [flatMap](flat-map.md)(f: ([A](index.md)) -&gt; [Resource](index.md)&lt;[B](flat-map.md)&gt;): [Resource](index.md)&lt;[B](flat-map.md)&gt;

Create a resource value of [B](flat-map.md) from a resource [A](index.md) by mapping [f](flat-map.md).

Useful when there is a need to create resources that depend on other resources, for combining independent values [zip](zip.md) provides nicer syntax without the need for callback nesting.

import arrow.fx.coroutines.*\
\
object Connection\
class DataSource {\
  fun connect(): Unit = println("Connecting dataSource")\
  fun connection(): Connection = Connection\
  fun close(): Unit = println("Closed dataSource")\
}\
\
class Database(private val database: DataSource) {\
  fun init(): Unit = println("Database initialising . . .")\
  fun shutdown(): Unit = println("Database shutting down . . .")\
}\
\
suspend fun main(): Unit {\
  //sampleStart\
  val dataSource = resource {\
    DataSource().also { it.connect() }\
  } release DataSource::close\
\
  fun database(ds: DataSource): Resource&lt;Database&gt; =\
    resource {\
      Database(ds).also(Database::init)\
    } release Database::shutdown\
\
  dataSource.flatMap(::database)\
    .use { println("Using database which uses dataSource") }\
  //sampleEnd\
}<!--- KNIT example-resource-05.kt -->

## See also

common

| | |
|---|---|
| [arrow.fx.coroutines.Resource](par-zip.md) | for combining independent resources in parallel |
