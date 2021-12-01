//[arrow-fx-coroutines](../../../index.md)/[arrow.fx.coroutines](../index.md)/[Resource](index.md)

# Resource

[common]\
sealed class [Resource](index.md)&lt;out [A](index.md)&gt;

[Resource](index.md) models resource allocation and releasing. It is especially useful when multiple resources that depend on each other need to be acquired and later released in reverse order. Or when you want to load independent resources in parallel.

When a resource is created we can call [use](use.md) to run a suspend computation with the resource. The finalizers are then guaranteed to run afterwards in reverse order of acquisition.

Consider the following use case:

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
  suspend fun processData(): List&lt;String&gt; = throw RuntimeException("I'm going to leak resources by not closing them")\
}\
\
//sampleStart\
suspend fun main(): Unit {\
  val userProcessor = UserProcessor().also { it.start() }\
  val dataSource = DataSource().also { it.connect() }\
  val service = Service(dataSource, userProcessor)\
\
  service.processData()\
\
  dataSource.close()\
  userProcessor.shutdown()\
}\
//sampleEnd<!--- KNIT example-resource-01.kt -->

In the following example, we are creating and using a service that has a dependency on two resources: A database and a processor. All resources need to be closed in the correct order at the end. However this program is not safe because it is prone to leaking dataSource and userProcessor when an exception or cancellation signal occurs whilst using the service. As a consequence of the resource leak, this program does not guarantee the correct release of resources if something fails while acquiring or using the resource. Additionally manually keeping track of acquisition effects is an unnecessary overhead.

We can split the above program into 3 different steps:

<ol><li>Acquiring the resource</li><li>Using the resource</li><li>Releasing the resource with either [ExitCase.Completed](../-exit-case/-completed/index.md), [ExitCase.Failure](../-exit-case/-failure/index.md) or [ExitCase.Cancelled](../-exit-case/-cancelled/index.md).</li></ol>

That is exactly what Resource does, and how we can solve our problem:

#  Constructing Resource

Creating a resource can be easily done by the resource DSL, and there are two ways to define the finalizers with release or releaseCase.

import arrow.fx.coroutines.*\
\
val resourceA = resource {\
  "A"\
} release { a -&gt;\
  println("Releasing $a")\
}\
\
val resourceB = resource {\
 "B"\
} releaseCase { b, exitCase -&gt;\
  println("Releasing $b with exit: $exitCase")\
}<!--- KNIT example-resource-02.kt -->

Here releaseCase also signals with what [ExitCase](../-exit-case/index.md) state the use step finished.

#  Using and composing Resource

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
  userProcessor.parZip(dataSource) { userProcessor, ds -&gt;\
      Service(ds, userProcessor)\
    }.use { service -&gt; service.processData() }\
}\
//sampleEnd<!--- KNIT example-resource-03.kt -->

[Resource](index.md)s are immutable and can be composed using [zip](zip.md) or [parZip](par-zip.md). [Resource](index.md)s guarantee that their release finalizers are always invoked in the correct order when an exception is raised or the context where the program is running gets canceled.

To achieve this [Resource](index.md) ensures that the acquire&release step are NonCancellable. If a cancellation signal, or an exception is received during acquire, the resource is assumed to not have been acquired and thus will not trigger the release function. => Any composed resources that are already acquired they will be guaranteed to release as expected.

If you don't need a data-type like [Resource](index.md) but want a function alternative to try/catch/finally with automatic error composition, and automatic NonCancellableacquire and release steps use [bracketCase](../bracket-case.md) or [bracket](../bracket.md).

## Types

| Name | Summary |
|---|---|
| [Allocate](-allocate/index.md) | [common]<br>class [Allocate](-allocate/index.md)&lt;[A](-allocate/index.md)&gt;(acquire: suspend () -&gt; [A](-allocate/index.md), release: suspend ([A](-allocate/index.md), [ExitCase](../-exit-case/index.md)) -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)) : [Resource](index.md)&lt;[A](-allocate/index.md)&gt; |
| [Companion](-companion/index.md) | [common]<br>object [Companion](-companion/index.md) |

## Functions

