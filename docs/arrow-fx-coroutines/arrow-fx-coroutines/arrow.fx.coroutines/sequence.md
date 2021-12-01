//[arrow-fx-coroutines](../../index.md)/[arrow.fx.coroutines](index.md)/[sequence](sequence.md)

# sequence

[common]\
inline fun &lt;[A](sequence.md)&gt; [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;[Resource](-resource/index.md)&lt;[A](sequence.md)&gt;&gt;.[sequence](sequence.md)(): [Resource](-resource/index.md)&lt;[List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[A](sequence.md)&gt;&gt;

Sequences this [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html) of [Resource](-resource/index.md)s. [Iterable.map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/index.html) and [sequence](sequence.md) is equivalent to [traverseResource](traverse-resource.md).

import arrow.fx.coroutines.*\
\
class File(url: String) {\
  suspend fun open(): File = this\
  suspend fun close(): Unit {}\
  override fun toString(): String = "This file contains some interesting content!"\
}\
\
suspend fun openFile(uri: String): File = File(uri).open()\
suspend fun closeFile(file: File): Unit = file.close()\
suspend fun fileToString(file: File): String = file.toString()\
\
suspend fun main(): Unit {\
  //sampleStart\
  val res: List&lt;String&gt; = listOf(\
    "data.json",\
    "user.json",\
    "resource.json"\
  ).map { uri -&gt;\
    resource {\
     openFile(uri)\
    } release { file -&gt;\
      closeFile(file)\
    }\
  }.sequence().use { files -&gt;\
    files.map { fileToString(it) }\
  }\
  //sampleEnd\
  res.forEach(::println)\
}<!--- KNIT example-resource-11.kt -->
