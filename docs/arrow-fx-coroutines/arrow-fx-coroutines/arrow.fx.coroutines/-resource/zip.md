//[arrow-fx-coroutines](../../../index.md)/[arrow.fx.coroutines](../index.md)/[Resource](index.md)/[zip](zip.md)

# zip

[common]\
inline fun &lt;[B](zip.md), [C](zip.md)&gt; [zip](zip.md)(other: [Resource](index.md)&lt;[B](zip.md)&gt;, crossinline combine: ([A](index.md), [B](zip.md)) -&gt; [C](zip.md)): [Resource](index.md)&lt;[C](zip.md)&gt;

fun &lt;[B](zip.md)&gt; [zip](zip.md)(other: [Resource](index.md)&lt;[B](zip.md)&gt;): [Resource](index.md)&lt;[Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[A](index.md), [B](zip.md)&gt;&gt;

inline fun &lt;[B](zip.md), [C](zip.md), [D](zip.md), [E](zip.md)&gt; [zip](zip.md)(b: [Resource](index.md)&lt;[B](zip.md)&gt;, c: [Resource](index.md)&lt;[C](zip.md)&gt;, d: [Resource](index.md)&lt;[D](zip.md)&gt;, crossinline map: ([A](index.md), [B](zip.md), [C](zip.md), [D](zip.md)) -&gt; [E](zip.md)): [Resource](index.md)&lt;[E](zip.md)&gt;

inline fun &lt;[B](zip.md), [C](zip.md), [D](zip.md), [E](zip.md), [F](zip.md), [G](zip.md)&gt; [zip](zip.md)(b: [Resource](index.md)&lt;[B](zip.md)&gt;, c: [Resource](index.md)&lt;[C](zip.md)&gt;, d: [Resource](index.md)&lt;[D](zip.md)&gt;, e: [Resource](index.md)&lt;[E](zip.md)&gt;, crossinline map: ([A](index.md), [B](zip.md), [C](zip.md), [D](zip.md), [E](zip.md)) -&gt; [G](zip.md)): [Resource](index.md)&lt;[G](zip.md)&gt;

inline fun &lt;[B](zip.md), [C](zip.md), [D](zip.md), [E](zip.md), [F](zip.md), [G](zip.md), [H](zip.md)&gt; [zip](zip.md)(b: [Resource](index.md)&lt;[B](zip.md)&gt;, c: [Resource](index.md)&lt;[C](zip.md)&gt;, d: [Resource](index.md)&lt;[D](zip.md)&gt;, e: [Resource](index.md)&lt;[E](zip.md)&gt;, f: [Resource](index.md)&lt;[F](zip.md)&gt;, crossinline map: ([A](index.md), [B](zip.md), [C](zip.md), [D](zip.md), [E](zip.md), [F](zip.md)) -&gt; [G](zip.md)): [Resource](index.md)&lt;[G](zip.md)&gt;

inline fun &lt;[B](zip.md), [C](zip.md), [D](zip.md), [E](zip.md), [F](zip.md), [G](zip.md), [H](zip.md)&gt; [zip](zip.md)(b: [Resource](index.md)&lt;[B](zip.md)&gt;, c: [Resource](index.md)&lt;[C](zip.md)&gt;, d: [Resource](index.md)&lt;[D](zip.md)&gt;, e: [Resource](index.md)&lt;[E](zip.md)&gt;, f: [Resource](index.md)&lt;[F](zip.md)&gt;, g: [Resource](index.md)&lt;[G](zip.md)&gt;, crossinline map: ([A](index.md), [B](zip.md), [C](zip.md), [D](zip.md), [E](zip.md), [F](zip.md), [G](zip.md)) -&gt; [H](zip.md)): [Resource](index.md)&lt;[H](zip.md)&gt;

inline fun &lt;[B](zip.md), [C](zip.md), [D](zip.md), [E](zip.md), [F](zip.md), [G](zip.md), [H](zip.md), [I](zip.md)&gt; [zip](zip.md)(b: [Resource](index.md)&lt;[B](zip.md)&gt;, c: [Resource](index.md)&lt;[C](zip.md)&gt;, d: [Resource](index.md)&lt;[D](zip.md)&gt;, e: [Resource](index.md)&lt;[E](zip.md)&gt;, f: [Resource](index.md)&lt;[F](zip.md)&gt;, g: [Resource](index.md)&lt;[G](zip.md)&gt;, h: [Resource](index.md)&lt;[H](zip.md)&gt;, crossinline map: ([A](index.md), [B](zip.md), [C](zip.md), [D](zip.md), [E](zip.md), [F](zip.md), [G](zip.md), [H](zip.md)) -&gt; [I](zip.md)): [Resource](index.md)&lt;[I](zip.md)&gt;

inline fun &lt;[B](zip.md), [C](zip.md), [D](zip.md), [E](zip.md), [F](zip.md), [G](zip.md), [H](zip.md), [I](zip.md), [J](zip.md)&gt; [zip](zip.md)(b: [Resource](index.md)&lt;[B](zip.md)&gt;, c: [Resource](index.md)&lt;[C](zip.md)&gt;, d: [Resource](index.md)&lt;[D](zip.md)&gt;, e: [Resource](index.md)&lt;[E](zip.md)&gt;, f: [Resource](index.md)&lt;[F](zip.md)&gt;, g: [Resource](index.md)&lt;[G](zip.md)&gt;, h: [Resource](index.md)&lt;[H](zip.md)&gt;, i: [Resource](index.md)&lt;[I](zip.md)&gt;, crossinline map: ([A](index.md), [B](zip.md), [C](zip.md), [D](zip.md), [E](zip.md), [F](zip.md), [G](zip.md), [H](zip.md), [I](zip.md)) -&gt; [J](zip.md)): [Resource](index.md)&lt;[J](zip.md)&gt;

inline fun &lt;[B](zip.md), [C](zip.md), [D](zip.md), [E](zip.md), [F](zip.md), [G](zip.md), [H](zip.md), [I](zip.md), [J](zip.md), [K](zip.md)&gt; [zip](zip.md)(b: [Resource](index.md)&lt;[B](zip.md)&gt;, c: [Resource](index.md)&lt;[C](zip.md)&gt;, d: [Resource](index.md)&lt;[D](zip.md)&gt;, e: [Resource](index.md)&lt;[E](zip.md)&gt;, f: [Resource](index.md)&lt;[F](zip.md)&gt;, g: [Resource](index.md)&lt;[G](zip.md)&gt;, h: [Resource](index.md)&lt;[H](zip.md)&gt;, i: [Resource](index.md)&lt;[I](zip.md)&gt;, j: [Resource](index.md)&lt;[J](zip.md)&gt;, crossinline map: ([A](index.md), [B](zip.md), [C](zip.md), [D](zip.md), [E](zip.md), [F](zip.md), [G](zip.md), [H](zip.md), [I](zip.md), [J](zip.md)) -&gt; [K](zip.md)): [Resource](index.md)&lt;[K](zip.md)&gt;

[common]\
inline fun &lt;[B](zip.md), [C](zip.md), [D](zip.md)&gt; [zip](zip.md)(b: [Resource](index.md)&lt;[B](zip.md)&gt;, c: [Resource](index.md)&lt;[C](zip.md)&gt;, crossinline map: ([A](index.md), [B](zip.md), [C](zip.md)) -&gt; [D](zip.md)): [Resource](index.md)&lt;[D](zip.md)&gt;

Combines two independent resource values with the provided [map](zip.md) function, returning the resulting immutable [Resource](index.md) value. The finalizers run in order of left to right by using [flatMap](flat-map.md) under the hood, but [zip](zip.md) provides a nicer syntax for combining values that don't depend on each-other.

Useful to compose up to 9 independent resources, see example for more details on how to use in code.

import arrow.fx.coroutines.*\
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
  userProcessor.zip(dataSource) { userProcessor, ds -&gt;\
      Service(ds, userProcessor)\
    }.use { service -&gt; service.processData() }\
}\
//sampleEnd<!--- KNIT example-resource-06.kt -->

## See also

common

| | |
|---|---|
| [arrow.fx.coroutines.Resource](flat-map.md) | to combine resources that rely on each-other. |