| Name | Summary |
|---|---|
| [ap](ap.md) | [common]<br>fun &lt;[B](ap.md)&gt; [ap](ap.md)(ff: [Resource](index.md)&lt;([A](index.md)) -&gt; [B](ap.md)&gt;): [Resource](index.md)&lt;[B](ap.md)&gt; |
| [flatMap](flat-map.md) | [common]<br>fun &lt;[B](flat-map.md)&gt; [flatMap](flat-map.md)(f: ([A](index.md)) -&gt; [Resource](index.md)&lt;[B](flat-map.md)&gt;): [Resource](index.md)&lt;[B](flat-map.md)&gt;<br>Create a resource value of [B](flat-map.md) from a resource [A](index.md) by mapping [f](flat-map.md). |
| [map](map.md) | [common]<br>fun &lt;[B](map.md)&gt; [map](map.md)(f: suspend ([A](index.md)) -&gt; [B](map.md)): [Resource](index.md)&lt;[B](map.md)&gt; |
| [parZip](par-zip.md) | [common]<br>fun &lt;[B](par-zip.md), [C](par-zip.md)&gt; [parZip](par-zip.md)(fb: [Resource](index.md)&lt;[B](par-zip.md)&gt;, f: suspend ([A](index.md), [B](par-zip.md)) -&gt; [C](par-zip.md)): [Resource](index.md)&lt;[C](par-zip.md)&gt;<br>[common]<br>fun &lt;[B](par-zip.md), [C](par-zip.md)&gt; [parZip](par-zip.md)(ctx: [CoroutineContext](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-coroutine-context/index.html) = Dispatchers.Default, fb: [Resource](index.md)&lt;[B](par-zip.md)&gt;, f: suspend ([A](index.md), [B](par-zip.md)) -&gt; [C](par-zip.md)): [Resource](index.md)&lt;[C](par-zip.md)&gt;<br>Composes two [Resource](index.md)s together by zipping them in parallel, by running both their acquire handlers in parallel, and both release handlers in parallel. |
| [tap](tap.md) | [common]<br>fun &lt;[B](tap.md)&gt; [tap](tap.md)(f: suspend ([A](index.md)) -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)): [Resource](index.md)&lt;[A](index.md)&gt;<br>Useful for setting up/configuring an acquired resource |
| [use](use.md) | [common]<br>infix suspend tailrec fun &lt;[B](use.md)&gt; [use](use.md)(f: suspend ([A](index.md)) -&gt; [B](use.md)): [B](use.md)<br>Use the created resource When done will run all finalizers |
| [zip](zip.md) | [common]<br>fun &lt;[B](zip.md)&gt; [zip](zip.md)(other: [Resource](index.md)&lt;[B](zip.md)&gt;): [Resource](index.md)&lt;[Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[A](index.md), [B](zip.md)&gt;&gt;<br>inline fun &lt;[B](zip.md), [C](zip.md)&gt; [zip](zip.md)(other: [Resource](index.md)&lt;[B](zip.md)&gt;, crossinline combine: ([A](index.md), [B](zip.md)) -&gt; [C](zip.md)): [Resource](index.md)&lt;[C](zip.md)&gt;<br>inline fun &lt;[B](zip.md), [C](zip.md), [D](zip.md), [E](zip.md)&gt; [zip](zip.md)(b: [Resource](index.md)&lt;[B](zip.md)&gt;, c: [Resource](index.md)&lt;[C](zip.md)&gt;, d: [Resource](index.md)&lt;[D](zip.md)&gt;, crossinline map: ([A](index.md), [B](zip.md), [C](zip.md), [D](zip.md)) -&gt; [E](zip.md)): [Resource](index.md)&lt;[E](zip.md)&gt;<br>inline fun &lt;[B](zip.md), [C](zip.md), [D](zip.md), [E](zip.md), [F](zip.md), [G](zip.md)&gt; [zip](zip.md)(b: [Resource](index.md)&lt;[B](zip.md)&gt;, c: [Resource](index.md)&lt;[C](zip.md)&gt;, d: [Resource](index.md)&lt;[D](zip.md)&gt;, e: [Resource](index.md)&lt;[E](zip.md)&gt;, crossinline map: ([A](index.md), [B](zip.md), [C](zip.md), [D](zip.md), [E](zip.md)) -&gt; [G](zip.md)): [Resource](index.md)&lt;[G](zip.md)&gt;<br>inline fun &lt;[B](zip.md), [C](zip.md), [D](zip.md), [E](zip.md), [F](zip.md), [G](zip.md), [H](zip.md)&gt; [zip](zip.md)(b: [Resource](index.md)&lt;[B](zip.md)&gt;, c: [Resource](index.md)&lt;[C](zip.md)&gt;, d: [Resource](index.md)&lt;[D](zip.md)&gt;, e: [Resource](index.md)&lt;[E](zip.md)&gt;, f: [Resource](index.md)&lt;[F](zip.md)&gt;, crossinline map: ([A](index.md), [B](zip.md), [C](zip.md), [D](zip.md), [E](zip.md), [F](zip.md)) -&gt; [G](zip.md)): [Resource](index.md)&lt;[G](zip.md)&gt;<br>inline fun &lt;[B](zip.md), [C](zip.md), [D](zip.md), [E](zip.md), [F](zip.md), [G](zip.md), [H](zip.md)&gt; [zip](zip.md)(b: [Resource](index.md)&lt;[B](zip.md)&gt;, c: [Resource](index.md)&lt;[C](zip.md)&gt;, d: [Resource](index.md)&lt;[D](zip.md)&gt;, e: [Resource](index.md)&lt;[E](zip.md)&gt;, f: [Resource](index.md)&lt;[F](zip.md)&gt;, g: [Resource](index.md)&lt;[G](zip.md)&gt;, crossinline map: ([A](index.md), [B](zip.md), [C](zip.md), [D](zip.md), [E](zip.md), [F](zip.md), [G](zip.md)) -&gt; [H](zip.md)): [Resource](index.md)&lt;[H](zip.md)&gt;<br>inline fun &lt;[B](zip.md), [C](zip.md), [D](zip.md), [E](zip.md), [F](zip.md), [G](zip.md), [H](zip.md), [I](zip.md)&gt; [zip](zip.md)(b: [Resource](index.md)&lt;[B](zip.md)&gt;, c: [Resource](index.md)&lt;[C](zip.md)&gt;, d: [Resource](index.md)&lt;[D](zip.md)&gt;, e: [Resource](index.md)&lt;[E](zip.md)&gt;, f: [Resource](index.md)&lt;[F](zip.md)&gt;, g: [Resource](index.md)&lt;[G](zip.md)&gt;, h: [Resource](index.md)&lt;[H](zip.md)&gt;, crossinline map: ([A](index.md), [B](zip.md), [C](zip.md), [D](zip.md), [E](zip.md), [F](zip.md), [G](zip.md), [H](zip.md)) -&gt; [I](zip.md)): [Resource](index.md)&lt;[I](zip.md)&gt;<br>inline fun &lt;[B](zip.md), [C](zip.md), [D](zip.md), [E](zip.md), [F](zip.md), [G](zip.md), [H](zip.md), [I](zip.md), [J](zip.md)&gt; [zip](zip.md)(b: [Resource](index.md)&lt;[B](zip.md)&gt;, c: [Resource](index.md)&lt;[C](zip.md)&gt;, d: [Resource](index.md)&lt;[D](zip.md)&gt;, e: [Resource](index.md)&lt;[E](zip.md)&gt;, f: [Resource](index.md)&lt;[F](zip.md)&gt;, g: [Resource](index.md)&lt;[G](zip.md)&gt;, h: [Resource](index.md)&lt;[H](zip.md)&gt;, i: [Resource](index.md)&lt;[I](zip.md)&gt;, crossinline map: ([A](index.md), [B](zip.md), [C](zip.md), [D](zip.md), [E](zip.md), [F](zip.md), [G](zip.md), [H](zip.md), [I](zip.md)) -&gt; [J](zip.md)): [Resource](index.md)&lt;[J](zip.md)&gt;<br>inline fun &lt;[B](zip.md), [C](zip.md), [D](zip.md), [E](zip.md), [F](zip.md), [G](zip.md), [H](zip.md), [I](zip.md), [J](zip.md), [K](zip.md)&gt; [zip](zip.md)(b: [Resource](index.md)&lt;[B](zip.md)&gt;, c: [Resource](index.md)&lt;[C](zip.md)&gt;, d: [Resource](index.md)&lt;[D](zip.md)&gt;, e: [Resource](index.md)&lt;[E](zip.md)&gt;, f: [Resource](index.md)&lt;[F](zip.md)&gt;, g: [Resource](index.md)&lt;[G](zip.md)&gt;, h: [Resource](index.md)&lt;[H](zip.md)&gt;, i: [Resource](index.md)&lt;[I](zip.md)&gt;, j: [Resource](index.md)&lt;[J](zip.md)&gt;, crossinline map: ([A](index.md), [B](zip.md), [C](zip.md), [D](zip.md), [E](zip.md), [F](zip.md), [G](zip.md), [H](zip.md), [I](zip.md), [J](zip.md)) -&gt; [K](zip.md)): [Resource](index.md)&lt;[K](zip.md)&gt;<br>[common]<br>inline fun &lt;[B](zip.md), [C](zip.md), [D](zip.md)&gt; [zip](zip.md)(b: [Resource](index.md)&lt;[B](zip.md)&gt;, c: [Resource](index.md)&lt;[C](zip.md)&gt;, crossinline map: ([A](index.md), [B](zip.md), [C](zip.md)) -&gt; [D](zip.md)): [Resource](index.md)&lt;[D](zip.md)&gt;<br>Combines two independent resource values with the provided [map](zip.md) function, returning the resulting immutable [Resource](index.md) value. The finalizers run in order of left to right by using [flatMap](flat-map.md) under the hood, but [zip](zip.md) provides a nicer syntax for combining values that don't depend on each-other. |

## Inheritors

| Name |
|---|
| [Resource](-allocate/index.md) |

## Extensions

| Name | Summary |
|---|---|
| [release](../release.md) | [common]<br>infix fun &lt;[A](../release.md)&gt; [Resource](index.md)&lt;[A](../release.md)&gt;.[release](../release.md)(release: suspend ([A](../release.md)) -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)): [Resource](index.md)&lt;[A](../release.md)&gt;<br>Composes a [release](../release.md) action to a [Resource.use](use.md) action creating a [Resource](index.md). |
| [releaseCase](../release-case.md) | [common]<br>infix fun &lt;[A](../release-case.md)&gt; [Resource](index.md)&lt;[A](../release-case.md)&gt;.[releaseCase](../release-case.md)(release: suspend ([A](../release-case.md), [ExitCase](../-exit-case/index.md)) -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)): [Resource](index.md)&lt;[A](../release-case.md)&gt;<br>Composes a [releaseCase](../../../../arrow-fx-coroutines/arrow.fx.coroutines/index.md) action to a [Resource.use](use.md) action creating a [Resource](index.md). |
