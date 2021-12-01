//[arrow-fx-coroutines](../../../index.md)/[arrow.fx.coroutines](../index.md)/[Resource](index.md)/[use](use.md)

# use

[common]\
infix suspend tailrec fun &lt;[B](use.md)&gt; [use](use.md)(f: suspend ([A](index.md)) -&gt; [B](use.md)): [B](use.md)

Use the created resource When done will run all finalizers

import arrow.fx.coroutines.*\
\
class DataSource {\
  fun connect(): Unit = println("Connecting dataSource")\
  fun users(): List&lt;String&gt; = listOf("User-1", "User-2", "User-3")\
  fun close(): Unit = println("Closed dataSource")\
}\
\
suspend fun main(): Unit {\
  //sampleStart\
  val dataSource = resource {\
    DataSource().also { it.connect() }\
  } release DataSource::close\
\
  val res = dataSource\
    .use { ds -&gt; "Using data source: ${ds.users()}" }\
    .also(::println)\
  //sampleEnd\
}<!--- KNIT example-resource-04.kt -->
